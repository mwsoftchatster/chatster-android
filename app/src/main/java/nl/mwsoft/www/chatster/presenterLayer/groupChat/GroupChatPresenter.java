package nl.mwsoft.www.chatster.presenterLayer.groupChat;


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
import nl.mwsoft.www.chatster.modelLayer.helper.groupE2E.ChatsterGroupE2EHelper;
import nl.mwsoft.www.chatster.modelLayer.model.Contact;
import nl.mwsoft.www.chatster.modelLayer.model.CreateGroupChatRequest;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChat;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChatMessage;
import nl.mwsoft.www.chatster.modelLayer.model.OneTimeGroupKeyPair;
import nl.mwsoft.www.chatster.modelLayer.model.OneTimeGroupPublicKey;
import nl.mwsoft.www.chatster.modelLayer.model.ReceivedOnlineGroupMessage;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.contact.ContactModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.groupChat.GroupChatModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.user.UserModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.image.ImageProcessingManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.util.UtilModelLayerManager;

public class GroupChatPresenter {

    private GroupChatModelLayerManager groupChatModelLayerManager;
    private UserModelLayerManager userModelLayerManager;
    private ContactModelLayerManager contactModelLayerManager;
    private ImageProcessingManager imageProcessingManager;
    private UtilModelLayerManager utilModelLayerManager;
    private ChatsterGroupE2EHelper chatsterGroupE2EHelper;


    // region Constructor

    public GroupChatPresenter() {
    }

    // GroupChatMessageAdapter
    public GroupChatPresenter(ContactModelLayerManager contactModelLayerManager,
                              GroupChatModelLayerManager groupChatModelLayerManager,
                              UserModelLayerManager userModelLayerManager){
        this.groupChatModelLayerManager = groupChatModelLayerManager;
        this.userModelLayerManager = userModelLayerManager;
        this.contactModelLayerManager = contactModelLayerManager;
    }

    // CreateGroupChatActivity
    public GroupChatPresenter(ContactModelLayerManager contactModelLayerManager,
                              GroupChatModelLayerManager groupChatModelLayerManager,
                              UserModelLayerManager userModelLayerManager,
                              UtilModelLayerManager utilModelLayerManager,
                              ChatsterGroupE2EHelper chatsterGroupE2EHelper){
        this.groupChatModelLayerManager = groupChatModelLayerManager;
        this.userModelLayerManager = userModelLayerManager;
        this.contactModelLayerManager = contactModelLayerManager;
        this.utilModelLayerManager = utilModelLayerManager;
        this.chatsterGroupE2EHelper = chatsterGroupE2EHelper;
    }

    // CreateGroupChatContactsAdapter && GroupChatInfoAdapter
    public GroupChatPresenter(ContactModelLayerManager contactModelLayerManager){
        this.contactModelLayerManager = contactModelLayerManager;
    }

    // GroupChatActivity
    public GroupChatPresenter(GroupChatModelLayerManager groupChatModelLayerManager,
                              UserModelLayerManager userModelLayerManager,
                              ImageProcessingManager imageProcessingManager,
                              ContactModelLayerManager contactModelLayerManager,
                              UtilModelLayerManager utilModelLayerManager,
                              ChatsterGroupE2EHelper chatsterGroupE2EHelper){
        this.imageProcessingManager = imageProcessingManager;
        this.groupChatModelLayerManager = groupChatModelLayerManager;
        this.userModelLayerManager = userModelLayerManager;
        this.contactModelLayerManager = contactModelLayerManager;
        this.utilModelLayerManager = utilModelLayerManager;
        this.chatsterGroupE2EHelper = chatsterGroupE2EHelper;
    }

    // GroupChatsAdapter && GroupChatsFragment
    public GroupChatPresenter(GroupChatModelLayerManager groupChatModelLayerManager){
        this.groupChatModelLayerManager = groupChatModelLayerManager;
    }

    // endregion

    // region Image Processing

    public File createImageFile(Context context) throws IOException {
        return this.imageProcessingManager.createImageFile(context);
    }

    public Bitmap decodeImage(String data) {
        return this.imageProcessingManager.decodeImage(data);
    }

    public Uri saveIncomingImageMessage(Context context, Bitmap inImage) {
        return this.imageProcessingManager.saveIncomingImageMessage(context, inImage);
    }

    public String encodeImageToString(Context context, Uri imageUrl) throws IOException {
        return this.imageProcessingManager.encodeImageToString(context, imageUrl);
    }

    public Bitmap decodeSampledBitmap(Context context, Uri imageUrl) throws IOException {
        return this.imageProcessingManager.decodeSampledBitmap(context, imageUrl);
    }

    // endregion

    // region Create Group Chat Network

    public Observable<GroupChat> createGroupChat(CreateGroupChatRequest createGroupChatRequest) {
        return this.groupChatModelLayerManager.createGroupChat(createGroupChatRequest);
    }

    // region Group One Time Keys

    public Observable<String> uploadGroupPublicKeys(String oneTimePreKeyPairPbks){
        return this.groupChatModelLayerManager.uploadGroupPublicKeys(oneTimePreKeyPairPbks);
    }

    public Observable<ArrayList<OneTimeGroupPublicKey>> getGroupOneTimeKeys(String groupChatId, long userId){
        return this.groupChatModelLayerManager.getGroupOneTimeKeys(groupChatId, userId);
    }

    // endregion

    // region Group Chat DB

    public void deleteGroupChatMessageQueueItemByUUID(String uuid, Context context){
        this.groupChatModelLayerManager.deleteGroupChatMessageQueueItemByUUID(uuid, context);
    }

    public void deleteGroupChat(String groupChatId, Context context){
        this.groupChatModelLayerManager.deleteGroupChat(context, groupChatId);
    }

    public ArrayList<GroupChat> getAllGroupChats(Context context) {
        return this.groupChatModelLayerManager.getAllGroupChats(context);
    }

    public String getContactProfilePicUriById(Context context, long contactId) {
        return this.contactModelLayerManager.getContactProfilePicUriById(context, contactId);
    }

    public void updateMessageHasBeenReadByMessageId(int messageId, Context context){
        this.groupChatModelLayerManager.updateMessageHasBeenReadByMessageId( messageId, context);
    }

    public void createGroupChat(GroupChat groupChat, Context context) {
        this.groupChatModelLayerManager.createGroupChat(groupChat, context);
    }

    public void createGroupChatMember(String groupChatId, long groupChatMemberId, Context context) {
        this.groupChatModelLayerManager.createGroupChatMember(groupChatId, groupChatMemberId, context);
    }

    public ArrayList<Contact> getAllContacts(Context context) {
        return this.contactModelLayerManager.getAllContacts(context);
    }

    public long getUserId(Context context) {
        return this.userModelLayerManager.getUserId(context);
    }

    public String getContactNameById(Context context, long contactId) {
        return this.contactModelLayerManager.getContactNameById(context, contactId);
    }

    public void deleteGroupChatMessage(int messageId, Context context){
        this.groupChatModelLayerManager.deleteGroupChatMessage(messageId, context);
    }

    public void updateMessageUnsentByUUID(String uuid, Context context){
        this.groupChatModelLayerManager.updateMessageUnsentByUUID(uuid, context);
    }

    public GroupChat getGroupChatById(Context context, String id) {
        return this.groupChatModelLayerManager.getGroupChatById(context, id);
    }

    public ArrayList<GroupChatMessage> getAllMessagesForGroupChatWithId(Context context, String groupChatId) {
        return this.groupChatModelLayerManager.getAllMessagesForGroupChatWithId(context, groupChatId);
    }

    public void insertGroupChatMessage(GroupChatMessage groupChatMessage, Context context) {
        this.groupChatModelLayerManager.insertGroupChatMessage(groupChatMessage, context);
    }

    public String getGroupProfilePicUriById(Context context, String groupId) {
        return this.groupChatModelLayerManager.getGroupProfilePicUriById(context, groupId);
    }

    public String getGroupChatNameById(Context context, String groupChatId) {
        return this.groupChatModelLayerManager.getGroupChatNameById(context, groupChatId);
    }

    public int getUnreadMessageCountByGroupChatId(Context context, String groupChatId) {
        return this.groupChatModelLayerManager.getUnreadMessageCountByGroupChatId(context, groupChatId);
    }

    public void insertGroupChatMessageQueueItem(String messageUUID, Context context) {
        this.groupChatModelLayerManager.insertGroupChatMessageQueueItem(messageUUID, context);
    }

    public void insertReceivedOnlineGroupMessage(String uuid, Context context) {
        this.groupChatModelLayerManager.insertReceivedOnlineGroupMessage(uuid, context);
    }

    public void deleteReceivedOnlineGroupMessageByUUID(String uuid, Context context){
        this.groupChatModelLayerManager.deleteReceivedOnlineGroupMessageByUUID(uuid, context);
    }

    public ReceivedOnlineGroupMessage getReceivedOnlineGroupMessage(Context context) {
        return this.groupChatModelLayerManager.getReceivedOnlineGroupMessage(context);
    }

    public OneTimeGroupKeyPair getGroupOneTimeKeyPair(Context context, String groupChatId, long userId) {
        return this.groupChatModelLayerManager.getGroupOneTimeKeyPair(context, groupChatId, userId);
    }

    public byte[] getGroupOneTimePublicPreKey(Context context, String groupChatId) {
        return this.groupChatModelLayerManager.getGroupOneTimePublicPreKey(context, groupChatId);
    }

    public byte[] getGroupOneTimePrivatePreKeyByUUID(Context context, String uuid) {
        return this.groupChatModelLayerManager.getGroupOneTimePrivatePreKeyByUUID(context, uuid);
    }

    public byte[] getGroupOneTimePublicPreKeyByUUID(Context context, String uuid) {
        return this.groupChatModelLayerManager.getGroupOneTimePublicPreKeyByUUID(context, uuid);
    }

    public void insertGroupOneTimeKeyPair(String uuid, String groupChatId, byte[] groupOneTimePrivatePreKey,
                                          byte[] groupOneTimePublicPreKey, Context context) {
        this.groupChatModelLayerManager.insertGroupOneTimeKeyPair(uuid, groupChatId, groupOneTimePrivatePreKey,
                groupOneTimePublicPreKey, context);
    }

    public void deleteOneTimeGroupKeyPairByUUID(String uuid, Context context){
        this.groupChatModelLayerManager.deleteOneTimeGroupKeyPairByUUID(uuid, context);
    }

    public OneTimeGroupKeyPair getUserOneGroupTimeKeyPairByUUID(Context context, String uuid, long userId, String groupChatId) {
        return this.groupChatModelLayerManager.getUserOneGroupTimeKeyPairByUUID(context, uuid, userId, groupChatId);
    }

    // endregion

    // region Create Group Chat Message

    @NonNull
    public GroupChatMessage createGroupChatImageMessage(long senderId, String groupChatId, Uri imageUrl, String uuid) {
        GroupChatMessage groupChatMessage = new GroupChatMessage();
        groupChatMessage.setGroupChatId(groupChatId);
        groupChatMessage.setSenderId(senderId);
        groupChatMessage.setMsgType(ConstantRegistry.IMAGE);
        groupChatMessage.setBinaryMessageFilePath(imageUrl);
        groupChatMessage.setUuid(uuid);
        groupChatMessage.setGroupChatMessageCreated(utilModelLayerManager.getDateTime());
        return groupChatMessage;
    }

    @NonNull
    public GroupChatMessage createGroupChatImageMessage(long senderId, String groupChatId, Uri imageUrl, String utc, String uuid) {
        GroupChatMessage groupChatMessage = new GroupChatMessage();
        groupChatMessage.setGroupChatId(groupChatId);
        groupChatMessage.setSenderId(senderId);
        groupChatMessage.setMsgType(ConstantRegistry.IMAGE);
        groupChatMessage.setBinaryMessageFilePath(imageUrl);
        groupChatMessage.setUuid(uuid);
        groupChatMessage.setGroupChatMessageCreated(utilModelLayerManager.convertFromUtcToLocal(utc));
        return groupChatMessage;
    }

    @NonNull
    public GroupChatMessage createGroupChatTextMessage(long senderId, String groupChatId, String message, String utc, String uuid) {
        GroupChatMessage groupChatMessage = new GroupChatMessage();
        groupChatMessage.setGroupChatId(groupChatId);
        groupChatMessage.setSenderId(senderId);
        groupChatMessage.setMessageText(message);
        groupChatMessage.setMsgType(ConstantRegistry.TEXT);
        groupChatMessage.setUuid(uuid);
        groupChatMessage.setGroupChatMessageCreated(utilModelLayerManager.convertFromUtcToLocal(utc));
        return groupChatMessage;
    }

    @NonNull
    public GroupChatMessage createGroupChatTextMessage(long senderId, String groupChatId, String message, String uuid) {
        GroupChatMessage groupChatMessage = new GroupChatMessage();
        groupChatMessage.setGroupChatId(groupChatId);
        groupChatMessage.setSenderId(senderId);
        groupChatMessage.setMessageText(message);
        groupChatMessage.setMsgType(ConstantRegistry.TEXT);
        groupChatMessage.setUuid(uuid);
        groupChatMessage.setGroupChatMessageCreated(utilModelLayerManager.getDateTime());
        return groupChatMessage;
    }

    // endregion

    // region Initialize Unsend Message Registry

    public Observable<HashMap<String,Integer>> initializeUnsendMessageRegistry(ArrayList<GroupChatMessage> messages){
        return Observable.create(new ObservableOnSubscribe<HashMap<String,Integer>>() {
            @Override
            public void subscribe(ObservableEmitter<HashMap<String,Integer>> emitter) throws Exception {
                try {
                    HashMap<String,Integer> unsendMessageRegistry = new HashMap<>();
                    int position = 0;
                    for(GroupChatMessage message : messages){
                        unsendMessageRegistry.put(message.getUuid(), position);
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

    // region ChatsterGroupE2EHelper

    public List<Curve25519KeyPair> generateOneTimeGroupKeys(int amount){
        return this.chatsterGroupE2EHelper.generateOneTimeGroupKeys(amount);
    }

    public List<String> generateUUIDS(int amount){
        return this.chatsterGroupE2EHelper.generateUUIDS(amount);
    }

    public String jsonifyOneTimeGroupKeys(List<Curve25519KeyPair> keyPairs, List<String> uuids, long userId, String groupChatId) {
        return this.chatsterGroupE2EHelper.jsonifyOneTimeGroupKeys(keyPairs, uuids, userId, groupChatId);
    }

    public byte[] generateSignature(byte[] senderPrivateKey, byte[] encryptedMessage){
        return this.chatsterGroupE2EHelper.generateSignature(senderPrivateKey, encryptedMessage);
    }

    public boolean verifySignature(byte[] senderPublicKey, byte[] encryptedMessage, byte[] signature){
        return this.chatsterGroupE2EHelper.verifySignature(senderPublicKey, encryptedMessage, signature);
    }

    public byte[] generateEncryptSharedSecret(byte[] receiverPublicKey, byte[] senderPrivateKey){
        return this.chatsterGroupE2EHelper.generateEncryptSharedSecret(receiverPublicKey, senderPrivateKey);
    }

    public byte[] generateDecryptSharedSecret(byte[] senderPublicKey, byte[] receiverPrivateKey){
        return this.chatsterGroupE2EHelper.generateDecryptSharedSecret(senderPublicKey, receiverPrivateKey);
    }

    public byte[] encrypt(byte[] message, byte[] sharedSecret){
        return this.chatsterGroupE2EHelper.encrypt(message, sharedSecret);
    }

    public byte[] decrypt(byte[] cipherMessage, byte[] sharedSecret){
        return this.chatsterGroupE2EHelper.decrypt(cipherMessage, sharedSecret);
    }

    public byte[] appendHMACSignatureAndPKTo(byte[] encryptedMessage, byte[] hmac, byte[] signature, byte[] userPublicKey){
        return this.chatsterGroupE2EHelper.appendHMACSignatureAndPKTo(encryptedMessage, hmac, signature, userPublicKey);
    }

    public byte[] getMessageFrom(byte[] messageWithHMACSignatureAndPK){
        return this.chatsterGroupE2EHelper.getMessageFrom(messageWithHMACSignatureAndPK);
    }

    public byte[] getHMACFrom(byte[] messageWithHMACSignatureAndPK){
        return this.chatsterGroupE2EHelper.getHMACFrom(messageWithHMACSignatureAndPK);
    }

    public byte[] getSignatureFrom(byte[] messageWithHMACSignatureAndPK){
        return this.chatsterGroupE2EHelper.getSignatureFrom(messageWithHMACSignatureAndPK);
    }

    public byte[] getPublicKeyFrom(byte[] messageWithHMACSignatureAndPK){
        return this.chatsterGroupE2EHelper.getPublicKeyFrom(messageWithHMACSignatureAndPK);
    }

    public String messageWithSignatureHMACAndPKToString(byte[] messageWithSignature){
        return this.chatsterGroupE2EHelper.messageWithSignatureHMACAndPKToString(messageWithSignature);
    }

    public byte[] messageWithSignatureHMACAndPKToByteArray(String messageWithSignatureHMACAndPK){
        return this.chatsterGroupE2EHelper.messageWithSignatureHMACAndPKToByteArray(messageWithSignatureHMACAndPK);
    }

    public String bytesToHex(byte[] bytes) {
        return this.chatsterGroupE2EHelper.bytesToHex(bytes);
    }

    public byte[] messageToHMAC(byte[] message, byte[] sharedSecret){
        return this.chatsterGroupE2EHelper.messageToHMAC(message, sharedSecret);
    }

    public String jsonifiedGroupOneTimePublicKeys(Context context, int amount, String groupChatId, long userId){
        // 1. Generate Curves
        List<Curve25519KeyPair> groupOneTimeKeys = generateOneTimeGroupKeys(amount);

        // 2. Generate UUIDS
        List<String> uuids = generateUUIDS(amount);

        // 3. Insert the keys locally
        this.groupChatModelLayerManager.
                insertOneTimeKeyPairs(context, groupChatId, groupOneTimeKeys, uuids);

        return jsonifyOneTimeGroupKeys(groupOneTimeKeys, uuids, userId, groupChatId);
    }

    // endregion
}
