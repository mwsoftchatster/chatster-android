package nl.mwsoft.www.chatster.coordinator;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.model.Chat;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorPost;
import nl.mwsoft.www.chatster.modelLayer.model.ImageDetailRequest;
import nl.mwsoft.www.chatster.viewLayer.addNewGroupMember.AddNewGroupMembersActivity;
import nl.mwsoft.www.chatster.viewLayer.chat.ChatActivity;
import nl.mwsoft.www.chatster.viewLayer.chatSettings.ChatSettingsActivity;
import nl.mwsoft.www.chatster.viewLayer.chatsterSettings.ChatsterSettingsActivity;
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

public class RootCoordinator {

    public RootCoordinator() {
    }

    public void navigateToInviteActivity(Context context, String userName, String inviteeName){
        Intent inviteIntent = new Intent(context, InviteActivity.class);
        inviteIntent.putExtra(ConstantRegistry.USER_NAME, userName);
        inviteIntent.putExtra(ConstantRegistry.INVITEE_NAME, inviteeName);
        inviteIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(inviteIntent);
    }

    public void navigateToGroupChatInfoActivity(Context context, String chatId){
        Intent groupChatIntent = new Intent(context, GroupChatInfoActivity.class);
        groupChatIntent.putExtra(ConstantRegistry.GROUP_CHAT_ID, chatId);
        groupChatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(groupChatIntent);
    }

    public void navigateToImageDetailActivity(Context context, ImageDetailRequest imageDetailRequest){
        Intent imageDetailIntent = new Intent(context, ImageDetailActivity.class);
        imageDetailIntent.putExtra(ConstantRegistry.IMAGE_DETAIL_REQUEST, imageDetailRequest);
        imageDetailIntent.setAction(ConstantRegistry.IMAGE_DETAIL);
        context.startActivity(imageDetailIntent);
    }

    public void navigateToChatSettingsActivity(Context context, Chat chat){
        Intent chatIntent = new Intent(context, ChatSettingsActivity.class);
        chatIntent.putExtra(ConstantRegistry.CHAT_REQUEST, chat);
        chatIntent.setAction(ConstantRegistry.CHATS_SETTINGS);
        context.startActivity(chatIntent);
    }

    public void navigateToMainActivity(Context context){
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(mainIntent);
    }

    public void navigateToChatActivity(Context context, Chat chat){
        Intent chatIntent = new Intent(context, ChatActivity.class);
        chatIntent.setAction(ConstantRegistry.CHATS_LIST);
        chatIntent.putExtra(ConstantRegistry.CHAT_REQUEST, chat);
        chatIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(chatIntent);
    }

    public void navigateToEditUserStatusActivity(Context context){
        Intent settingsIntent = new Intent(context, EditUserStatusActivity.class);
        context.startActivity(settingsIntent);
    }
    public void navigateToCommentsActivity(CreatorPost creatorPost, Context context){
        Intent commentsIntent = new Intent(context, PostCommentsActivity.class);
        commentsIntent.putExtra(ConstantRegistry.CREATORS_POST, creatorPost);
        context.startActivity(commentsIntent);
    }

    public void navigateToPrivacyPolicyWebPage(Context context){
        Uri privacyPolicy = Uri.parse(ConstantRegistry.CHATSTER_PRIVACY_POLICY_URL);
        Intent intent = new Intent(Intent.ACTION_VIEW, privacyPolicy);
        context.startActivity(intent);
    }

    public void navigateToRegisterUserActivity(Context context){
        Intent phoneHasBeenConfirmed = new Intent(context, RegisterUserActivity.class);
        phoneHasBeenConfirmed.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(phoneHasBeenConfirmed);
    }

    public void navigateToChatsterSettingsActivity(Context context){
        Intent mainIntent = new Intent(context, ChatsterSettingsActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(mainIntent);
    }

    public void navigateToGroupChatActivity(Context context, String groupChatId){
        Intent groupChatIntent = new Intent(context, GroupChatActivity.class);
        groupChatIntent.putExtra(ConstantRegistry.GROUP_CHAT_ID, groupChatId);
        groupChatIntent.setAction(ConstantRegistry.GROUP_CHAT_ID);
        context.startActivity(groupChatIntent);
    }

    public void navigateToAddNewGroupMembersActivity(Context context, String groupChatId){
        Intent addNewMembersIntent = new Intent(context, AddNewGroupMembersActivity.class);
        addNewMembersIntent.putExtra(ConstantRegistry.GROUP_CHAT_ID, groupChatId);
        context.startActivity(addNewMembersIntent);
    }

    public void navigateToMainActivityAfterExitGroup(Context context){
        Intent exitGroupIntent = new Intent(context,MainActivity.class);
        exitGroupIntent.setAction(ConstantRegistry.LEFT_GROUP);
        context.startActivity(exitGroupIntent);
    }

    public void navigateToWelcomeActivity(Context context){
        Intent welcomeIntent = new Intent(context, WelcomeActivity.class);
        context.startActivity(welcomeIntent);
    }

    public void navigateToChatsterSettingsActivityFromMain(Context context){
        Intent settingsIntent = new Intent(context, ChatsterSettingsActivity.class);
        context.startActivity(settingsIntent);
    }

    public void navigateToChatsterTermsAndPoliciesWebPage(Context context){
        Uri termsPolicies = Uri.parse(ConstantRegistry.CHATSTER_TERMS_AND_POLICIES_URL);
        Intent intent = new Intent(Intent.ACTION_VIEW, termsPolicies);
        context.startActivity(intent);
    }


    public void navigateToChatsterGDPRWebPage(Context context){
        Uri termsPolicies = Uri.parse(ConstantRegistry.CHATSTER_GDPR_URL);
        Intent intent = new Intent(Intent.ACTION_VIEW, termsPolicies);
        context.startActivity(intent);
    }

    public void navigateToCreateGroupChatActivity(Context context){
        Intent createGroupChat = new Intent(context, CreateGroupChatActivity.class);
        context.startActivity(createGroupChat);
    }

    public void navigateToIntroActivity(Context context){
        Intent registerUserIntent = new Intent(context,IntroActivity.class);
        context.startActivity(registerUserIntent);
    }

    public void navigateToConfirmPhoneActivity(Context context){
        Intent confirmPhoneIntent = new Intent(context, ConfirmPhoneActivity.class);
        confirmPhoneIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(confirmPhoneIntent);
    }

    public void navigateToPermissionsActivity(Context context){
        Intent permissionsIntent = new Intent(context, PermissionsRequestActivity.class);
        context.startActivity(permissionsIntent);
    }

    public void navigateToCreatorsActivity(Context context){
        Intent creators = new Intent(context, CreatorsActivity.class);
        context.startActivity(creators);
    }


    public void navigateToCreatorsActivityAfterPosting(Context context){
        Intent creators = new Intent(context, CreatorsActivity.class);
        creators.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(creators);
    }

    public void navigateToCreatePostActivity(Context context, Uri uri, String action){
        Intent postEditIntent = new Intent(context, CreatePostActivity.class);
        postEditIntent.putExtra(ConstantRegistry.CREATORS_URI, uri);
        postEditIntent.setAction(action);
        context.startActivity(postEditIntent);
    }

    public void navigateToCreatePostActivity(Context context, String action){
        Intent postCreateIntent = new Intent(context, CreatePostActivity.class);
        postCreateIntent.setAction(action);
        context.startActivity(postCreateIntent);
    }

    public void navigateToPostEditingActivity(){

    }
}
