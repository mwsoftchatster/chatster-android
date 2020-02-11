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
package nl.mwsoft.www.chatster.viewLayer.main;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.fabric.sdk.android.Fabric;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.transports.WebSocket;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.coordinator.RootCoordinator;
import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.model.Chat;
import nl.mwsoft.www.chatster.modelLayer.model.Contact;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChat;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChatInvitation;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import nl.mwsoft.www.chatster.presenterLayer.chat.ChatPresenter;
import nl.mwsoft.www.chatster.presenterLayer.groupChat.GroupChatPresenter;
import nl.mwsoft.www.chatster.presenterLayer.main.MainPresenter;
import nl.mwsoft.www.chatster.viewLayer.chat.activity.ChatActivity;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.mainChatsterToast.MainChatsterToast;
import nl.mwsoft.www.chatster.viewLayer.main.adapter.ChatsAdapter;
import nl.mwsoft.www.chatster.viewLayer.main.adapter.GroupChatsAdapter;
import nl.mwsoft.www.chatster.viewLayer.main.adapter.ViewPagerAdapter;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.vpMainActivity)
    ViewPager vpMainActivity;
    private ViewPagerAdapter viewPagerAdapter;
    @BindView(R.id.tlMainActivity)
    TabLayout tlMainActivity;
    @BindView(R.id.fab)
    FloatingActionButton fabCreateNewGroupChat;
    private Socket socket;
    private Unbinder unbinder;
    private MainPresenter mainPresenter;
    private RootCoordinator rootCoordinator;
    private MainChatsterToast mainChatsterToast;
    private Disposable subscribeUpdateUserToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DependencyRegistry.shared.inject(this);

        if (userNotRegistered()) {
            registerUser();

            return;
        }

        runUserRegisteredRoutine();
    }

    // region Configure

    public void configureWith(MainPresenter mainPresenter, RootCoordinator rootCoordinator,
                              MainChatsterToast mainChatsterToast) {
        this.mainPresenter = mainPresenter;
        this.rootCoordinator = rootCoordinator;
        this.mainChatsterToast = mainChatsterToast;
    }

    // endregion

    // region FireBase

    private void sendRegistrationToServer() {
        if (this.mainPresenter.getUserId(MainActivity.this) != 0) {
            subscribeUpdateUserToken = this.mainPresenter.
                    updateUserToken(this.mainPresenter.getUserId(MainActivity.this),
                            FirebaseInstanceId.getInstance().getToken()).
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(res -> {

                    }, Throwable::printStackTrace);
        }
    }

    // endregion

    // region UI

    private void runUserRegisteredRoutine() {
        attachUI();
        handleNotificationAction();
        mainPresenter.startServices(MainActivity.this);
        sendRegistrationToServer();
    }

    private void configureViewPager() {
        vpMainActivity.addOnPageChangeListener(myOnPageChangeListener);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        vpMainActivity.setOffscreenPageLimit(2);
        vpMainActivity.setAdapter(viewPagerAdapter);
        tlMainActivity.setupWithViewPager(vpMainActivity);
    }

    private void configureTabIcons() {
        tlMainActivity.getTabAt(firstPosition()).setIcon(R.drawable.chats_tab_128);
        tlMainActivity.getTabAt(secondPosition()).setIcon(R.drawable.groups_tab_128);
        tlMainActivity.getTabAt(thirdPosition()).setIcon(R.drawable.contacts_tab_128);
    }

    private int thirdPosition() {
        return 2;
    }

    private int secondPosition() {
        return 1;
    }

    private int firstPosition() {
        return 0;
    }

    private void attachUI() {
        configureViewPager();
        configureTabIcons();
    }

    public ViewPager.OnPageChangeListener myOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            setCreateNewGroupChatButtonVisibility(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private void setCreateNewGroupChatButtonVisibility(int position) {
        if (isShowingGroupChatFragment(position)) {
            fabCreateNewGroupChat.setVisibility(View.VISIBLE);
        } else {
            if (fabCreateNewGroupChat.getVisibility() == View.VISIBLE) {
                fabCreateNewGroupChat.setVisibility(View.INVISIBLE);
            }
        }
    }

    private boolean isShowingGroupChatFragment(int position) {
        return position == 1;
    }

    public void showDialogDeleteChat(final int chatId, final int position, final ChatPresenter chatPresenter,
                                     final ArrayList<Chat> chats, final ChatsAdapter chatsAdapter) {

        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        handleDeleteChatDialogButtonClick(button, chatPresenter, chatId, chats, position, chatsAdapter);
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(R.string.delete_chat)
                .setPositiveButton(R.string.delete, dialogClickListener)
                .setNegativeButton(R.string.cancel, dialogClickListener)
                .show();
    }

    private void handleDeleteChatDialogButtonClick(int button, ChatPresenter chatPresenter, int chatId, ArrayList<Chat> chats, int position, ChatsAdapter chatsAdapter) {
        if (button == DialogInterface.BUTTON_POSITIVE) {
            chatPresenter.deleteChatById(chatId, MainActivity.this);
            chats.remove(position);
            chatsAdapter.notifyDataSetChanged();
        }
    }

    public void showDialogDeleteGroupChat(final String groupChatId, final int position, final GroupChatsAdapter groupChatsAdapter,
                                          final ArrayList<GroupChat> groupChats,
                                          final GroupChatPresenter groupChatPresenter) {

        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        handleDeleteGroupChatDialogButtonClick(button, groupChatPresenter, groupChatId, groupChats, position, groupChatsAdapter);
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(R.string.delete_group_chat)
                .setPositiveButton(R.string.delete, dialogClickListener)
                .setNegativeButton(R.string.cancel, dialogClickListener)
                .show();
    }

    private void handleDeleteGroupChatDialogButtonClick(int button, GroupChatPresenter groupChatPresenter, String groupChatId, ArrayList<GroupChat> groupChats, int position, GroupChatsAdapter groupChatsAdapter) {
        if (button == DialogInterface.BUTTON_POSITIVE) {
            groupChatPresenter.deleteGroupChat(groupChatId, MainActivity.this);
            groupChats.remove(position);
            groupChatsAdapter.notifyDataSetChanged();
        }
    }

    // endregion

    // region Activity Life Cycle And Other Overrides

    @Override
    protected void onRestart() {
        super.onRestart();
        attachUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        runOnDestroyRoutine();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void runOnDestroyRoutine() {
        disconnectSocket();
        unbindButterKnife();
    }

    private void unbindButterKnife() {
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    private void disconnectSocket() {
        if (socket != null) {
            socket.disconnect();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            rootCoordinator.navigateToChatsterSettingsActivityFromMain(MainActivity.this);
            return true;
        } else if (id == R.id.action_privacy_policy) {
            rootCoordinator.navigateToPrivacyPolicyWebPage(MainActivity.this);
            return true;
        } else if (id == R.id.action_terms_policies) {
            rootCoordinator.navigateToChatsterTermsAndPoliciesWebPage(MainActivity.this);
            return true;
        } else if (id == R.id.action_gdpr) {
            rootCoordinator.navigateToChatsterGDPRWebPage(MainActivity.this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // endregion

    // region Create New Group ClickListener

    @OnClick(R.id.fab)
    public void createNewGroupListener() {
        rootCoordinator.navigateToCreateGroupChatActivity(MainActivity.this);
    }

    // endregion

    // region Handle Notifications

    private void setUpConnectionToServer() {
        // default settings for all sockets
        IO.setDefaultOkHttpWebSocketFactory(OkHttpClientManager.setUpSecureClient());
        IO.setDefaultOkHttpCallFactory(OkHttpClientManager.setUpSecureClient());

        // set as an option
        IO.Options opts = new IO.Options();
        opts.transports = new String[]{WebSocket.NAME};
        opts.callFactory = OkHttpClientManager.setUpSecureClient();
        opts.webSocketFactory = OkHttpClientManager.setUpSecureClient();
        try {
            socket = IO.socket(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_GROUP_CHAT_PORT), opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        socket.connect();
    }

    private void runUserHasJoinedGroupChatRoutine(GroupChat groupChat) {
        setUpConnectionToServer();
        connectToGroupChat(groupChat);
        tellOtherGroupMembersYouJoined(groupChat);
    }

    private void deleteGroupChatInvitation(String groupChatId, long userId) {

        Disposable subscribeDeleteGroupChatInvitation =
                mainPresenter.deleteGroupChatInvitation(groupChatId, userId)
                        .subscribeOn(Schedulers.io())
                        .subscribe(res -> {
                        }, throwable -> {
                        });
    }

    private void tellOtherGroupMembersYouJoined(GroupChat groupChat) {
        if (mainPresenter.hasInternetConnection()) {
            socket.emit(
                    ConstantRegistry.CHATSTER_GROUP_CHAT_MESSAGE,
                    ConstantRegistry.JOINED_GROUP_CHAT,
                    createUserJoinedGroupMessage()
            );

            deleteGroupChatInvitation(groupChat.get_id(), mainPresenter.getUserId(MainActivity.this));
        } else {
            mainChatsterToast.notifyUserNoInternet();
        }
    }

    private void connectToGroupChat(GroupChat groupChat) {
        socket.emit(
                ConstantRegistry.CHATSTER_OPEN_GROUP_CHAT_MESSAGE,
                mainPresenter.getUserId(MainActivity.this),
                groupChat.get_id()
        );
    }

    @NonNull
    private String createUserJoinedGroupMessage() {
        return String.valueOf(mainPresenter.getUserId(MainActivity.this))
                .concat(ConstantRegistry.CHATSTER_SPACE_STRING)
                .concat(getString(R.string.joined_the_group));
    }

    private void handleNotificationAction() {
        if (getIntent().getExtras() != null) {
            handleGroupChatInvitationNotificationAction();
        }
    }

    private void handleGroupChatInvitationNotificationAction() {
        if (getIntent().getAction().equals(ConstantRegistry.ACCEPT_GROUP_INVITATION_REQUEST)) {
            handleAcceptGroupInvitationRequest();
        } else if (getIntent().getAction().equals(ConstantRegistry.DECLINE_GROUP_INVITATION_REQUEST)) {
            handleDeclineGroupInvitationRequest();
        }
    }

    private void handleDeclineGroupInvitationRequest() {
        GroupChatInvitation groupChatInvitation = getIntent().getExtras()
                .getParcelable(ConstantRegistry.DECLINE_GROUP_INVITATION_INTENT);

        mainPresenter.deleteGroupChatInvitationNotification(groupChatInvitation.getGroupChatInvitationChatId(), this);
        deleteGroupChatInvitation(
                groupChatInvitation.getGroupChatInvitationChatId(),
                mainPresenter.getUserId(MainActivity.this)
        );

        removeNotifications();
    }

    private void handleAcceptGroupInvitationRequest() {
        GroupChatInvitation groupChatInvitation = getIntent().getExtras()
                .getParcelable(ConstantRegistry.ACCEPT_GROUP_INVITATION_INTENT);

        if (groupChatInvitation != null) {
            GroupChat groupChat = createGroupChat(groupChatInvitation);
            if (mainPresenter.hasInternetConnection()) {
                processNewGroupChat(groupChatInvitation, groupChat);
            } else {
                mainChatsterToast.notifyUserNoInternet();
            }
        } else {
            mainChatsterToast.notifyUserSomethingWentWrong();
        }

        removeNotifications();
    }

    private void removeNotifications() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
    }

    private void processNewGroupChat(GroupChatInvitation groupChatInvitation, GroupChat groupChat) {
        mainPresenter.deleteGroupChatInvitationNotification(groupChatInvitation.getGroupChatInvitationChatId(), this);
        mainPresenter.insertGroupChat(groupChat, MainActivity.this);
        insertGroupChatMembers(groupChat);
        runUserHasJoinedGroupChatRoutine(groupChat);
    }

    private void insertGroupChatMembers(GroupChat groupChat) {
        for (long groupChatMemberId : groupChat.getGroupChatMembers()) {
            mainPresenter.insertGroupChatMember(groupChat.get_id(), groupChatMemberId, MainActivity.this);
        }
    }

    @NonNull
    private GroupChat createGroupChat(GroupChatInvitation groupChatInvitation) {
        GroupChat groupChat = new GroupChat();
        groupChat.setGroupChatName(groupChatInvitation.getGroupChatInvitationChatName());
        groupChat.set_id(groupChatInvitation.getGroupChatInvitationChatId());
        groupChat.setGroupChatAdminId(groupChatInvitation.getGroupChatInvitationSenderId());
        groupChat.setGroupChatMembers(groupChatInvitation.getGroupChatInvitationGroupChatMembers());
        groupChat.setGroupChatImage(groupChatInvitation.getGroupProfilePicPath());

        return groupChat;
    }

    // endregion

    // region Register New User

    private boolean userNotRegistered() {
        return mainPresenter.getUserName(MainActivity.this).equals(ConstantRegistry.DEFAULT);
    }

    private void registerUser() {
        rootCoordinator.navigateToIntroActivity(MainActivity.this);
        finish();
    }

    // endregion

    public void navigateToInviteActivity(Context context, String inviteeName) {
        rootCoordinator.navigateToInviteActivity(context, mainPresenter.getUserName(context), inviteeName);
    }

    public void openChat(Contact contact) {
        Intent contactIntent = new Intent(MainActivity.this, ChatActivity.class);
        contactIntent.putExtra(ConstantRegistry.CONTACT_REQUEST, contact);
        contactIntent.setAction(ConstantRegistry.CONTACTS_LIST);
        startActivity(contactIntent);
    }
}
