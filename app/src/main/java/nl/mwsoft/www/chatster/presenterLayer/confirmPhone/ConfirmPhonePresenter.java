package nl.mwsoft.www.chatster.presenterLayer.confirmPhone;



import android.content.Context;
import android.util.Log;


import org.whispersystems.curve25519.Curve25519KeyPair;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.e2e.ChatsterE2EHelper;
import nl.mwsoft.www.chatster.modelLayer.model.ConfirmPhoneResponse;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.contact.ContactModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.user.UserModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.util.UtilModelLayerManager;

public class ConfirmPhonePresenter {

    private UserModelLayerManager userModelLayerManager;
    private ContactModelLayerManager contactModelLayerManager;
    private UtilModelLayerManager utilModelLayerManager;
    private ChatsterE2EHelper chatsterE2EHelper;

    public ConfirmPhonePresenter() {
    }

    public ConfirmPhonePresenter(UserModelLayerManager userModelLayerManager,
                                 ContactModelLayerManager contactModelLayerManager,
                                 UtilModelLayerManager utilModelLayerManager,
                                 ChatsterE2EHelper chatsterE2EHelper) {
        this.userModelLayerManager = userModelLayerManager;
        this.contactModelLayerManager = contactModelLayerManager;
        this.utilModelLayerManager = utilModelLayerManager;
        this.chatsterE2EHelper = chatsterE2EHelper;
    }

    // region DB

    public void updateUserId(long phoneToVerify, Context context){
        this.userModelLayerManager.updateUserId(phoneToVerify, context);
    }

    public void updateUser(ConfirmPhoneResponse result, Context context) {
        this.userModelLayerManager.updateUser(result, context);
    }

    public void updateContacts(ArrayList<Long> chatsterContacts, Context context){
        this.contactModelLayerManager.updateContacts(chatsterContacts, context);
    }

    public ArrayList<Long> getAllContactIds(Context context){
        return this.contactModelLayerManager.getAllContactIds(context);
    }

    // endregion

    // region Confirm Phone Number

    public Observable<ConfirmPhoneResponse> confirmPhoneNumber(String phoneToVerify, String messagingToken,ArrayList<Long> contacts, Context context){
        return this.userModelLayerManager.confirmPhoneNumber(phoneToVerify, messagingToken, contacts, context);
    }

    // endregion

    // region Insert Contacts From Phone Contacts List

    public Observable<String> insertContactsFromPhone(Context context){
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                try {
                    String result = contactModelLayerManager.insertContacts(context);
                    emitter.onNext(result);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    // endregion

    // region process provided phone number

    public String removeFirstChar(String s){
        return s.substring(1);
    }

    public String processProvidedPhoneNumber(String phoneNo){
        if(phoneNo.contains(ConstantRegistry.CHATSTER_OPEN_ROUND_BRACKETS)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_OPEN_ROUND_BRACKETS, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }

        if(phoneNo.contains(ConstantRegistry.CHATSTER_CLOSE_ROUND_BRACKETS)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_CLOSE_ROUND_BRACKETS, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }

        if(phoneNo.contains(ConstantRegistry.CHATSTER_FORWARD_SLASH)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_FORWARD_SLASH, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }

        if(phoneNo.contains(ConstantRegistry.CHATSTER_LETTER_N)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_LETTER_N, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }

        if(phoneNo.contains(ConstantRegistry.CHATSTER_COMMA)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_COMMA, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }

        if(phoneNo.contains(ConstantRegistry.CHATSTER_POINT)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_POINT, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }

        if(phoneNo.contains(ConstantRegistry.CHATSTER_STAR)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_STAR, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }

        if(phoneNo.contains(ConstantRegistry.CHATSTER_SEMICOLON)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_SEMICOLON, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }

        if(phoneNo.contains(ConstantRegistry.CHATSTER_HASH_TAG)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_HASH_TAG, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }

        if(phoneNo.contains(ConstantRegistry.CHATSTER_MINUS)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_MINUS, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }

        if(phoneNo.contains(ConstantRegistry.CHATSTER_PLUS)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_PLUS, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }

        if(phoneNo.contains(ConstantRegistry.CHATSTER_SPACE_STRING)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_SPACE_STRING, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }

        if(phoneNo.length() > 0){
            if(phoneNo.startsWith(ConstantRegistry.CHATSTER_ZERO)){
                phoneNo = removeFirstChar(phoneNo);
            }
        }

        return phoneNo;
    }

    // endregion

    // region Util

    public boolean hasInternetConnection(){
        return utilModelLayerManager.hasInternetConnection();
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

    public byte[] encrypt(byte[] message, byte[] sharedSecret){
        return chatsterE2EHelper.encrypt(message, sharedSecret);
    }

    public byte[] decrypt(byte[] cipherMessage, byte[] sharedSecret){
        return chatsterE2EHelper.decrypt(cipherMessage, sharedSecret);
    }

    public byte[] appendHMACSignatureAndPKTo(byte[] encryptedMessage, byte[] hmac, byte[] signature, byte[] userPublicKey){
        return chatsterE2EHelper.appendHMACSignatureAndPKTo(encryptedMessage, hmac, signature, userPublicKey);
    }

    public byte[] getMessageFrom(byte[] messageWithSignature){
        return chatsterE2EHelper.getMessageFrom(messageWithSignature);
    }

    public byte[] getHMACFrom(byte[] messageWithHMACAndSignature){
        return chatsterE2EHelper.getHMACFrom(messageWithHMACAndSignature);
    }

    public byte[] getSignatureFrom(byte[] messageWithSignature){
        return chatsterE2EHelper.getSignatureFrom(messageWithSignature);
    }

    public String messageWithSignatureToString(byte[] messageWithSignature){
        return chatsterE2EHelper.messageWithSignatureToString(messageWithSignature);
    }

    public byte[] messageWithSignatureToByteArray(String messageWithSignature){
        return chatsterE2EHelper.messageWithSignatureHMACAndPKToByteArray(messageWithSignature);
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

    // region Upload User One Time Keys

    public Observable<String> uploadReRegisterPublicKeys(long userId, String  oneTimePreKeyPairPbks){
        return this.userModelLayerManager.uploadReRegisterPublicKeys(userId, oneTimePreKeyPairPbks);
    }

    // endregion

}
