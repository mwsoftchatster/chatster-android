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
package nl.mwsoft.www.chatster.modelLayer.helper.util.jobService;


import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.service.local.ChatsterLocalNotificationJobService;
import nl.mwsoft.www.chatster.modelLayer.service.contactLatest.ContactLatestJobService;
import nl.mwsoft.www.chatster.modelLayer.service.messageQueue.ChatsterMessageQueueJobService;

public class ChatsterJobServiceUtil {

    public ChatsterJobServiceUtil() {
    }

    public void startChatsterMessageQueueJobService(Context context){
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo jobInfo = new JobInfo.Builder(ConstantRegistry.RESEND_MESSAGE_JOB_SERVICE,
                new ComponentName(context, ChatsterMessageQueueJobService.class))
                .setMinimumLatency(60000)
                .setRequiresCharging(false)
                .build();
        jobScheduler.schedule(jobInfo);
    }

    public void startLocalNotificationJobService(Context context){
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo jobInfo = new JobInfo.Builder(ConstantRegistry.LOCAL_NOTIFICATION_JOB_SERVICE,
                new ComponentName(context, ChatsterLocalNotificationJobService.class))
                .setMinimumLatency(5000)
                .setRequiresCharging(false)
                .build();
        jobScheduler.schedule(jobInfo);
    }

    public void startContactLatestJobService(Context context){
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo jobInfo = new JobInfo.Builder(ConstantRegistry.CONTACT_LATEST_NOTIFICATION_JOB_SERVICE,
                new ComponentName(context, ContactLatestJobService.class))
                .setMinimumLatency(60000)
                .setRequiresCharging(false)
                .build();
        jobScheduler.schedule(jobInfo);
    }

    public void startServices(Context context) {

        startLocalNotificationJobService(context);

        startContactLatestJobService(context);

        startChatsterMessageQueueJobService(context);
    }
}
