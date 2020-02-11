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
package nl.mwsoft.www.chatster.modelLayer.service.messageQueue;


import android.app.job.JobParameters;
import android.app.job.JobService;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;

import io.fabric.sdk.android.Fabric;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.database.chat.ChatDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.database.groupChat.GroupChatDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.database.user.UserDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.helper.util.jobService.ChatsterJobServiceUtil;
import nl.mwsoft.www.chatster.modelLayer.helper.util.internet.InternetConnectionUtil;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChatMessage;
import nl.mwsoft.www.chatster.modelLayer.model.GroupMessageQueueItem;
import nl.mwsoft.www.chatster.modelLayer.model.Message;
import nl.mwsoft.www.chatster.modelLayer.model.MessageQueueItem;
import nl.mwsoft.www.chatster.modelLayer.network.NetworkLayer;
import okhttp3.MultipartBody;

public class ChatsterMessageQueueJobService extends JobService {

    private CompositeDisposable disposable;
    private Disposable subscribeGetResendChatMessageResponse;
    private Disposable subscribeGetResendGroupChatMessageResponse;
    private NetworkLayer networkLayer;
    private UserDatabaseLayer userDatabaseLayer;
    private GroupChatDatabaseLayer groupChatDatabaseLayer;
    private ChatDatabaseLayer chatDatabaseLayer;
    private JobParameters mParams;
    private ChatsterJobServiceUtil chatsterJobServiceUtil;
    private InternetConnectionUtil internetConnectionUtil;

    public ChatsterMessageQueueJobService() {
    }

    public void configureWith(NetworkLayer networkLayer, UserDatabaseLayer userDatabaseLayer,
                              ChatDatabaseLayer chatDatabaseLayer, GroupChatDatabaseLayer groupChatDatabaseLayer,
                              ChatsterJobServiceUtil chatsterJobServiceUtil, InternetConnectionUtil internetConnectionUtil) {
        this.networkLayer = networkLayer;
        this.userDatabaseLayer = userDatabaseLayer;
        this.chatDatabaseLayer = chatDatabaseLayer;
        this.groupChatDatabaseLayer = groupChatDatabaseLayer;
        this.chatsterJobServiceUtil = chatsterJobServiceUtil;
        this.internetConnectionUtil = internetConnectionUtil;
    }


    @Override
    public boolean onStartJob(JobParameters params) {
        mParams = params;
        disposable = new CompositeDisposable();
        Fabric.with(this, new Crashlytics());
        DependencyRegistry.shared.inject(this);

        if (internetConnectionUtil.hasInternetConnection()) {
            processChatMessageQueue();
        }

        return true;
    }

    private void processGroupChatMessageQueue() {
        GroupMessageQueueItem groupMessageQueueItem =
                groupChatDatabaseLayer.getGroupMessageQueueItemGroupChat(ChatsterMessageQueueJobService.this);
        if (groupMessageQueueItem != null) {
            GroupChatMessage groupChatMessage = groupChatDatabaseLayer.getGroupChatMessageByUUID(
                    ChatsterMessageQueueJobService.this,
                    groupMessageQueueItem.getMessageUUID());
            if (groupChatMessage != null) {
                switch (groupChatMessage.getMsgType()) {
                    case ConstantRegistry.TEXT:
                        sendTextMessage(
                                groupMessageQueueItem.getGroupChatId(),
                                userDatabaseLayer.getUserId(ChatsterMessageQueueJobService.this),
                                groupChatMessage
                        );
                        break;
//                    case ConstantRegistry.IMAGE:
//                        try {
//                            File file = createFileFromBytes(getBytesFromUri(groupChatMessage.getBinaryMessageFilePath()));
//                            if(file != null){
//                                RequestBody reqFile =
//                                        RequestBody.create(MediaType.parse(ConstantRegistry.CHATSTER_DOCUMENT_TYPE_IMAGE),
//                                                file);
//                                MultipartBody.Part body =
//                                        MultipartBody.Part.createFormData(ConstantRegistry.IMAGE, file.getName(), reqFile);
//                                getResendGroupChatImageMessageResponse(
//                                        body,
//                                        userDatabaseLayer.getUserId(ChatsterMessageQueueJobService.this),
//                                        groupChatMessage.getUuid(),
//                                        groupChatMessage.getGroupChatId(),
//                                        groupChatMessage.getMsgType());
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//                        break;
                }
            } else {
                handleJobFinished();
            }
        } else {
            handleJobFinished();
        }
    }

    private void handleJobFinished() {
        jobFinished(mParams, false);
        chatsterJobServiceUtil
                .startChatsterMessageQueueJobService(ChatsterMessageQueueJobService.this);
    }

    private void processChatMessageQueue() {
        MessageQueueItem messageQueueItem = chatDatabaseLayer.getMessageQueueItemChat(ChatsterMessageQueueJobService.this);
        if (messageQueueItem != null) {
            Message message = chatDatabaseLayer.getChatMessageByUUID(
                    ChatsterMessageQueueJobService.this,
                    messageQueueItem.getMessageUUID()
            );

            if (message != null) {
                switch (message.getMsgType()) {
                    case ConstantRegistry.TEXT:
                        getResendChatMessageResponse(
                                message.getMessageText(),
                                userDatabaseLayer.getUserId(ChatsterMessageQueueJobService.this),
                                userDatabaseLayer.getUserName(ChatsterMessageQueueJobService.this),
                                messageQueueItem.getReceiverId(),
                                chatDatabaseLayer.getChatById(ChatsterMessageQueueJobService.this, message.getMessageChatId()).getChatName(),
                                message.getMessageUUID(),
                                message.getMsgType()
                        );

                        break;
                    case ConstantRegistry.IMAGE:
//                        try {
//                            File file = createFileFromBytes(getBytesFromUri(message.getBinaryMessageFilePath()));
//                            if(file != null){
//                                RequestBody reqFile =
//                                        RequestBody.create(MediaType.parse(ConstantRegistry.CHATSTER_DOCUMENT_TYPE_IMAGE),
//                                                file);
//                                MultipartBody.Part body =
//                                        MultipartBody.Part.createFormData(ConstantRegistry.IMAGE, file.getName(), reqFile);
//                                getResendChatImageMessageResponse(
//                                        body,
//                                        userDatabaseLayer.getUserId(ChatsterMessageQueueJobService.this),
//                                        userDatabaseLayer.getUserName(ChatsterMessageQueueJobService.this),
//                                        messageQueueItem.getReceiverId(),
//                                        chatDatabaseLayer.getChatById(ChatsterMessageQueueJobService.this, message.getMessageChatId()).getChatName(),
//                                        message.getMessageUUID(),
//                                        message.getMsgType());
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                        break;
                }
            } else {
                processGroupChatMessageQueue();
            }
        } else {
            processGroupChatMessageQueue();
        }
    }

    private byte[] getBytesFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] b = baos.toByteArray();
        parcelFileDescriptor.close();

        return b;
    }

    private File createFileFromBytes(byte[] bytes) throws IOException {
        File f = new File(
                Environment.getExternalStorageDirectory()
                        + File.separator
                        + ConstantRegistry.CHATSTER_TEMP_IMAGE_NAME
        );

        //write the bytes in file
        FileOutputStream fos = new FileOutputStream(f);
        try {
            fos.write(bytes);
            fos.flush();
            fos.close();

            return f;
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }

        return true;
    }

    private void getResendChatMessageResponse(String message, long senderId, String senderName,
                                              long receiverId, String chatName, String uuid,
                                              String contentType) {
        subscribeGetResendChatMessageResponse =
                networkLayer.
                        getResendChatMessageResponse(
                                message,
                                senderId,
                                senderName,
                                receiverId,
                                chatName,
                                uuid,
                                contentType)
                        .subscribeOn(Schedulers.io())
                        .subscribe(result -> {
                            if (result.getResponse() == null) {
                                return;
                            }

                            if (result.getResponse().equals(ConstantRegistry.NULL)) {
                                return;
                            }

                            if (result.getResponse().equals(ConstantRegistry.SUCCESS)) {
                                chatDatabaseLayer.deleteChatMessageQueueItemByUUID(
                                        result.getUuid(),
                                        ChatsterMessageQueueJobService.this
                                );
                            }

                            processGroupChatMessageQueue();
                        }, Throwable::printStackTrace);
        disposable.add(subscribeGetResendChatMessageResponse);
    }

    private void getResendChatImageMessageResponse(MultipartBody.Part body, long senderId, String senderName,
                                                   long receiverId, String chatName, String uuid,
                                                   String contentType) {
        subscribeGetResendChatMessageResponse =
                networkLayer.getResendChatImageMessageResponse(body, senderId,
                        senderName, receiverId, chatName, uuid, contentType)
                        .subscribeOn(Schedulers.io())
                        .subscribe(result -> {
                            if (result.getResponse() == null) {
                                return;
                            }

                            if (result.getResponse().equals(ConstantRegistry.NULL)) {
                                return;
                            }

                            if (result.getResponse().equals(ConstantRegistry.SUCCESS)) {
                                chatDatabaseLayer.deleteChatMessageQueueItemByUUID(
                                        result.getUuid(),
                                        ChatsterMessageQueueJobService.this
                                );
                            }

                            processGroupChatMessageQueue();
                        }, Throwable::printStackTrace);
        disposable.add(subscribeGetResendChatMessageResponse);
    }

    private void getResendGroupChatMessageResponse(String messages) {
        subscribeGetResendGroupChatMessageResponse =
                networkLayer.getResendGroupChatMessageResponse(messages)
                        .subscribeOn(Schedulers.io())
                        .subscribe(result -> {
                            if (result.getResponse() == null) {
                                return;
                            }

                            if (result.getResponse().equals(ConstantRegistry.NULL)) {
                                return;
                            }

                            if (result.getResponse().equals(ConstantRegistry.SUCCESS)) {
                                groupChatDatabaseLayer.deleteGroupChatMessageQueueItemByUUID(
                                        result.getUuid(),
                                        ChatsterMessageQueueJobService.this
                                );
                            }

                            handleJobFinished();
                        }, Throwable::printStackTrace);
        disposable.add(subscribeGetResendGroupChatMessageResponse);
    }

    // TO-DO: this needs refactoring, as now without the E2E logic changes for sending group chat messages.
    private void sendTextMessage(String groupChatId, long userId, GroupChatMessage groupChatMessage) {
        JsonArray groupMessages = new JsonArray();

        Gson gson = new Gson();
        JsonElement msg_type = gson.toJsonTree(ConstantRegistry.CHATSTER_GROUP_CHAT_MSG);
        JsonElement content_type = gson.toJsonTree(ConstantRegistry.TEXT);
        JsonElement sender_id = gson.toJsonTree(String.valueOf(userDatabaseLayer.getUserId(ChatsterMessageQueueJobService.this)));
        JsonElement receiver_id = gson.toJsonTree(groupChatMessage.getGroupChatId());
        JsonElement group_chat_id = gson.toJsonTree(groupChatId);
        JsonElement message = gson.toJsonTree(groupChatMessage.getMessageText());
        JsonElement message_uuid = gson.toJsonTree(groupChatMessage.getUuid());
        JsonElement item_created = gson.toJsonTree("");
        JsonObject groupMessage = new JsonObject();
        groupMessage.add("msg_type", msg_type);
        groupMessage.add("content_type", content_type);
        groupMessage.add("sender_id", sender_id);
        groupMessage.add("receiver_id", receiver_id);
        groupMessage.add("group_chat_id", group_chat_id);
        groupMessage.add("message", message);
        groupMessage.add("message_uuid", message_uuid);
        groupMessage.add("item_created", item_created);
        groupMessages.add(groupMessage);

        JsonObject objMain = new JsonObject();
        objMain.add("groupChatOfflineMessages", groupMessages);

        if (internetConnectionUtil.hasInternetConnection()) {
            getResendGroupChatMessageResponse(objMain.toString());
        }
    }

    private void getResendGroupChatImageMessageResponse(MultipartBody.Part body, long senderId,
                                                        String uuid, String groupChatId,
                                                        String contentType) {
        subscribeGetResendGroupChatMessageResponse =
                networkLayer.
                        getResendGroupChatImageMessageResponse(
                                body,
                                senderId,
                                uuid,
                                groupChatId,
                                contentType
                        )
                        .subscribeOn(Schedulers.io())
                        .subscribe(result -> {
                            if (result.getResponse() == null) {
                                return;
                            }

                            if (result.getResponse().equals(ConstantRegistry.NULL)) {
                                return;
                            }

                            if (result.getResponse().equals(ConstantRegistry.SUCCESS)) {
                                groupChatDatabaseLayer.deleteGroupChatMessageQueueItemByUUID(
                                        result.getUuid(),
                                        ChatsterMessageQueueJobService.this
                                );
                            }

                            handleJobFinished();
                        }, Throwable::printStackTrace);
        disposable.add(subscribeGetResendGroupChatMessageResponse);
    }
}
