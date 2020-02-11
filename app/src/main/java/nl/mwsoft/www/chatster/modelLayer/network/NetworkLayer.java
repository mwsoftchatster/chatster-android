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
package nl.mwsoft.www.chatster.modelLayer.network;


import android.content.Context;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.database.contact.ContactDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.database.user.UserDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.model.ConfirmPhoneResponse;
import nl.mwsoft.www.chatster.modelLayer.model.ContactLatestInformation;
import nl.mwsoft.www.chatster.modelLayer.model.CreateGroupChatRequest;
import nl.mwsoft.www.chatster.modelLayer.model.DeleteRetrievedMessagesResponse;
import nl.mwsoft.www.chatster.modelLayer.model.ExitGroupChatReq;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChat;
import nl.mwsoft.www.chatster.modelLayer.model.OfflineContactResponse;
import nl.mwsoft.www.chatster.modelLayer.model.OfflineMessageResponse;
import nl.mwsoft.www.chatster.modelLayer.model.RegisterUserResponse;
import nl.mwsoft.www.chatster.modelLayer.model.ResendGroupMessageResponse;
import nl.mwsoft.www.chatster.modelLayer.model.ResendMessageResponse;
import nl.mwsoft.www.chatster.modelLayer.network.addNewMembersRequest.AddNewMembersToGroupRequestImpl;
import nl.mwsoft.www.chatster.modelLayer.network.confirmPhone.PhoneConfirmationImpl;
import nl.mwsoft.www.chatster.modelLayer.network.contactLatest.ContactLatestRequestImpl;
import nl.mwsoft.www.chatster.modelLayer.network.deleteGroupChatInvitation.DeleteGroupChatInvitationImpl;
import nl.mwsoft.www.chatster.modelLayer.network.invite.InviteRequestImpl;
import nl.mwsoft.www.chatster.modelLayer.network.groupChatInvitation.GroupChatInvitationImpl;
import nl.mwsoft.www.chatster.modelLayer.network.createGroupChatRequest.CreateGroupChatRequestImpl;
import nl.mwsoft.www.chatster.modelLayer.network.deleteRetrievedOfflineMessages.DeleteRetrievedMessagesImpl;
import nl.mwsoft.www.chatster.modelLayer.network.exitGroupChat.ExitGroupChatRequestImpl;
import nl.mwsoft.www.chatster.modelLayer.network.messageQueue.chat.ResendMessageRequestImpl;
import nl.mwsoft.www.chatster.modelLayer.network.messageQueue.groupChat.ResendGroupMessageRequestImpl;
import nl.mwsoft.www.chatster.modelLayer.network.offlineMessagePoll.OfflineMessagePollImpl;
import nl.mwsoft.www.chatster.modelLayer.network.registerUserRequest.RegisterUserRequestImpl;
import nl.mwsoft.www.chatster.modelLayer.network.updateToken.UpdateTokenImpl;
import okhttp3.MultipartBody;

public class NetworkLayer {

    private ContactDatabaseLayer contactDatabaseLayer;
    private UserDatabaseLayer userDatabaseLayer;

    public NetworkLayer() {
        this.contactDatabaseLayer = DependencyRegistry.shared.createContactDatabaseLayer();
        this.userDatabaseLayer = DependencyRegistry.shared.createUserDatabaseLayer();
    }

    // region Add New Group Member

    public Observable<String> addNewGroupMember(String chatId, long groupChatAdminId, String groupChatName,
                                                ArrayList<Long> newGroupChatMembers, String groupChatPicPath){
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                try {
                    AddNewMembersToGroupRequestImpl addNewMembersToGroupRequest = new AddNewMembersToGroupRequestImpl();
                    String response = addNewMembersToGroupRequest.getAddNewMembersToGroupResponse(
                            chatId,
                            groupChatAdminId,
                            groupChatName,
                            newGroupChatMembers,
                            groupChatPicPath);
                    emitter.onNext(response);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    // endregion

    // region Confirm Phone Number

    public Observable<ConfirmPhoneResponse> confirmPhoneNumber(String phoneToVerify, String messagingToken, ArrayList<Long> contacts, Context context){
        return Observable.create(new ObservableOnSubscribe<ConfirmPhoneResponse>() {
            @Override
            public void subscribe(ObservableEmitter<ConfirmPhoneResponse> emitter) throws Exception {
                try {
                    ConfirmPhoneResponse result;
                    PhoneConfirmationImpl phoneConfirmation = new PhoneConfirmationImpl();
                    result = phoneConfirmation.getPhoneConfirmationCode(phoneToVerify, messagingToken, contacts);
                    emitter.onNext(result);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    // endregion

    // region Create Group Chat

    public Observable<GroupChat> createGroupChat(CreateGroupChatRequest createGroupChatRequest) {
        return Observable.create(new ObservableOnSubscribe<GroupChat>() {
            @Override
            public void subscribe(ObservableEmitter<GroupChat> emitter) throws Exception {
                try {
                    GroupChat groupChat = new GroupChat();
                    CreateGroupChatRequestImpl createChatGroup = new CreateGroupChatRequestImpl();
                    groupChat = createChatGroup.getCreateGroupChatResponse(createGroupChatRequest.getAdminId(),
                            createGroupChatRequest.getGroupChatId(),
                            createGroupChatRequest.getGroupChatName(),
                            createGroupChatRequest.getInvitedGroupChatMembers());
                    emitter.onNext(groupChat);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    // endregion

    // region Exit Group Chat

    public Observable<ExitGroupChatReq> exitGroupChat(ExitGroupChatReq exitGroupChatReq, Context context){
        return Observable.create(new ObservableOnSubscribe<ExitGroupChatReq>() {
            @Override
            public void subscribe(ObservableEmitter<ExitGroupChatReq> emitter) throws Exception {
                try {
                    String result = "";
                    ExitGroupChatRequestImpl exitGroupChat = new ExitGroupChatRequestImpl();
                    result = exitGroupChat.getExitGroupChatResponse(exitGroupChatReq.getGroupChatId(),
                            exitGroupChatReq.getUserId());

                    if(result.equals(context.getString(R.string.left_group))){
                        emitter.onNext(exitGroupChatReq);
                    }else{
                        ExitGroupChatReq notExited = new ExitGroupChatReq(null,0);
                        emitter.onNext(notExited);
                    }
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    // endregion

    // region Register User

    public Observable<String> registerUser(Context context, long userId, String userName,
                                           String  userStatusMessage,String  messagingToken, ArrayList<Long> myContactIds){
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                try {
                    String result = "";
                    RegisterUserRequestImpl registerUser = new RegisterUserRequestImpl();
                    RegisterUserResponse registerUserResponse;
                    registerUserResponse = registerUser.getRegisterUserResponse(
                            userId,
                            userName, userStatusMessage, messagingToken, myContactIds);
                    if(!registerUserResponse.status.equals(ConstantRegistry.ERROR)){
                        if(registerUserResponse.isUserAlreadyExists()){
                            result = context.getString(R.string.username_already_exists);
                        }else{
                            result = context.getString(R.string.success_register);
                        }
                        userDatabaseLayer.updateUser(registerUserResponse, context);
                        if(registerUserResponse.getChatsterContacts() != null && registerUserResponse.getChatsterContacts().size() > 0){
                            contactDatabaseLayer.updateContacts(registerUserResponse.getChatsterContacts(), context);
                        }
                    }else{
                        result = context.getString(R.string.smth_went_wrong);
                    }
                    emitter.onNext(result);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    public Observable<String> updateUserToken(long userId, String  messagingToken){
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                try {
                    String result = "";
                    UpdateTokenImpl updateToken = new UpdateTokenImpl();
                    result = updateToken.getUpdateUserTokenResponse(userId, messagingToken);
                    emitter.onNext(result);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    // endregion

    // region Group Chat Invitations

    public Observable<ArrayList<OfflineContactResponse>> getGroupChatInvitations(long userId){
        return Observable.create(new ObservableOnSubscribe<ArrayList<OfflineContactResponse>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<OfflineContactResponse>> emitter) throws Exception {
                try {
                    // fetch them and pass them to the onPostExecute
                    // and it will generate notification/notifications
                    GroupChatInvitationImpl groupChatInvitation = new GroupChatInvitationImpl();
                    ArrayList<OfflineContactResponse> groupChatInvitations = new ArrayList<>();
                    groupChatInvitations = groupChatInvitation.getGroupChatInvitations(userId);
                    emitter.onNext(groupChatInvitations);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    // endregion

    // region Message Notifications

    public Observable<ArrayList<OfflineMessageResponse>> getOfflineMessages(long userId){
        return Observable.create(new ObservableOnSubscribe<ArrayList<OfflineMessageResponse>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<OfflineMessageResponse>> emitter) throws Exception {
                try {
                    ArrayList<OfflineMessageResponse> offlineMessages = new ArrayList<>();
                    OfflineMessagePollImpl offlineMessagePollImpl = new OfflineMessagePollImpl();
                    offlineMessages = offlineMessagePollImpl.getOfflineMessages(userId);
                    emitter.onNext(offlineMessages);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }


    public Observable<ArrayList<OfflineMessageResponse>> getGroupOfflineMessages(long userId, ArrayList<String> groupChatIds){
        return Observable.create(new ObservableOnSubscribe<ArrayList<OfflineMessageResponse>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<OfflineMessageResponse>> emitter) throws Exception {
                try {
                    ArrayList<OfflineMessageResponse> offlineMessages = new ArrayList<>();
                    OfflineMessagePollImpl offlineMessagePollImpl = new OfflineMessagePollImpl();
                    offlineMessages = offlineMessagePollImpl.getGroupOfflineMessages(userId, groupChatIds);
                    emitter.onNext(offlineMessages);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    // endregion

    // region Contact Latest Updates

    public Observable<ArrayList<ContactLatestInformation>> getContactLatest(ArrayList<Long> allContactIds){
        return Observable.create(new ObservableOnSubscribe<ArrayList<ContactLatestInformation>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<ContactLatestInformation>> emitter) throws Exception {
                try {
                    ContactLatestRequestImpl contactLatestRequest = new ContactLatestRequestImpl();
                    ArrayList<ContactLatestInformation> response;
                    response = contactLatestRequest.getContactLatestInformation(allContactIds);
                    if(response == null){
                        response = new ArrayList<>();
                        emitter.onNext(response);
                    }else{
                        emitter.onNext(response);
                    }
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    // endregion

    // region Message Queue Resend Chat/Group Chat Message Response

    public Observable<ResendMessageResponse> getResendChatMessageResponse(String message, long senderId, String senderName,
                                                                          long receiverId, String chatName, String uuid,
                                                                          String contentType){
        return Observable.create(new ObservableOnSubscribe<ResendMessageResponse>() {
            @Override
            public void subscribe(ObservableEmitter<ResendMessageResponse> emitter) throws Exception {
                try {
                    ResendMessageResponse resendMessageResponse = new ResendMessageResponse();
                    ResendMessageRequestImpl resendMessageRequest = new ResendMessageRequestImpl();
                    resendMessageResponse = resendMessageRequest.getResendMessageResponse(message, senderId,
                    senderName, receiverId, chatName, uuid, contentType);
                    if(resendMessageResponse == null){
                        emitter.onNext(new ResendMessageResponse(ConstantRegistry.NULL));
                    }else{
                        emitter.onNext(resendMessageResponse);
                    }
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    public Observable<ResendMessageResponse> getResendChatImageMessageResponse(MultipartBody.Part body, long senderId, String senderName,
                                                                               long receiverId, String chatName, String uuid,
                                                                               String contentType){
        return Observable.create(new ObservableOnSubscribe<ResendMessageResponse>() {
            @Override
            public void subscribe(ObservableEmitter<ResendMessageResponse> emitter) throws Exception {
                try {
                    ResendMessageResponse resendMessageResponse = new ResendMessageResponse();
                    ResendMessageRequestImpl resendMessageRequest = new ResendMessageRequestImpl();
                    resendMessageResponse = resendMessageRequest.getResendImageMessageResponse(body, senderId,
                    senderName, receiverId, chatName, uuid, contentType);
                    if(resendMessageResponse == null){
                        emitter.onNext(new ResendMessageResponse(ConstantRegistry.NULL));
                    }else{
                        emitter.onNext(resendMessageResponse);
                    }
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    public Observable<ResendGroupMessageResponse> getResendGroupChatMessageResponse(String messages){
        return Observable.create(new ObservableOnSubscribe<ResendGroupMessageResponse>() {
            @Override
            public void subscribe(ObservableEmitter<ResendGroupMessageResponse> emitter) throws Exception {
                try {
                    ResendGroupMessageResponse resendGroupMessageResponse = new ResendGroupMessageResponse();
                    ResendGroupMessageRequestImpl resendGroupMessageRequest = new ResendGroupMessageRequestImpl();
                    resendGroupMessageResponse = resendGroupMessageRequest.getResendGroupMessageResponse(messages);
                    if(resendGroupMessageResponse == null){
                        emitter.onNext(new ResendGroupMessageResponse(ConstantRegistry.NULL));
                    }else{
                        emitter.onNext(resendGroupMessageResponse);
                    }
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    public Observable<ResendGroupMessageResponse> getResendGroupChatImageMessageResponse(MultipartBody.Part body, long senderId,
                                                                                String uuid, String groupChatId,
                                                                                String contentType){
        return Observable.create(new ObservableOnSubscribe<ResendGroupMessageResponse>() {
            @Override
            public void subscribe(ObservableEmitter<ResendGroupMessageResponse> emitter) throws Exception {
                try {
                    ResendGroupMessageResponse resendGroupMessageResponse = new ResendGroupMessageResponse();
                    ResendGroupMessageRequestImpl resendGroupMessageRequest = new ResendGroupMessageRequestImpl();
                    resendGroupMessageResponse = resendGroupMessageRequest.getResendGroupImageMessageResponse(body,
                            senderId, uuid, groupChatId, contentType);
                    if(resendGroupMessageResponse == null){
                        emitter.onNext(new ResendGroupMessageResponse(ConstantRegistry.NULL));
                    }else{
                        emitter.onNext(resendGroupMessageResponse);
                    }
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    // endregion

    // region Delete Retrieved Messages

    public Observable<ArrayList<DeleteRetrievedMessagesResponse>> deleteRetrievedMessages(String[] uuids, long userId){
        return Observable.create(new ObservableOnSubscribe<ArrayList<DeleteRetrievedMessagesResponse>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<DeleteRetrievedMessagesResponse>> emitter) throws Exception {
                try {
                    ArrayList<DeleteRetrievedMessagesResponse> result = new ArrayList<>();
                    DeleteRetrievedMessagesImpl deleteRetrievedOfflineMessages = new DeleteRetrievedMessagesImpl();
                    result = deleteRetrievedOfflineMessages.deleteRetrievedOfflineMessages(uuids, userId);
                    emitter.onNext(result);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    public Observable<ArrayList<DeleteRetrievedMessagesResponse>> deleteRetrievedGroupMessages(String[] uuids, long userId){
        return Observable.create(new ObservableOnSubscribe<ArrayList<DeleteRetrievedMessagesResponse>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<DeleteRetrievedMessagesResponse>> emitter) throws Exception {
                try {
                    ArrayList<DeleteRetrievedMessagesResponse> result = new ArrayList<>();
                    DeleteRetrievedMessagesImpl deleteRetrievedOfflineMessages = new DeleteRetrievedMessagesImpl();
                    result = deleteRetrievedOfflineMessages.deleteRetrievedGroupOfflineMessages(uuids, userId);
                    emitter.onNext(result);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    // endregion

    // region Invite User To Join Chatster

    public Observable<String> inviteUser(String userName, String inviteeName, String inviteeEmail){
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                try {
                    InviteRequestImpl inviteRequest = new InviteRequestImpl();
                    String response = inviteRequest.getInviteUserResponse(userName, inviteeName, inviteeEmail);
                    emitter.onNext(response);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    // endregion

    public Observable<String> deleteGroupChatInvitation(String groupChatId, long userId){
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                try {
                    String result = "";
                    DeleteGroupChatInvitationImpl deleteGroupChatInvitation = new DeleteGroupChatInvitationImpl();
                    result = deleteGroupChatInvitation.deleteGroupChatInvitation(groupChatId,userId);
                    emitter.onNext(result);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

}
