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
package nl.mwsoft.www.chatster.modelLayer.service.local;


import android.app.job.JobParameters;
import android.app.job.JobService;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.modelLayer.database.groupChat.GroupChatDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.helper.util.jobService.ChatsterJobServiceUtil;
import nl.mwsoft.www.chatster.modelLayer.helper.util.notification.ChatsterNotificationUtil;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChatInvitation;

public class ChatsterLocalNotificationJobService extends JobService {

    private GroupChatDatabaseLayer groupChatDatabaseLayer;
    private ChatsterJobServiceUtil chatsterJobServiceUtil;

    public ChatsterLocalNotificationJobService() {
    }

    public void configureWith(GroupChatDatabaseLayer groupChatDatabaseLayer, ChatsterJobServiceUtil chatsterJobServiceUtil){
        this.groupChatDatabaseLayer = groupChatDatabaseLayer;
        this.chatsterJobServiceUtil = chatsterJobServiceUtil;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        Fabric.with(this, new Crashlytics());
        DependencyRegistry.shared.inject(this);

        if(groupChatDatabaseLayer.getGroupChatInvitation(ChatsterLocalNotificationJobService.this) != null){
            GroupChatInvitation groupChatInvitation = groupChatDatabaseLayer.getGroupChatInvitation(
                    ChatsterLocalNotificationJobService.this);

            ChatsterNotificationUtil.sendGroupChatInvitationNotification(
                    ChatsterLocalNotificationJobService.this,groupChatInvitation);
        }

        jobFinished(params, false);
        chatsterJobServiceUtil.startLocalNotificationJobService(ChatsterLocalNotificationJobService.this);

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
