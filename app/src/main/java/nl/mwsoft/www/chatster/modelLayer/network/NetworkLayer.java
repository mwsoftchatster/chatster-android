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
import nl.mwsoft.www.chatster.modelLayer.model.CreatorContact;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorPost;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorPostComment;
import nl.mwsoft.www.chatster.modelLayer.model.DeleteRetrievedMessagesResponse;
import nl.mwsoft.www.chatster.modelLayer.model.ExitGroupChatReq;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChat;
import nl.mwsoft.www.chatster.modelLayer.model.HistoryItem;
import nl.mwsoft.www.chatster.modelLayer.model.OfflineContactResponse;
import nl.mwsoft.www.chatster.modelLayer.model.OfflineMessageResponse;
import nl.mwsoft.www.chatster.modelLayer.model.OneTimeGroupPublicKey;
import nl.mwsoft.www.chatster.modelLayer.model.OneTimePublicKey;
import nl.mwsoft.www.chatster.modelLayer.model.RegisterUserResponse;
import nl.mwsoft.www.chatster.modelLayer.model.ResendGroupMessageResponse;
import nl.mwsoft.www.chatster.modelLayer.model.ResendMessageResponse;
import nl.mwsoft.www.chatster.modelLayer.network.addNewMembersRequest.AddNewMembersToGroupRequestImpl;
import nl.mwsoft.www.chatster.modelLayer.network.checkIfGroupKeysNeeded.CheckIfGroupKeysNeededImpl;
import nl.mwsoft.www.chatster.modelLayer.network.checkPublicKeys.CheckPublicKeysImpl;
import nl.mwsoft.www.chatster.modelLayer.network.confirmPhone.PhoneConfirmationImpl;
import nl.mwsoft.www.chatster.modelLayer.network.contactLatest.ContactLatestRequestImpl;
import nl.mwsoft.www.chatster.modelLayer.network.creatorFollowers.CreatorFollowersRequestImpl;
import nl.mwsoft.www.chatster.modelLayer.network.creatorFollowing.CreatorFollowingRequestImpl;
import nl.mwsoft.www.chatster.modelLayer.network.creatorHistory.CreatorHistoryRequestImpl;
import nl.mwsoft.www.chatster.modelLayer.network.creatorPostCommentsRequest.CreatorPostCommentsRequestImpl;
import nl.mwsoft.www.chatster.modelLayer.network.creatorProfile.CreatorProfileRequestImpl;
import nl.mwsoft.www.chatster.modelLayer.network.deleteGroupChatInvitation.DeleteGroupChatInvitationImpl;
import nl.mwsoft.www.chatster.modelLayer.network.discoverPosts.DiscoverPostsImpl;
import nl.mwsoft.www.chatster.modelLayer.network.getGroupOneTimeKeys.GetGroupOneTimeKeysImpl;
import nl.mwsoft.www.chatster.modelLayer.network.getOneTimePublicKey.GetOneTimePublicKeyImpl;
import nl.mwsoft.www.chatster.modelLayer.network.getOneTimePublicKeyByUUID.GetOneTimePublicKeyByUUIDImpl;
import nl.mwsoft.www.chatster.modelLayer.network.invite.InviteRequestImpl;
import nl.mwsoft.www.chatster.modelLayer.network.latestCreatorContactPosts.LatestCreatorContactPostsImpl;
import nl.mwsoft.www.chatster.modelLayer.network.groupChatInvitation.GroupChatInvitationImpl;
import nl.mwsoft.www.chatster.modelLayer.network.createGroupChatRequest.CreateGroupChatRequestImpl;
import nl.mwsoft.www.chatster.modelLayer.network.deleteRetrievedOfflineMessages.DeleteRetrievedMessagesImpl;
import nl.mwsoft.www.chatster.modelLayer.network.exitGroupChat.ExitGroupChatRequestImpl;
import nl.mwsoft.www.chatster.modelLayer.network.loadMorePosts.LoadMorePostsRequestImpl;
import nl.mwsoft.www.chatster.modelLayer.network.messageQueue.chat.ResendMessageRequestImpl;
import nl.mwsoft.www.chatster.modelLayer.network.messageQueue.groupChat.ResendGroupMessageRequestImpl;
import nl.mwsoft.www.chatster.modelLayer.network.offlineMessagePoll.OfflineMessagePollImpl;
import nl.mwsoft.www.chatster.modelLayer.network.registerUserRequest.RegisterUserRequestImpl;
import nl.mwsoft.www.chatster.modelLayer.network.updateToken.UpdateTokenImpl;
import nl.mwsoft.www.chatster.modelLayer.network.uploadGroupPublicKeys.UploadGroupPublicKeysImpl;
import nl.mwsoft.www.chatster.modelLayer.network.uploadPublicKeys.UploadPublicKeysImpl;
import nl.mwsoft.www.chatster.modelLayer.network.uploadVideoPost.UploadVideoPostImpl;
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
                                           String  userStatusMessage,String  messagingToken, ArrayList<Long> myContactIds,
                                           String oneTimePreKeyPairPbks){
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                try {
                    String result = "";
                    RegisterUserRequestImpl registerUser = new RegisterUserRequestImpl();
                    RegisterUserResponse registerUserResponse;
                    registerUserResponse = registerUser.getRegisterUserResponse(
                            userId,
                            userName, userStatusMessage, messagingToken, myContactIds,oneTimePreKeyPairPbks);
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
                                                                          String contactPublicKeyUUID, String userPublicKeyUUID,
                                                                          String contentType){
        return Observable.create(new ObservableOnSubscribe<ResendMessageResponse>() {
            @Override
            public void subscribe(ObservableEmitter<ResendMessageResponse> emitter) throws Exception {
                try {
                    ResendMessageResponse resendMessageResponse = new ResendMessageResponse();
                    ResendMessageRequestImpl resendMessageRequest = new ResendMessageRequestImpl();
                    resendMessageResponse = resendMessageRequest.getResendMessageResponse(message, senderId,
                    senderName, receiverId, chatName, uuid, contactPublicKeyUUID, userPublicKeyUUID, contentType);
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

    public Observable<ResendGroupMessageResponse> getResendGroupChatMessageResponse(String messages,String senderPublicKeyUUID){
        return Observable.create(new ObservableOnSubscribe<ResendGroupMessageResponse>() {
            @Override
            public void subscribe(ObservableEmitter<ResendGroupMessageResponse> emitter) throws Exception {
                try {
                    ResendGroupMessageResponse resendGroupMessageResponse = new ResendGroupMessageResponse();
                    ResendGroupMessageRequestImpl resendGroupMessageRequest = new ResendGroupMessageRequestImpl();
                    resendGroupMessageResponse = resendGroupMessageRequest.getResendGroupMessageResponse(messages,senderPublicKeyUUID);
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

    // region Creator Posts

    public Observable<ArrayList<CreatorPost>> getLatestCreatorPosts(long creator, String creatorsName){
        return Observable.create(new ObservableOnSubscribe<ArrayList<CreatorPost>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<CreatorPost>> emitter) throws Exception {
                try {
                    LatestCreatorContactPostsImpl creatorPostsRequest = new LatestCreatorContactPostsImpl();
                    ArrayList<CreatorPost> creatorPosts = new ArrayList<>();
                    creatorPosts = creatorPostsRequest.getLatestCreatorPosts(creator, creatorsName);
                    emitter.onNext(creatorPosts);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    // endregion

    // region Creator Post Comments

    public Observable<ArrayList<CreatorPostComment>> getCreatorPostComments(String postUUID){
        return Observable.create(new ObservableOnSubscribe<ArrayList<CreatorPostComment>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<CreatorPostComment>> emitter) throws Exception {
                try {
                    CreatorPostCommentsRequestImpl creatorPostCommentsRequest = new CreatorPostCommentsRequestImpl();
                    ArrayList<CreatorPostComment> creatorPostComments = new ArrayList<>();
                    creatorPostComments = creatorPostCommentsRequest.getCreatorPostComments(postUUID);
                    emitter.onNext(creatorPostComments);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    // endregion

    // region Creator Profile

    public Observable<CreatorContact> getCreatorContactProfile(String creator, long userId){
        return Observable.create(new ObservableOnSubscribe<CreatorContact>() {
            @Override
            public void subscribe(ObservableEmitter<CreatorContact> emitter) throws Exception {
                try {
                    CreatorProfileRequestImpl creatorProfileRequest = new CreatorProfileRequestImpl();
                    CreatorContact creatorContact = new CreatorContact();
                    creatorContact = creatorProfileRequest.getCreatorContactProfile(creator, userId);
                    emitter.onNext(creatorContact);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    // endregion

    // region Discover Creator Posts

    public Observable<ArrayList<CreatorPost>> discoverPosts(long userId){
        return Observable.create(new ObservableOnSubscribe<ArrayList<CreatorPost>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<CreatorPost>> emitter) throws Exception {
                try {
                    ArrayList<CreatorPost> creatorPosts = new ArrayList<>();
                    DiscoverPostsImpl discoverPosts = new DiscoverPostsImpl();
                    creatorPosts = discoverPosts.discoverPosts(userId);
                    emitter.onNext(creatorPosts);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    // endregion


    // region Creator Followers

    public Observable<ArrayList<CreatorContact>> getCreatorFollowers(String creatorName,long userId){
        return Observable.create(new ObservableOnSubscribe<ArrayList<CreatorContact>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<CreatorContact>> emitter) throws Exception {
                try {
                    ArrayList<CreatorContact> creatorFollowers = new ArrayList<>();
                    CreatorFollowersRequestImpl creatorFollowersRequest = new CreatorFollowersRequestImpl();
                    creatorFollowers = creatorFollowersRequest.getCreatorFollowers(creatorName, userId);
                    emitter.onNext(creatorFollowers);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    // endregion

    // region Creator Followers

    public Observable<ArrayList<CreatorContact>> getCreatorFollowing(String creatorName, long userId){
        return Observable.create(new ObservableOnSubscribe<ArrayList<CreatorContact>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<CreatorContact>> emitter) throws Exception {
                try {
                    ArrayList<CreatorContact> creatorFollowing = new ArrayList<>();
                    CreatorFollowingRequestImpl creatorFollowingRequest = new CreatorFollowingRequestImpl();
                    creatorFollowing = creatorFollowingRequest.getCreatorFollowing(creatorName, userId);
                    emitter.onNext(creatorFollowing);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    // endregion

    // region Creator History

    public Observable<ArrayList<HistoryItem>> getCreatorHistory(String creatorName, long userId){
        return Observable.create(new ObservableOnSubscribe<ArrayList<HistoryItem>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<HistoryItem>> emitter) throws Exception {
                try {
                    ArrayList<HistoryItem> historyItems = new ArrayList<>();
                    CreatorHistoryRequestImpl creatorHistoryRequest = new CreatorHistoryRequestImpl();
                    historyItems = creatorHistoryRequest.getCreatorHistory(creatorName, userId);
                    emitter.onNext(historyItems);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    // endregion

    // region Load More Creator Posts

    public Observable<ArrayList<CreatorPost>> loadMorePosts(String creatorName, String lastPostCreated){
        return Observable.create(new ObservableOnSubscribe<ArrayList<CreatorPost>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<CreatorPost>> emitter) throws Exception {
                try {
                    ArrayList<CreatorPost> creatorPosts = new ArrayList<>();
                    LoadMorePostsRequestImpl loadMorePostsRequest = new LoadMorePostsRequestImpl();
                    creatorPosts = loadMorePostsRequest.loadMorePosts(creatorName, lastPostCreated);
                    emitter.onNext(creatorPosts);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    // endregion

    // region Upload Video Post

    public Observable<String> getUploadVideoPostResponse(MultipartBody.Part body, String userName, String postCapture,
                                                         String postType, String creatorProfilePic, String uuid){
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                try {
                    UploadVideoPostImpl uploadVideoPost = new UploadVideoPostImpl();
                    String response = uploadVideoPost.getUploadVideoPostResponse(body,userName,
                            postCapture,postType,creatorProfilePic,uuid);
                    emitter.onNext(response);
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

    // region One Time Keys

    public Observable<String> uploadPublicKeys(final long userId,
                                               final String oneTimePreKeyPairPbks){
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                try {
                    String result = "";
                    UploadPublicKeysImpl uploadPublicKeys = new UploadPublicKeysImpl();
                    result = uploadPublicKeys.uploadPublicKeys(userId, oneTimePreKeyPairPbks);
                    emitter.onNext(result);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    public Observable<String> uploadReRegisterPublicKeys(final long userId,
                                               final String oneTimePreKeyPairPbks){
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                try {
                    String result = "";
                    UploadPublicKeysImpl uploadPublicKeys = new UploadPublicKeysImpl();
                    result = uploadPublicKeys.uploadReRegisterPublicKeys(userId, oneTimePreKeyPairPbks);
                    emitter.onNext(result);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    public Observable<String> checkPublicKeys(final long userId){
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                try {
                    String result = "";
                    CheckPublicKeysImpl checkPublicKeys = new CheckPublicKeysImpl();
                    result = checkPublicKeys.checkPublicKeys(userId);
                    emitter.onNext(result);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    public Observable<OneTimePublicKey> getPublicKey(final long contactId){
        return Observable.create(new ObservableOnSubscribe<OneTimePublicKey>() {
            @Override
            public void subscribe(ObservableEmitter<OneTimePublicKey> emitter) throws Exception {
                try {
                    OneTimePublicKey result;
                    GetOneTimePublicKeyImpl getOneTimePublicKey = new GetOneTimePublicKeyImpl();
                    result = getOneTimePublicKey.getOneTimePublicKey(contactId);
                    emitter.onNext(result);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    public Observable<OneTimePublicKey> getPublicKeyByUUID(final long contactId, final String uuid){
        return Observable.create(new ObservableOnSubscribe<OneTimePublicKey>() {
            @Override
            public void subscribe(ObservableEmitter<OneTimePublicKey> emitter) throws Exception {
                try {
                    OneTimePublicKey result = new OneTimePublicKey();
                    GetOneTimePublicKeyByUUIDImpl getOneTimePublicKeyByUUID = new GetOneTimePublicKeyByUUIDImpl();
                    result = getOneTimePublicKeyByUUID.getOneTimePublicKeyByUUID(contactId, uuid);
                    emitter.onNext(result);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    // endregion

    // region Group One Time Keys

    public Observable<String> uploadGroupPublicKeys(String oneTimePreKeyPairPbks){
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                try {
                    String result = "";
                    UploadGroupPublicKeysImpl uploadGroupPublicKeysImpl = new UploadGroupPublicKeysImpl();
                    result = uploadGroupPublicKeysImpl.uploadGroupPublicKeys(oneTimePreKeyPairPbks);
                    emitter.onNext(result);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    public Observable<ArrayList<OneTimeGroupPublicKey>> getGroupOneTimeKeys(String groupChatId, long userId){
        return Observable.create(new ObservableOnSubscribe<ArrayList<OneTimeGroupPublicKey>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<OneTimeGroupPublicKey>> emitter) throws Exception {
                try {
                    ArrayList<OneTimeGroupPublicKey> result = new ArrayList<>();
                    GetGroupOneTimeKeysImpl getGroupOneTimeKeys = new GetGroupOneTimeKeysImpl();
                    result = getGroupOneTimeKeys.getGroupOneTimeKeys(groupChatId,userId);
                    emitter.onNext(result);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    public Observable<ArrayList<String>> checkIfGroupKeysNeeded(String groupChatIds, long userId){
        return Observable.create(new ObservableOnSubscribe<ArrayList<String>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<String>> emitter) throws Exception {
                try {
                    ArrayList<String> result = new ArrayList<>();
                    CheckIfGroupKeysNeededImpl checkIfGroupKeysNeeded = new CheckIfGroupKeysNeededImpl();
                    result = checkIfGroupKeysNeeded.checkIfGroupKeysNeeded(groupChatIds,userId);
                    emitter.onNext(result);
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
