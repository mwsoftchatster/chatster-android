package nl.mwsoft.www.chatster.modelLayer.modelLayerManager.groupChat;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;


import org.whispersystems.curve25519.Curve25519KeyPair;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.modelLayer.database.dblocal.ChatsProvider;
import nl.mwsoft.www.chatster.modelLayer.database.dblocal.DBOpenHelper;
import nl.mwsoft.www.chatster.modelLayer.database.groupChat.GroupChatDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.model.CreateGroupChatRequest;
import nl.mwsoft.www.chatster.modelLayer.model.ExitGroupChatReq;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChat;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChatMessage;
import nl.mwsoft.www.chatster.modelLayer.model.OneTimeGroupKeyPair;
import nl.mwsoft.www.chatster.modelLayer.model.OneTimeGroupPublicKey;
import nl.mwsoft.www.chatster.modelLayer.model.ReceivedOnlineGroupMessage;
import nl.mwsoft.www.chatster.modelLayer.network.NetworkLayer;
import nl.mwsoft.www.chatster.modelLayer.network.deleteGroupChatInvitation.DeleteGroupChatInvitationImpl;
import nl.mwsoft.www.chatster.modelLayer.network.getGroupOneTimeKeys.GetGroupOneTimeKeysImpl;
import nl.mwsoft.www.chatster.modelLayer.network.uploadGroupPublicKeys.UploadGroupPublicKeysImpl;

public class GroupChatModelLayerManager {

    private GroupChatDatabaseLayer groupChatDatabaseLayer;
    private NetworkLayer networkLayer;

    public GroupChatModelLayerManager() {
        this.networkLayer = DependencyRegistry.shared.createNetworkLayer();
        this.groupChatDatabaseLayer = DependencyRegistry.shared.createGroupChatDatabaseLayer();
    }

    // region Group Chat DB

    public void updateMessageHasBeenReadByMessageId(int messageId, Context context){
        this.groupChatDatabaseLayer.updateMessageHasBeenReadByMessageId( messageId, context);
    }

    public String getGroupProfilePicUriById(Context context, String groupId) {
        return this.groupChatDatabaseLayer.getGroupProfilePicUriById(context, groupId);
    }

    public int getUnreadMessageCountByGroupChatId(Context context, String groupChatId) {
        return this.groupChatDatabaseLayer.getUnreadMessageCountByGroupChatId(context, groupChatId);
    }

    public ArrayList<GroupChatMessage> getAllMessagesForGroupChatWithId(Context context, String groupChatId) {
        return this.groupChatDatabaseLayer.getAllMessagesForGroupChatWithId(context, groupChatId);
    }

    public String getGroupChatNameById(Context context, String groupChatId) {
        return this.groupChatDatabaseLayer.getGroupChatNameById(context, groupChatId);
    }

    public long getGroupChatAdminByGroupId(Context context, String groupChatId) {
        return this.groupChatDatabaseLayer.getGroupChatAdminByGroupId(context, groupChatId);
    }

    public ArrayList<GroupChat> getAllGroupChats(Context context) {
        return this.groupChatDatabaseLayer.getAllGroupChats(context);
    }

    public GroupChat getGroupChatById(Context context, String id) {
        return this.groupChatDatabaseLayer.getGroupChatById(context, id);
    }

    public void deleteGroupChatMessage(int messageId, Context context){
        this.groupChatDatabaseLayer.deleteGroupChatMessage(messageId, context);
    }

    public void updateMessageUnsentByUUID(String uuid, Context context){
        this.groupChatDatabaseLayer.updateMessageUnsentByUUID(uuid, context);
    }

    public void insertGroupChatMessage(GroupChatMessage groupChatMessage, Context context) {
        this.groupChatDatabaseLayer.insertGroupChatMessage(groupChatMessage, context);
    }

    public void createGroupChat(GroupChat groupChat, Context context) {
        this.groupChatDatabaseLayer.createGroupChat(groupChat, context);
    }

    public void createGroupChatMember(String groupChatId, long groupChatMemberId, Context context) {
        this.groupChatDatabaseLayer.createGroupChatMember(groupChatId, groupChatMemberId, context);
    }

    public void updateGroupProfilePic(String uri, String groupChatId, Context context){
        this.groupChatDatabaseLayer.updateGroupProfilePic(uri, groupChatId, context);
    }

    public void deleteGroupChat(Context context, String groupChatId){
        this.groupChatDatabaseLayer.deleteGroupChat(context, groupChatId);
    }

    public void insertGroupChat(GroupChat groupChat, Context context) {
        this.groupChatDatabaseLayer.insertGroupChat(groupChat, context);
    }

    public void insertGroupChatMember(String groupChatId, long groupChatMemberId, Context context) {
        this.groupChatDatabaseLayer.insertGroupChatMember(groupChatId, groupChatMemberId, context);
    }

    public void deleteGroupChatMessageQueueItemByUUID(String uuid, Context context){
        this.groupChatDatabaseLayer.deleteGroupChatMessageQueueItemByUUID(uuid, context);
    }

    public void insertGroupChatMessageQueueItem(String messageUUID, Context context) {
        this.groupChatDatabaseLayer.insertGroupChatMessageQueueItem(messageUUID, context);
    }

    public void insertReceivedOnlineGroupMessage(String uuid, Context context) {
        this.groupChatDatabaseLayer.insertReceivedOnlineGroupMessage(uuid, context);
    }

    public void deleteReceivedOnlineGroupMessageByUUID(String uuid, Context context){
        this.groupChatDatabaseLayer.deleteReceivedOnlineGroupMessageByUUID(uuid, context);
    }

    public ReceivedOnlineGroupMessage getReceivedOnlineGroupMessage(Context context) {
        return this.groupChatDatabaseLayer.getReceivedOnlineGroupMessage(context);
    }

    public OneTimeGroupKeyPair getGroupOneTimeKeyPair(Context context, String groupChatId, long userId) {
        return this.groupChatDatabaseLayer.getGroupOneTimeKeyPair(context, groupChatId, userId);
    }

    public byte[] getGroupOneTimePublicPreKey(Context context, String groupChatId) {
        return this.groupChatDatabaseLayer.getGroupOneTimePublicPreKey(context, groupChatId);
    }

    public byte[] getGroupOneTimePrivatePreKeyByUUID(Context context, String uuid) {
        return this.groupChatDatabaseLayer.getGroupOneTimePrivatePreKeyByUUID(context, uuid);
    }

    public byte[] getGroupOneTimePublicPreKeyByUUID(Context context, String uuid) {
        return this.groupChatDatabaseLayer.getGroupOneTimePublicPreKeyByUUID(context, uuid);
    }

    public void insertGroupOneTimeKeyPair(String uuid, String groupChatId, byte[] groupOneTimePrivatePreKey,
                                          byte[] groupOneTimePublicPreKey, Context context) {
        this.groupChatDatabaseLayer.insertGroupOneTimeKeyPair(uuid, groupChatId, groupOneTimePrivatePreKey,
         groupOneTimePublicPreKey, context);
    }

    public void insertOneTimeKeyPairs(Context context, String groupChatId, List<Curve25519KeyPair> keyPairs, List<String> uuids) {
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

    public void deleteOneTimeGroupKeyPairByUUID(String uuid, Context context){
        this.groupChatDatabaseLayer.deleteOneTimeGroupKeyPairByUUID(uuid, context);
    }

    public OneTimeGroupKeyPair getUserOneGroupTimeKeyPairByUUID(Context context, String uuid, long userId, String groupChatId) {
        return this.groupChatDatabaseLayer.getUserOneGroupTimeKeyPairByUUID(context, uuid, userId, groupChatId);
    }

    //endregion

    // region Group Chat Network

    public Observable<String> addNewGroupMember(String chatId, long groupChatAdminId, String groupChatName,
                                                ArrayList<Long> newGroupChatMembers, String groupChatPicPath){
            return this.networkLayer.addNewGroupMember(chatId, groupChatAdminId, groupChatName,
                    newGroupChatMembers, groupChatPicPath);
    }

    public Observable<GroupChat> createGroupChat(CreateGroupChatRequest createGroupChatRequest) {
        return this.networkLayer.createGroupChat(createGroupChatRequest);
    }

    public Observable<ExitGroupChatReq> exitGroupChat(ExitGroupChatReq exitGroupChatReq, Context context){
        return this.networkLayer.exitGroupChat(exitGroupChatReq, context);
    }

    // region Group One Time Keys

    public Observable<String> uploadGroupPublicKeys(String oneTimePreKeyPairPbks){
        return this.networkLayer.uploadGroupPublicKeys(oneTimePreKeyPairPbks);
    }

    public Observable<ArrayList<OneTimeGroupPublicKey>> getGroupOneTimeKeys(String groupChatId, long userId){
        return this.networkLayer.getGroupOneTimeKeys(groupChatId, userId);
    }

    // endregion

    // endregion

    public Observable<String> deleteGroupChatInvitation(String groupChatId, long userId){
        return this.networkLayer.deleteGroupChatInvitation(groupChatId, userId);
    }
}
