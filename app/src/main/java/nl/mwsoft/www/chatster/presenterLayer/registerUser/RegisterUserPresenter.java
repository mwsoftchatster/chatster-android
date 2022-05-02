package nl.mwsoft.www.chatster.presenterLayer.registerUser;


import android.content.Context;

import org.whispersystems.curve25519.Curve25519KeyPair;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import nl.mwsoft.www.chatster.modelLayer.helper.e2e.ChatsterE2EHelper;
import nl.mwsoft.www.chatster.modelLayer.model.OneTimePublicKey;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.contact.ContactModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.user.UserModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.util.UtilModelLayerManager;


public class RegisterUserPresenter {

    private UserModelLayerManager userModelLayerManager;
    private ContactModelLayerManager contactModelLayerManager;
    private UtilModelLayerManager utilModelLayerManager;
    private ChatsterE2EHelper chatsterE2EHelper;

    public RegisterUserPresenter() {
    }

    public RegisterUserPresenter(UserModelLayerManager userModelLayerManager,
                                 ContactModelLayerManager contactModelLayerManager,
                                 UtilModelLayerManager utilModelLayerManager,
                                 ChatsterE2EHelper chatsterE2EHelper) {
        this.userModelLayerManager = userModelLayerManager;
        this.contactModelLayerManager = contactModelLayerManager;
        this.utilModelLayerManager = utilModelLayerManager;
        this.chatsterE2EHelper = chatsterE2EHelper;
    }

    public long getUserId(Context context) {
        return this.userModelLayerManager.getUserId(context);
    }

    public ArrayList<Long> getAllContactIds(Context context){
        return this.contactModelLayerManager.getAllContactIds(context);
    }


    public Observable<String> registerUser(Context context, long userId, String userName,
                                           String  userStatusMessage,String  messagingToken, ArrayList<Long> myContactIds,
                                           String oneTimePreKeyPairPbks){
        return this.userModelLayerManager.registerUser(context, userId, userName, userStatusMessage, messagingToken, myContactIds,
                oneTimePreKeyPairPbks);
    }

    // region Upload User One Time Keys

    public Observable<String> uploadPublicKeys(long userId, String  oneTimePreKeyPairPbks){
        return this.userModelLayerManager.uploadPublicKeys(userId, oneTimePreKeyPairPbks);
    }

    // endregion

    // region Get One Time Key

    public Observable<OneTimePublicKey> getPublicKey(long userId){
        return this.userModelLayerManager.getPublicKey(userId);
    }

    // endregion

    // region Get One Time Key By UUID

    public Observable<OneTimePublicKey> getPublicKeyByUUID(long userId, String uuid){
        return this.userModelLayerManager.getPublicKeyByUUID(userId, uuid);
    }

    // endregion

    public boolean hasInternetConnection(){
        return utilModelLayerManager.hasInternetConnection();
    }

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
