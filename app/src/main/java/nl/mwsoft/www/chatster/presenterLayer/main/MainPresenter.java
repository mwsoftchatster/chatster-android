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
package nl.mwsoft.www.chatster.presenterLayer.main;


import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import nl.mwsoft.www.chatster.modelLayer.helper.util.jobService.ChatsterJobServiceUtil;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChat;
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

    public MainPresenter() {
    }

    public MainPresenter(ChatsterJobServiceUtil chatsterServiceUtil,
                         UserModelLayerManager userModelLayerManager,
                         NotificationModelLayerManager notificationModelLayerManager,
                         GroupChatModelLayerManager groupChatModelLayerManager,
                         UtilModelLayerManager utilModelLayerManager) {
        this.chatsterServiceUtil = chatsterServiceUtil;
        this.userModelLayerManager = userModelLayerManager;
        this.notificationModelLayerManager = notificationModelLayerManager;
        this.groupChatModelLayerManager = groupChatModelLayerManager;
        this.utilModelLayerManager = utilModelLayerManager;
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

    // endregion

    //  endregion

    // region Update User Token

    public Observable<String> updateUserToken(long userId, String  messagingToken){
        return this.userModelLayerManager.updateUserToken(userId, messagingToken);
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
}
