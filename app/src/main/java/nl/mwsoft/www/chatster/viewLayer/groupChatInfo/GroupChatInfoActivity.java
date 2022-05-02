package nl.mwsoft.www.chatster.viewLayer.groupChatInfo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.fabric.sdk.android.Fabric;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;
import nl.mwsoft.www.chatster.coordinator.RootCoordinator;
import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.presenterLayer.groupChatInfo.GroupChatInfoPresenter;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.groupChatInfoChatsterToast.GroupChatInfoChatsterToast;
import nl.mwsoft.www.chatster.viewLayer.dialog.loadingDialog.LoadingDialogFragment;
import nl.mwsoft.www.chatster.modelLayer.model.Contact;
import nl.mwsoft.www.chatster.modelLayer.model.ExitGroupChatReq;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChat;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import nl.mwsoft.www.chatster.modelLayer.helper.util.image.ImageCircleTransformUtil;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class GroupChatInfoActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1005;
    @BindView(R.id.ivGroupChatInfo)
    ImageView ivGroupChatInfo;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.ivAddNewMember)
    ImageView ivAddNewMember;
    @BindView(R.id.tvGroupChatInfoGroupName)
    TextView tvGroupChatInfoGroupName;
    @BindView(R.id.tvGroupChatInfoAdminName)
    TextView tvGroupChatInfoAdminName;
    @BindView(R.id.tvToolbarGroupChatInfo)
    TextView tvToolbarGroupChatInfo;
    @BindView(R.id.btnExitGroup)
    Button btnExitGroup;
    @BindView(R.id.btnUpdateGroup)
    Button btnUpdateGroup;
    @BindView(R.id.rvGroupChatInfo)
    RecyclerView rvGroupChatInfo;
    @BindView(R.id.groupInfoToolbar)
    Toolbar toolbar;
    private GroupChatInfoAdapter groupChatInfoAdapter;
    private ArrayList<Contact> contacts;
    private String groupChatId = "";
    private Socket socket;
    private LoadingDialogFragment loadingDialogFragment;
    private GroupChat groupChat;
    private CompositeDisposable disposable;
    private Disposable subscribeExitGroupChat;
    private GroupChatInfoPresenter groupChatInfoPresenter;
    private RootCoordinator rootCoordinator;
    private GroupChatInfoChatsterToast groupChatInfoChatsterToast;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_group_chat_info);
        unbinder = ButterKnife.bind(this);
        setUpConnectionToServer();

        disposable = new CompositeDisposable();

        loadingDialogFragment = new LoadingDialogFragment();

        DependencyRegistry.shared.inject(this);

        handleOpenGroupChatInfo();

        setGroupProfilePic();
    }

    // region Configure

    public void configureWith(GroupChatInfoPresenter groupChatInfoPresenter, RootCoordinator rootCoordinator,
                              GroupChatInfoChatsterToast groupChatInfoChatsterToast) {
        this.groupChatInfoPresenter = groupChatInfoPresenter;
        this.rootCoordinator = rootCoordinator;
        this.groupChatInfoChatsterToast = groupChatInfoChatsterToast;
    }

    // endregion

    // region UI

    private void attachUI() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvGroupChatInfo.setLayoutManager(mLayoutManager);
        rvGroupChatInfo.setItemAnimator(new DefaultItemAnimator());

        contacts = new ArrayList<>();
        contacts.addAll(groupChatInfoPresenter.getAllContactsByGroupChat(GroupChatInfoActivity.this, groupChatId));

        groupChatInfoAdapter = new GroupChatInfoAdapter(contacts);

        rvGroupChatInfo.setAdapter(groupChatInfoAdapter);
    }

    public void setGroupProfilePic() {
        if (groupHasProfilePic() && groupProfilePicIsNotEmpty()) {
            if (groupChatInfoPresenter.hasInternetConnection()) {
                if (userIsAdmin()) {
                    loadGroupProfilePicForAdmin();
                } else {
                    loadGroupProfilePicFromS3();
                }
            } else {
                loadDefaultGroupProfilePic();
            }
        } else {
            loadDefaultGroupProfilePic();
        }
    }

    private void loadGroupProfilePicFromS3() {
        Picasso.with(GroupChatInfoActivity.this).
                load(
                        ConstantRegistry.IMAGE_URL_PREFIX.concat(
                                groupChatInfoPresenter.getGroupProfilePicUriById(
                                        GroupChatInfoActivity.this, groupChatId
                                )
                        )
                ).
                transform(new ImageCircleTransformUtil()).
                into(ivGroupChatInfo);
    }

    private void loadGroupProfilePicForAdmin() {
        Picasso.with(GroupChatInfoActivity.this).
                load(
                        groupChatInfoPresenter.getGroupProfilePicUriById(
                                GroupChatInfoActivity.this,
                                groupChatId
                        )
                ).
                transform(new ImageCircleTransformUtil()).
                into(ivGroupChatInfo);
    }

    private void loadDefaultGroupProfilePic() {
        Picasso.with(GroupChatInfoActivity.this).
                load(R.drawable.group_profile_256).
                transform(new ImageCircleTransformUtil()).
                into(ivGroupChatInfo);
    }

    private boolean groupProfilePicIsNotEmpty() {
        return !groupChatInfoPresenter.getGroupProfilePicUriById(GroupChatInfoActivity.this, groupChatId).isEmpty();
    }

    private boolean groupHasProfilePic() {
        return groupChatInfoPresenter.getGroupProfilePicUriById(GroupChatInfoActivity.this, groupChatId) != null;
    }

    private boolean userIsNotAdmin() {
        return groupChatInfoPresenter.getUserId(this) !=
                groupChatInfoPresenter.getGroupChatById(this,
                        getIntent().getStringExtra(ConstantRegistry.GROUP_CHAT_ID)).getGroupChatAdminId();
    }

    public void showPopupDeniedPermission(View v) {
        LayoutInflater layoutInflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.permissions_denied_pop_up, null);
        Button btnPermissionsRequestDeniedGrant = (Button) popupView.findViewById(R.id.btnPermissionsRequestDeniedGrant);

        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        popupWindow.showAsDropDown(v);

        btnPermissionsRequestDeniedGrant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

    }


    // endregion

    // region Handle Group Chat Info Opened

    private void handleOpenGroupChatInfo() {
        if (!getIntent().getStringExtra(ConstantRegistry.GROUP_CHAT_ID).equals(null)) {
            groupChatId = getIntent().getStringExtra(ConstantRegistry.GROUP_CHAT_ID);
            if (userIsAdmin()) {
                configureAdminUI();
            } else {
                configureNotAdminUI();
            }
            groupChat = groupChatInfoPresenter.getGroupChatById(GroupChatInfoActivity.this, groupChatId);
            tvGroupChatInfoGroupName.setText(groupChat.getGroupChatName());
            if (userIsNotAdmin()) {
                tvGroupChatInfoAdminName.setText(getString(R.string.group_admin_is,
                        groupChatInfoPresenter.getContactById(GroupChatInfoActivity.this, groupChat.getGroupChatAdminId())
                                .getUserName()));
            }

            attachUI();
        } else {
            groupChatInfoChatsterToast.notifyUserSomethingWentWrong();
        }
    }

    private void configureNotAdminUI() {
        ivGroupChatInfo.setEnabled(false);
        btnExitGroup.setEnabled(true);
    }

    private void configureAdminUI() {
        ivGroupChatInfo.setClickable(true);
        ivGroupChatInfo.setVisibility(View.VISIBLE);
        ivGroupChatInfo.setEnabled(true);
        ivAddNewMember.setVisibility(View.VISIBLE);
        btnUpdateGroup.setVisibility(View.VISIBLE);
        btnExitGroup.setVisibility(View.GONE);
    }

    private boolean userIsAdmin() {
        return groupChatInfoPresenter.getUserId(this) ==
                groupChatInfoPresenter.getGroupChatById(this,
                        getIntent().getStringExtra(ConstantRegistry.GROUP_CHAT_ID)).getGroupChatAdminId();
    }

    // endregion

    // region Activity Life Cycle And Other Overrides

    @Override
    protected void onRestart() {
        super.onRestart();
        setGroupProfilePic();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        runOnDestroyRoutine();
    }

    private void runOnDestroyRoutine() {
        disconnectSocket();
        cleanUpData();
        unbindButterKnife();
    }

    private void disconnectSocket() {
        if (socket != null) {
            socket.disconnect();
        }
    }

    private void unbindButterKnife() {
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    private void cleanUpData() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (getIntent().getIntExtra(ConstantRegistry.GROUP_CHAT_ID, 0) > 0) {
            if (userIsAdmin()) {
                getMenuInflater().inflate(R.menu.menu_group_chat_info, menu);
            }
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_group_add_contact) {

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            groupChatInfoPresenter.updateGroupProfilePic(uri.toString(), groupChatId, GroupChatInfoActivity.this);
            setGroupProfilePic();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigateToGroupChat();
    }

    private boolean accessFilesPermissionIsGranted() {
        if (ContextCompat.checkSelfPermission(GroupChatInfoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermissionWriteExternalStorage() {
        ActivityCompat.requestPermissions(GroupChatInfoActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                ConstantRegistry.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ConstantRegistry.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showProfilePicChoice();
                } else {
                    showPopupDeniedPermission(findViewById(android.R.id.content).getRootView());
                }
                return;
            }
        }
    }

    // endregion

    // region Socket.IO

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
        socket.on(ConstantRegistry.CHATSTER_UPDATED_GROUP, updatedGroup);
    }

    private Emitter.Listener updatedGroup = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String response = args[0].toString();
                    handleGroupUpdatedResponse(response);
                }
            });
        }
    };

    private void handleGroupUpdatedResponse(String response) {
        // hide dialog fragment here
        loadingDialogFragment.dismiss();

        if (response.equals(ConstantRegistry.ERROR)) {
            groupChatInfoChatsterToast.notifyUserSomethingWentWrong();
        } else {
            groupChatInfoPresenter.updateGroupProfilePic(response, groupChatId, GroupChatInfoActivity.this);
            groupChatInfoChatsterToast.notifyUserGroupUpdated();
        }
    }

    private void sendUpdateToServer() {
        try {
            socket.emit(ConstantRegistry.CHATSTER_UPDATE_GROUP,
                    groupChatId,
                    groupChatInfoPresenter.
                            getGroupChatById(GroupChatInfoActivity.this, groupChatId).getGroupChatStatusMessage(),
                    groupChatInfoPresenter.encodeImageToString(GroupChatInfoActivity.this,
                            Uri.parse(groupChatInfoPresenter.getGroupProfilePicUriById(GroupChatInfoActivity.this, groupChatId)))
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // endregion

    // region Dialog

    private void showCancelableLoadingDialog() {
        loadingDialogFragment.setCancelable(true);
        loadingDialogFragment.show(getSupportFragmentManager(), ConstantRegistry.LOADING);
    }

    private void closeLoadingDialog() {
        if (loadingDialogFragment != null) {
            loadingDialogFragment.dismiss();
        }
    }

    private void showLoadingDialog() {
        loadingDialogFragment = new LoadingDialogFragment();
        loadingDialogFragment.setCancelable(false);
        loadingDialogFragment.show(getSupportFragmentManager(), ConstantRegistry.LOADING);
    }

    // endregion

    // region OnClick Listeners

    @OnClick(R.id.btnUpdateGroup)
    public void updateGroupProfileSettings() {
        showCancelableLoadingDialog();
        sendUpdateToServer();
    }

    @OnClick(R.id.ivGroupChatInfo)
    public void changeGroupPicListener() {
        if (!accessFilesPermissionIsGranted()) {
            requestPermissionWriteExternalStorage();

            return;
        }

        showProfilePicChoice();
    }

    private void showProfilePicChoice() {
        Intent intent = new Intent();

        // Show only images, no videos or anything else.
        intent.setType(ConstantRegistry.CHATSTER_DOCUMENT_TYPE_IMAGE);
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);//Intent.ACTION_OPEN_DOCUMENT Intent.ACTION_GET_CONTENT
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Always show the chooser (if there are multiple options available).
        startActivityForResult(Intent.createChooser(intent, ConstantRegistry.CHATSTER_SELECT_PICTURE), PICK_IMAGE_REQUEST);
    }

    @OnClick(R.id.btnExitGroup)
    public void exitGroupListener() {
        if (!groupChatInfoPresenter.hasInternetConnection()) {
            groupChatInfoChatsterToast.notifyUserNoInternet();

            return;
        }

        exitGroupChat(createExitGroupChatReq());
    }

    @NonNull
    private ExitGroupChatReq createExitGroupChatReq() {
        ExitGroupChatReq exitGroupChatRequest = new ExitGroupChatReq();
        exitGroupChatRequest.setUserId(groupChatInfoPresenter.getUserId(GroupChatInfoActivity.this));
        exitGroupChatRequest.setGroupChatId(groupChatId);

        return exitGroupChatRequest;
    }

    @OnClick(R.id.ivBack)
    public void backListener() {
        navigateToGroupChat();
    }

    private void navigateToGroupChat() {
        rootCoordinator.navigateToGroupChatActivity(GroupChatInfoActivity.this, groupChatId);
        finish();
    }

    @OnClick(R.id.ivAddNewMember)
    public void addNewMemberListener() {
        rootCoordinator.navigateToAddNewGroupMembersActivity(GroupChatInfoActivity.this, groupChatId);
    }

    // endregion

    // region Exit Group Chat

    private void exitGroupChat(ExitGroupChatReq exitGroupChatReq) {
        showLoadingDialog();

        subscribeExitGroupChat = groupChatInfoPresenter.exitGroupChat(exitGroupChatReq, GroupChatInfoActivity.this)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    closeLoadingDialog();
                    handleExitGroupResponse(result);
                }, Throwable::printStackTrace);

        disposable.add(subscribeExitGroupChat);
    }

    private void handleExitGroupResponse(ExitGroupChatReq result) {
        if (result.getGroupChatId() == null) {
            return;
        }

        if (result.getUserId() == 0) {
            groupChatInfoChatsterToast.notifyUserSomethingWentWrong();

            return;
        }

        groupChatInfoChatsterToast.notifyUserLeftGroup();
        groupChatInfoPresenter.deleteGroupChat(GroupChatInfoActivity.this, result.getGroupChatId());
        rootCoordinator.navigateToMainActivityAfterExitGroup(GroupChatInfoActivity.this);

        finish();
    }

    // endregion

}
