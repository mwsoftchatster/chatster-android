package nl.mwsoft.www.chatster.modelLayer.modelLayerManager.notification;


import android.content.Context;

import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.modelLayer.database.notification.NotificationDatabaseLayer;

public class NotificationModelLayerManager {

    private NotificationDatabaseLayer notificationDatabaseLayer;

    // region Notification DB

    public NotificationModelLayerManager() {
        this.notificationDatabaseLayer = DependencyRegistry.shared.createNotificationDatabaseLayer();
    }

    public void deleteGroupChatInvitationNotification(String groupChatId, Context context){
        this.notificationDatabaseLayer.deleteGroupChatInvitationNotification(groupChatId, context);
    }

    public void updateMessageUnsentByMessageUUID(String uuid, Context context){
        this.notificationDatabaseLayer.updateMessageUnsentByMessageUUID(uuid, context);
    }

    // endregion
}
