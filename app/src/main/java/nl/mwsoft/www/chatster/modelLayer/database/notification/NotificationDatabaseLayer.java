package nl.mwsoft.www.chatster.modelLayer.database.notification;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;

import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.database.chat.ChatDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.database.dblocal.ChatsProvider;
import nl.mwsoft.www.chatster.modelLayer.database.dblocal.DBOpenHelper;
import nl.mwsoft.www.chatster.modelLayer.helper.util.dateTime.ChatsterDateTimeUtil;
import nl.mwsoft.www.chatster.modelLayer.model.Chat;
import nl.mwsoft.www.chatster.modelLayer.model.ContactLatestInformation;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChatInvitation;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChatMessage;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChatOfflineMessage;
import nl.mwsoft.www.chatster.modelLayer.model.Message;
import nl.mwsoft.www.chatster.modelLayer.model.OfflineMessage;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.image.ImageProcessingManager;

public class NotificationDatabaseLayer {

    private ChatsterDateTimeUtil chatsterDateTimeUtil;

    public NotificationDatabaseLayer() {
    }

    public NotificationDatabaseLayer(ChatsterDateTimeUtil chatsterDateTimeUtil) {
        this.chatsterDateTimeUtil = chatsterDateTimeUtil;
    }

    public void deleteGroupChatInvitationNotification(String groupChatId, Context context){
        String notificationSelectionUpdate = DBOpenHelper.OFFLINE_CONTACT_RESPONSE_GROUP_CHAT_INVITATION_CHAT_ID + " LIKE '" + groupChatId + "'";
        context.getContentResolver().delete(ChatsProvider.CONTENT_URI_OFFLINE_CONTACT_RESPONSE, notificationSelectionUpdate, null);
    }

    public void insertGroupChatInvitationNotification(GroupChatInvitation groupChatInvitation, Context context) {
        ContentValues setValues = new ContentValues();
        setValues.put(DBOpenHelper.OFFLINE_CONTACT_RESPONSE_TYPE_MESSAGE, ConstantRegistry.CHATSTER_GROUP_CHAT_INVITATION_MSG);
        setValues.put(DBOpenHelper.OFFLINE_CONTACT_RESPONSE_GROUP_CHAT_INVITATION_CHAT_ID,
                groupChatInvitation.getGroupChatInvitationChatId());
        setValues.put(DBOpenHelper.OFFLINE_CONTACT_RESPONSE_GROUP_CHAT_INVITATION_SENDER_ID,
                groupChatInvitation.getGroupChatInvitationSenderId());
        setValues.put(DBOpenHelper.OFFLINE_CONTACT_RESPONSE_GROUP_CHAT_MEMBERS,
                groupChatInvitation.getGroupChatInvitationGroupChatMembers().toString()
                        .replace(ConstantRegistry.CHATSTER_OPEN_SQUARE_BRACKETS, ConstantRegistry.CHATSTER_EMPTY_STRING)
                        .replace(ConstantRegistry.CHATSTER_CLOSE_SQUARE_BRACKETS, ConstantRegistry.CHATSTER_EMPTY_STRING));
        setValues.put(DBOpenHelper.OFFLINE_CONTACT_RESPONSE_GROUP_CHAT_CHAT_NAME,
                groupChatInvitation.getGroupChatInvitationChatName());
        setValues.put(DBOpenHelper.OFFLINE_CONTACT_RESPONSE_GROUP_CHAT_PROFILE_IMAGE,
                groupChatInvitation.getGroupProfilePicPath());

        Uri setUri = context.getContentResolver().insert(ChatsProvider.CONTENT_URI_OFFLINE_CONTACT_RESPONSE, setValues);
    }

    public void insertIncomingImageMessage(Message chatMessage, Context context) {
        ContentValues setValues = new ContentValues();
        setValues.put(DBOpenHelper.MESSAGE_SENDER_ID, chatMessage.getMessageSenderId());
        setValues.put(DBOpenHelper.TYPE_MESSAGE, chatMessage.getMsgType());
        setValues.put(DBOpenHelper.MESSAGE_CHAT_ID, chatMessage.getMessageChatId());
        setValues.put(DBOpenHelper.MESSAGE_UUID, chatMessage.getMessageUUID());
        setValues.put(DBOpenHelper.BINARY_MESSAGE_FILE_PATH, chatMessage.getBinaryMessageFilePath().toString());
        setValues.put(DBOpenHelper.MESSAGE_CREATED, chatMessage.getMessageCreated());
        Uri setUri = context.getContentResolver().insert(ChatsProvider.CONTENT_URI_MESSAGE, setValues);
    }

    public void insertOfflineMessage(OfflineMessage message, ImageProcessingManager imageProcessingManager,
                                            Context context) {
        ChatDatabaseLayer chatDatabaseLayer = DependencyRegistry.shared.createChatDatabaseLayer();
        Chat chat = new Chat();
        ContentValues setValues = new ContentValues();
        setValues.put(DBOpenHelper.MESSAGE_SENDER_ID, message.getSenderId());
        setValues.put(DBOpenHelper.TYPE_MESSAGE, message.getContentType());
        setValues.put(DBOpenHelper.MESSAGE_UUID, message.getUuid());
        int chatId = chatDatabaseLayer.getChatIdByChatName(context, message.getChatName());
        if(chatId == 0){
            //that means the chatName needs to be reversed
            String[] splitChatName = message.getChatName().split(ConstantRegistry.CHATSTER_AT);
            String reversedChatName = splitChatName[1].concat(ConstantRegistry.CHATSTER_AT).concat(splitChatName[0]);
            chatId = chatDatabaseLayer.getChatIdByChatName(context, reversedChatName);
        }

        if(chatId == 0){// if chatId is still 0 that means chat does not exist
            // insert new chat
            chatDatabaseLayer.insertChat(message.getSenderId(), message.getChatName(),
                    context);
            chat = chatDatabaseLayer.getChatByContactId(context, message.getSenderId());
            chatId = chat.getChatId();
        }
        setValues.put(DBOpenHelper.MESSAGE_CHAT_ID, chatId);
        setValues.put(DBOpenHelper.MESSAGE_CREATED, chatsterDateTimeUtil.convertFromUtcToLocal(message.getMessageCreated()));
        if(message.getContentType().equals(ConstantRegistry.TEXT)){
            setValues.put(DBOpenHelper.TEXT_MESSAGE, message.getMessageData());
            Uri setUri = context.getContentResolver().insert(ChatsProvider.CONTENT_URI_MESSAGE, setValues);
        }else{
            Message imageMessage = new Message();
            imageMessage.setMsgType(ConstantRegistry.IMAGE);
            imageMessage.setMessageSenderId(message.getSenderId());
            imageMessage.setMessageChatId(chatId);
            imageMessage.setMessageUUID(message.getUuid());
            imageMessage.setBinaryMessageFilePath(imageProcessingManager.saveIncomingImageMessage(context,
                    imageProcessingManager.decodeImage(message.getMessageData())));
            imageMessage.setMessageCreated(chatsterDateTimeUtil.convertFromUtcToLocal(message.getMessageCreated()));
            insertIncomingImageMessage(imageMessage, context);
        }
    }

    public void insertGroupImageMessage(GroupChatMessage groupChatMessage, Context context) {
        ContentValues setValues = new ContentValues();
        setValues.put(DBOpenHelper.MESSAGE_SENDER_ID, groupChatMessage.getSenderId());
        setValues.put(DBOpenHelper.TYPE_MESSAGE, groupChatMessage.getMsgType());
        setValues.put(DBOpenHelper.MESSAGE_UUID, groupChatMessage.getUuid());
        setValues.put(DBOpenHelper.MESSAGE_GROUP_CHAT_ID, groupChatMessage.getGroupChatId());
        setValues.put(DBOpenHelper.BINARY_MESSAGE_FILE_PATH, groupChatMessage.getBinaryMessageFilePath().toString());
        setValues.put(DBOpenHelper.MESSAGE_CREATED, groupChatMessage.getGroupChatMessageCreated());
        Uri setUri = context.getContentResolver().insert(ChatsProvider.CONTENT_URI_MESSAGE, setValues);
    }

    public void addIncomingGroupImageMessage(long senderId, String groupChatId, Uri imageUrl,
                                             Context context, String localFromUtc, String uuid){
        GroupChatMessage groupChatMessage = new GroupChatMessage();
        groupChatMessage.setGroupChatId(groupChatId);
        groupChatMessage.setSenderId(senderId);
        groupChatMessage.setMsgType(ConstantRegistry.IMAGE);
        groupChatMessage.setUuid(uuid);
        groupChatMessage.setBinaryMessageFilePath(imageUrl);
        groupChatMessage.setGroupChatMessageCreated(localFromUtc);
        insertGroupImageMessage(groupChatMessage, context);
    }

    public void insertGroupMessage(GroupChatOfflineMessage groupChatOfflineMessage,
                                          ImageProcessingManager imageProcessingManager,
                                          Context context) {
        ContentValues setValues = new ContentValues();
        setValues.put(DBOpenHelper.MESSAGE_SENDER_ID, groupChatOfflineMessage.getSenderId());
        setValues.put(DBOpenHelper.TYPE_MESSAGE, groupChatOfflineMessage.getContentType());
        setValues.put(DBOpenHelper.MESSAGE_GROUP_CHAT_ID, groupChatOfflineMessage.getGroupChatId());
        setValues.put(DBOpenHelper.MESSAGE_UUID, groupChatOfflineMessage.getUuid());
        setValues.put(DBOpenHelper.MESSAGE_CREATED, chatsterDateTimeUtil.convertFromUtcToLocal(groupChatOfflineMessage.getMessageCreated()));
        if(groupChatOfflineMessage.getContentType().equals(ConstantRegistry.TEXT)
                || groupChatOfflineMessage.getContentType().equals(ConstantRegistry.JOINED_GROUP_CHAT)){
            setValues.put(DBOpenHelper.TEXT_MESSAGE, groupChatOfflineMessage.getGroupChatMessage());
            Uri setUri = context.getContentResolver().insert(ChatsProvider.CONTENT_URI_MESSAGE, setValues);
        }else if(groupChatOfflineMessage.getContentType().equals(ConstantRegistry.IMAGE)){
            addIncomingGroupImageMessage(groupChatOfflineMessage.getSenderId(),
                    groupChatOfflineMessage.getGroupChatId(),
                    imageProcessingManager.saveIncomingImageMessage(context,
                            imageProcessingManager.decodeImage(groupChatOfflineMessage.getGroupChatMessage())),
                    context, chatsterDateTimeUtil.convertFromUtcToLocal(groupChatOfflineMessage.getMessageCreated()), groupChatOfflineMessage.getUuid());
        }
    }

    public void updateMessageUnsentByMessageUUID(String uuid, Context context){
        String selection = DBOpenHelper.MESSAGE_UUID.concat(ConstantRegistry.CHATSTER_EQUALS).concat(ConstantRegistry.CHATSTER_QUESTION_MARK);
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBOpenHelper.BINARY_MESSAGE_FILE_PATH, ConstantRegistry.CHATSTER_EMPTY_STRING);
        contentValues.put(DBOpenHelper.TYPE_MESSAGE, ConstantRegistry.TEXT);
        contentValues.put(DBOpenHelper.TEXT_MESSAGE, context.getString(R.string.message_deleted));
        context.getContentResolver().update(ChatsProvider.CONTENT_URI_MESSAGE, contentValues, selection,
                new String[]{uuid});
    }

    public void updateContacts(ArrayList<ContactLatestInformation> chatsterContacts, Context context){
        for(ContactLatestInformation contact : chatsterContacts){
            String selection = DBOpenHelper.CONTACT_ID + "=" + contact.get_id();
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBOpenHelper.CONTACT_NAME, contact.getName());
            contentValues.put(DBOpenHelper.CONTACT_STATUS_MESSAGE, contact.getStatusMessage());
            contentValues.put(DBOpenHelper.CONTACT_PROFILE_PIC, contact.getProfilePic());
            contentValues.put(DBOpenHelper.CONTACT_TYPE, 1);
            context.getContentResolver().update(ChatsProvider.CONTENT_URI_CONTACT, contentValues, selection, null);
        }
    }

    public void deleteRetrievedOfflineMessageByUUID(String uuid, Context context){
        String msgSelectionUpdate = DBOpenHelper.RETRIEVED_OFFLINE_MESSAGE_UUID + "='" + uuid + "'";
        context.getContentResolver().delete(ChatsProvider.CONTENT_URI_RETRIEVED_OFFLINE_MESSAGE_UUID, msgSelectionUpdate, null);
    }

    public void insertRetrievedOfflineMessageUUID(String uuid, Context context) {
        ContentValues setValues = new ContentValues();
        setValues.put(DBOpenHelper.RETRIEVED_OFFLINE_MESSAGE_UUID, uuid);

        Uri setUri = context.getContentResolver().insert(ChatsProvider.CONTENT_URI_RETRIEVED_OFFLINE_MESSAGE_UUID, setValues);
    }

    public String[] getRetrievedOfflineMessageUUIDs(Context context) {
        ArrayList<String> retrievedOfflineMessageUUIDs = new ArrayList<>();
        int size = 0;
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_RETRIEVED_OFFLINE_MESSAGE_UUID , null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String retrievedOfflineMessageUUID = cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.RETRIEVED_OFFLINE_MESSAGE_UUID));
                retrievedOfflineMessageUUIDs.add(retrievedOfflineMessageUUID);
                size++;
            }
        }
        cursor.close();
        db.close();
        String[] uuids = new String[size];
        return retrievedOfflineMessageUUIDs.toArray(uuids);
    }

}
