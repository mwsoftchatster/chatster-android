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
package nl.mwsoft.www.chatster.modelLayer.firebase.offlineMessageService;


import android.content.Context;

import androidx.annotation.NonNull;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.lang.reflect.Array;
import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.database.chat.ChatDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.database.groupChat.GroupChatDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.database.notification.NotificationDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.database.user.UserDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.helper.util.dateTime.ChatsterDateTimeUtil;
import nl.mwsoft.www.chatster.modelLayer.helper.util.notification.ChatsterNotificationUtil;
import nl.mwsoft.www.chatster.modelLayer.helper.util.internet.InternetConnectionUtil;
import nl.mwsoft.www.chatster.modelLayer.model.DeleteRetrievedMessagesResponse;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChatInvitation;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChatOfflineMessage;
import nl.mwsoft.www.chatster.modelLayer.model.OfflineContactResponse;
import nl.mwsoft.www.chatster.modelLayer.model.OfflineMessage;
import nl.mwsoft.www.chatster.modelLayer.model.OfflineMessageResponse;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.image.ImageProcessingManager;
import nl.mwsoft.www.chatster.modelLayer.network.NetworkLayer;

public class ChatsterFirebaseOfflineMessageService extends FirebaseMessagingService {

    private CompositeDisposable disposable;
    private Disposable subscribeGetOfflineMessages;
    private Disposable subscribeDeleteRetrievedMessages;
    private Disposable subscribeGetGroupChatInvitations;
    private NetworkLayer networkLayer;
    private ImageProcessingManager imageProcessingManager;
    private UserDatabaseLayer userDatabaseLayer;
    private GroupChatDatabaseLayer groupChatDatabaseLayer;
    private ChatDatabaseLayer chatDatabaseLayer;
    private NotificationDatabaseLayer notificationDatabaseLayer;
    private InternetConnectionUtil internetConnectionUtil;
    private ChatsterDateTimeUtil chatsterDateTimeUtil;

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
        try {
            String notificationData = remoteMessage.getData().toString();
            if (!notificationData.equals(null) && notificationData.length() > 0) {
                if (notificationData.equals(ConstantRegistry.OFFLINE_MESSAGE_NOTIFICATION)) {
                    getChatOfflineMessages();
                } else if (notificationData.equals(ConstantRegistry.GROUP_OFFLINE_MESSAGE_NOTIFICATION)) {
                    getGroupChatOfflineMessages();
                } else if (notificationData.equals(ConstantRegistry.GROUP_INVITATION_NOTIFICATION)) {
                    getGroupInvitations();
                }
            }
        } catch (NullPointerException e) {
            throw new RuntimeException("Firebase remote message error ==> " + e.getMessage());
        }
    }

    // endregion

    // region Configure Firebase Messaging Service

    public void configureWith(NetworkLayer networkLayer, ImageProcessingManager imageProcessingManager,
                              UserDatabaseLayer userDatabaseLayer, GroupChatDatabaseLayer groupChatDatabaseLayer,
                              ChatDatabaseLayer chatDatabaseLayer, NotificationDatabaseLayer notificationDatabaseLayer) {
        this.networkLayer = networkLayer;
        this.imageProcessingManager = imageProcessingManager;
        this.userDatabaseLayer = userDatabaseLayer;
        this.groupChatDatabaseLayer = groupChatDatabaseLayer;
        this.chatDatabaseLayer = chatDatabaseLayer;
        this.notificationDatabaseLayer = notificationDatabaseLayer;
    }

    // endregion

    // region Offline Messages

    private void getGroupChatOfflineMessages() {

        if (internetConnectionUtil.hasInternetConnection()) {
            if (combinedUUIDS() != null && combinedUUIDS().length > 0) {
                processRetrievedGroupMessages(combinedUUIDS(),
                        userDatabaseLayer.getUserId(ChatsterFirebaseOfflineMessageService.this));
            } else {
                getGroupOfflineMessages(userDatabaseLayer.getUserId(ChatsterFirebaseOfflineMessageService.this),
                        groupChatDatabaseLayer.getAllGroupChatIds(ChatsterFirebaseOfflineMessageService.this));
            }
        }
    }

    private void getChatOfflineMessages() {
        if (internetConnectionUtil.hasInternetConnection()) {
            if (combinedUUIDS() != null && combinedUUIDS().length > 0) {
                processRetrievedMessages(combinedUUIDS(),
                        userDatabaseLayer.getUserId(ChatsterFirebaseOfflineMessageService.this));
            } else {
                getOfflineMessages(userDatabaseLayer.getUserId(ChatsterFirebaseOfflineMessageService.this));
            }
        }
    }

    private <T> T[] joinArrays(T[]... arrays) {
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

    private String[] combinedUUIDS() {
        String[] offlineUUIDS = notificationDatabaseLayer.
                getRetrievedOfflineMessageUUIDs(ChatsterFirebaseOfflineMessageService.this);
        String[] onlineUUIDS = chatDatabaseLayer.getReceivedOnlineMessageUUIDs(ChatsterFirebaseOfflineMessageService.this);
        String[] onlineGroupUUIDS = groupChatDatabaseLayer.getReceivedOnlineGroupMessageUUIDs(ChatsterFirebaseOfflineMessageService.this);

        return joinArrays(offlineUUIDS, onlineUUIDS, onlineGroupUUIDS);
    }

    private void processRetrievedMessages(String[] uuids, long userId) {
        subscribeDeleteRetrievedMessages =
                networkLayer.deleteRetrievedMessages(uuids, userId)
                        .subscribeOn(Schedulers.io())
                        .subscribe(result -> {
                            if (result != null) {
                                boolean isSuccessful = true;
                                for (DeleteRetrievedMessagesResponse response : result) {
                                    if (!response.getReturnType().equals(ConstantRegistry.ERROR)) {
                                        if (response.getMsgType().equals(ConstantRegistry.CHATSTER_CHAT_MSG)) {
                                            handleOnlineChatMessage(response);
                                        } else if (response.getMsgType().equals(ConstantRegistry.CHATSTER_GROUP_CHAT_MSG)) {
                                            handleOnlineGroupChatMessage(response,
                                                    ChatsterFirebaseOfflineMessageService.this);
                                        }
                                    } else {
                                        isSuccessful = false;
                                    }
                                }
                                if (isSuccessful) {
                                    deleteProcessedUUIDS(uuids);
                                }
                            }
                            getOfflineMessages(userDatabaseLayer.getUserId(ChatsterFirebaseOfflineMessageService.this));
                        });
        disposable.add(subscribeDeleteRetrievedMessages);
    }

    private void processRetrievedGroupMessages(String[] uuids, long userId) {
        subscribeDeleteRetrievedMessages =
                networkLayer.deleteRetrievedGroupMessages(uuids, userId)
                        .subscribeOn(Schedulers.io())
                        .subscribe(result -> {
                            if (result != null) {
                                boolean isSuccessful = true;
                                for (DeleteRetrievedMessagesResponse response : result) {
                                    if (!response.getReturnType().equals(ConstantRegistry.ERROR)) {
                                        handleOnlineGroupChatMessage(response,
                                                ChatsterFirebaseOfflineMessageService.this);
                                    } else {
                                        isSuccessful = false;
                                    }
                                }
                                if (isSuccessful) {
                                    deleteProcessedUUIDS(uuids);
                                }
                            }
                            getGroupOfflineMessages(userDatabaseLayer.getUserId(ChatsterFirebaseOfflineMessageService.this),
                                    groupChatDatabaseLayer.getAllGroupChatIds(ChatsterFirebaseOfflineMessageService.this));
                        });
        disposable.add(subscribeDeleteRetrievedMessages);
    }

    private void deleteProcessedUUIDS(String[] uuids) {
        for (String uuid : uuids) {
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

        // insert message in local db
        notificationDatabaseLayer.insertOfflineMessage(message, imageProcessingManager,
                ChatsterFirebaseOfflineMessageService.this);

        // if it is an image change messageData to 'Sent you an image'
        if (offlineMessage.getContentType().equals(ConstantRegistry.IMAGE)) {
            message.setMessageData(ConstantRegistry.CHATSTER_IMAGE_MESSAGE);
        }

        // show the user notification
        ChatsterNotificationUtil.sendOfflineMessageNotification(ChatsterFirebaseOfflineMessageService.this,
                message);
    }

    private void handleOnlineGroupChatMessage(DeleteRetrievedMessagesResponse offlineMessage, Context context) {
        GroupChatOfflineMessage groupChatOfflineMessage = createGroupChatOnlineMessage(offlineMessage);

        // insert message uuid into local db for verification
        groupChatDatabaseLayer.insertReceivedOnlineGroupMessage(offlineMessage.getUuid(), context);

        // insert message in local db
        notificationDatabaseLayer.insertGroupMessage(groupChatOfflineMessage, imageProcessingManager, context);

        // if it is an image change messageData to 'Sent you an image'
        if (offlineMessage.getContentType().equals(ConstantRegistry.IMAGE)) {
            groupChatOfflineMessage.setGroupChatMessage(ConstantRegistry.CHATSTER_IMAGE_MESSAGE);
        }

        // show the user notification
        ChatsterNotificationUtil.sendGroupChatOfflineMessageNotification(
                ChatsterFirebaseOfflineMessageService.this,
                groupChatOfflineMessage);
    }

    private void getOfflineMessages(long userId) {
        subscribeGetOfflineMessages =
                networkLayer.getOfflineMessages(userId)
                        .subscribeOn(Schedulers.io())
                        .subscribe(result -> {
                            for (OfflineMessageResponse offlineMessage : result) {
                                if (offlineMessage.getMsgType().equals(ConstantRegistry.CHATSTER_CHAT_MSG)) {
                                    handleOfflineChatMessage(offlineMessage);
                                } else if (offlineMessage.getMsgType().equals(ConstantRegistry.CHATSTER_UNSEND_MESSAGE)) {
                                    handleOfflineUnsendChatMessage(offlineMessage,
                                            ChatsterFirebaseOfflineMessageService.this);
                                }
                            }
                            if (combinedUUIDS() != null && combinedUUIDS().length > 0) {
                                deleteRetrievedMessages(combinedUUIDS(),
                                        userDatabaseLayer.getUserId(ChatsterFirebaseOfflineMessageService.this));
                            }
                        });
        disposable.add(subscribeGetOfflineMessages);
    }

    private void getGroupOfflineMessages(long userId, ArrayList<String> groupChatIds) {
        subscribeGetOfflineMessages =
                networkLayer.getGroupOfflineMessages(userId, groupChatIds)
                        .subscribeOn(Schedulers.io())
                        .subscribe(result -> {
                            for (OfflineMessageResponse offlineMessage : result) {
                                if (offlineMessage.getMsgType().equals(ConstantRegistry.CHATSTER_GROUP_CHAT_MSG)) {
                                    handleOfflineGroupChatMessage(offlineMessage,
                                            ChatsterFirebaseOfflineMessageService.this);
                                } else if (offlineMessage.getMsgType().equals(ConstantRegistry.CHATSTER_UNSEND_MESSAGE_GROUP)) {
                                    handleOfflineUnsendGroupChatMessage(offlineMessage,
                                            ChatsterFirebaseOfflineMessageService.this);
                                }
                            }
                            if (combinedUUIDS() != null && combinedUUIDS().length > 0) {
                                deleteRetrievedGroupMessages(combinedUUIDS(),
                                        userDatabaseLayer.getUserId(ChatsterFirebaseOfflineMessageService.this));
                            }
                        });
        disposable.add(subscribeGetOfflineMessages);
    }

    private void deleteRetrievedMessages(String[] uuids, long userId) {
        subscribeDeleteRetrievedMessages =
                networkLayer.deleteRetrievedMessages(uuids, userId)
                        .subscribeOn(Schedulers.io())
                        .subscribe(result -> {
                            if (result != null) {
                                boolean isSuccessful = true;
                                for (DeleteRetrievedMessagesResponse response : result) {
                                    if (!response.getReturnType().equals(ConstantRegistry.ERROR)) {
                                        if (response.getMsgType().equals(ConstantRegistry.CHATSTER_CHAT_MSG)) {
                                            handleOnlineChatMessage(response);
                                        } else if (response.getMsgType().equals(ConstantRegistry.CHATSTER_GROUP_CHAT_MSG)) {
                                            handleOnlineGroupChatMessage(response,
                                                    ChatsterFirebaseOfflineMessageService.this);
                                        }
                                    } else {
                                        isSuccessful = false;
                                    }
                                }
                                if (isSuccessful) {
                                    deleteProcessedUUIDS(uuids);
                                }
                            }
                        });
        disposable.add(subscribeDeleteRetrievedMessages);
    }

    private void deleteRetrievedGroupMessages(String[] uuids, long userId) {
        subscribeDeleteRetrievedMessages =
                networkLayer.deleteRetrievedGroupMessages(uuids, userId)
                        .subscribeOn(Schedulers.io())
                        .subscribe(result -> {
                            if (result != null) {
                                boolean isSuccessful = true;
                                for (DeleteRetrievedMessagesResponse response : result) {
                                    if (!response.getReturnType().equals(ConstantRegistry.ERROR)) {
                                        if (response.getMsgType().equals(ConstantRegistry.CHATSTER_CHAT_MSG)) {
                                            handleOnlineChatMessage(response);
                                        } else if (response.getMsgType().equals(ConstantRegistry.CHATSTER_GROUP_CHAT_MSG)) {
                                            handleOnlineGroupChatMessage(response,
                                                    ChatsterFirebaseOfflineMessageService.this);
                                        }
                                    } else {
                                        isSuccessful = false;
                                    }
                                }
                                if (isSuccessful) {
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

    private void handleOfflineGroupChatMessage(OfflineMessageResponse offlineMessage, Context context) {
        if (groupChatDatabaseLayer.getGroupChatMessageByUUID(ChatsterFirebaseOfflineMessageService.this,
                offlineMessage.getUuid()) == null) {
            notificationDatabaseLayer.insertRetrievedOfflineMessageUUID(offlineMessage.getUuid(),
                    ChatsterFirebaseOfflineMessageService.this);

            GroupChatOfflineMessage groupChatOfflineMessage = createGroupChatOfflineMessage(offlineMessage);

            // insert message in local db
            notificationDatabaseLayer.insertGroupMessage(groupChatOfflineMessage, imageProcessingManager, context);

            // if it is an image change messageData to 'Sent you an image'
            if (offlineMessage.getContentType().equals(ConstantRegistry.IMAGE)) {
                groupChatOfflineMessage.setGroupChatMessage(ConstantRegistry.CHATSTER_IMAGE_MESSAGE);
            }

            // show the user notification
            ChatsterNotificationUtil.sendGroupChatOfflineMessageNotification(
                    ChatsterFirebaseOfflineMessageService.this,
                    groupChatOfflineMessage
            );
        }
    }

    private void handleOfflineChatMessage(OfflineMessageResponse offlineMessage) {
        if (chatDatabaseLayer.getChatMessageByUUID(ChatsterFirebaseOfflineMessageService.this,
                offlineMessage.getUuid()) == null) {
            notificationDatabaseLayer.insertRetrievedOfflineMessageUUID(offlineMessage.getUuid(),
                    ChatsterFirebaseOfflineMessageService.this);

            OfflineMessage message = createOfflineChatMessage(offlineMessage);

            // insert message in local db
            notificationDatabaseLayer.insertOfflineMessage(message,
                    imageProcessingManager,
                    ChatsterFirebaseOfflineMessageService.this);

            // if it is an image change messageData to 'Sent you an image'
            if (offlineMessage.getContentType().equals(ConstantRegistry.IMAGE)) {
                message.setMessageData(ConstantRegistry.CHATSTER_IMAGE_MESSAGE);
            }

            // show the user notification
            ChatsterNotificationUtil.sendOfflineMessageNotification(ChatsterFirebaseOfflineMessageService.this,
                    message);
        }
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
        if (internetConnectionUtil.hasInternetConnection()) {
            getGroupChatInvitations(userDatabaseLayer.getUserId(ChatsterFirebaseOfflineMessageService.this));
        }
    }

    private void getGroupChatInvitations(long userId) {
        subscribeGetGroupChatInvitations = networkLayer.getGroupChatInvitations(userId)
                .subscribeOn(Schedulers.io())
                .subscribe(result -> {
                    processGetGroupChatInvitationsResult(result);
                });
        disposable.add(subscribeGetGroupChatInvitations);
    }

    private void processGetGroupChatInvitationsResult(ArrayList<OfflineContactResponse> result) {
        for (OfflineContactResponse contactMessage : result) {
            if (contactMessage.getMsgType().equals(ConstantRegistry.CHATSTER_GROUP_CHAT_INVITATION_MSG)) {
                GroupChatInvitation groupChatInvitation = createGroupChatInvitation(contactMessage);
                if (senderIsNotUser(contactMessage)) {
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
        GroupChatInvitation groupChatInvitation = new GroupChatInvitation();
        groupChatInvitation.setGroupChatInvitationChatId(contactMessage.getGroupChatInvitationChatId());
        groupChatInvitation.setGroupChatInvitationSenderId(contactMessage.getGroupChatInvitationSenderId());
        for (long memberId : contactMessage.getGroupChatInvitationGroupChatMembers()) {
            groupChatInvitation.addGroupChatMember(memberId);
        }
        groupChatInvitation.setGroupChatInvitationChatName(contactMessage.getGroupChatInvitationChatName());
        groupChatInvitation.setGroupProfilePicPath(contactMessage.getGroupProfilePicPath());
        return groupChatInvitation;
    }

    // endregion
}
