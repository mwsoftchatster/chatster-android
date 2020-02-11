/*
  Copyright (C) 2017 - 2020 MWSOFT

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package nl.mwsoft.www.chatster.modelLayer.modelLayerManager.groupChat;


import android.content.Context;

import java.util.ArrayList;

import io.reactivex.Observable;
import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.modelLayer.database.groupChat.GroupChatDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.model.CreateGroupChatRequest;
import nl.mwsoft.www.chatster.modelLayer.model.ExitGroupChatReq;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChat;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChatMessage;
import nl.mwsoft.www.chatster.modelLayer.model.ReceivedOnlineGroupMessage;
import nl.mwsoft.www.chatster.modelLayer.network.NetworkLayer;

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

    // endregion

    public Observable<String> deleteGroupChatInvitation(String groupChatId, long userId){
        return this.networkLayer.deleteGroupChatInvitation(groupChatId, userId);
    }
}
