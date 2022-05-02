package nl.mwsoft.www.chatster.viewLayer.chatsterSettings;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.fabric.sdk.android.Fabric;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;
import nl.mwsoft.www.chatster.coordinator.RootCoordinator;
import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.presenterLayer.chatsterSettings.SettingsPresenter;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.chatsterSettingsChatsterToast.ChatsterSettingsChatsterToast;
import nl.mwsoft.www.chatster.viewLayer.dialog.loadingDialog.LoadingDialogFragment;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import nl.mwsoft.www.chatster.modelLayer.helper.util.image.ImageCircleTransformUtil;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ChatsterSettingsActivity extends AppCompatActivity {

    private static final int PICK_PROFILE_IMAGE_REQUEST = 2;
    @BindView(R.id.ivSettingsUserProfile) ImageView ivSettingsUserProfile;
    @BindView(R.id.tvSettingsUserProfileName) TextView tvSettingsUserProfileName;
    @BindView(R.id.tvSettingsUserProfileStatus) TextView tvSettingsUserProfileStatus;
    @BindView(R.id.btnUpdateUserProfileSettings) Button btnUpdateUserProfileSettings;
    @BindView(R.id.toolbar) Toolbar toolbar;
    private Socket socket;
    private LoadingDialogFragment loadingDialogFragment;
    private Unbinder unbinder;
    private SettingsPresenter settingsPresenter;
    private RootCoordinator rootCoordinator;
    private ChatsterSettingsChatsterToast chatsterSettingsChatsterToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_chatster_settings);
        unbinder = ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        DependencyRegistry.shared.inject(this);

        attachUI();

        setUpConnectionToServer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // region Configure

    public void configureWith(SettingsPresenter settingsPresenter, RootCoordinator rootCoordinator,
                              ChatsterSettingsChatsterToast chatsterSettingsChatsterToast){
        this.settingsPresenter = settingsPresenter;
        this.rootCoordinator = rootCoordinator;
        this.chatsterSettingsChatsterToast = chatsterSettingsChatsterToast;
    }

    // endregion

    // region UI

    private void attachUI() {
        setUserProfilePic(ivSettingsUserProfile);
        setUserProfileName(tvSettingsUserProfileName);
        setUserProfileStatus(tvSettingsUserProfileStatus);
    }

    public void setUserProfilePic(ImageView imageView){
        if(profilePicIsValid()){
            loadProfilePic(imageView);
        }else{
            loadDefaultProfilePic(imageView);
        }
    }

    private boolean profilePicIsValid() {
        return hasProfilePic() && profilePicUriNotEmpty();
    }

    private void loadDefaultProfilePic(ImageView imageView) {
        Picasso.with(ChatsterSettingsActivity.this).
                load(R.drawable.user_256).
                transform(new ImageCircleTransformUtil()).
                into(imageView);
    }

    private void loadProfilePic(ImageView imageView) {
        Picasso.with(ChatsterSettingsActivity.this).
                load(settingsPresenter.getUserProfilePicUri(ChatsterSettingsActivity.this)).
                transform(new ImageCircleTransformUtil()).
                into(imageView);
    }

    private boolean profilePicUriNotEmpty() {
        return !settingsPresenter.getUserProfilePicUri(
                ChatsterSettingsActivity.this).equals(ConstantRegistry.CHATSTER_EMPTY_STRING);
    }

    private boolean hasProfilePic() {
        return settingsPresenter.getUserProfilePicUri(ChatsterSettingsActivity.this) != null;
    }

    public void setUserProfileName(TextView textView){
        if(hasUserName() && userNameNotEmpty()){
            textView.setText(settingsPresenter.getUserName(this));
        }
    }

    private boolean userNameNotEmpty() {
        return !settingsPresenter.getUserName(this).equals(ConstantRegistry.CHATSTER_EMPTY_STRING);
    }

    private boolean hasUserName() {
        return settingsPresenter.getUserName(this) != null;
    }

    public void setUserProfileStatus(TextView textView){
        if(hasStatusMessage() && statusMessageNotEmpty()){
            textView.setText(settingsPresenter.getUserStatusMessage(this));
        }
    }

    private boolean statusMessageNotEmpty() {
        return !settingsPresenter.getUserStatusMessage(this).equals(ConstantRegistry.CHATSTER_EMPTY_STRING);
    }

    private boolean hasStatusMessage() {
        return settingsPresenter.getUserStatusMessage(this) != null;
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

    // region Activity Life Cycle And Other Overrides

    @Override
    protected void onRestart() {
        super.onRestart();
        attachUI();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        runOnDestroyRoutine();
    }

    private void runOnDestroyRoutine() {
        disconnectSocket();
        unbindButterKnife();
    }

    private void disconnectSocket() {
        if(socket != null){
            socket.disconnect();
        }
    }

    private void unbindButterKnife() {
        if(unbinder != null){
            unbinder.unbind();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    private boolean accessFilesPermissionIsGranted() {
        return ContextCompat.checkSelfPermission(
                ChatsterSettingsActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionWriteExternalStorage() {
        ActivityCompat.requestPermissions(ChatsterSettingsActivity.this,
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PROFILE_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            processUpdateProfilePic(uri);
        }
    }

    private void processUpdateProfilePic(Uri uri) {
        Picasso
            .with(ChatsterSettingsActivity.this)
            .load(uri)
            .transform(new ImageCircleTransformUtil())
            .into(ivSettingsUserProfile);

        settingsPresenter.updateUserProfilePic(uri.toString(), ChatsterSettingsActivity.this);
    }

    // endregion

    // region Socket.IO

    private void setUpConnectionToServer() {
        // default settings for all sockets
        IO.setDefaultOkHttpWebSocketFactory(OkHttpClientManager.setUpSecureClient());
        IO.setDefaultOkHttpCallFactory(OkHttpClientManager.setUpSecureClient());

        // set as an option
        IO.Options opts = new IO.Options();
        opts.transports = new String[] { WebSocket.NAME };
        opts.callFactory = OkHttpClientManager.setUpSecureClient();
        opts.webSocketFactory = OkHttpClientManager.setUpSecureClient();
        try {
            socket = IO.socket(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_API_USER_PORT),opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        socket.connect();
        socket.on(ConstantRegistry.CHATSTER_UPDATED_USER, updatedUser);
    }

    private Emitter.Listener updatedUser = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String status =  args[0].toString();
                    processProfileUpdated(status);
                }
            });
        }
    };

    private void processProfileUpdated(String status) {
        closeLoadingDialog();
        enableUpdateButton();
        notifyUserUpdatedStatus(status);
    }

    private void notifyUserUpdatedStatus(String status) {
        if(status.equals(ConstantRegistry.SUCCESS)){
            chatsterSettingsChatsterToast.notifyUserProfileUpdated();
        }else{
            chatsterSettingsChatsterToast.notifyUserSomethingWentWrong();
        }
    }

    private void enableUpdateButton() {
        btnUpdateUserProfileSettings.setEnabled(true);
    }

    private void closeLoadingDialog() {
        if(loadingDialogFragment != null){
            loadingDialogFragment.dismiss();
        }
    }

    // endregion

    // region OnClick Listeners

    @OnClick(R.id.btnUpdateUserProfileSettings)
    public void updateUserProfileSettings() {
        disableUpdateButton();
        showLoadingDialog();
        try {
            if(settingsPresenter.hasInternetConnection()){
                updateUserProfile();
            }else {
                chatsterSettingsChatsterToast.notifyUserNoInternet();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateUserProfile() throws IOException {
        socket.emit(ConstantRegistry.CHATSTER_UPDATE_USER,
                settingsPresenter.getUserId(ChatsterSettingsActivity.this),
                settingsPresenter.getUserStatusMessage(ChatsterSettingsActivity.this),
                settingsPresenter.encodeImageToString(ChatsterSettingsActivity.this,
                        Uri.parse(settingsPresenter.getUserProfilePicUri(ChatsterSettingsActivity.this)))
        );
    }

    private void disableUpdateButton() {
        btnUpdateUserProfileSettings.setEnabled(false);
    }

    private void showLoadingDialog() {
        loadingDialogFragment = new LoadingDialogFragment();
        loadingDialogFragment.setCancelable(false);
        loadingDialogFragment.show(getSupportFragmentManager(), ConstantRegistry.LOADING);
    }

    @OnClick(R.id.tvSettingsUserProfileStatus)
    public void changeUserStatusMessageListener() {
        rootCoordinator.navigateToEditUserStatusActivity(ChatsterSettingsActivity.this);
    }

    @OnClick(R.id.ivSettingsUserProfile)
    public void changeUserProfilePicListener() {
        if(accessFilesPermissionIsGranted()){
            showProfilePicChoice();
        }else{
            requestPermissionWriteExternalStorage();
        }
    }

    private void showProfilePicChoice() {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType(ConstantRegistry.CHATSTER_DOCUMENT_TYPE_IMAGE);
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);//Intent.ACTION_OPEN_DOCUMENT Intent.ACTION_GET_CONTENT
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, ConstantRegistry.CHATSTER_SELECT_PICTURE), PICK_PROFILE_IMAGE_REQUEST);
    }

    // endregion

}
