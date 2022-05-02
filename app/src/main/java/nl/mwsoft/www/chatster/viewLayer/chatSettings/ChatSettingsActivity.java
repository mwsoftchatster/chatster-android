package nl.mwsoft.www.chatster.viewLayer.chatSettings;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.Picasso;

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
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.coordinator.RootCoordinator;
import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.presenterLayer.chat.ChatPresenter;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.chatSettingsChatsterToast.ChatSettingsChatsterToast;
import nl.mwsoft.www.chatster.viewLayer.dialog.loadingDialog.LoadingDialogFragment;
import nl.mwsoft.www.chatster.modelLayer.model.Chat;
import nl.mwsoft.www.chatster.modelLayer.model.Contact;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import nl.mwsoft.www.chatster.modelLayer.helper.util.image.ImageCircleTransformUtil;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ChatSettingsActivity extends AppCompatActivity {

    @BindView(R.id.ivChatSettingsContactPic) ImageView ivChatSettingsContactPic;
    @BindView(R.id.ivChatSettingsBack) ImageView ivChatSettingsBack;
    @BindView(R.id.tvChatSettingsContactName) TextView tvChatSettingsContactName;
    @BindView(R.id.tvChatSettingsContactStatus) TextView tvChatSettingsContactStatus;
    @BindView(R.id.tvChatSettingsContactPhoneNumber) TextView tvChatSettingsContactPhoneNumber;
    @BindView(R.id.swChatSettingsUnsend) Switch swChatSettingsUnsend;
    @BindView(R.id.chatSettingsToolbar) Toolbar chatSettingsToolbar;
    private Socket socket;
    private LoadingDialogFragment loadingDialogFragment;
    private Chat chat;
    private Unbinder unbinder;
    private ChatPresenter chatPresenter;
    private RootCoordinator rootCoordinator;
    private ChatSettingsChatsterToast chatSettingsChatsterToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_chat_settings);
        unbinder = ButterKnife.bind(this);
        setSupportActionBar(chatSettingsToolbar);

        DependencyRegistry.shared.inject(this);

        handleOpenChatSettings();

        swChatSettingsUnsend.setOnCheckedChangeListener(switchListener);

        setUpConnectionToServer();
    }

    // region Configure

    public void configureWith(ChatPresenter chatPresenter, RootCoordinator rootCoordinator,
                              ChatSettingsChatsterToast chatSettingsChatsterToast){
        this.chatPresenter = chatPresenter;
        this.rootCoordinator = rootCoordinator;
        this.chatSettingsChatsterToast = chatSettingsChatsterToast;
    }

    // endregion

    // region Activity Life Cycle And Other Overrides

    @Override
    public void onDestroy() {
        super.onDestroy();
        runOnDestroyRoutine();
    }

    private void runOnDestroyRoutine() {
        disconnectSocket();
        unbindButterKnife();
    }

    private void unbindButterKnife() {
        if(unbinder != null){
            unbinder.unbind();
        }
    }

    private void disconnectSocket() {
        if(socket != null){
            socket.disconnect();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        navigateToChat();
    }

    private void navigateToChat() {
        rootCoordinator.navigateToChatActivity(ChatSettingsActivity.this, chat);
        finish();
    }

    // endregion

    // region Handle Open Chat Settings

    private void handleOpenChatSettings() {
        if(getIntent().getAction().equals(ConstantRegistry.CHATS_SETTINGS)){
            if(getIntent().getExtras().getParcelable(ConstantRegistry.CHAT_REQUEST) != null){
                chat = getIntent().getExtras().getParcelable(ConstantRegistry.CHAT_REQUEST);
                initChatSettings();
            }else{
                chatSettingsChatsterToast.notifyUserSomethingWentWrong();
            }
        }else{
            chatSettingsChatsterToast.notifyUserSomethingWentWrong();
        }
    }

    private void initChatSettings() {
        loadContactProfilePic();
        setCurrentAllowedToUnsend();
        configureContactDetails();
    }

    private void loadContactProfilePic() {
        if(hasProfilePic() && profilePicNotEmpty()){
            loadProfilePic();
        }else{
            loadDefaultProfilePic();
        }
    }

    private void loadDefaultProfilePic() {
        Picasso.with(this).load(R.drawable.user_512)
                .transform(new ImageCircleTransformUtil())
                .into(ivChatSettingsContactPic);
    }

    private void loadProfilePic() {
        Picasso.with(this)
                .load(ConstantRegistry.IMAGE_URL_PREFIX.concat(chatPresenter.getContactProfilePicUriById(this, chat.getContactId())))
                .transform(new ImageCircleTransformUtil())
                .into(ivChatSettingsContactPic);
    }

    private boolean profilePicNotEmpty() {
        return !chatPresenter.getContactProfilePicUriById(this, chat.getContactId())
                .equals(ConstantRegistry.CHATSTER_EMPTY_STRING);
    }

    private boolean hasProfilePic() {
        return chatPresenter.getContactProfilePicUriById(this, chat.getContactId()) != null;
    }

    private void setCurrentAllowedToUnsend() {
        swChatSettingsUnsend.setChecked(chatPresenter.getIsAllowedToUnsendByChatId(this, chat.getChatId()));
    }

    private void configureContactDetails() {
        Contact contact = chatPresenter.getContactById(this, chat.getContactId());
        tvChatSettingsContactName.setText(contact.getUserName());
        tvChatSettingsContactStatus.setText(contact.getStatusMessage());
        tvChatSettingsContactPhoneNumber.setText(ConstantRegistry.CHATSTER_PLUS.concat(String.valueOf(contact.getUserId())));
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
        socket.on(ConstantRegistry.CHATSTER_UPDATED_UNSEND, updatedUnsend);
    }

    private Emitter.Listener updatedUnsend = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String status =  args[0].toString();
                    closeLoadingDialog();
                    processUpdatedUnsend(status);
                }
            });
        }
    };

    private void processUpdatedUnsend(String status) {
        if(status.equals(ConstantRegistry.SUCCESS)){
            chatSettingsChatsterToast.notifyUserUnsendUpdated();
        }else{
            chatSettingsChatsterToast.notifyUserSomethingWentWrong();
        }
    }

    private void closeLoadingDialog() {
        if(loadingDialogFragment != null){
            loadingDialogFragment.dismiss();
        }
    }

    private CompoundButton.OnCheckedChangeListener switchListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked){
                if(chatPresenter.hasInternetConnection()){
                    processUnsendAllow();
                }else{
                    chatSettingsChatsterToast.notifyUserNoInternet();
                }
            }else{
                if(chatPresenter.hasInternetConnection()){
                    processUnsendForbid();
                }else{
                    chatSettingsChatsterToast.notifyUserNoInternet();
                }
            }
        }
    };

    private void processUnsendForbid() {
        showLoadingDialog();
        sendUnsendForbidMessageToServer();
        updateUnsendForbidLocally();
    }

    private void processUnsendAllow() {
        showLoadingDialog();
        sendUnsendAllowMessageToServer();
        updateUnsendAllowLocally();
    }

    private void updateUnsendForbidLocally() {
        chatPresenter.updateContactIsAllowedToUnsendByChatId(chat.getChatId(),
                ConstantRegistry.NOT_ALLOWED_TO_UNSEND,
                ChatSettingsActivity.this);
    }

    private void updateUnsendAllowLocally() {
        chatPresenter.updateContactIsAllowedToUnsendByChatId(chat.getChatId(),
                ConstantRegistry.ALLOWED_TO_UNSEND,
                ChatSettingsActivity.this);
    }

    private void sendUnsendAllowMessageToServer() {
        socket.emit(ConstantRegistry.CHATSTER_UNSEND_ALLOW,
                chatPresenter.getUserId(ChatSettingsActivity.this),
                chat.getContactId()
        );
    }

    private void sendUnsendForbidMessageToServer() {
        socket.emit(ConstantRegistry.CHATSTER_UNSEND_FORBID,
                chatPresenter.getUserId(ChatSettingsActivity.this),
                chat.getContactId()
        );
    }

    private void showLoadingDialog() {
        loadingDialogFragment = new LoadingDialogFragment();
        loadingDialogFragment.setCancelable(false);
        loadingDialogFragment.show(getSupportFragmentManager(), ConstantRegistry.LOADING);
    }

    // endregion

    // region Navigation

    @OnClick(R.id.ivChatSettingsBack)
    public void ivChatSettingsBackListener(){
        navigateToChat();
    }

    // endregion
}
