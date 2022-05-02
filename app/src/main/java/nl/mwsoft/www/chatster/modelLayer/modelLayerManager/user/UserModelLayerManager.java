package nl.mwsoft.www.chatster.modelLayer.modelLayerManager.user;


import android.content.Context;

import org.whispersystems.curve25519.Curve25519KeyPair;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.modelLayer.database.user.UserDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.model.ConfirmPhoneResponse;
import nl.mwsoft.www.chatster.modelLayer.model.OneTimeKeyPair;
import nl.mwsoft.www.chatster.modelLayer.model.OneTimePublicKey;
import nl.mwsoft.www.chatster.modelLayer.network.NetworkLayer;

public class UserModelLayerManager {

    private UserDatabaseLayer userDatabaseLayer;
    private NetworkLayer networkLayer;

    public UserModelLayerManager() {
        this.userDatabaseLayer = DependencyRegistry.shared.createUserDatabaseLayer();
        this.networkLayer = DependencyRegistry.shared.createNetworkLayer();
    }

    // region User DB

    public long getUserId(Context context) {
        return this.userDatabaseLayer.getUserId(context);
    }

    public String getUserName(Context context) {
        return this.userDatabaseLayer.getUserName(context);
    }

    public String getUserStatusMessage(Context context) {
        return this.userDatabaseLayer.getUserStatusMessage(context);
    }

    public String getUserProfilePicUri(Context context) {
        return this.userDatabaseLayer.getUserProfilePicUri(context);
    }

    public String getUserProfilePicUrl(Context context) {
        return this.userDatabaseLayer.getUserProfilePicUrl(context);
    }

    public void updateUserStatusMessage(String newStatusMessage, Context context){
        this.userDatabaseLayer.updateUserStatusMessage(newStatusMessage, context);
    }

    public void updateUser(ConfirmPhoneResponse result, Context context) {
        this.userDatabaseLayer.updateUser(result, context);
    }

    public void updateUserId(long phoneToVerify, Context context){
        this.userDatabaseLayer.updateUserId(phoneToVerify, context);
    }

    public void updateUserProfilePic(String uri, Context context){
        this.userDatabaseLayer.updateUserProfilePic(uri, context);
    }

    public void insertUserOneTimeKeyPair(String uuid, long userId, byte[] userOneTimePrivatePreKey,
                                     byte[] userOneTimePublicPreKey, Context context){
        this.userDatabaseLayer.insertUserOneTimeKeyPair(uuid, userId, userOneTimePrivatePreKey,
                                                        userOneTimePublicPreKey, context);
    }

    public OneTimeKeyPair getUserOneTimeKeyPair(Context context, long userId) {
        return this.userDatabaseLayer.getUserOneTimeKeyPair(context, userId);
    }

    public OneTimeKeyPair getUserOneTimeKeyPairByUUID(Context context, String uuid, long userId) {
        return this.userDatabaseLayer.getUserOneTimeKeyPairByUUID(context, uuid, userId);
    }

    public byte[] getUserOneTimePublicPreKey(Context context, long userId){
        return this.userDatabaseLayer.getUserOneTimePublicPreKey(context, userId);
    }

    public byte[] getUserOneTimePrivatePreKeyByUUID(Context context, String uuid){
        return this.userDatabaseLayer.getUserOneTimePrivatePreKeyByUUID(context, uuid);
    }

    public byte[] getUserOneTimePublicPreKeyByUUID(Context context, String uuid){
        return this.userDatabaseLayer.getUserOneTimePublicPreKeyByUUID(context, uuid);
    }

    public void deleteOneTimeKeyPairByUUID(String uuid, Context context){
        this.userDatabaseLayer.deleteOneTimeKeyPairByUUID(uuid, context);
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

    // endregion

    // region User Network

    // region Confirm Phone Number

    public Observable<ConfirmPhoneResponse> confirmPhoneNumber(String phoneToVerify, String messagingToken, ArrayList<Long> contacts, Context context){
        return this.networkLayer.confirmPhoneNumber(phoneToVerify, messagingToken, contacts, context);
    }

    // endregion

    // region Register User

    public Observable<String> registerUser(Context context, long userId, String userName,
                                           String  userStatusMessage,String  messagingToken, ArrayList<Long> myContactIds,
                                           String oneTimePreKeyPairPbks){
        return this.networkLayer.registerUser(context, userId, userName, userStatusMessage,messagingToken, myContactIds,
                oneTimePreKeyPairPbks);
    }

    // endregion

    // region Update User Token

    public Observable<String> updateUserToken(long userId, String  messagingToken){
        return this.networkLayer.updateUserToken(userId, messagingToken);
    }

    // endregion

    // region Upload User One Time Keys

    public Observable<String> uploadPublicKeys(long userId, String  oneTimePreKeyPairPbks){
        return this.networkLayer.uploadPublicKeys(userId, oneTimePreKeyPairPbks);
    }

    public Observable<String> uploadReRegisterPublicKeys(long userId, String  oneTimePreKeyPairPbks){
        return this.networkLayer.uploadReRegisterPublicKeys(userId, oneTimePreKeyPairPbks);
    }

    // region Upload New Batch Of Keys

    public Observable<String> uploadNewBatchOfKeys(long userId, String oneTimePreKeyPairPbks){
        return this.networkLayer.uploadPublicKeys(userId, oneTimePreKeyPairPbks);
    }

    public Observable<String> checkKeys(long userId){
        return this.networkLayer.checkPublicKeys(userId);
    }

    // endregion

    // endregion

    // region Check If New Public Keys Needed

    public Observable<String> checkPublicKeys(final long userId){
        return this.networkLayer.checkPublicKeys(userId);
    }

    // endregion

    // region Get One Time Key

    public Observable<OneTimePublicKey> getPublicKey(long contactId){
        return this.networkLayer.getPublicKey(contactId);
    }

    // endregion

    // region Get One Time Key By UUID

    public Observable<OneTimePublicKey> getPublicKeyByUUID(long contactId, String uuid){
        return this.networkLayer.getPublicKeyByUUID(contactId, uuid);
    }

    // endregion

    public Observable<String> uploadGroupPublicKeys(String oneTimePreKeyPairPbks){
        return this.networkLayer.uploadGroupPublicKeys(oneTimePreKeyPairPbks);
    }

    // endregion
}
