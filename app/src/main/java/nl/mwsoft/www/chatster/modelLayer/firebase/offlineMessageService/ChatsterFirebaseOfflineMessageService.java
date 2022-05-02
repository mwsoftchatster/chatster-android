package nl.mwsoft.www.chatster.modelLayer.firebase.offlineMessageService;


import android.content.Context;
import androidx.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.whispersystems.curve25519.Curve25519KeyPair;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.database.chat.ChatDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.database.groupChat.GroupChatDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.database.notification.NotificationDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.database.user.UserDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.helper.e2e.ChatsterE2EHelper;
import nl.mwsoft.www.chatster.modelLayer.helper.groupE2E.ChatsterGroupE2EHelper;
import nl.mwsoft.www.chatster.modelLayer.helper.util.dateTime.ChatsterDateTimeUtil;
import nl.mwsoft.www.chatster.modelLayer.helper.util.notification.ChatsterNotificationUtil;
import nl.mwsoft.www.chatster.modelLayer.helper.util.internet.InternetConnectionUtil;
import nl.mwsoft.www.chatster.modelLayer.model.DeleteRetrievedMessagesResponse;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChatInvitation;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChatOfflineMessage;
import nl.mwsoft.www.chatster.modelLayer.model.HistoryItem;
import nl.mwsoft.www.chatster.modelLayer.model.OfflineContactResponse;
import nl.mwsoft.www.chatster.modelLayer.model.OfflineMessage;
import nl.mwsoft.www.chatster.modelLayer.model.OfflineMessageResponse;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.image.ImageProcessingManager;
import nl.mwsoft.www.chatster.modelLayer.network.NetworkLayer;

public class ChatsterFirebaseOfflineMessageService extends FirebaseMessagingService{

    private CompositeDisposable disposable;
    private Disposable subscribeGetOfflineMessages;
    private Disposable subscribeDeleteRetrievedMessages;
    private Disposable subscribeGetGroupChatInvitations;
    private Disposable subscribeHistory;
    private NetworkLayer networkLayer;
    private ImageProcessingManager imageProcessingManager;
    private UserDatabaseLayer userDatabaseLayer;
    private GroupChatDatabaseLayer groupChatDatabaseLayer;
    private ChatDatabaseLayer chatDatabaseLayer;
    private NotificationDatabaseLayer notificationDatabaseLayer;
    private InternetConnectionUtil internetConnectionUtil;
    private ChatsterDateTimeUtil chatsterDateTimeUtil;
    private ChatsterE2EHelper chatsterE2EHelper;
    private ChatsterGroupE2EHelper chatsterGroupE2EHelper;

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        disposable = new CompositeDisposable();
        internetConnectionUtil = new InternetConnectionUtil();
        chatsterDateTimeUtil = new ChatsterDateTimeUtil();
        Fabric.with(this, new Crashlytics());
        DependencyRegistry.shared.inject(this);
        processHighPriorityMessage(remoteMessage);
    }

    // region Process High Priority Messages

    private void processHighPriorityMessage(RemoteMessage remoteMessage) {
        try{
            String notificationData = remoteMessage.getData().toString();
            if(!notificationData.equals(null) && notificationData.length() > 0){
                if(notificationData.equals(ConstantRegistry.OFFLINE_MESSAGE_NOTIFICATION)){
                    getChatOfflineMessages();
                }else if(notificationData.equals(ConstantRegistry.GROUP_OFFLINE_MESSAGE_NOTIFICATION)){
                    getGroupChatOfflineMessages();
                }else if(notificationData.equals(ConstantRegistry.GROUP_INVITATION_NOTIFICATION)){
                    getGroupInvitations();
                }else if(notificationData.equals(ConstantRegistry.CREATOR_POST_NOTIFICATION)){
                    getCreatorHistory(userDatabaseLayer.getUserName(ChatsterFirebaseOfflineMessageService.this),
                            userDatabaseLayer.getUserId(ChatsterFirebaseOfflineMessageService.this));
                }else if(notificationData.equals(ConstantRegistry.CREATOR_FOLLOW_NOTIFICATION)){
                    getCreatorHistory(userDatabaseLayer.getUserName(ChatsterFirebaseOfflineMessageService.this),
                            userDatabaseLayer.getUserId(ChatsterFirebaseOfflineMessageService.this));
                }else if(notificationData.equals(ConstantRegistry.CREATOR_UNFOLLOW_NOTIFICATION)){
                    getCreatorHistory(userDatabaseLayer.getUserName(ChatsterFirebaseOfflineMessageService.this),
                            userDatabaseLayer.getUserId(ChatsterFirebaseOfflineMessageService.this));
                }else if(notificationData.equals(ConstantRegistry.CREATOR_POST_COMMENT_NOTIFICATION)){
                    getCreatorHistory(userDatabaseLayer.getUserName(ChatsterFirebaseOfflineMessageService.this),
                            userDatabaseLayer.getUserId(ChatsterFirebaseOfflineMessageService.this));
                }else if(notificationData.equals(ConstantRegistry.CREATOR_POST_LIKE_NOTIFICATION)){
                    getCreatorHistory(userDatabaseLayer.getUserName(ChatsterFirebaseOfflineMessageService.this),
                            userDatabaseLayer.getUserId(ChatsterFirebaseOfflineMessageService.this));
                }else if(notificationData.equals(ConstantRegistry.CREATOR_POST_UNLIKE_NOTIFICATION)){
                    getCreatorHistory(userDatabaseLayer.getUserName(ChatsterFirebaseOfflineMessageService.this),
                            userDatabaseLayer.getUserId(ChatsterFirebaseOfflineMessageService.this));
                }
            }
        }catch (NullPointerException e){
            throw new RuntimeException("Firebase remote message error ==> " + e.getMessage());
        }
    }

    // endregion

    // region Configure Firebase Messaging Service

    public void configureWith(NetworkLayer networkLayer, ImageProcessingManager imageProcessingManager,
                              UserDatabaseLayer userDatabaseLayer, GroupChatDatabaseLayer groupChatDatabaseLayer,
                              ChatDatabaseLayer chatDatabaseLayer, NotificationDatabaseLayer notificationDatabaseLayer,
                              ChatsterE2EHelper chatsterE2EHelper, ChatsterGroupE2EHelper chatsterGroupE2EHelper){
        this.networkLayer = networkLayer;
        this.imageProcessingManager = imageProcessingManager;
        this.userDatabaseLayer = userDatabaseLayer;
        this.groupChatDatabaseLayer = groupChatDatabaseLayer;
        this.chatDatabaseLayer = chatDatabaseLayer;
        this.notificationDatabaseLayer = notificationDatabaseLayer;
        this.chatsterE2EHelper = chatsterE2EHelper;
        this.chatsterGroupE2EHelper = chatsterGroupE2EHelper;
    }

    // endregion

    // region Offline Messages

    private void getGroupChatOfflineMessages() {

        if(internetConnectionUtil.hasInternetConnection()){
            if(combinedUUIDS() != null && combinedUUIDS().length > 0){
                processRetrievedGroupMessages(combinedUUIDS(),
                        userDatabaseLayer.getUserId(ChatsterFirebaseOfflineMessageService.this));
            }else{
                getGroupOfflineMessages(userDatabaseLayer.getUserId(ChatsterFirebaseOfflineMessageService.this),
                        groupChatDatabaseLayer.getAllGroupChatIds(ChatsterFirebaseOfflineMessageService.this));
            }
        }
    }

    private void getChatOfflineMessages() {
        if(internetConnectionUtil.hasInternetConnection()){
            if(combinedUUIDS() != null && combinedUUIDS().length > 0){
                processRetrievedMessages(combinedUUIDS(),
                        userDatabaseLayer.getUserId(ChatsterFirebaseOfflineMessageService.this));
            }else{
                getOfflineMessages(userDatabaseLayer.getUserId(ChatsterFirebaseOfflineMessageService.this));
            }
        }
    }

    private  <T> T[] joinArrays(T[]... arrays) {
        int length = 0;
        for (T[] array : arrays) {
            length += array.length;
        }

        final T[] result = (T[]) Array.newInstance(arrays[0].getClass().getComponentType(), length);

        int offset = 0;
        for (T[] array : arrays) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }

        return result;
    }

    private String[] combinedUUIDS(){
        String[] offlineUUIDS = notificationDatabaseLayer.
                getRetrievedOfflineMessageUUIDs(ChatsterFirebaseOfflineMessageService.this);
        String[] onlineUUIDS = chatDatabaseLayer.getReceivedOnlineMessageUUIDs(ChatsterFirebaseOfflineMessageService.this);
        String[] onlineGroupUUIDS = groupChatDatabaseLayer.getReceivedOnlineGroupMessageUUIDs(ChatsterFirebaseOfflineMessageService.this);

        return joinArrays(offlineUUIDS, onlineUUIDS, onlineGroupUUIDS);
    }

    private void processRetrievedMessages(String[] uuids, long userId){
        subscribeDeleteRetrievedMessages =
                networkLayer.deleteRetrievedMessages(uuids, userId)
                        .subscribeOn(Schedulers.io())
                        .subscribe(result -> {
                            if (result != null) {
                                boolean isSuccessful = true;
                                for(DeleteRetrievedMessagesResponse response : result){
                                    if(!response.getReturnType().equals(ConstantRegistry.ERROR)){
                                        if(response.getMsgType().equals(ConstantRegistry.CHATSTER_CHAT_MSG)){
                                            handleOnlineChatMessage(response);
                                        }else if(response.getMsgType().equals(ConstantRegistry.CHATSTER_GROUP_CHAT_MSG)){
                                            handleOnlineGroupChatMessage(response,
                                                    ChatsterFirebaseOfflineMessageService.this);
                                        }
                                    }else{
                                        isSuccessful = false;
                                    }
                                }
                                if(isSuccessful){
                                    deleteProcessedUUIDS(uuids);
                                }
                            }
                            getOfflineMessages(userDatabaseLayer.getUserId(ChatsterFirebaseOfflineMessageService.this));
                        });
        disposable.add(subscribeDeleteRetrievedMessages);
    }

    private void processRetrievedGroupMessages(String[] uuids, long userId){
        subscribeDeleteRetrievedMessages =
                networkLayer.deleteRetrievedGroupMessages(uuids, userId)
                        .subscribeOn(Schedulers.io())
                        .subscribe(result -> {
                            if (result != null) {
                                boolean isSuccessful = true;
                                for(DeleteRetrievedMessagesResponse response : result){
                                    if(!response.getReturnType().equals(ConstantRegistry.ERROR)){
                                        handleOnlineGroupChatMessage(response,
                                                ChatsterFirebaseOfflineMessageService.this);
                                    }else{
                                        isSuccessful = false;
                                    }
                                }
                                if(isSuccessful){
                                    deleteProcessedUUIDS(uuids);
                                }
                            }
                            getGroupOfflineMessages(userDatabaseLayer.getUserId(ChatsterFirebaseOfflineMessageService.this),
                                    groupChatDatabaseLayer.getAllGroupChatIds(ChatsterFirebaseOfflineMessageService.this));
                        });
        disposable.add(subscribeDeleteRetrievedMessages);
    }

    public void getCreatorHistory(String creatorName, long userId){
        if(internetConnectionUtil.hasInternetConnection()){
            ArrayList<HistoryItem> historyItems = new ArrayList<>();
            subscribeHistory = networkLayer.getCreatorHistory(creatorName, userId).
                    subscribeOn(Schedulers.io()).
                    subscribe(res -> {
                        for(HistoryItem historyItem: res){
                            historyItem.setCreated(chatsterDateTimeUtil.convertFromUtcToLocal(historyItem.getCreated()));
                            historyItems.add(historyItem);
                        }

                        ChatsterNotificationUtil.sendCreatorNotification(ChatsterFirebaseOfflineMessageService.this,
                                historyItems.get(0));
                    },
                    throwable -> {
                        throwable.printStackTrace();
                    });
            disposable.add(subscribeHistory);
        }

    }

    private void deleteProcessedUUIDS(String[] uuids) {
        for(String uuid : uuids){
            // delete offline uuids
            notificationDatabaseLayer.deleteRetrievedOfflineMessageByUUID(uuid,
                    ChatsterFirebaseOfflineMessageService.this);
            // delete online uuids
            chatDatabaseLayer.deleteReceivedOnlineMessageByUUID(uuid,
                    ChatsterFirebaseOfflineMessageService.this);
            // delete online group uuids
            groupChatDatabaseLayer.deleteReceivedOnlineGroupMessageByUUID(uuid,
                    ChatsterFirebaseOfflineMessageService.this);
        }
    }

    private void handleOnlineChatMessage(DeleteRetrievedMessagesResponse offlineMessage) {
        OfflineMessage message = createOnlineChatMessage(offlineMessage);

        // insert message uuid into local db for verification
        chatDatabaseLayer.insertReceivedOnlineMessage(offlineMessage.getUuid(),
                ChatsterFirebaseOfflineMessageService.this);

        byte[] messageWithSignatureHMACAndPK = chatsterE2EHelper.messageWithSignatureHMACAndPKToByteArray(message.getMessageData());

        byte[] encryptedMessage = chatsterE2EHelper.getMessageFrom(messageWithSignatureHMACAndPK);
        byte[] hmac = chatsterE2EHelper.getHMACFrom(messageWithSignatureHMACAndPK);
        byte[] signature = chatsterE2EHelper.getSignatureFrom(messageWithSignatureHMACAndPK);
        byte[] senderPublicKey = chatsterE2EHelper.getPublicKeyFrom(messageWithSignatureHMACAndPK);

        byte[] receiverPrivateKey = userDatabaseLayer.getUserOneTimePrivatePreKeyByUUID(
                ChatsterFirebaseOfflineMessageService.this, message.getContactPublicKeyUUID());

        byte[] decryptSharedSecret = chatsterE2EHelper.generateDecryptSharedSecret(senderPublicKey,receiverPrivateKey);

        boolean signatureIsCorrect = chatsterE2EHelper.verifySignature(senderPublicKey, encryptedMessage, signature);

        if(signatureIsCorrect){
            byte[] decryptedMessage = chatsterE2EHelper.decrypt(encryptedMessage, decryptSharedSecret);

            String hmacAfterDecryption = chatsterE2EHelper.bytesToHex(
                    chatsterE2EHelper.messageToHMAC(decryptedMessage,decryptSharedSecret)
            );

            if(chatsterE2EHelper.bytesToHex(hmac).equals(hmacAfterDecryption)){
                String msg = new String(decryptedMessage, StandardCharsets.UTF_8);

                message.setMessageData(msg);

                userDatabaseLayer.deleteOneTimeKeyPairByUUID(message.getContactPublicKeyUUID(),
                        ChatsterFirebaseOfflineMessageService.this);

                // insert message in local db
                notificationDatabaseLayer.insertOfflineMessage(message, imageProcessingManager,
                        ChatsterFirebaseOfflineMessageService.this);

                // if it is an image change messageData to 'Sent you an image'
                if(offlineMessage.getContentType().equals(ConstantRegistry.IMAGE)){
                    message.setMessageData(ConstantRegistry.CHATSTER_IMAGE_MESSAGE);
                }

                // show the user notification
                ChatsterNotificationUtil.sendOfflineMessageNotification(ChatsterFirebaseOfflineMessageService.this,
                        message);
            }else{
                throw new RuntimeException("E2EFirebase =====> HMAC is incorrect");
            }
        }else{
            throw new RuntimeException("E2EFirebase =====> signature is incorrect");
        }
    }

    private void handleOnlineGroupChatMessage(DeleteRetrievedMessagesResponse offlineMessage, Context context) {
        GroupChatOfflineMessage groupChatOfflineMessage = createGroupChatOnlineMessage(offlineMessage);

        // insert message uuid into local db for verification
        groupChatDatabaseLayer.insertReceivedOnlineGroupMessage(offlineMessage.getUuid(), context);

        byte[] messageWithSignatureHMACAndPK =
                chatsterGroupE2EHelper.messageWithSignatureHMACAndPKToByteArray(groupChatOfflineMessage.getGroupChatMessage());

        byte[] encryptedMessage = chatsterGroupE2EHelper.getMessageFrom(messageWithSignatureHMACAndPK);
        byte[] hmac = chatsterGroupE2EHelper.getHMACFrom(messageWithSignatureHMACAndPK);
        byte[] signature = chatsterGroupE2EHelper.getSignatureFrom(messageWithSignatureHMACAndPK);
        byte[] senderPublicKey = chatsterGroupE2EHelper.getPublicKeyFrom(messageWithSignatureHMACAndPK);

        byte[] receiverPrivateKey = groupChatDatabaseLayer.getGroupOneTimePrivatePreKeyByUUID(
                ChatsterFirebaseOfflineMessageService.this,
                groupChatOfflineMessage.getGroupMemberPBKUUID()
        );

        byte[] decryptSharedSecret = chatsterGroupE2EHelper.generateDecryptSharedSecret(senderPublicKey,receiverPrivateKey);

        boolean signatureIsCorrect = chatsterGroupE2EHelper.verifySignature(senderPublicKey, encryptedMessage, signature);

        if(signatureIsCorrect){
            byte[] decryptedMessage = chatsterGroupE2EHelper.decrypt(encryptedMessage, decryptSharedSecret);

            String hmacAfterDecryption = chatsterGroupE2EHelper.bytesToHex(
                    chatsterGroupE2EHelper.messageToHMAC(decryptedMessage,decryptSharedSecret)
            );

            if(chatsterGroupE2EHelper.bytesToHex(hmac).equals(hmacAfterDecryption)){
                String msg = new String(decryptedMessage, StandardCharsets.UTF_8);

                groupChatOfflineMessage.setGroupChatMessage(msg);

                // insert message in local db
                notificationDatabaseLayer.insertGroupMessage(groupChatOfflineMessage, imageProcessingManager, context);

                // if it is an image change messageData to 'Sent you an image'
                if(offlineMessage.getContentType().equals(ConstantRegistry.IMAGE)){
                    groupChatOfflineMessage.setGroupChatMessage(ConstantRegistry.CHATSTER_IMAGE_MESSAGE);
                }
                // show the user notification
                ChatsterNotificationUtil.sendGroupChatOfflineMessageNotification(
                        ChatsterFirebaseOfflineMessageService.this,
                        groupChatOfflineMessage);
            }else{
                throw new RuntimeException("E2EFirebase =====> HMAC is incorrect");
            }
        }else{
            throw new RuntimeException("E2EFirebase =====> signature is incorrect");
        }
    }

    private void getOfflineMessages(long userId){
        subscribeGetOfflineMessages =
                networkLayer.getOfflineMessages(userId)
                        .subscribeOn(Schedulers.io())
                        .subscribe(result -> {
                            for(OfflineMessageResponse offlineMessage : result){
                                if(offlineMessage.getMsgType().equals(ConstantRegistry.CHATSTER_CHAT_MSG)){
                                    handleOfflineChatMessage(offlineMessage);
                                }else if(offlineMessage.getMsgType().equals(ConstantRegistry.CHATSTER_UNSEND_MESSAGE)){
                                    handleOfflineUnsendChatMessage(offlineMessage,
                                            ChatsterFirebaseOfflineMessageService.this);
                                }
                            }
                            if(combinedUUIDS() != null && combinedUUIDS().length > 0){
                                deleteRetrievedMessages(combinedUUIDS(),
                                        userDatabaseLayer.getUserId(ChatsterFirebaseOfflineMessageService.this));
                            }
                        });
        disposable.add(subscribeGetOfflineMessages);
    }

    private void getGroupOfflineMessages(long userId, ArrayList<String> groupChatIds){
        subscribeGetOfflineMessages =
                networkLayer.getGroupOfflineMessages(userId, groupChatIds)
                        .subscribeOn(Schedulers.io())
                        .subscribe(result -> {
                            for(OfflineMessageResponse offlineMessage : result){
                                if(offlineMessage.getMsgType().equals(ConstantRegistry.CHATSTER_GROUP_CHAT_MSG)){
                                    handleOfflineGroupChatMessage(offlineMessage,
                                            ChatsterFirebaseOfflineMessageService.this);
                                }else if(offlineMessage.getMsgType().equals(ConstantRegistry.CHATSTER_UNSEND_MESSAGE_GROUP)){
                                    handleOfflineUnsendGroupChatMessage(offlineMessage,
                                            ChatsterFirebaseOfflineMessageService.this);
                                }
                            }
                            if(combinedUUIDS() != null && combinedUUIDS().length > 0){
                                deleteRetrievedGroupMessages(combinedUUIDS(),
                                        userDatabaseLayer.getUserId(ChatsterFirebaseOfflineMessageService.this));
                            }
                        });
        disposable.add(subscribeGetOfflineMessages);
    }

    private void deleteRetrievedMessages(String[] uuids, long userId){
        subscribeDeleteRetrievedMessages =
                networkLayer.deleteRetrievedMessages(uuids, userId)
                        .subscribeOn(Schedulers.io())
                        .subscribe(result -> {
                            if (result != null) {
                                boolean isSuccessful = true;
                                for(DeleteRetrievedMessagesResponse response : result){
                                    if(!response.getReturnType().equals(ConstantRegistry.ERROR)){
                                        if(response.getMsgType().equals(ConstantRegistry.CHATSTER_CHAT_MSG)){
                                            handleOnlineChatMessage(response);
                                        }else if(response.getMsgType().equals(ConstantRegistry.CHATSTER_GROUP_CHAT_MSG)){
                                            handleOnlineGroupChatMessage(response,
                                                    ChatsterFirebaseOfflineMessageService.this);
                                        }
                                    }else{
                                        isSuccessful = false;
                                    }
                                }
                                if(isSuccessful){
                                    deleteProcessedUUIDS(uuids);
                                }
                            }
                        });
        disposable.add(subscribeDeleteRetrievedMessages);
    }

    private void deleteRetrievedGroupMessages(String[] uuids, long userId){
        subscribeDeleteRetrievedMessages =
                networkLayer.deleteRetrievedGroupMessages(uuids, userId)
                        .subscribeOn(Schedulers.io())
                        .subscribe(result -> {
                            if (result != null) {
                                boolean isSuccessful = true;
                                for(DeleteRetrievedMessagesResponse response : result){
                                    if(!response.getReturnType().equals(ConstantRegistry.ERROR)){
                                        if(response.getMsgType().equals(ConstantRegistry.CHATSTER_CHAT_MSG)){
                                            handleOnlineChatMessage(response);
                                        }else if(response.getMsgType().equals(ConstantRegistry.CHATSTER_GROUP_CHAT_MSG)){
                                            handleOnlineGroupChatMessage(response,
                                                    ChatsterFirebaseOfflineMessageService.this);
                                        }
                                    }else{
                                        isSuccessful = false;
                                    }
                                }
                                if(isSuccessful){
                                    deleteProcessedUUIDS(uuids);
                                }
                            }
                        });
        disposable.add(subscribeDeleteRetrievedMessages);
    }

    private void handleOfflineUnsendGroupChatMessage(OfflineMessageResponse offlineMessage, Context context) {
        notificationDatabaseLayer.insertRetrievedOfflineMessageUUID(offlineMessage.getUuid(),
                ChatsterFirebaseOfflineMessageService.this);
        groupChatDatabaseLayer.updateMessageUnsentByUUID(offlineMessage.getUuid(), context);
    }

    private void handleOfflineUnsendChatMessage(OfflineMessageResponse offlineMessage, Context context) {
        notificationDatabaseLayer.insertRetrievedOfflineMessageUUID(offlineMessage.getUuid(),
                ChatsterFirebaseOfflineMessageService.this);
        notificationDatabaseLayer.updateMessageUnsentByMessageUUID(offlineMessage.getUuid(), context);
    }


    // region Check Group Keys

    public void insertGroupOneTimeKeyPair(String uuid, String groupChatId, byte[] groupOneTimePrivatePreKey,
                                          byte[] groupOneTimePublicPreKey, Context context) {
        this.groupChatDatabaseLayer.insertGroupOneTimeKeyPair(uuid, groupChatId, groupOneTimePrivatePreKey,
                groupOneTimePublicPreKey, context);
    }

    public void insertGroupOneTimeKeyPairs(Context context, String groupChatId, List<Curve25519KeyPair> keyPairs, List<String> uuids) {
        int counter = 0;

        for (Curve25519KeyPair keyPair: keyPairs) {
            insertGroupOneTimeKeyPair(
                    uuids.get(counter),
                    groupChatId,
                    keyPair.getPrivateKey(),
                    keyPair.getPublicKey(),
                    context
            );

            counter++;
        }
    }

    public List<Curve25519KeyPair> generateOneTimeGroupKeys(int amount){
        return this.chatsterGroupE2EHelper.generateOneTimeGroupKeys(amount);
    }

    public List<String> generateGroupUUIDS(int amount){
        return this.chatsterGroupE2EHelper.generateUUIDS(amount);
    }

    private void uploadGroupPublicOneTimeKeys(String oneTimePreKeyPairPbks){
        if(internetConnectionUtil.hasInternetConnection()){
            Disposable subscribeUploadGroupPublicOneTimeKeys = networkLayer.uploadGroupPublicKeys(oneTimePreKeyPairPbks)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(res -> {

                    }, Throwable::printStackTrace);
        }
    }

    private void checkGroupKeys(String groupChatIds, long userId){
        Disposable subscribeCheckGroupKeys = networkLayer.checkIfGroupKeysNeeded(groupChatIds, userId)
                .subscribeOn(Schedulers.io())
                .subscribe(res -> {
                    JsonObject objMain = new JsonObject();
                    JsonArray oneTimeGroupPreKeys = new JsonArray();
                    int groupIdCounter = 0;
                    for(String groupId : res){
                        // 1. Generate Curves
                        List<Curve25519KeyPair> groupOneTimeKeys =
                                generateOneTimeGroupKeys(ConstantRegistry.AMOUNT_OF_GROUP_ONE_TIME_KEY_PAIRS_AT_REPLENISHMENT);

                        // 2. Generate UUIDS
                        List<String> uuids = generateGroupUUIDS(ConstantRegistry.AMOUNT_OF_GROUP_ONE_TIME_KEY_PAIRS_AT_REPLENISHMENT);

                        // 3. Insert the keys locally
                        insertGroupOneTimeKeyPairs(
                                ChatsterFirebaseOfflineMessageService.this,
                                groupId,
                                groupOneTimeKeys,
                                uuids
                        );

                        int counter = 0;

                        for (Curve25519KeyPair keyPair: groupOneTimeKeys) {
                            Gson gson = new Gson();
                            JsonElement u_id = gson.toJsonTree(userId);
                            JsonElement g_id = gson.toJsonTree(groupId);
                            JsonElement uuid = gson.toJsonTree(uuids.get(counter));
                            JsonElement otpk = gson.toJsonTree(keyPair.getPublicKey());
                            JsonObject oneTimeGroupPreKey = new JsonObject();
                            oneTimeGroupPreKey.add("user_id", u_id);
                            oneTimeGroupPreKey.add("group_id", g_id);
                            oneTimeGroupPreKey.add("group_one_time_pre_key_pair_pbk", otpk);
                            oneTimeGroupPreKey.add("group_one_time_pre_key_pair_uuid", uuid);
                            oneTimeGroupPreKeys.add(oneTimeGroupPreKey);

                            counter++;
                        }

                        groupIdCounter++;
                    }

                    objMain.add("oneTimeGroupPreKeyPairPbks",oneTimeGroupPreKeys);

                    uploadGroupPublicOneTimeKeys(objMain.toString());
                },throwable -> throwable.printStackTrace());
    }

    // endregion


    private void handleOfflineGroupChatMessage(OfflineMessageResponse offlineMessage, Context context) {
        if(groupChatDatabaseLayer.getGroupChatMessageByUUID(ChatsterFirebaseOfflineMessageService.this,
                offlineMessage.getUuid()) == null){
            notificationDatabaseLayer.insertRetrievedOfflineMessageUUID(offlineMessage.getUuid(),
                    ChatsterFirebaseOfflineMessageService.this);

            GroupChatOfflineMessage groupChatOfflineMessage = createGroupChatOfflineMessage(offlineMessage);

            byte[] messageWithSignatureHMACAndPK =
                    chatsterGroupE2EHelper.messageWithSignatureHMACAndPKToByteArray(groupChatOfflineMessage.getGroupChatMessage());

            byte[] encryptedMessage = chatsterGroupE2EHelper.getMessageFrom(messageWithSignatureHMACAndPK);
            byte[] hmac = chatsterGroupE2EHelper.getHMACFrom(messageWithSignatureHMACAndPK);
            byte[] signature = chatsterGroupE2EHelper.getSignatureFrom(messageWithSignatureHMACAndPK);
            byte[] senderPublicKey = chatsterGroupE2EHelper.getPublicKeyFrom(messageWithSignatureHMACAndPK);

            byte[] receiverPrivateKey = groupChatDatabaseLayer.getGroupOneTimePrivatePreKeyByUUID(
                    ChatsterFirebaseOfflineMessageService.this,
                    groupChatOfflineMessage.getGroupMemberPBKUUID()
            );

            byte[] decryptSharedSecret = chatsterGroupE2EHelper.generateDecryptSharedSecret(senderPublicKey,receiverPrivateKey);

            boolean signatureIsCorrect = chatsterGroupE2EHelper.verifySignature(senderPublicKey, encryptedMessage, signature);

            if(signatureIsCorrect){
                byte[] decryptedMessage = chatsterGroupE2EHelper.decrypt(encryptedMessage, decryptSharedSecret);

                String hmacAfterDecryption = chatsterGroupE2EHelper.bytesToHex(
                        chatsterGroupE2EHelper.messageToHMAC(decryptedMessage,decryptSharedSecret)
                );

                if(chatsterGroupE2EHelper.bytesToHex(hmac).equals(hmacAfterDecryption)){
                    String msg = new String(decryptedMessage, StandardCharsets.UTF_8);

                    groupChatOfflineMessage.setGroupChatMessage(msg);

                    // insert message in local db
                    notificationDatabaseLayer.insertGroupMessage(groupChatOfflineMessage, imageProcessingManager, context);
                    // if it is an image change messageData to 'Sent you an image'
                    if(offlineMessage.getContentType().equals(ConstantRegistry.IMAGE)){
                        groupChatOfflineMessage.setGroupChatMessage(ConstantRegistry.CHATSTER_IMAGE_MESSAGE);
                    }
                    // show the user notification
                    ChatsterNotificationUtil.sendGroupChatOfflineMessageNotification(
                            ChatsterFirebaseOfflineMessageService.this,
                            groupChatOfflineMessage);
                }else{
                    throw new RuntimeException("E2EFirebase =====> HMAC is incorrect");
                }
            }else{
                throw new RuntimeException("E2EFirebase =====> signature is incorrect");
            }
        }

        String groupChatIds =
                groupChatDatabaseLayer.getAllGroupChatIds(
                        ChatsterFirebaseOfflineMessageService.this).
                        toString().replace("[", "").replace("]","");

        checkGroupKeys(groupChatIds,
                userDatabaseLayer.getUserId(
                        ChatsterFirebaseOfflineMessageService.this
                )
        );
    }

    // region Upload New Batch Of Keys

    private void uploadNewBatchOfKeys(long userId, String oneTimePreKeyPairPbks){
        Disposable subscribeUploadKeys = networkLayer.uploadPublicKeys(userId, oneTimePreKeyPairPbks)
                .subscribeOn(Schedulers.io())
                .subscribe(res -> {
                    if(res.equals(ConstantRegistry.ERROR)){
                        throw new RuntimeException("Firebase =====> upload new keys error");
                    }
                }, Throwable::printStackTrace);
    }

    // endregion

    // region Check Keys

    public void insertOneTimeKeyPairs(Context context, long userId, List<Curve25519KeyPair> keyPairs, List<String> uuids) {
        int counter = 0;

        for (Curve25519KeyPair keyPair: keyPairs) {
            this.userDatabaseLayer.insertUserOneTimeKeyPair(
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
        insertOneTimeKeyPairs(context, userId, oneTimeKeys, uuids);

        // 4. JSONify public keys to be stored on the server
        return jsonifyOneTimeKeys(oneTimeKeys, uuids, userId);
    }

    private void checkKeys(long userId){
        Disposable subscribeCheckKeys = networkLayer.checkPublicKeys(userId)
                .subscribeOn(Schedulers.io())
                .subscribe(res -> {
                    if(res.equals(ConstantRegistry.YES)){
                        uploadNewBatchOfKeys(
                            userId,
                            jsonifiedOneTimeKeys(
                                ChatsterFirebaseOfflineMessageService.this,
                                userId,
                                ConstantRegistry.AMOUNT_OF_ONE_TIME_KEY_PAIRS_AT_REPLENISHMENT
                            )
                        );
                    }else if(res.equals(ConstantRegistry.ERROR)){

                    }
                },throwable -> throwable.printStackTrace());
    }

    // endregion

    private void handleOfflineChatMessage(OfflineMessageResponse offlineMessage) {
        if(chatDatabaseLayer.getChatMessageByUUID(ChatsterFirebaseOfflineMessageService.this,
                offlineMessage.getUuid()) == null){
            notificationDatabaseLayer.insertRetrievedOfflineMessageUUID(offlineMessage.getUuid(),
                    ChatsterFirebaseOfflineMessageService.this);

            OfflineMessage message = createOfflineChatMessage(offlineMessage);

            byte[] messageWithSignatureHMACAndPK =
                    chatsterE2EHelper.messageWithSignatureHMACAndPKToByteArray(message.getMessageData());

            byte[] encryptedMessage = chatsterE2EHelper.getMessageFrom(messageWithSignatureHMACAndPK);
            byte[] hmac = chatsterE2EHelper.getHMACFrom(messageWithSignatureHMACAndPK);
            byte[] signature = chatsterE2EHelper.getSignatureFrom(messageWithSignatureHMACAndPK);
            byte[] senderPublicKey = chatsterE2EHelper.getPublicKeyFrom(messageWithSignatureHMACAndPK);

            byte[] receiverPrivateKey = userDatabaseLayer.getUserOneTimePrivatePreKeyByUUID(
                    ChatsterFirebaseOfflineMessageService.this, message.getContactPublicKeyUUID());

            byte[] decryptSharedSecret = chatsterE2EHelper.generateDecryptSharedSecret(senderPublicKey,receiverPrivateKey);

            boolean signatureIsCorrect = chatsterE2EHelper.verifySignature(senderPublicKey, encryptedMessage, signature);

            if(signatureIsCorrect){
                byte[] decryptedMessage = chatsterE2EHelper.decrypt(encryptedMessage, decryptSharedSecret);

                String hmacAfterDecryption = chatsterE2EHelper.bytesToHex(
                        chatsterE2EHelper.messageToHMAC(decryptedMessage,decryptSharedSecret)
                );
                if(chatsterE2EHelper.bytesToHex(hmac).equals(hmacAfterDecryption)){
                    String msg = new String(decryptedMessage, StandardCharsets.UTF_8);

                    message.setMessageData(msg);

                    userDatabaseLayer.deleteOneTimeKeyPairByUUID(message.getContactPublicKeyUUID(),
                            ChatsterFirebaseOfflineMessageService.this);

                    // insert message in local db
                    notificationDatabaseLayer.insertOfflineMessage(message,
                            imageProcessingManager,
                            ChatsterFirebaseOfflineMessageService.this);

                    // if it is an image change messageData to 'Sent you an image'
                    if(offlineMessage.getContentType().equals(ConstantRegistry.IMAGE)){
                        message.setMessageData(ConstantRegistry.CHATSTER_IMAGE_MESSAGE);
                    }

                    // show the user notification
                    ChatsterNotificationUtil.sendOfflineMessageNotification(ChatsterFirebaseOfflineMessageService.this,
                            message);
                }else{
                    throw new RuntimeException("E2EFirebase =====> HMAC is incorrect");
                }
            }else{
                throw new RuntimeException("E2EFirebase =====> signature is incorrect");
            }
        }

        // check if new keys needed if so generate and upload
        checkKeys(userDatabaseLayer.getUserId(ChatsterFirebaseOfflineMessageService.this));
    }

    // region Create Messages

    @NonNull
    private OfflineMessage createOfflineChatMessage(OfflineMessageResponse offlineMessage) {
        OfflineMessage message = new OfflineMessage();
        message.setMsgType(offlineMessage.getMsgType());
        message.setSenderId(offlineMessage.getSenderId());
        message.setSenderName(offlineMessage.getSenderName());
        message.setChatName(offlineMessage.getChatName());
        message.setMessageData(offlineMessage.getMessageData());
        message.setContentType(offlineMessage.getContentType());
        message.setUuid(offlineMessage.getUuid());
        message.setContactPublicKeyUUID(offlineMessage.getContactPublicKeyUUID());
        message.setMessageCreated(offlineMessage.getMessageCreated());
        return message;
    }

    private OfflineMessage createOnlineChatMessage(DeleteRetrievedMessagesResponse offlineMessage) {
        OfflineMessage message = new OfflineMessage();
        message.setMsgType(offlineMessage.getMsgType());
        message.setSenderId(offlineMessage.getSenderId());
        message.setSenderName(offlineMessage.getSenderName());
        message.setChatName(offlineMessage.getChatname());
        message.setMessageData(offlineMessage.getMessageText());
        message.setContentType(offlineMessage.getContentType());
        message.setUuid(offlineMessage.getUuid());
        message.setContactPublicKeyUUID(offlineMessage.getContactPublicKeyUUID());
        message.setMessageCreated(offlineMessage.getMessageCreated());
        return message;
    }

    @NonNull
    private GroupChatOfflineMessage createGroupChatOfflineMessage(OfflineMessageResponse offlineMessage) {
        GroupChatOfflineMessage groupChatOfflineMessage = new GroupChatOfflineMessage();
        groupChatOfflineMessage.setMsgType(offlineMessage.getMsgType());
        groupChatOfflineMessage.setGroupChatId(offlineMessage.getGroupChatId());
        groupChatOfflineMessage.setSenderId(offlineMessage.getSenderId());
        groupChatOfflineMessage.setGroupChatMessage(offlineMessage.getMessageText());
        groupChatOfflineMessage.setContentType(offlineMessage.getContentType());
        groupChatOfflineMessage.setUuid(offlineMessage.getUuid());
        groupChatOfflineMessage.setGroupMemberPBKUUID(offlineMessage.getContactPublicKeyUUID());
        groupChatOfflineMessage.setMessageCreated(offlineMessage.getMessageCreated());
        return groupChatOfflineMessage;
    }

    @NonNull
    private GroupChatOfflineMessage createGroupChatOnlineMessage(DeleteRetrievedMessagesResponse offlineMessage) {
        GroupChatOfflineMessage groupChatOfflineMessage = new GroupChatOfflineMessage();
        groupChatOfflineMessage.setMsgType(offlineMessage.getMsgType());
        groupChatOfflineMessage.setGroupChatId(offlineMessage.getGroupChatId());
        groupChatOfflineMessage.setSenderId(offlineMessage.getSenderId());
        groupChatOfflineMessage.setGroupChatMessage(offlineMessage.getMessageText());
        groupChatOfflineMessage.setContentType(offlineMessage.getContentType());
        groupChatOfflineMessage.setUuid(offlineMessage.getUuid());
        groupChatOfflineMessage.setGroupMemberPBKUUID(offlineMessage.getContactPublicKeyUUID());
        groupChatOfflineMessage.setMessageCreated(offlineMessage.getMessageCreated());
        return groupChatOfflineMessage;
    }

    // endregion

    // endregion

    // region Group Chat Invitations

    private void getGroupInvitations() {
        if(internetConnectionUtil.hasInternetConnection()){
            getGroupChatInvitations(userDatabaseLayer.getUserId(ChatsterFirebaseOfflineMessageService.this));
        }
    }

    private void getGroupChatInvitations(long userId){
        subscribeGetGroupChatInvitations = networkLayer.getGroupChatInvitations(userId)
                .subscribeOn(Schedulers.io())
                .subscribe(result -> {
                    processGetGroupChatInvitationsResult(result);
                });
        disposable.add(subscribeGetGroupChatInvitations);
    }

    private void processGetGroupChatInvitationsResult(ArrayList<OfflineContactResponse> result) {
        for(OfflineContactResponse contactMessage : result){
            if(contactMessage.getMsgType().equals(ConstantRegistry.CHATSTER_GROUP_CHAT_INVITATION_MSG)){
                GroupChatInvitation groupChatInvitation = createGroupChatInvitation(contactMessage);
                if(senderIsNotUser(contactMessage)){
                    saveInvitation(groupChatInvitation);
                }
            }
        }
    }

    private void saveInvitation(GroupChatInvitation groupChatInvitation) {
        notificationDatabaseLayer.insertGroupChatInvitationNotification(groupChatInvitation,
                ChatsterFirebaseOfflineMessageService.this);
    }

    private boolean senderIsNotUser(OfflineContactResponse contactMessage) {
        return userDatabaseLayer.getUserId(ChatsterFirebaseOfflineMessageService.this)
                != contactMessage.getGroupChatInvitationSenderId();
    }

    @NonNull
    private GroupChatInvitation createGroupChatInvitation(OfflineContactResponse contactMessage) {
        GroupChatInvitation groupChatInvitation  = new GroupChatInvitation();
        groupChatInvitation.setGroupChatInvitationChatId(contactMessage.getGroupChatInvitationChatId());
        groupChatInvitation.setGroupChatInvitationSenderId(contactMessage.getGroupChatInvitationSenderId());
        for(long memberId : contactMessage.getGroupChatInvitationGroupChatMembers()){
            groupChatInvitation.addGroupChatMember(memberId);
        }
        groupChatInvitation.setGroupChatInvitationChatName(contactMessage.getGroupChatInvitationChatName());
        groupChatInvitation.setGroupProfilePicPath(contactMessage.getGroupProfilePicPath());
        return groupChatInvitation;
    }

    // endregion
}
