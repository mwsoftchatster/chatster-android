package nl.mwsoft.www.chatster.presenterLayer.main;


import android.content.Context;

import org.whispersystems.curve25519.Curve25519KeyPair;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import nl.mwsoft.www.chatster.modelLayer.helper.e2e.ChatsterE2EHelper;
import nl.mwsoft.www.chatster.modelLayer.helper.groupE2E.ChatsterGroupE2EHelper;
import nl.mwsoft.www.chatster.modelLayer.helper.util.jobService.ChatsterJobServiceUtil;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChat;
import nl.mwsoft.www.chatster.modelLayer.model.OneTimeGroupKeyPair;
import nl.mwsoft.www.chatster.modelLayer.model.OneTimeGroupPublicKey;
import nl.mwsoft.www.chatster.modelLayer.model.OneTimeKeyPair;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.groupChat.GroupChatModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.notification.NotificationModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.user.UserModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.util.UtilModelLayerManager;

public class MainPresenter {

    private ChatsterJobServiceUtil chatsterServiceUtil;
    private UserModelLayerManager userModelLayerManager;
    private NotificationModelLayerManager notificationModelLayerManager;
    private GroupChatModelLayerManager groupChatModelLayerManager;
    private UtilModelLayerManager utilModelLayerManager;
    private ChatsterE2EHelper chatsterE2EHelper;
    private ChatsterGroupE2EHelper chatsterGroupE2EHelper;

    public MainPresenter() {
    }

    public MainPresenter(ChatsterJobServiceUtil chatsterServiceUtil,
                         UserModelLayerManager userModelLayerManager,
                         NotificationModelLayerManager notificationModelLayerManager,
                         GroupChatModelLayerManager groupChatModelLayerManager,
                         UtilModelLayerManager utilModelLayerManager,
                         ChatsterE2EHelper chatsterE2EHelper,
                         ChatsterGroupE2EHelper chatsterGroupE2EHelper) {
        this.chatsterServiceUtil = chatsterServiceUtil;
        this.userModelLayerManager = userModelLayerManager;
        this.notificationModelLayerManager = notificationModelLayerManager;
        this.groupChatModelLayerManager = groupChatModelLayerManager;
        this.utilModelLayerManager = utilModelLayerManager;
        this.chatsterE2EHelper = chatsterE2EHelper;
        this.chatsterGroupE2EHelper = chatsterGroupE2EHelper;
    }

    // region DB

    public String getUserName(Context context) {
        return this.userModelLayerManager.getUserName(context);
    }

    public long getUserId(Context context) {
        return this.userModelLayerManager.getUserId(context);
    }

    public void deleteGroupChatInvitationNotification(String groupChatId, Context context){
        this.notificationModelLayerManager.deleteGroupChatInvitationNotification(groupChatId, context);
    }

    public void insertGroupChat(GroupChat groupChat, Context context) {
        this.groupChatModelLayerManager.insertGroupChat(groupChat, context);
    }

    public void insertGroupChatMember(String groupChatId, long groupChatMemberId, Context context) {
        this.groupChatModelLayerManager.insertGroupChatMember(groupChatId, groupChatMemberId, context);
    }

    public void insertUserOneTimeKeyPair(String uuid, long userId, byte[] userOneTimePrivatePreKey,
                                         byte[] userOneTimePublicPreKey, Context context){
        this.userModelLayerManager.insertUserOneTimeKeyPair(uuid, userId, userOneTimePrivatePreKey,
                userOneTimePublicPreKey, context);
    }

    public OneTimeKeyPair getUserOneTimeKeyPair(Context context, long userId) {
        return this.userModelLayerManager.getUserOneTimeKeyPair(context, userId);
    }

    public OneTimeKeyPair getUserOneTimeKeyPairByUUID(Context context, String uuid, long userId) {
        return this.userModelLayerManager.getUserOneTimeKeyPairByUUID(context, uuid, userId);
    }

    public byte[] getUserOneTimePublicPreKey(Context context, long userId){
        return this.userModelLayerManager.getUserOneTimePublicPreKey(context, userId);
    }

    public byte[] getUserOneTimePrivatePreKeyByUUID(Context context, String uuid){
        return this.userModelLayerManager.getUserOneTimePrivatePreKeyByUUID(context, uuid);
    }

    public byte[] getUserOneTimePublicPreKeyByUUID(Context context, String uuid){
        return this.userModelLayerManager.getUserOneTimePublicPreKeyByUUID(context, uuid);
    }

    public void deleteOneTimeKeyPairByUUID(String uuid, Context context){
        this.userModelLayerManager.deleteOneTimeKeyPairByUUID(uuid, context);
    }

    public void insertOneTimeKeyPairs(Context context, long userId, List<Curve25519KeyPair> keyPairs, List<String> uuids) {
        int counter = 0;

        for (Curve25519KeyPair keyPair: keyPairs) {
            insertUserOneTimeKeyPair(
                    uuids.get(counter),
                    userId,
                    keyPair.getPrivateKey(),
                    keyPair.getPublicKey(),
                    context
            );

            counter++;
        }
    }

    private List<Curve25519KeyPair> generateOneTimeKeys(int amount){
        return chatsterE2EHelper.generateOneTimeKeys(amount);
    }

    private List<String> generateUUIDS(int amount){
        return chatsterE2EHelper.generateUUIDS(amount);
    }

    public String jsonifyOneTimeKeys(List<Curve25519KeyPair> keyPairs, List<String> uuids, long userId) {
        return chatsterE2EHelper.jsonifyOneTimeKeys(keyPairs, uuids, userId);
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

    //  endregion

    // region Update User Token

    public Observable<String> updateUserToken(long userId, String  messagingToken){
        return this.userModelLayerManager.updateUserToken(userId, messagingToken);
    }

    // endregion

    // region Upload User One Time Keys

    public Observable<String> uploadPublicKeys(long userId, String  oneTimePreKeyPairPbks){
        return this.userModelLayerManager.uploadPublicKeys(userId, oneTimePreKeyPairPbks);
    }

    // endregion

    // region Check If New Public Keys Needed

    public Observable<String> checkPublicKeys(final long userId){
        return this.userModelLayerManager.checkPublicKeys(userId);
    }

    // endregion

    // region Upload Group One Time Keys

    public Observable<String> uploadGroupPublicKeys(String oneTimePreKeyPairPbks){
        return this.userModelLayerManager.uploadGroupPublicKeys(oneTimePreKeyPairPbks);
    }

    // endregion

    // region Get Group One Time Keys

    public Observable<ArrayList<OneTimeGroupPublicKey>> getGroupOneTimeKeys(String groupChatId, long userId){
        return this.groupChatModelLayerManager.getGroupOneTimeKeys(groupChatId, userId);
    }

    public OneTimeGroupKeyPair getGroupOneTimeKeyPair(Context context, String groupChatId, long userId) {
        return this.groupChatModelLayerManager.getGroupOneTimeKeyPair(context, groupChatId, userId);
    }

    // endregion

    public Observable<String> deleteGroupChatInvitation(String groupChatId, long userId){
        return this.groupChatModelLayerManager.deleteGroupChatInvitation(groupChatId, userId);
    }

    // region Services

    public void startServices(Context context){
        this.chatsterServiceUtil.startServices(context);
    }

    // endregion

    // region Util

    public String createUUID(){
       return utilModelLayerManager.createUUID();
    }

    public boolean hasInternetConnection(){
        return utilModelLayerManager.hasInternetConnection();
    }

    // endregion

    // region ChatsterGroupE2EHelper

    public List<Curve25519KeyPair> generateOneTimeGroupKeys(int amount){
        return this.chatsterGroupE2EHelper.generateOneTimeGroupKeys(amount);
    }

    public String jsonifyOneTimeGroupKeys(List<Curve25519KeyPair> keyPairs, List<String> uuids, long userId, String groupChatId) {
        return this.chatsterGroupE2EHelper.jsonifyOneTimeGroupKeys(keyPairs, uuids, userId, groupChatId);
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

    // endregion
}
