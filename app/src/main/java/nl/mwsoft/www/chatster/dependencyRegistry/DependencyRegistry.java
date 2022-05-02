package nl.mwsoft.www.chatster.dependencyRegistry;


import android.content.Context;
import android.widget.Toast;

import nl.mwsoft.www.chatster.coordinator.RootCoordinator;
import nl.mwsoft.www.chatster.modelLayer.database.chat.ChatDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.database.contact.ContactDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.database.creators.CreatorsDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.database.groupChat.GroupChatDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.database.notification.NotificationDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.database.user.UserDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.firebase.offlineMessageService.ChatsterFirebaseOfflineMessageService;
import nl.mwsoft.www.chatster.modelLayer.helper.e2e.ChatsterE2EHelper;
import nl.mwsoft.www.chatster.modelLayer.helper.groupE2E.ChatsterGroupE2EHelper;
import nl.mwsoft.www.chatster.modelLayer.helper.util.dateTime.ChatsterDateTimeUtil;
import nl.mwsoft.www.chatster.modelLayer.helper.util.image.ChatsterImageProcessingUtil;
import nl.mwsoft.www.chatster.modelLayer.helper.util.jobService.ChatsterJobServiceUtil;
import nl.mwsoft.www.chatster.modelLayer.helper.util.internet.InternetConnectionUtil;
import nl.mwsoft.www.chatster.modelLayer.helper.util.uuid.ChatsterUUIDUtil;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.chat.ChatModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.contact.ContactModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.creators.CreatorsModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.groupChat.GroupChatModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.notification.NotificationModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.user.UserModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.util.UtilModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.network.NetworkLayer;
import nl.mwsoft.www.chatster.modelLayer.service.local.ChatsterLocalNotificationJobService;
import nl.mwsoft.www.chatster.modelLayer.service.contactLatest.ContactLatestJobService;
import nl.mwsoft.www.chatster.modelLayer.service.messageQueue.ChatsterMessageQueueJobService;
import nl.mwsoft.www.chatster.presenterLayer.addNewGroupMember.AddNewGroupMemberPresenter;
import nl.mwsoft.www.chatster.presenterLayer.chat.ChatPresenter;
import nl.mwsoft.www.chatster.presenterLayer.chatsterSettings.SettingsPresenter;
import nl.mwsoft.www.chatster.presenterLayer.confirmPhone.ConfirmPhonePresenter;
import nl.mwsoft.www.chatster.presenterLayer.creators.CreatorsPresenter;
import nl.mwsoft.www.chatster.presenterLayer.groupChat.GroupChatPresenter;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.image.ImageProcessingManager;
import nl.mwsoft.www.chatster.presenterLayer.groupChatInfo.GroupChatInfoPresenter;
import nl.mwsoft.www.chatster.presenterLayer.invite.InvitePresenter;
import nl.mwsoft.www.chatster.presenterLayer.postCommentsPresenter.PostCommentsPresenter;
import nl.mwsoft.www.chatster.presenterLayer.postEditing.PostEditingPresenter;
import nl.mwsoft.www.chatster.presenterLayer.registerUser.RegisterUserPresenter;
import nl.mwsoft.www.chatster.presenterLayer.main.MainPresenter;
import nl.mwsoft.www.chatster.viewLayer.addNewGroupMember.AddNewGroupMembersActivity;
import nl.mwsoft.www.chatster.viewLayer.chat.ChatActivity;
import nl.mwsoft.www.chatster.viewLayer.chatSettings.ChatSettingsActivity;
import nl.mwsoft.www.chatster.viewLayer.chatsterSettings.ChatsterSettingsActivity;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.addNewGroupMemberChatsterToast.AddNewGroupMemberChatsterToast;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.chatChatsterToast.ChatChatsterToast;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.chatSettingsChatsterToast.ChatSettingsChatsterToast;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.chatsterSettingsChatsterToast.ChatsterSettingsChatsterToast;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.confirmPhoneChatsterToast.ConfirmPhoneChatsterToast;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.createGroupChatsterToast.CreateGroupChatsterToast;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.groupChatChatsterToast.GroupChatChatsterToast;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.groupChatInfoChatsterToast.GroupChatInfoChatsterToast;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.imageDetailChatsterToast.ImageDetailChatsterToast;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.mainChatsterToast.MainChatsterToast;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.permissionsRequestChatsterToast.PermissionsRequestChatsterToast;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.registerUserChatsterToast.RegisterUserChatsterToast;
import nl.mwsoft.www.chatster.viewLayer.confirmPhone.ConfirmPhoneActivity;
import nl.mwsoft.www.chatster.viewLayer.createGroupChat.CreateGroupChatActivity;
import nl.mwsoft.www.chatster.viewLayer.creators.CreatorsActivity;
import nl.mwsoft.www.chatster.viewLayer.creators.PostCommentsActivity;
import nl.mwsoft.www.chatster.viewLayer.creators.CreatePostActivity;
import nl.mwsoft.www.chatster.viewLayer.editUserStatus.EditUserStatusActivity;
import nl.mwsoft.www.chatster.viewLayer.groupChat.GroupChatActivity;
import nl.mwsoft.www.chatster.viewLayer.groupChatInfo.GroupChatInfoActivity;
import nl.mwsoft.www.chatster.viewLayer.imageDetail.ImageDetailActivity;
import nl.mwsoft.www.chatster.viewLayer.intro.IntroActivity;
import nl.mwsoft.www.chatster.viewLayer.invite.InviteActivity;
import nl.mwsoft.www.chatster.viewLayer.main.MainActivity;
import nl.mwsoft.www.chatster.viewLayer.permissionsRequest.PermissionsRequestActivity;
import nl.mwsoft.www.chatster.viewLayer.registerUser.RegisterUserActivity;
import nl.mwsoft.www.chatster.viewLayer.welcome.WelcomeActivity;

public class DependencyRegistry {

    public static DependencyRegistry shared = new DependencyRegistry();

    // region ChatsterE2EHelper

    private ChatsterE2EHelper createChatsterE2EHelper(){
        return new ChatsterE2EHelper();
    }

    // endregion

    // region ChatsterGroupE2EHelper

    private ChatsterGroupE2EHelper createChatsterGroupE2EHelper() { return new ChatsterGroupE2EHelper(); }

    // endregion

    // region Coordinators

    public RootCoordinator rootCoordinator = new RootCoordinator();

    // endregion

    // region Toast

    private Toast createToast(Context context){
        return new Toast(context);
    }

    // endregion


    // region ToastUtil

    private AddNewGroupMemberChatsterToast createAddNewGroupMemberChatsterToast(Context context){
        return new AddNewGroupMemberChatsterToast(context, createToast(context));
    }

    private ChatChatsterToast createChatChatsterToast(Context context){
        return new ChatChatsterToast(context, createToast(context));
    }

    private ChatSettingsChatsterToast createChatSettingsChatsterToast(Context context){
        return new ChatSettingsChatsterToast(context, createToast(context));
    }

    private ConfirmPhoneChatsterToast createConfirmPhoneChatsterToast(Context context){
        return new ConfirmPhoneChatsterToast(context, createToast(context));
    }

    private CreateGroupChatsterToast createCreateGroupChatsterToast(Context context){
        return new CreateGroupChatsterToast(context, createToast(context));
    }

    private GroupChatChatsterToast createGroupChatChatsterToast(Context context){
        return new GroupChatChatsterToast(context, createToast(context));
    }

    private GroupChatInfoChatsterToast createGroupChatInfoChatsterToast(Context context){
        return new GroupChatInfoChatsterToast(context, createToast(context));
    }

    private ImageDetailChatsterToast createImageDetailChatsterToast(Context context){
        return new ImageDetailChatsterToast(context, createToast(context));
    }

    private ChatsterSettingsChatsterToast createChatsterSettingsChatsterToast(Context context){
        return new ChatsterSettingsChatsterToast(context, createToast(context));
    }

    private MainChatsterToast createMainChatsterToast(Context context){
        return new MainChatsterToast(context, createToast(context));
    }

    private PermissionsRequestChatsterToast createPermissionsRequestChatsterToast(Context context){
        return new PermissionsRequestChatsterToast(context, createToast(context));
    }

    private RegisterUserChatsterToast createRegisterUserChatsterToast(Context context){
        return new RegisterUserChatsterToast(context, createToast(context));
    }

    // endregion


    // region InternetUtil

    private InternetConnectionUtil createInternetConnectionUtil(){
        return new InternetConnectionUtil();
    }

    // endregion


    // region UUID

    private ChatsterUUIDUtil createChatsterUUIDUtil(){
        return new ChatsterUUIDUtil();
    }

    // endregion


    // region DateTimeUtil

    private ChatsterDateTimeUtil createChatsterDateTimeUtil(){return new ChatsterDateTimeUtil();}

    // endregion


    // region UtilModelLayerManager

    // region AddNewGroupMemberPresenter

    private UtilModelLayerManager createUtilModelLayerManager(InternetConnectionUtil internetConnectionUtil){
        return new UtilModelLayerManager(internetConnectionUtil);
    }

    // endregion

    // region ChatPresenter

    private UtilModelLayerManager createUtilModelLayerManager(InternetConnectionUtil internetConnectionUtil,
                                                              ChatsterUUIDUtil chatsterUUIDUtil,
                                                              ChatsterDateTimeUtil chatsterDateTimeUtil){
        return new UtilModelLayerManager(chatsterUUIDUtil, internetConnectionUtil, chatsterDateTimeUtil);
    }
    private UtilModelLayerManager createUtilModelLayerManager(ChatsterDateTimeUtil chatsterDateTimeUtil){
        return new UtilModelLayerManager(chatsterDateTimeUtil);
    }

    private UtilModelLayerManager createUtilModelLayerManager(InternetConnectionUtil internetConnectionUtil,
                                                              ChatsterUUIDUtil chatsterUUIDUtil){
        return new UtilModelLayerManager(chatsterUUIDUtil, internetConnectionUtil);
    }

    // endregion


    // region Service Utility

    private ChatsterJobServiceUtil createServiceUtil(){
        return new ChatsterJobServiceUtil();
    }

    // endregion

    private ChatsterImageProcessingUtil createChatsterImageProcessingUtil(ChatsterDateTimeUtil chatsterDateTimeUtil){
        return new ChatsterImageProcessingUtil(chatsterDateTimeUtil);
    }


    // region Image Processing

    private ImageProcessingManager createImageProcessingManager(ChatsterImageProcessingUtil chatsterImageProcessingUtil){
        return new ImageProcessingManager(chatsterImageProcessingUtil);
    }

    // endregion


    // region Database Layers

    public ChatDatabaseLayer createChatDatabaseLayer (){
       return new ChatDatabaseLayer(createChatsterDateTimeUtil());
    }

    public ChatDatabaseLayer createChatDatabaseLayer (ChatsterDateTimeUtil chatsterDateTimeUtil){
       return new ChatDatabaseLayer(chatsterDateTimeUtil);
    }

    public ContactDatabaseLayer createContactDatabaseLayer (){
        return new ContactDatabaseLayer();
    }

    public GroupChatDatabaseLayer createGroupChatDatabaseLayer(ChatsterDateTimeUtil chatsterDateTimeUtil){
        return new GroupChatDatabaseLayer(chatsterDateTimeUtil);
    }
    public GroupChatDatabaseLayer createGroupChatDatabaseLayer(){
        return new GroupChatDatabaseLayer(createChatsterDateTimeUtil());
    }

    public NotificationDatabaseLayer createNotificationDatabaseLayer(ChatsterDateTimeUtil chatsterDateTimeUtil){
        return new NotificationDatabaseLayer(chatsterDateTimeUtil);
    }
    public NotificationDatabaseLayer createNotificationDatabaseLayer(){
        return new NotificationDatabaseLayer(createChatsterDateTimeUtil());
    }

    public UserDatabaseLayer createUserDatabaseLayer (){
        return new UserDatabaseLayer();
    }

    public CreatorsDatabaseLayer createCreatorsDatabaseLayer(ContactDatabaseLayer contactDatabaseLayer){
        return new CreatorsDatabaseLayer(contactDatabaseLayer);
    }

    // endregion


    // region ChatsterJobServiceUtil

    private ChatsterJobServiceUtil createChatsterJobServiceUtil(){
        return new ChatsterJobServiceUtil();
    }

    //endregion


    // region Network Layer

    public NetworkLayer createNetworkLayer(){
        return new NetworkLayer();
    }

    // endregion


    // region Model Layer Managers

    private ChatModelLayerManager createChatModelLayerManager(){
        return new ChatModelLayerManager();
    }

    private ContactModelLayerManager createContactModelLayerManager(){
        return new ContactModelLayerManager();
    }

    private GroupChatModelLayerManager createGroupChatModelLayerManager(){
        return new GroupChatModelLayerManager();
    }

    private NotificationModelLayerManager createNotificationModelLayerManager(){
        return new NotificationModelLayerManager();
    }

    private UserModelLayerManager createUserModelLayerManager(){
        return new UserModelLayerManager();
    }

    private CreatorsModelLayerManager createCreatorsModelLayerManager(CreatorsDatabaseLayer creatorsDatabaseLayer,
                                                                      NetworkLayer networkLayer){
        return new CreatorsModelLayerManager(creatorsDatabaseLayer, networkLayer);
    }

    // endregion


    // region Injection Methods

    public void inject(AddNewGroupMembersActivity addNewGroupMembersActivity){
        addNewGroupMembersActivity.configureWith(new AddNewGroupMemberPresenter(
                        createGroupChatModelLayerManager(),
                        createContactModelLayerManager(),
                        createUtilModelLayerManager(createInternetConnectionUtil())),
                rootCoordinator,
                createAddNewGroupMemberChatsterToast(addNewGroupMembersActivity));
    }

    public void inject(ChatActivity chatActivity){
        chatActivity.configureWith(new ChatPresenter(
                        createUserModelLayerManager(),
                        createContactModelLayerManager(),
                        createChatModelLayerManager(),
                        createNotificationModelLayerManager(),
                        createImageProcessingManager(createChatsterImageProcessingUtil(createChatsterDateTimeUtil())),
                        createUtilModelLayerManager(
                                    createInternetConnectionUtil(),
                                    createChatsterUUIDUtil(),
                                    createChatsterDateTimeUtil()),
                        createChatsterE2EHelper()),
                rootCoordinator,
                createChatChatsterToast(chatActivity));
    }

    public ChatPresenter injectChatMessageAdapter(){
        return new ChatPresenter(
                        createUserModelLayerManager(),
                        createContactModelLayerManager(),
                        createChatModelLayerManager(),
                        createUtilModelLayerManager(createChatsterDateTimeUtil()));
    }

    public void inject(ChatSettingsActivity chatSettingsActivity){
        chatSettingsActivity.configureWith(new ChatPresenter(createUserModelLayerManager(),
                        createContactModelLayerManager(),
                        createChatModelLayerManager(),
                        createUtilModelLayerManager(createInternetConnectionUtil())),
                rootCoordinator, createChatSettingsChatsterToast(chatSettingsActivity));
    }

    public void inject(ImageDetailActivity imageDetailActivity){
        imageDetailActivity.configureWith(new ChatPresenter(
                        createChatModelLayerManager()),
                        rootCoordinator,
                        createImageDetailChatsterToast(imageDetailActivity));
    }

    public ChatPresenter injectChatsAdapter(){
        return new ChatPresenter(createContactModelLayerManager(), createChatModelLayerManager(), createChatsterE2EHelper());
    }

    public ChatPresenter injectChatsFragment(){
        return new ChatPresenter(createChatModelLayerManager());
    }

    public void inject(ChatsterSettingsActivity chatsterSettingsActivity){
        chatsterSettingsActivity.configureWith(new SettingsPresenter(
                        createUserModelLayerManager(),
                        createImageProcessingManager(createChatsterImageProcessingUtil(createChatsterDateTimeUtil())),
                        createUtilModelLayerManager(createInternetConnectionUtil())),
                rootCoordinator,
                createChatsterSettingsChatsterToast(chatsterSettingsActivity));
    }

    public void inject(EditUserStatusActivity editUserStatusActivity){
        editUserStatusActivity.configureWith(new SettingsPresenter(createUserModelLayerManager()), rootCoordinator);
    }

    public void inject(ConfirmPhoneActivity confirmPhoneActivity){
        confirmPhoneActivity.configureWith(new ConfirmPhonePresenter(
                        createUserModelLayerManager(),
                        createContactModelLayerManager(),
                        createUtilModelLayerManager(createInternetConnectionUtil()),
                        createChatsterE2EHelper()),
                rootCoordinator,
                createConfirmPhoneChatsterToast(confirmPhoneActivity));
    }

    public void inject(CreateGroupChatActivity createGroupChatActivity){
        createGroupChatActivity.configureWith(new GroupChatPresenter(
                        createContactModelLayerManager(),
                        createGroupChatModelLayerManager(),
                        createUserModelLayerManager(),
                        createUtilModelLayerManager(createInternetConnectionUtil(), createChatsterUUIDUtil()),
                        createChatsterGroupE2EHelper()),
                rootCoordinator,
                createCreateGroupChatsterToast(createGroupChatActivity));
    }

    public GroupChatPresenter injectCreateGroupChatContactsAdapter(){
        return new GroupChatPresenter(createContactModelLayerManager());
    }

    public void inject(GroupChatActivity groupChatActivity){
        groupChatActivity.configureWith(new GroupChatPresenter(
                        createGroupChatModelLayerManager(),
                        createUserModelLayerManager(),
                        createImageProcessingManager(createChatsterImageProcessingUtil(createChatsterDateTimeUtil())),
                        createContactModelLayerManager(),
                        createUtilModelLayerManager(
                                createInternetConnectionUtil(),
                                createChatsterUUIDUtil(),
                                createChatsterDateTimeUtil()),
                        createChatsterGroupE2EHelper()),
                rootCoordinator,
                createGroupChatChatsterToast(groupChatActivity));
    }

    public GroupChatPresenter injectGroupChatMessageAdapter(){
        return new GroupChatPresenter(
                createContactModelLayerManager(),
                createGroupChatModelLayerManager(),
                createUserModelLayerManager(),
                createUtilModelLayerManager(createChatsterDateTimeUtil()),
                createChatsterGroupE2EHelper());
    }

    public GroupChatPresenter injectGroupChatInfoAdapter(){
        return new GroupChatPresenter(createContactModelLayerManager());
    }

    public GroupChatPresenter injectGroupChatsAdapter(){
        return new GroupChatPresenter(createGroupChatModelLayerManager());
    }

    public GroupChatPresenter injectGroupChatsFragment(){
        return new GroupChatPresenter(createGroupChatModelLayerManager());
    }

    public void inject(GroupChatInfoActivity groupChatInfoActivity){
        groupChatInfoActivity.configureWith(new GroupChatInfoPresenter(
                        createGroupChatModelLayerManager(),
                        createContactModelLayerManager(),
                        createUserModelLayerManager(),
                        createImageProcessingManager(createChatsterImageProcessingUtil(createChatsterDateTimeUtil())),
                        createUtilModelLayerManager(createInternetConnectionUtil())),
                rootCoordinator,
                createGroupChatInfoChatsterToast(groupChatInfoActivity));
    }

    public void inject(RegisterUserActivity registerUserActivity){
        registerUserActivity.configureWith(new RegisterUserPresenter(
                        createUserModelLayerManager(),
                        createContactModelLayerManager(),
                        createUtilModelLayerManager(createInternetConnectionUtil()),
                        createChatsterE2EHelper()),
                rootCoordinator,
                createRegisterUserChatsterToast(registerUserActivity));
    }

    public void inject(MainActivity mainActivity){
        mainActivity.configureWith(new MainPresenter(
                        createServiceUtil(),
                        createUserModelLayerManager(),
                        createNotificationModelLayerManager(),
                        createGroupChatModelLayerManager(),
                        createUtilModelLayerManager(createInternetConnectionUtil(), createChatsterUUIDUtil()),
                        createChatsterE2EHelper(),
                        createChatsterGroupE2EHelper()),
                rootCoordinator,
                createMainChatsterToast(mainActivity));
    }

    public void inject(ChatsterFirebaseOfflineMessageService chatsterFirebaseOfflineMessageService){
        chatsterFirebaseOfflineMessageService.configureWith(
                createNetworkLayer(),
                createImageProcessingManager(createChatsterImageProcessingUtil(createChatsterDateTimeUtil())),
                createUserDatabaseLayer(),
                createGroupChatDatabaseLayer(createChatsterDateTimeUtil()),
                createChatDatabaseLayer(createChatsterDateTimeUtil()),
                createNotificationDatabaseLayer(createChatsterDateTimeUtil()),
                createChatsterE2EHelper(),
                createChatsterGroupE2EHelper());
    }

    public void inject(ChatsterMessageQueueJobService chatsterMessageQueueJobService){
        chatsterMessageQueueJobService.configureWith(
                createNetworkLayer(),
                createUserDatabaseLayer(),
                createChatDatabaseLayer(createChatsterDateTimeUtil()),
                createGroupChatDatabaseLayer(createChatsterDateTimeUtil()),
                createChatsterJobServiceUtil(),
                createInternetConnectionUtil(),
                createChatsterE2EHelper(),
                createChatsterGroupE2EHelper());
    }

    public void inject(ChatsterLocalNotificationJobService chatsterLocalNotificationJobService){
        chatsterLocalNotificationJobService.configureWith(
                createGroupChatDatabaseLayer(createChatsterDateTimeUtil()),
                createChatsterJobServiceUtil());
    }

    public void inject(ContactLatestJobService contactLatestJobService){
        contactLatestJobService.configureWith(
                createNetworkLayer(),
                createContactDatabaseLayer(),
                createNotificationDatabaseLayer(createChatsterDateTimeUtil()),
                createChatsterJobServiceUtil());
    }

    public void inject(IntroActivity introActivity){
        introActivity.configureWith(rootCoordinator);
    }

    public void inject(WelcomeActivity welcomeActivity){
        welcomeActivity.configureWith(rootCoordinator);
    }

    public void inject(PermissionsRequestActivity permissionsRequestActivity){
        permissionsRequestActivity.configureWith(
                rootCoordinator,
                createPermissionsRequestChatsterToast(permissionsRequestActivity));
    }

    public void inject(CreatorsActivity creatorsActivity){
        creatorsActivity.configureWith(new CreatorsPresenter(
                        createImageProcessingManager(createChatsterImageProcessingUtil(createChatsterDateTimeUtil())),
                        createCreatorsModelLayerManager(
                                createCreatorsDatabaseLayer(createContactDatabaseLayer()),
                                createNetworkLayer()),
                        createUserModelLayerManager(),
                        createUtilModelLayerManager(createInternetConnectionUtil(), createChatsterUUIDUtil(), createChatsterDateTimeUtil())),
                rootCoordinator);
    }

    public void inject(CreatePostActivity createPostActivity){
        createPostActivity.configureWith(rootCoordinator,
                new PostEditingPresenter(
                        createImageProcessingManager(createChatsterImageProcessingUtil(createChatsterDateTimeUtil())),
                        createCreatorsModelLayerManager(
                            createCreatorsDatabaseLayer(createContactDatabaseLayer()), createNetworkLayer()),
                        createUserModelLayerManager(),
                        createUtilModelLayerManager(createInternetConnectionUtil(), createChatsterUUIDUtil())));
    }


    public void inject(PostCommentsActivity postCommentsActivity){
        postCommentsActivity.configureWith(new PostCommentsPresenter(
                        createCreatorsModelLayerManager(
                                createCreatorsDatabaseLayer(createContactDatabaseLayer()),
                                createNetworkLayer()),
                createUserModelLayerManager(),
                createUtilModelLayerManager(createInternetConnectionUtil())));
    }

    public void inject(InviteActivity inviteActivity){
        inviteActivity.configureWith(new InvitePresenter(createNetworkLayer()));
    }

    // endregion

}
