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
