package nl.mwsoft.www.chatster.presenterLayer.chat;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.annotation.NonNull;

import org.whispersystems.curve25519.Curve25519KeyPair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.e2e.ChatsterE2EHelper;
import nl.mwsoft.www.chatster.modelLayer.model.Chat;
import nl.mwsoft.www.chatster.modelLayer.model.Contact;
import nl.mwsoft.www.chatster.modelLayer.model.Message;
import nl.mwsoft.www.chatster.modelLayer.model.OneTimeKeyPair;
import nl.mwsoft.www.chatster.modelLayer.model.OneTimePublicKey;
import nl.mwsoft.www.chatster.modelLayer.model.ReceivedOnlineMessage;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.chat.ChatModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.contact.ContactModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.notification.NotificationModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.user.UserModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.image.ImageProcessingManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.util.UtilModelLayerManager;


public class ChatPresenter {

    private ChatModelLayerManager chatModelLayerManager;
    private UserModelLayerManager userModelLayerManager;
    private ContactModelLayerManager contactModelLayerManager;
    private NotificationModelLayerManager notificationModelLayerManager;
    private ImageProcessingManager imageProcessingManager;
    private UtilModelLayerManager utilModelLayerManager;
    private ChatsterE2EHelper chatsterE2EHelper;

    // region Constructor

    // ChatActivity
    public ChatPresenter(UserModelLayerManager userModelLayerManager, ContactModelLayerManager contactModelLayerManager,
                         ChatModelLayerManager chatModelLayerManager, NotificationModelLayerManager notificationModelLayerManager,
                         ImageProcessingManager imageProcessingManager, UtilModelLayerManager utilModelLayerManager,
                         ChatsterE2EHelper chatsterE2EHelper){
        this.imageProcessingManager = imageProcessingManager;
        this.chatModelLayerManager = chatModelLayerManager;
        this.userModelLayerManager = userModelLayerManager;
        this.contactModelLayerManager = contactModelLayerManager;
        this.notificationModelLayerManager = notificationModelLayerManager;
        this.utilModelLayerManager = utilModelLayerManager;
        this.chatsterE2EHelper = chatsterE2EHelper;
    }


    // ChatSettingsActivity
    public ChatPresenter(UserModelLayerManager userModelLayerManager, ContactModelLayerManager contactModelLayerManager,
                         ChatModelLayerManager chatModelLayerManager, UtilModelLayerManager utilModelLayerManager){
        this.chatModelLayerManager = chatModelLayerManager;
        this.userModelLayerManager = userModelLayerManager;
        this.contactModelLayerManager = contactModelLayerManager;
        this.utilModelLayerManager = utilModelLayerManager;
    }

    // ChatMessageAdapter
    public ChatPresenter(UserModelLayerManager userModelLayerManager, ContactModelLayerManager contactModelLayerManager,
                         ChatModelLayerManager chatModelLayerManager){
        this.chatModelLayerManager = chatModelLayerManager;
        this.userModelLayerManager = userModelLayerManager;
        this.contactModelLayerManager = contactModelLayerManager;
    }

    // ChatsFragment
    public ChatPresenter(ChatModelLayerManager chatModelLayerManager){
        this.chatModelLayerManager = chatModelLayerManager;
    }


    // ChatsAdapter
    public ChatPresenter(ContactModelLayerManager contactModelLayerManager, ChatModelLayerManager chatModelLayerManager,
                         ChatsterE2EHelper chatsterE2EHelper){
        this.contactModelLayerManager = contactModelLayerManager;
        this.chatModelLayerManager = chatModelLayerManager;
        this.chatsterE2EHelper = chatsterE2EHelper;
    }

    // endregion

    // region DB

    public Bitmap decodeImage(String data) {
        return this.imageProcessingManager.decodeImage(data);
    }

    public Uri saveIncomingImageMessage(Context context, Bitmap inImage) {
        return this.imageProcessingManager.saveIncomingImageMessage(context, inImage);
    }

    public String encodeImageToString(Context context, Uri imageUrl) throws IOException {
        return this.imageProcessingManager.encodeImageToString(context, imageUrl);
    }

    public File createImageFile(Context context) throws IOException {
        return this.imageProcessingManager.createImageFile(context);
    }

    public Bitmap decodeSampledBitmap(Context context, Uri imageUrl) throws IOException {
        return this.imageProcessingManager.decodeSampledBitmap(context, imageUrl);
    }

    public void insertImageMessage(Message chatMessage, Context context) {
        this.chatModelLayerManager.insertImageMessage(chatMessage, context);
    }

    public Chat getChatById(Context context, int id) {
        return this.chatModelLayerManager.getChatById(context, id);
    }

    public void deleteChatById(int chatId, Context context){
        this.chatModelLayerManager.deleteChatById(chatId, context);
    }

    public ArrayList<Chat> getAllChats(Context context) {
        return this.chatModelLayerManager.getAllChats(context);
    }

    public Message getLastChatMessageByChatId(Context context, int chatId) {
        return this.chatModelLayerManager.getLastChatMessageByChatId(context, chatId);
    }

    public int getUnreadMessageCountByChatId(Context context, int chatId) {
        return this.chatModelLayerManager.getUnreadMessageCountByChatId(context, chatId);
    }

    public void updateMessageHasBeenReadByMessageId(int messageId, Context context){
        this.chatModelLayerManager.updateMessageHasBeenReadByMessageId(messageId, context);
    }

    public long getUserId(Context context) {
        return this.userModelLayerManager.getUserId(context);
    }

    public String getUserName(Context context) {
        return this.userModelLayerManager.getUserName(context);
    }

    public String getContactNameById(Context context, long contactId) {
        return this.contactModelLayerManager.getContactNameById(context, contactId);
    }

    public void deleteChatMessageById(int messageId, Context context){
        this.chatModelLayerManager.deleteChatMessageById(messageId, context);
    }

    public void updateMessageUnsentByMessageUUID(String uuid, Context context){
        this.notificationModelLayerManager.updateMessageUnsentByMessageUUID(uuid, context);
    }

    public Contact getContactById(Context context, long contactId) {
        return this.contactModelLayerManager.getContactById(context, contactId);
    }

    public ArrayList<Message> getAllMessagesForChatWithId(Context context, int chatId) {
        return this.chatModelLayerManager.getAllMessagesForChatWithId(context, chatId);
    }

    public Chat getChatByContactId(Context context, long contactId) {
        return this.chatModelLayerManager.getChatByContactId(context, contactId);
    }

    public void insertChat(long chatContactId, String chatName, Context context) {
        this.chatModelLayerManager.insertChat(chatContactId, chatName, context);
    }

    public void insertContact(long contactId, String contactName, String contactStatusMessage, Context context) {
        this.contactModelLayerManager.insertContact(contactId, contactName, contactStatusMessage, context);
    }

    public void deleteChatMessageBySenderId(long senderId, Context context){
        this.chatModelLayerManager.deleteChatMessageBySenderId(senderId,context);
    }

    public void insertChatMessage(Message chatMessage, Context context) {
        this.chatModelLayerManager.insertChatMessage(chatMessage,context);
    }

    public int getChatIdByChatName(Context context, String chatName) {
        return this.chatModelLayerManager.getChatIdByChatName(context, chatName);
    }

    public String getContactProfilePicUriById(Context context, long contactId) {
        return this.contactModelLayerManager.getContactProfilePicUriById(context, contactId);
    }

    public boolean getIsAllowedToUnsendByChatId(Context context, int chatId) {
        return this.chatModelLayerManager.getIsAllowedToUnsendByChatId(context, chatId);
    }

    public void updateContactIsAllowedToUnsendByChatId(int chatId, int isAllowedToUnsend, Context context){
        this.chatModelLayerManager.updateContactIsAllowedToUnsendByChatId(chatId, isAllowedToUnsend, context);
    }

    public void insertChatMessageQueueItem(String messageUUID, String contactPKUUID, String userPKUUID, long receiverId, Context context) {
        this.chatModelLayerManager.insertChatMessageQueueItem(messageUUID, contactPKUUID, userPKUUID, receiverId, context);
    }

    public void deleteChatMessageQueueItemByUUID(String uuid, Context context){
        this.chatModelLayerManager.deleteChatMessageQueueItemByUUID(uuid, context);
    }

    public void insertReceivedOnlineMessage(String uuid, Context context) {
        this.chatModelLayerManager.insertReceivedOnlineMessage(uuid, context);
    }

    public void deleteReceivedOnlineMessageByUUID(String uuid, Context context){
        this.chatModelLayerManager.deleteReceivedOnlineMessageByUUID(uuid, context);
    }

    public ReceivedOnlineMessage getReceivedOnlineMessage(Context context) {
        return this.chatModelLayerManager.getReceivedOnlineMessage(context);
    }

    public OneTimeKeyPair getUserOneTimeKeyPair(Context context, long userId) {
        return this.userModelLayerManager.getUserOneTimeKeyPair(context, userId);
    }

    public byte[] getUserOneTimePublicPreKey(Context context, long userId){
        return this.userModelLayerManager.getUserOneTimePublicPreKey(context, userId);
    }

    public byte[] getUserOneTimePrivatePreKeyByUUID(Context context, String uuid){
        return this.userModelLayerManager.getUserOneTimePrivatePreKeyByUUID(context, uuid);
    }

    public byte[] getUserOneTimePublicPreKey(Context context, String uuid){
        return this.userModelLayerManager.getUserOneTimePublicPreKeyByUUID(context, uuid);
    }

    public void deleteOneTimeKeyPairByUUID(String uuid, Context context){
        this.userModelLayerManager.deleteOneTimeKeyPairByUUID(uuid, context);
    }

    // endregion

    // region Create Message

    @NonNull
    public Message createTextMessage(long senderId, int chatId, String message, String utc, String uuid) {
        Message chatMessage = new Message();
        chatMessage.setMsgType(ConstantRegistry.TEXT);
        chatMessage.setMessageSenderId(senderId);
        chatMessage.setMessageChatId(chatId);
        chatMessage.setMessageText(message);
        chatMessage.setMessageUUID(uuid);
        chatMessage.setMessageCreated(utilModelLayerManager.convertFromUtcToLocal(utc));
        return chatMessage;
    }

    @NonNull
    public Message createTextMessage(String msgType,long senderId, int chatId, String message, String dateTime, String uuid) {
        Message chatMessage = new Message();
        chatMessage.setMsgType(msgType);
        chatMessage.setMessageSenderId(senderId);
        chatMessage.setMessageChatId(chatId);
        chatMessage.setMessageText(message);
        chatMessage.setMessageUUID(uuid);
        chatMessage.setMessageCreated(dateTime);
        return chatMessage;
    }

    @NonNull
    public Message createImageMessage(long senderId, int chatId, Uri imageUrl, String uuid){
        Message chatMessage = new Message();
        chatMessage.setMsgType(ConstantRegistry.IMAGE);
        chatMessage.setMessageSenderId(senderId);
        chatMessage.setMessageChatId(chatId);
        chatMessage.setBinaryMessageFilePath(imageUrl);
        chatMessage.setMessageUUID(uuid);
        chatMessage.setMessageCreated(utilModelLayerManager.getDateTime());
        return chatMessage;
    }

    @NonNull
    public Message createImageMessage(long senderId, int chatId, Uri imageUrl, String utc, String uuid){
        Message chatMessage = new Message();
        chatMessage.setMsgType(ConstantRegistry.IMAGE);
        chatMessage.setMessageSenderId(senderId);
        chatMessage.setMessageChatId(chatId);
        chatMessage.setBinaryMessageFilePath(imageUrl);
        chatMessage.setMessageUUID(uuid);
        chatMessage.setMessageCreated(utilModelLayerManager.convertFromUtcToLocal(utc));
        return chatMessage;
    }

    // endregion

    // region Initialize Unsend Message Registry

    public Observable<HashMap<String,Integer>> initializeUnsendMessageRegistry(ArrayList<Message> messages){
        return Observable.create(new ObservableOnSubscribe<HashMap<String,Integer>>() {
            @Override
            public void subscribe(ObservableEmitter<HashMap<String,Integer>> emitter) throws Exception {
                try {
                    HashMap<String,Integer> unsendMessageRegistry = new HashMap<>();
                    int position = 0;
                    for(Message message : messages){
                        unsendMessageRegistry.put(message.getMessageUUID(), position);
                        position += 1;
                    }
                    emitter.onNext(unsendMessageRegistry);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    // endregion

    // region Network

    // region Upload User One Time Keys

    public Observable<String> uploadPublicKeys(long userId, String  oneTimePreKeyPairPbks){
        return this.userModelLayerManager.uploadPublicKeys(userId, oneTimePreKeyPairPbks);
    }

    // endregion

    // region Get One Time Key

    public Observable<OneTimePublicKey> getPublicKey(long contactId){
        return this.userModelLayerManager.getPublicKey(contactId);
    }

    // endregion

    // region Get One Time Key By UUID

    public Observable<OneTimePublicKey> getPublicKeyByUUID(long contactId, String uuid){
        return this.userModelLayerManager.getPublicKeyByUUID(contactId, uuid);
    }

    // endregion

    // region Upload New Batch Of Keys

    public Observable<String> uploadNewBatchOfKeys(long userId, String oneTimePreKeyPairPbks){
        return this.userModelLayerManager.uploadPublicKeys(userId, oneTimePreKeyPairPbks);
    }

    public Observable<String> checkPublicKeys(long userId){
        return this.userModelLayerManager.checkPublicKeys(userId);
    }

    // endregion

    // endregion

    // region Util

    public boolean hasInternetConnection(){
        return utilModelLayerManager.hasInternetConnection();
    }

    public String createUUID(){
        return utilModelLayerManager.createUUID();
    }


    public String getDateTime(){
        return utilModelLayerManager.getDateTime();
    }

    // endregion

    // region ChatsterE2EHelper

    private List<Curve25519KeyPair> generateOneTimeKeys(int amount){
        return chatsterE2EHelper.generateOneTimeKeys(amount);
    }

    private List<String> generateUUIDS(int amount){
        return chatsterE2EHelper.generateUUIDS(amount);
    }

    public String jsonifyOneTimeKeys(List<Curve25519KeyPair> keyPairs, List<String> uuids, long userId) {
        return chatsterE2EHelper.jsonifyOneTimeKeys(keyPairs, uuids, userId);
    }

    public byte[] generateEncryptSharedSecret(byte[] receiverPublicKey, byte[] senderPrivateKey){
        return chatsterE2EHelper.generateEncryptSharedSecret(receiverPublicKey, senderPrivateKey);
    }

    public byte[] generateDecryptSharedSecret(byte[] senderPublicKey, byte[] receiverPrivateKey){
        return chatsterE2EHelper.generateDecryptSharedSecret(senderPublicKey, receiverPrivateKey);
    }

    public byte[] generateSignature(byte[] senderPrivateKey, byte[] encryptedMessage){
        return chatsterE2EHelper.generateSignature(senderPrivateKey, encryptedMessage);
    }

    public boolean verifySignature(byte[] senderPublicKey, byte[] encryptedMessage, byte[] signature){
        return chatsterE2EHelper.verifySignature(senderPublicKey, encryptedMessage, signature);
    }

    public byte[] encrypt(byte[] message, byte[] sharedSecret){
        return chatsterE2EHelper.encrypt(message, sharedSecret);
    }

    public byte[] decrypt(byte[] cipherMessage, byte[] sharedSecret){
        return chatsterE2EHelper.decrypt(cipherMessage, sharedSecret);
    }

    public byte[] appendHMACSignatureAndPKTo(byte[] encryptedMessage, byte[] hmac, byte[] signature, byte[] userPublicKey){
        return chatsterE2EHelper.appendHMACSignatureAndPKTo(encryptedMessage, hmac, signature, userPublicKey);
    }

    public byte[] getMessageFrom(byte[] messageWithHMACSignatureAndPK){
        return chatsterE2EHelper.getMessageFrom(messageWithHMACSignatureAndPK);
    }

    public byte[] getHMACFrom(byte[] messageWithHMACSignatureAndPK){
        return chatsterE2EHelper.getHMACFrom(messageWithHMACSignatureAndPK);
    }

    public byte[] getSignatureFrom(byte[] messageWithHMACSignatureAndPK){
        return chatsterE2EHelper.getSignatureFrom(messageWithHMACSignatureAndPK);
    }

    public byte[] getPublicKeyFrom(byte[] messageWithHMACSignatureAndPK){
        return chatsterE2EHelper.getPublicKeyFrom(messageWithHMACSignatureAndPK);
    }

    public String messageWithSignatureHMACAndPKToString(byte[] messageWithSignatureHMACAndPK){
        return chatsterE2EHelper.messageWithSignatureToString(messageWithSignatureHMACAndPK);
    }

    public byte[] messageWithSignatureHMACAndPKToByteArray(String messageWithSignatureHMACAndPK){
        return chatsterE2EHelper.messageWithSignatureHMACAndPKToByteArray(messageWithSignatureHMACAndPK);
    }

    public String bytesToHex(byte[] bytes) {
        return chatsterE2EHelper.bytesToHex(bytes);
    }

    public byte[] messageToHMAC(byte[] message, byte[] sharedSecret){
        return chatsterE2EHelper.messageToHMAC(message, sharedSecret);
    }

    public String jsonifiedOneTimeKeys(Context context, long userId, int amount){
        // 1. Generate n one time keys
        List<Curve25519KeyPair> oneTimeKeys = generateOneTimeKeys(amount);

        // 2. Generate n uuid's
        List<String> uuids = generateUUIDS(amount);

        // 3. Store both private and public keys locally
        this.userModelLayerManager.insertOneTimeKeyPairs(context, userId, oneTimeKeys, uuids);

        // 4. JSONify public keys to be stored on the server
        return jsonifyOneTimeKeys(oneTimeKeys, uuids, userId);
    }

    // endregion



}
