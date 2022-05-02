package nl.mwsoft.www.chatster.modelLayer.helper.util.notification;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import androidx.core.app.NotificationCompat;

import nl.mwsoft.www.chatster.modelLayer.model.HistoryItem;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.contact.ContactModelLayerManager;
import nl.mwsoft.www.chatster.viewLayer.chat.ChatActivity;
import nl.mwsoft.www.chatster.viewLayer.creators.CreatorsActivity;
import nl.mwsoft.www.chatster.viewLayer.groupChat.GroupChatActivity;
import nl.mwsoft.www.chatster.viewLayer.main.MainActivity;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChatInvitation;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChatOfflineMessage;
import nl.mwsoft.www.chatster.modelLayer.model.OfflineMessage;

public class ChatsterNotificationUtil {

    private static ContactModelLayerManager contactModelLayerManager = new ContactModelLayerManager();

    public ChatsterNotificationUtil(){
    }

    public static void sendGroupChatInvitationNotification(Context context, GroupChatInvitation groupChatInvitation) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelId = context.getString(R.string.channel_group_invitation);
        String channelName = context.getString(R.string.group_chat_invitation_channel);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, channelId);

        //Create the intent that’ll fire when the user taps the notification/
        Intent acceptIntent = new Intent(context, MainActivity.class);
        acceptIntent.putExtra(ConstantRegistry.ACCEPT_GROUP_INVITATION_INTENT, ConstantRegistry.ACCEPT_GROUP_INVITATION_INTENT);
        acceptIntent.putExtra(ConstantRegistry.ACCEPT_GROUP_INVITATION_INTENT, groupChatInvitation);
        acceptIntent.setAction(ConstantRegistry.ACCEPT_GROUP_INVITATION_REQUEST);
        acceptIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent acceptPendingIntent = PendingIntent.getActivity(context, ConstantRegistry.ACCEPT_PENDING_INTENT_REQUEST,
                acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent declineIntent = new Intent(context, MainActivity.class);
        declineIntent.putExtra(ConstantRegistry.DECLINE_GROUP_INVITATION_INTENT, ConstantRegistry.DECLINE_GROUP_INVITATION_INTENT);
        declineIntent.putExtra(ConstantRegistry.DECLINE_GROUP_INVITATION_INTENT,groupChatInvitation);
        declineIntent.setAction(ConstantRegistry.DECLINE_GROUP_INVITATION_REQUEST);
        declineIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent declinePendingIntent = PendingIntent.getActivity(context, ConstantRegistry.DECLINE_PENDING_INTENT_REQUEST,
                declineIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setSmallIcon(R.drawable.chatster_logo);
        mBuilder.setContentTitle(groupChatInvitation.getGroupChatInvitationChatName());
        mBuilder.setContentText(context.getString(R.string.join_group_chat));
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setAutoCancel(true);
        mBuilder.setOnlyAlertOnce(true);
        mBuilder.setVibrate(new long[] {0, 200, 200,200 });
        mBuilder.setLights(Color.MAGENTA, 500, 500);
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        mBuilder.addAction(R.drawable.accept_64,context.getString(R.string.accept),acceptPendingIntent);
        mBuilder.addAction(R.drawable.decline_64,context.getString(R.string.decline),declinePendingIntent);

        mNotificationManager.notify(notificationId, mBuilder.build());
    }

    public static void sendGroupChatOfflineMessageNotification(Context context, GroupChatOfflineMessage groupChatOfflineMessage) {

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelId = context.getString(R.string.channel_group_message);
        String channelName = context.getString(R.string.group_chat_message_channel);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, channelId);

        Intent replyMessageIntent = new Intent(context, GroupChatActivity.class);
        replyMessageIntent.putExtra(ConstantRegistry.READ_GROUP_CHAT_MESSAGE_INTENT, groupChatOfflineMessage);
        replyMessageIntent.setAction(ConstantRegistry.READ_GROUP_CHAT_MESSAGE_REQUEST);
        replyMessageIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent replyMessagePendingIntent = PendingIntent.getActivity(context, ConstantRegistry.REPLY_MESSAGE_PENDING_INTENT_REQUEST,
                replyMessageIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setSmallIcon(R.drawable.chatster_logo);
        mBuilder.setContentTitle(contactModelLayerManager.getContactNameById(context,
                groupChatOfflineMessage.getSenderId()));
        mBuilder.setContentText(context.getString(R.string.new_message));
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setAutoCancel(true);
        mBuilder.setOnlyAlertOnce(true);
        mBuilder.setVibrate(new long[] {0, 200, 200,200 });
        mBuilder.setLights(Color.MAGENTA, 500, 500);
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        mBuilder.addAction(R.drawable.reply_message_64,context.getString(R.string.reply),replyMessagePendingIntent);

        mNotificationManager.notify(notificationId, mBuilder.build());
    }

    public static void sendOfflineMessageNotification(Context context, OfflineMessage offlineMessage) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelId = context.getString(R.string.channel_message);
        String channelName = context.getString(R.string.chat_message_channel);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, channelId);

        if(contactModelLayerManager.getContactById(context,offlineMessage.getSenderId()) != null){
            // this contact already exists
            Intent replyMessageIntent = new Intent(context, ChatActivity.class);
            replyMessageIntent.putExtra(ConstantRegistry.READ_MESSAGE_INTENT, offlineMessage);
            replyMessageIntent.setAction(ConstantRegistry.READ_MESSAGE_REQUEST);
            replyMessageIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent replyMessagePendingIntent = PendingIntent.getActivity(context, ConstantRegistry.REQUEST_CODE,
                    replyMessageIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setSmallIcon(R.drawable.chatster_logo);
            mBuilder.setContentTitle(contactModelLayerManager.getContactNameById(context, offlineMessage.getSenderId()));
            mBuilder.setContentText(offlineMessage.getMessageData());
            mBuilder.setPriority(Notification.PRIORITY_MAX);
            mBuilder.setAutoCancel(true);
            mBuilder.setOnlyAlertOnce(true);
            mBuilder.setVibrate(new long[] {0, 200, 200,200 });
            mBuilder.setLights(Color.MAGENTA, 500, 500);
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            mBuilder.addAction(R.drawable.reply_message_64,context.getString(R.string.reply),replyMessagePendingIntent);
        }else{
            // sender of this message has this user's phone number
            // but this user does not have senders phone number
            // ask if this user wants to add sender as contact
            Intent acceptMessageUnknownIntent = new Intent(context, ChatActivity.class);
            acceptMessageUnknownIntent.putExtra(ConstantRegistry.ACCEPT_MESSAGE_FROM_UNKNOWN_INTENT, offlineMessage);
            acceptMessageUnknownIntent.setAction(ConstantRegistry.ACCEPT_MESSAGE_FROM_UNKNOWN_REQUEST);
            acceptMessageUnknownIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent acceptMessageUnknownPendingIntent = PendingIntent.getActivity(context, ConstantRegistry.REQUEST_CODE,
                    acceptMessageUnknownIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent declineMessageUnknownIntent = new Intent(context, ChatActivity.class);
            declineMessageUnknownIntent.putExtra(ConstantRegistry.DECLINE_MESSAGE_FROM_UNKNOWN_INTENT, offlineMessage);
            declineMessageUnknownIntent.setAction(ConstantRegistry.DECLINE_MESSAGE_FROM_UNKNOWN_REQUEST);
            declineMessageUnknownIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent declineMessageUnknownPendingIntent = PendingIntent.getActivity(context, ConstantRegistry.REQUEST_CODE,
                    declineMessageUnknownIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setSmallIcon(R.drawable.chatster_logo);
            mBuilder.setContentTitle(offlineMessage.getSenderName().concat(ConstantRegistry.CHATSTER_SPACE_STRING)
                    .concat(String.valueOf(offlineMessage.getSenderId())));
            mBuilder.setContentText(context.getString(R.string.not_in_your_contacts));
            mBuilder.setPriority(Notification.PRIORITY_MAX);
            mBuilder.setAutoCancel(true);
            mBuilder.setOnlyAlertOnce(true);
            mBuilder.setVibrate(new long[] {0, 200, 200,200 });
            mBuilder.setLights(Color.MAGENTA, 500, 500);
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            mBuilder.addAction(R.drawable.accept_64,context.getString(R.string.accept),acceptMessageUnknownPendingIntent);
            mBuilder.addAction(R.drawable.decline_64,context.getString(R.string.decline),declineMessageUnknownPendingIntent);
        }

        mNotificationManager.notify(notificationId, mBuilder.build());
    }

    public static void sendCreatorNotification(Context context, HistoryItem historyItem) {

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelId = context.getString(R.string.channel_creator);
        String channelName = context.getString(R.string.creator_channel);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context, channelId);

        Intent replyMessageIntent = new Intent(context, CreatorsActivity.class);
        replyMessageIntent.putExtra(ConstantRegistry.READ_HISTORY_ITEM_INTENT, historyItem);
        replyMessageIntent.setAction(ConstantRegistry.READ_HISTORY_ITEM_REQUEST);
        replyMessageIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent replyMessagePendingIntent = PendingIntent.getActivity(context, ConstantRegistry.READ_HISTORY_ITEM_PENDING_INTENT_REQUEST,
                replyMessageIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setSmallIcon(R.drawable.chatster_logo);
        mBuilder.setContentTitle(historyItem.getUserName());
        mBuilder.setContentText(historyItem.getDescription());
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setAutoCancel(true);
        mBuilder.setOnlyAlertOnce(true);
        mBuilder.setVibrate(new long[] {0, 200, 200,200 });
        mBuilder.setLights(Color.MAGENTA, 500, 500);
        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        mBuilder.addAction(R.drawable.views_64,context.getString(R.string.reply),replyMessagePendingIntent);

        mNotificationManager.notify(notificationId, mBuilder.build());
    }
}
