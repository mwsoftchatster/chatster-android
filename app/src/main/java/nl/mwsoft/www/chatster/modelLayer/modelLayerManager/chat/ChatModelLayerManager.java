package nl.mwsoft.www.chatster.modelLayer.modelLayerManager.chat;


import android.content.Context;

import java.util.ArrayList;

import io.reactivex.Observable;
import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.modelLayer.database.chat.ChatDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.model.Chat;
import nl.mwsoft.www.chatster.modelLayer.model.Message;
import nl.mwsoft.www.chatster.modelLayer.model.ReceivedOnlineMessage;

public class ChatModelLayerManager {

    private ChatDatabaseLayer chatDatabaseLayer;


    // region 1-to-1 Chat DB

    public ChatModelLayerManager() {
        this.chatDatabaseLayer = DependencyRegistry.shared.createChatDatabaseLayer();
    }

    public void updateMessageHasBeenReadByMessageId(int messageId, Context context){
        this.chatDatabaseLayer.updateMessageHasBeenReadByMessageId(messageId, context);
    }

    public void updateContactIsAllowedToUnsendByChatId(int chatId, int isAllowedToUnsend, Context context){
        this.chatDatabaseLayer.updateContactIsAllowedToUnsendByChatId(chatId, isAllowedToUnsend, context);
    }

    public int getUnreadMessageCountByChatId(Context context, int chatId) {
        return this.chatDatabaseLayer.getUnreadMessageCountByChatId(context, chatId);
    }

    public Message getLastChatMessageByChatId(Context context, int chatId) {
        return this.chatDatabaseLayer.getLastChatMessageByChatId(context, chatId);
    }

    public ArrayList<Message> getAllMessagesForChatWithId(Context context, int chatId) {
        return this.chatDatabaseLayer.getAllMessagesForChatWithId(context, chatId);
    }

    public boolean getIsAllowedToUnsendByChatId(Context context, int chatId) {
        return this.chatDatabaseLayer.getIsAllowedToUnsendByChatId(context, chatId);
    }

    public int getChatIdByChatName(Context context, String chatName) {
        return this.chatDatabaseLayer.getChatIdByChatName(context, chatName);
    }

    public ArrayList<Chat> getAllChats(Context context) {
        return this.chatDatabaseLayer.getAllChats(context);
    }

    public Chat getChatByContactId(Context context, long contactId) {
        return this.chatDatabaseLayer.getChatByContactId(context, contactId);
    }

    public Chat getChatById(Context context, int id) {
        return this.chatDatabaseLayer.getChatById(context, id);
    }

    public void deleteChatMessageById(int messageId, Context context){
        this.chatDatabaseLayer.deleteChatMessageById(messageId, context);
    }

    public void deleteChatMessageBySenderId(long senderId, Context context){
        this.chatDatabaseLayer.deleteChatMessageBySenderId(senderId,context);
    }

    public void insertChat(long chatContactId, String chatName, Context context) {
        this.chatDatabaseLayer.insertChat(chatContactId, chatName, context);
    }

    public void insertChatMessageQueueItem(String messageUUID, String contactPKUUID, String userPKUUID, long receiverId, Context context) {
        this.chatDatabaseLayer.insertChatMessageQueueItem(messageUUID, contactPKUUID, userPKUUID, receiverId, context);
    }

    public void insertChatMessage(Message chatMessage, Context context) {
        this.chatDatabaseLayer.insertChatMessage(chatMessage,context);
    }

    public void insertImageMessage(Message chatMessage, Context context) {
        this.chatDatabaseLayer.insertImageMessage(chatMessage, context);
    }

    public void deleteChatById(int chatId, Context context){
        this.chatDatabaseLayer.deleteChatById(chatId, context);
    }

    public void deleteChatMessageQueueItemByUUID(String uuid, Context context){
        this.chatDatabaseLayer.deleteChatMessageQueueItemByUUID(uuid, context);
    }

    public void insertReceivedOnlineMessage(String uuid, Context context) {
        this.chatDatabaseLayer.insertReceivedOnlineMessage(uuid, context);
    }

    public void deleteReceivedOnlineMessageByUUID(String uuid, Context context){
        this.chatDatabaseLayer.deleteReceivedOnlineMessageByUUID(uuid, context);
    }

    public ReceivedOnlineMessage getReceivedOnlineMessage(Context context) {
        return this.chatDatabaseLayer.getReceivedOnlineMessage(context);
    }

    // endregion

}
