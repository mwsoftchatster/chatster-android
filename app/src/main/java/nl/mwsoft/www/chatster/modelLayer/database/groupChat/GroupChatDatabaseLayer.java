package nl.mwsoft.www.chatster.modelLayer.database.groupChat;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.ArrayList;

import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.database.dblocal.ChatsProvider;
import nl.mwsoft.www.chatster.modelLayer.database.dblocal.DBOpenHelper;
import nl.mwsoft.www.chatster.modelLayer.helper.util.dateTime.ChatsterDateTimeUtil;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChat;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChatInvitation;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChatMessage;
import nl.mwsoft.www.chatster.modelLayer.model.GroupMessageQueueItem;
import nl.mwsoft.www.chatster.modelLayer.model.OneTimeGroupKeyPair;
import nl.mwsoft.www.chatster.modelLayer.model.OneTimeKeyPair;
import nl.mwsoft.www.chatster.modelLayer.model.ReceivedOnlineGroupMessage;

public class GroupChatDatabaseLayer {

    private ChatsterDateTimeUtil chatsterDateTimeUtil;

    public GroupChatDatabaseLayer() {
    }

    public GroupChatDatabaseLayer(ChatsterDateTimeUtil chatsterDateTimeUtil) {
        this.chatsterDateTimeUtil = chatsterDateTimeUtil;
    }

    public void updateMessageHasBeenReadByMessageId(int messageId, Context context){
        ContentValues messageValues = new ContentValues();
        String selection = DBOpenHelper.MESSAGE_ID + "=" + messageId;
        messageValues.put(DBOpenHelper.MESSAGE_HAS_BEEN_READ, 1);

        context.getContentResolver().update(ChatsProvider.CONTENT_URI_MESSAGE, messageValues, selection, null);
    }

    public String getGroupProfilePicUriById(Context context, String groupId) {
        String groupProfilePicUri = "";
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_GROUP_CHAT + " WHERE " +
                DBOpenHelper.GROUP_CHAT_ID + " LIKE'" + groupId + "'", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                groupProfilePicUri = cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.GROUP_CHAT_PROFILE_PIC));
            }
        }

        cursor.close();
        db.close();

        return groupProfilePicUri;
    }

    public int getUnreadMessageCountByGroupChatId(Context context, String groupChatId) {
        int count = 0;
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT COUNT (*) AS c FROM " + DBOpenHelper.TABLE_MESSAGE +
                " WHERE " + DBOpenHelper.MESSAGE_HAS_BEEN_READ + "=0 AND " +
                DBOpenHelper.MESSAGE_GROUP_CHAT_ID + " LIKE '" + groupChatId + "'", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow("c")));
            }
        }

        cursor.close();
        db.close();

        return count;
    }

    public ArrayList<GroupChatMessage> getAllMessagesForGroupChatWithId(Context context, String groupChatId) {
        ArrayList<GroupChatMessage> groupChatMessages = new ArrayList<>();
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_MESSAGE + " WHERE " +
                DBOpenHelper.MESSAGE_GROUP_CHAT_ID + " LIKE '" + groupChatId + "'", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                GroupChatMessage groupChatMessage = new GroupChatMessage();
                groupChatMessage.setGetGroupChatMessageId(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.MESSAGE_ID))));
                groupChatMessage.setGroupChatId(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.MESSAGE_GROUP_CHAT_ID)));
                groupChatMessage.setSenderId(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.MESSAGE_SENDER_ID))));
                groupChatMessage.setGroupChatMessageCreated(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.MESSAGE_CREATED)));
                groupChatMessage.setMsgType(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.TYPE_MESSAGE)));
                groupChatMessage.setUuid(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.MESSAGE_UUID)));
                if (cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.TYPE_MESSAGE)).equals(ConstantRegistry.TEXT)
                        ||cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.TYPE_MESSAGE)).equals(ConstantRegistry.JOINED_GROUP_CHAT)) {
                    groupChatMessage.setMessageText(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.TEXT_MESSAGE)));
                } else {
                    groupChatMessage.setBinaryMessageFilePath(Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.BINARY_MESSAGE_FILE_PATH))));
                }

                groupChatMessages.add(groupChatMessage);
            }
        }

        cursor.close();
        db.close();

        return groupChatMessages;
    }

    public GroupChatMessage getLastGroupChatMessageByChatId(Context context, String groupChatId) {
        GroupChatMessage lastMessage = new GroupChatMessage();
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_MESSAGE + " WHERE " + DBOpenHelper.MESSAGE_GROUP_CHAT_ID
                + " LIKE '" + groupChatId + "' ORDER BY " + DBOpenHelper.MESSAGE_ID + " DESC LIMIT 1", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                lastMessage.setGetGroupChatMessageId(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.MESSAGE_ID))));
                try {
                    lastMessage.setMessageText(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.TEXT_MESSAGE)));
                } catch (NullPointerException e) {
                    lastMessage.setMessageText(context.getString(R.string.img));
                }
                lastMessage.setGroupChatMessageCreated(chatsterDateTimeUtil.getMessageCreated(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.MESSAGE_CREATED))));
            }
        }
        cursor.close();
        db.close();

        return lastMessage;
    }

    public GroupChatInvitation getGroupChatInvitation(Context context) {
        GroupChatInvitation groupChatInvitation = null;
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_OFFLINE_CONTACT_RESPONSE +
                " WHERE " + DBOpenHelper.OFFLINE_CONTACT_RESPONSE_TYPE_MESSAGE + "= 'groupChatInvitation' AND _id =(" +
                " SELECT MAX(_id) FROM " + DBOpenHelper.TABLE_OFFLINE_CONTACT_RESPONSE +
                " )", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                groupChatInvitation = new GroupChatInvitation();
                groupChatInvitation.setGroupChatInvitationChatId(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.OFFLINE_CONTACT_RESPONSE_GROUP_CHAT_INVITATION_CHAT_ID)));
                groupChatInvitation.setGroupChatInvitationSenderId(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.OFFLINE_CONTACT_RESPONSE_GROUP_CHAT_INVITATION_SENDER_ID))));
                ArrayList<Long> ids = new ArrayList<>();
                String allIds = cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.OFFLINE_CONTACT_RESPONSE_GROUP_CHAT_MEMBERS));
                String[] splitIds = allIds.split(",");
                for (int i = 0; i < splitIds.length; i++) {
                    ids.add(Long.parseLong(splitIds[i].trim()));
                }
                groupChatInvitation.setGroupChatInvitationGroupChatMembers(ids);
                groupChatInvitation.setGroupChatInvitationChatName(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.OFFLINE_CONTACT_RESPONSE_GROUP_CHAT_CHAT_NAME)));
                groupChatInvitation.setGroupProfilePicPath(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.OFFLINE_CONTACT_RESPONSE_GROUP_CHAT_PROFILE_IMAGE)));
            }
        }

        cursor.close();
        db.close();

        return groupChatInvitation;
    }

    public ArrayList<String> getAllGroupChatIds(Context context) {
        ArrayList<String> groupChatIds = new ArrayList<>();
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_GROUP_CHAT, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.GROUP_CHAT_ID));
                groupChatIds.add(id);
            }
        }

        cursor.close();
        db.close();

        return groupChatIds;
    }

    public String getGroupChatNameById(Context context, String groupChatId) {
        String groupChatName = "";
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_GROUP_CHAT + " WHERE "
                + DBOpenHelper.GROUP_CHAT_ID + " LIKE '" + groupChatId + "'", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                groupChatName = cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.GROUP_CHAT_NAME));
            }
        }

        cursor.close();
        db.close();

        return groupChatName;
    }

    public long getGroupChatAdminByGroupId(Context context, String groupChatId) {
        long groupChatAdmin = 0L;
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_GROUP_CHAT + " WHERE " +
                DBOpenHelper.GROUP_CHAT_ID + " LIKE '" + groupChatId + "'", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                groupChatAdmin = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.GROUP_CHAT_ADMIN_ID)));
            }
        }

        cursor.close();
        db.close();

        return groupChatAdmin;
    }

    public ArrayList<GroupChat> getAllGroupChats(Context context) {
        ArrayList<GroupChat> groupChats = new ArrayList<>();
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_GROUP_CHAT, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                GroupChat groupChat = new GroupChat();
                groupChat.setGroupChatAdminId(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.GROUP_CHAT_ADMIN_ID))));
                groupChat.set_id(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.GROUP_CHAT_ID)));
                groupChat.setGroupChatName(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.GROUP_CHAT_NAME)));
                groupChat.setGroupChatLastMessage(getLastGroupChatMessageByChatId(context, groupChat.get_id()).getMessageText());
                groupChat.setGroupChatLastActivity(getLastGroupChatMessageByChatId(context, groupChat.get_id()).getGroupChatMessageCreated());
                groupChats.add(groupChat);
            }
        }
        cursor.close();
        db.close();

        return groupChats;
    }

    public GroupChat getGroupChatById(Context context, String id) {
        GroupChat groupChat = new GroupChat();
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_GROUP_CHAT + " WHERE " +
                DBOpenHelper.GROUP_CHAT_ID + " LIKE '" + id + "'", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                groupChat.setGroupChatAdminId(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.GROUP_CHAT_ADMIN_ID))));
                groupChat.set_id(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.GROUP_CHAT_ID)));
                groupChat.setGroupChatName(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.GROUP_CHAT_NAME)));
                groupChat.setGroupChatStatusMessage(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.GROUP_CHAT_STATUS_MESSAGE)));
                groupChat.setGroupChatImage(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.GROUP_CHAT_PROFILE_PIC)));
            }
        }
        cursor.close();
        db.close();

        return groupChat;
    }

    public void deleteGroupChatMessage(int messageId, Context context){
        String msgSelectionUpdate = DBOpenHelper.MESSAGE_ID + "=" + messageId;
        context.getContentResolver().delete(ChatsProvider.CONTENT_URI_MESSAGE, msgSelectionUpdate, null);
    }

    public void updateMessageUnsentByUUID(String uuid, Context context){
        String selection = DBOpenHelper.MESSAGE_UUID.concat(ConstantRegistry.CHATSTER_EQUALS).concat("?");
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBOpenHelper.BINARY_MESSAGE_FILE_PATH, ConstantRegistry.CHATSTER_EMPTY_STRING);
        contentValues.put(DBOpenHelper.TYPE_MESSAGE, ConstantRegistry.TEXT);
        contentValues.put(DBOpenHelper.TEXT_MESSAGE, context.getString(R.string.message_deleted));
        context.getContentResolver().update(ChatsProvider.CONTENT_URI_MESSAGE, contentValues, selection,
                new String[]{uuid});
    }

    public void insertGroupChatMessage(GroupChatMessage groupChatMessage, Context context) {
        ContentValues setValues = new ContentValues();
        setValues.put(DBOpenHelper.MESSAGE_SENDER_ID, groupChatMessage.getSenderId());
        setValues.put(DBOpenHelper.TYPE_MESSAGE, groupChatMessage.getMsgType());
        setValues.put(DBOpenHelper.MESSAGE_GROUP_CHAT_ID, groupChatMessage.getGroupChatId());
        setValues.put(DBOpenHelper.MESSAGE_HAS_BEEN_READ, 1);
        setValues.put(DBOpenHelper.MESSAGE_UUID, groupChatMessage.getUuid());
        setValues.put(DBOpenHelper.MESSAGE_CREATED, groupChatMessage.getGroupChatMessageCreated());
        if(groupChatMessage.getMsgType().equals(ConstantRegistry.TEXT)){
            setValues.put(DBOpenHelper.TEXT_MESSAGE, groupChatMessage.getMessageText());
        }else{
            setValues.put(DBOpenHelper.BINARY_MESSAGE_FILE_PATH, groupChatMessage.getBinaryMessageFilePath().toString());
        }

        Uri setUri = context.getContentResolver().insert(ChatsProvider.CONTENT_URI_MESSAGE, setValues);
    }

    public void createGroupChat(GroupChat groupChat, Context context) {
        ContentValues setValues = new ContentValues();
        setValues.put(DBOpenHelper.GROUP_CHAT_ID, groupChat.get_id());
        setValues.put(DBOpenHelper.GROUP_CHAT_ADMIN_ID, groupChat.getGroupChatAdminId());
        setValues.put(DBOpenHelper.GROUP_CHAT_NAME, groupChat.getGroupChatName());
        setValues.put(DBOpenHelper.GROUP_CHAT_STATUS_MESSAGE, groupChat.getGroupChatStatusMessage());
        setValues.put(DBOpenHelper.GROUP_CHAT_PROFILE_PIC, groupChat.getGroupChatImage());

        Uri setUri = context.getContentResolver().insert(ChatsProvider.CONTENT_URI_GROUP_CHAT, setValues);
    }

    public void createGroupChatMember(String groupChatId, long groupChatMemberId, Context context) {
        ContentValues setValues = new ContentValues();
        setValues.put(DBOpenHelper.GROUP_CHAT_MEMBER_GROUP_CHAT_ID, groupChatId);
        setValues.put(DBOpenHelper.GROUP_CHAT_MEMBER_ID, groupChatMemberId);

        Uri setUri = context.getContentResolver().insert(ChatsProvider.CONTENT_URI_GROUP_CHAT_MEMBER, setValues);
    }

    public void updateGroupProfilePic(String uri, String groupChatId, Context context){
        String selectionUpdate = DBOpenHelper.GROUP_CHAT_ID + " LIKE '" + groupChatId + "'";
        ContentValues profilePicUri = new ContentValues();
        profilePicUri.put(DBOpenHelper.GROUP_CHAT_PROFILE_PIC, uri);
        context.getContentResolver().update(ChatsProvider.CONTENT_URI_GROUP_CHAT, profilePicUri, selectionUpdate, null);
    }

    public void deleteGroupChat(Context context, String groupChatId){
        // 1. delete all the messages that were received for this group
        String msgSelectionUpdate = DBOpenHelper.MESSAGE_GROUP_CHAT_ID + " LIKE '" + groupChatId + "'";
        context.getContentResolver().delete(ChatsProvider.CONTENT_URI_MESSAGE, msgSelectionUpdate, null);

        // 2. delete all group chat members entries in GROUP_CHAT_MEMBERS table
        String contactSelectionUpdate = DBOpenHelper.GROUP_CHAT_MEMBER_GROUP_CHAT_ID + " LIKE '" + groupChatId + "'";
        context.getContentResolver().delete(ChatsProvider.CONTENT_URI_GROUP_CHAT_MEMBER, contactSelectionUpdate, null);

        // 3. delete group chat
        String chatSelectionUpdate = DBOpenHelper.GROUP_CHAT_ID + " LIKE '" + groupChatId + "'";
        context.getContentResolver().delete(ChatsProvider.CONTENT_URI_GROUP_CHAT, chatSelectionUpdate, null);
    }

    public void deleteGroupChatInvitationNotification(String groupChatId, Context context){
        String notifSelectionUpdate = DBOpenHelper.OFFLINE_CONTACT_RESPONSE_GROUP_CHAT_INVITATION_CHAT_ID + " LIKE '" + groupChatId + "'";
        context.getContentResolver().delete(ChatsProvider.CONTENT_URI_OFFLINE_CONTACT_RESPONSE, notifSelectionUpdate, null);
    }

    public void insertGroupChat(GroupChat groupChat, Context context) {
        ContentValues setValues = new ContentValues();
        setValues.put(DBOpenHelper.GROUP_CHAT_ID, groupChat.get_id());
        setValues.put(DBOpenHelper.GROUP_CHAT_ADMIN_ID, groupChat.getGroupChatAdminId());
        setValues.put(DBOpenHelper.GROUP_CHAT_NAME, groupChat.getGroupChatName());
        setValues.put(DBOpenHelper.GROUP_CHAT_PROFILE_PIC, groupChat.getGroupChatImage());
        setValues.put(DBOpenHelper.GROUP_CHAT_STATUS_MESSAGE, groupChat.getGroupChatStatusMessage());

        Uri setUri = context.getContentResolver().insert(ChatsProvider.CONTENT_URI_GROUP_CHAT, setValues);
    }

    public void insertGroupChatMember(String groupChatId, long groupChatMemberId, Context context) {
        ContentValues setValues = new ContentValues();
        setValues.put(DBOpenHelper.GROUP_CHAT_MEMBER_GROUP_CHAT_ID, groupChatId);
        setValues.put(DBOpenHelper.GROUP_CHAT_MEMBER_ID, groupChatMemberId);

        Uri setUri = context.getContentResolver().insert(ChatsProvider.CONTENT_URI_GROUP_CHAT_MEMBER, setValues);
    }

    public void deleteGroupChatMessageQueueItemByUUID(String uuid, Context context){
        String msgSelectionUpdate = DBOpenHelper.GROUP_MESSAGE_QUEUE_MESSAGE_UUID + "='" + uuid + "'";
        context.getContentResolver().delete(ChatsProvider.CONTENT_URI_GROUP_MESSAGE_QUEUE, msgSelectionUpdate, null);
    }

    public void insertGroupChatMessageQueueItem(String messageUUID, Context context) {
        ContentValues setValues = new ContentValues();
        setValues.put(DBOpenHelper.GROUP_MESSAGE_QUEUE_MESSAGE_UUID, messageUUID);

        Uri setUri = context.getContentResolver().insert(ChatsProvider.CONTENT_URI_GROUP_MESSAGE_QUEUE, setValues);
    }

    public GroupMessageQueueItem getGroupMessageQueueItemGroupChat(Context context){
        GroupMessageQueueItem groupMessageQueueItem = null;
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_GROUP_MESSAGE_QUEUE, null);
        if (cursor != null) {
            while(cursor.moveToNext()) {
                groupMessageQueueItem = new GroupMessageQueueItem();
                groupMessageQueueItem.set_id(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.GROUP_MESSAGE_QUEUE_ITEM_ID))));
                groupMessageQueueItem.setMessageUUID(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.GROUP_MESSAGE_QUEUE_MESSAGE_UUID)));
            }
        }
        cursor.close();
        db.close();

        return groupMessageQueueItem;
    }

    public GroupChatMessage getGroupChatMessageByUUID(Context context, String uuid) {
        GroupChatMessage groupChatMessage = null;
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_MESSAGE + " WHERE " + DBOpenHelper.MESSAGE_UUID
                + " LIKE '" + uuid + "'", null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                groupChatMessage = new GroupChatMessage();
                groupChatMessage.setGetGroupChatMessageId(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.MESSAGE_ID))));
                groupChatMessage.setGroupChatId(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.MESSAGE_GROUP_CHAT_ID)));
                groupChatMessage.setSenderId(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.MESSAGE_SENDER_ID))));
                groupChatMessage.setGroupChatMessageCreated(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.MESSAGE_CREATED)));
                groupChatMessage.setMsgType(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.TYPE_MESSAGE)));
                if (cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.TEXT_MESSAGE)) != null) {
                    groupChatMessage.setMessageText(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.TEXT_MESSAGE)));
                } else {
                    groupChatMessage.setBinaryMessageFilePath(Uri.parse(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.BINARY_MESSAGE_FILE_PATH))));
                }
                groupChatMessage.setUuid(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.MESSAGE_UUID)));
            }
        }
        cursor.close();
        db.close();

        return groupChatMessage;
    }

    public void insertReceivedOnlineGroupMessage(String uuid, Context context) {
        ContentValues setValues = new ContentValues();
        setValues.put(DBOpenHelper.RECEIVED_ONLINE_GROUP_MESSAGE_UUID, uuid);

        Uri setUri = context.getContentResolver().insert(ChatsProvider.CONTENT_URI_RECEIVED_ONLINE_GROUP_MESSAGE, setValues);
    }

    public void deleteReceivedOnlineGroupMessageByUUID(String uuid, Context context){
        String chatSelectionUpdate = DBOpenHelper.RECEIVED_ONLINE_GROUP_MESSAGE_UUID + " LIKE ? ";
        context.getContentResolver().delete(ChatsProvider.CONTENT_URI_RECEIVED_ONLINE_GROUP_MESSAGE,
                chatSelectionUpdate, new String[]{uuid});
    }

    public ReceivedOnlineGroupMessage getReceivedOnlineGroupMessage(Context context) {
        ReceivedOnlineGroupMessage receivedOnlineGroupMessage = null;
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_RECEIVED_ONLINE_GROUP_MESSAGE, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                receivedOnlineGroupMessage = new ReceivedOnlineGroupMessage();
                receivedOnlineGroupMessage.set_id(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.RECEIVED_ONLINE_GROUP_MESSAGE_ID))));
                receivedOnlineGroupMessage.setUuid(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.RECEIVED_ONLINE_GROUP_MESSAGE_UUID)));
            }
        }
        cursor.close();
        db.close();

        return receivedOnlineGroupMessage;
    }

    public String[] getReceivedOnlineGroupMessageUUIDs(Context context) {
        ArrayList<String> receivedOnlineGroupMessageUUIDs = new ArrayList<>();
        int size = 0;
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_RECEIVED_ONLINE_GROUP_MESSAGE , null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String receivedOnlineGroupMessageUUID = cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.RECEIVED_ONLINE_GROUP_MESSAGE_UUID));
                receivedOnlineGroupMessageUUIDs.add(receivedOnlineGroupMessageUUID);
                size++;
            }
        }
        cursor.close();
        db.close();
        String[] uuids = new String[size];
        return receivedOnlineGroupMessageUUIDs.toArray(uuids);
    }

    public OneTimeGroupKeyPair getGroupOneTimeKeyPair(Context context, String groupChatId, long userId) {
        OneTimeGroupKeyPair oneTimeGroupKeyPair = null;
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_GROUP_ONE_TIME_PRE_KEY_PAIR
                + " WHERE " + DBOpenHelper.GROUP_ONE_TIME_PRE_KEY_PAIR_GROUP_ID + " LIKE '" + groupChatId + "'", null);
        if (cursor != null) {
            oneTimeGroupKeyPair = new OneTimeGroupKeyPair();
            while (cursor.moveToNext()) {
                oneTimeGroupKeyPair.setOneTimeGroupPrivateKey(cursor.getBlob(cursor.getColumnIndexOrThrow(DBOpenHelper.GROUP_ONE_TIME_PRE_KEY_PAIR_PRK)));
                oneTimeGroupKeyPair.setOneTimeGroupPublicKey(cursor.getBlob(cursor.getColumnIndexOrThrow(DBOpenHelper.GROUP_ONE_TIME_PRE_KEY_PAIR_PBK)));
                oneTimeGroupKeyPair.setUuid(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.GROUP_ONE_TIME_PRE_KEY_PAIR_UUID)));
                oneTimeGroupKeyPair.setGroupChatId(groupChatId);
                oneTimeGroupKeyPair.setUserId(userId);
            }
        }
        cursor.close();
        db.close();

        return oneTimeGroupKeyPair;
    }

    public byte[] getGroupOneTimePublicPreKey(Context context, String groupChatId) {
        byte[] groupOneTimePublicPreKey = null;
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT "+ DBOpenHelper.GROUP_ONE_TIME_PRE_KEY_PAIR_PBK
                + " FROM " + DBOpenHelper.TABLE_GROUP_ONE_TIME_PRE_KEY_PAIR
                + " WHERE " + DBOpenHelper.GROUP_ONE_TIME_PRE_KEY_PAIR_GROUP_ID + " LIKE '" + groupChatId + "'", null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                groupOneTimePublicPreKey =
                        cursor.getBlob(cursor.getColumnIndexOrThrow(DBOpenHelper.GROUP_ONE_TIME_PRE_KEY_PAIR_PBK));
            }
        }
        cursor.close();
        db.close();

        return groupOneTimePublicPreKey;
    }

    public byte[] getGroupOneTimePrivatePreKeyByUUID(Context context, String uuid) {
        byte[] groupOneTimePrivatePreKey = null;
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT "+ DBOpenHelper.GROUP_ONE_TIME_PRE_KEY_PAIR_PRK
                + " FROM " + DBOpenHelper.TABLE_GROUP_ONE_TIME_PRE_KEY_PAIR
                + " WHERE " + DBOpenHelper.GROUP_ONE_TIME_PRE_KEY_PAIR_UUID + " LIKE '" + uuid + "'", null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                groupOneTimePrivatePreKey = cursor.getBlob(cursor.getColumnIndexOrThrow(DBOpenHelper.GROUP_ONE_TIME_PRE_KEY_PAIR_PRK));
            }
        }
        cursor.close();
        db.close();

        return groupOneTimePrivatePreKey;
    }

    public byte[] getGroupOneTimePublicPreKeyByUUID(Context context, String uuid) {
        byte[] groupOneTimePublicPreKey = null;
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT "+ DBOpenHelper.GROUP_ONE_TIME_PRE_KEY_PAIR_PBK
                + " FROM " + DBOpenHelper.TABLE_GROUP_ONE_TIME_PRE_KEY_PAIR
                + " WHERE " + DBOpenHelper.GROUP_ONE_TIME_PRE_KEY_PAIR_UUID + " LIKE '" + uuid + "'", null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                groupOneTimePublicPreKey =
                        cursor.getBlob(cursor.getColumnIndexOrThrow(DBOpenHelper.GROUP_ONE_TIME_PRE_KEY_PAIR_PBK));
            }
        }
        cursor.close();
        db.close();

        return groupOneTimePublicPreKey;
    }

    public void insertGroupOneTimeKeyPair(String uuid, String groupChatId, byte[] groupOneTimePrivatePreKey,
                                         byte[] groupOneTimePublicPreKey, Context context) {
        ContentValues setValues = new ContentValues();
        setValues.put(DBOpenHelper.GROUP_ONE_TIME_PRE_KEY_PAIR_UUID, uuid);
        setValues.put(DBOpenHelper.GROUP_ONE_TIME_PRE_KEY_PAIR_GROUP_ID, groupChatId);
        setValues.put(DBOpenHelper.GROUP_ONE_TIME_PRE_KEY_PAIR_PRK, groupOneTimePrivatePreKey);
        setValues.put(DBOpenHelper.GROUP_ONE_TIME_PRE_KEY_PAIR_PBK, groupOneTimePublicPreKey);

        Uri setUri = context.getContentResolver().insert(ChatsProvider.CONTENT_URI_GROUP_ONE_TIME_PRE_KEY_PAIR, setValues);
    }

    public void deleteOneTimeGroupKeyPairByUUID(String uuid, Context context){
        String selection = DBOpenHelper.GROUP_ONE_TIME_PRE_KEY_PAIR_UUID + "='" + uuid + "'";
        context.getContentResolver().delete(ChatsProvider.CONTENT_URI_GROUP_ONE_TIME_PRE_KEY_PAIR, selection, null);
    }

    public OneTimeGroupKeyPair getUserOneGroupTimeKeyPairByUUID(Context context, String uuid, long userId, String groupChatId) {
        OneTimeGroupKeyPair oneTimeGroupKeyPair = null;
        Cursor cursor = null;
        SQLiteDatabase db = new DBOpenHelper(context).getReadableDatabase();
        cursor = db.rawQuery("SELECT * FROM " + DBOpenHelper.TABLE_GROUP_ONE_TIME_PRE_KEY_PAIR
                + " WHERE " + DBOpenHelper.GROUP_ONE_TIME_PRE_KEY_PAIR_UUID + "='" + uuid + "'", null);
        if (cursor != null) {
            oneTimeGroupKeyPair = new OneTimeGroupKeyPair();
            while (cursor.moveToNext()) {
                oneTimeGroupKeyPair.setOneTimeGroupPrivateKey(cursor.getBlob(cursor.getColumnIndexOrThrow(DBOpenHelper.GROUP_ONE_TIME_PRE_KEY_PAIR_PRK)));
                oneTimeGroupKeyPair.setOneTimeGroupPublicKey(cursor.getBlob(cursor.getColumnIndexOrThrow(DBOpenHelper.GROUP_ONE_TIME_PRE_KEY_PAIR_PBK)));
                oneTimeGroupKeyPair.setUserId(userId);
                oneTimeGroupKeyPair.setGroupChatId(groupChatId);
                oneTimeGroupKeyPair.setUuid(cursor.getString(cursor.getColumnIndexOrThrow(DBOpenHelper.GROUP_ONE_TIME_PRE_KEY_PAIR_UUID)));
            }
        }
        cursor.close();
        db.close();

        return oneTimeGroupKeyPair;
    }
}
