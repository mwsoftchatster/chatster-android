package nl.mwsoft.www.chatster.viewLayer.groupChat;

import android.Manifest;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

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
import nl.mwsoft.www.chatster.modelLayer.event.chat.SpeechToTextMessageEvent;
import nl.mwsoft.www.chatster.modelLayer.event.groupChat.DeleteGroupChatMessageEvent;
import nl.mwsoft.www.chatster.modelLayer.event.groupChat.ImageGroupChatMessageEvent;
import nl.mwsoft.www.chatster.modelLayer.event.groupChat.TextGroupChatMessageEvent;
import nl.mwsoft.www.chatster.modelLayer.event.groupChat.UnsendGroupChatMessageEvent;
import nl.mwsoft.www.chatster.modelLayer.model.OneTimeGroupKeyPair;
import nl.mwsoft.www.chatster.modelLayer.model.OneTimeGroupPublicKey;
import nl.mwsoft.www.chatster.presenterLayer.groupChat.GroupChatPresenter;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.groupChatChatsterToast.GroupChatChatsterToast;
import nl.mwsoft.www.chatster.viewLayer.imageDetail.ImageDetailActivity;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChat;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChatMessage;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChatOfflineMessage;
import nl.mwsoft.www.chatster.modelLayer.model.ImageDetailRequest;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry.GROUP_CHATS_LIST;
import static nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry.GROUP_CHATS_LIST_REQUEST;

public class GroupChatActivity extends AppCompatActivity {

    @BindView(R.id.ivGroupChatBack) ImageView ivGroupChatBack;
    @BindView(R.id.tvToolbarGroupChatName) TextView tvToolbarGroupChatName;
    @BindView(R.id.tvToolbarGroupChatTapInfo) TextView tvToolbarGroupChatTapInfo;
    private ArrayList<GroupChatMessage> groupChatMessages;
    private Socket socket;
    private GroupChat groupChat;
    private ClipboardManager clipboard;
    private ClipData clip;
    @BindView(R.id.groupChatToolbar) Toolbar toolbar;
    private Uri photoURI;
    private HashMap<String,Integer> unsendMessageRegistry;
    private CompositeDisposable disposable;
    private Disposable subscribeInitializeUnsendMessageRegistry;
    private GroupChatPresenter groupChatPresenter;
    private RootCoordinator rootCoordinator;
    private GroupChatChatsterToast groupChatChatsterToast;
    private Unbinder unbinder;
    public static String FRAGMENT_TAG = "fragment_tag";
    private ArrayList<String> groupOneTimePublicKeyUUIDS;
    private boolean hasBeenOpenedFromNotification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_group_chat);
        unbinder = ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        initAttributes();

        DependencyRegistry.shared.inject(this);

        attachUI();

        setUpConnectionToServer();

        handleGroupChatOpen();
    }



    // region Configure

    public void configureWith(GroupChatPresenter groupChatPresenter, RootCoordinator rootCoordinator,
                              GroupChatChatsterToast groupChatChatsterToast){
        this.groupChatPresenter = groupChatPresenter;
        this.rootCoordinator = rootCoordinator;
        this.groupChatChatsterToast = groupChatChatsterToast;
    }

    private void initAttributes() {
        disposable = new CompositeDisposable();
        unsendMessageRegistry = new HashMap<>();
        groupChat = new GroupChat();
        groupChatMessages = new ArrayList<>();
        groupOneTimePublicKeyUUIDS = new ArrayList<>();
    }

    // endregion

    //region Show Fragment

    private void showGroupChatFragment(){
        GroupChatFragment groupChatFragment = GroupChatFragment.newInstance(groupChatMessages);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_group_chat_container, groupChatFragment, FRAGMENT_TAG)
                .commit();
    }

    // endregion

    // region Image Details

    public void showImageDetails(ImageDetailRequest imageDetailRequest, GroupChatMessage groupChatMessage){
        imageDetailRequest.setSenderName(groupChatPresenter.getContactNameById(GroupChatActivity.this,
                groupChatMessage.getSenderId()));

        Intent imageDetailIntent = new Intent(GroupChatActivity.this, ImageDetailActivity.class);
        imageDetailIntent.putExtra(ConstantRegistry.IMAGE_DETAIL_REQUEST, imageDetailRequest);
        imageDetailIntent.setAction(ConstantRegistry.IMAGE_DETAIL);
        startActivity(imageDetailIntent);
        finish();
    }

    //endregion

    // region UI

    private void attachUI() {
        // Gets a handle to the clipboard service.
        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    }

    // endregion

    // region Dialogs

    public void showPopupImageMessageOptions(View v, final GroupChatMessage message, final int position) {

        LayoutInflater layoutInflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.selected_image_message_actions_pop_up, null);
        ImageView ivImageMessagePopUpDelete = (ImageView) popupView.findViewById(R.id.ivImageMessagePopUpDelete);
        ImageView ivImageMessagePopUpUnsend = (ImageView) popupView.findViewById(R.id.ivImageMessagePopUpUnsend);
        ImageView ivImageMessagePopUpClose = (ImageView) popupView.findViewById(R.id.ivImageMessagePopUpClose);

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

        ivImageMessagePopUpClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        ivImageMessagePopUpDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processDeleteMessage(message, position);
                unsendMessageRegistry.remove(position);
                sendDeleteMessageToFragment();
                popupWindow.dismiss();
            }
        });

        ivImageMessagePopUpUnsend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(isUsersMessage(message)){
                    unsendMessageRequestToServer(message);
                    processUnsendImageLocally(message, position);
                    sendUnsendMessageEventToFragment(message, position);
                }else{
                    groupChatChatsterToast.notifyUserCanOnlyUnsendOwnMessages();
                }
                popupWindow.dismiss();
            }
        });
    }

    private void processUnsendImageLocally(GroupChatMessage message, int position) {
        groupChatPresenter.updateMessageUnsentByUUID(message.getUuid(),GroupChatActivity.this);
        updateImageMessageInList(position);
        unsendMessageRegistry.remove(position);
    }

    private void updateImageMessageInList(int position) {
        groupChatMessages.get(position).setMsgType(ConstantRegistry.TEXT);
        groupChatMessages.get(position).setBinaryMessageFilePath(Uri.EMPTY);
        groupChatMessages.get(position).setMessageText(getString(R.string.message_deleted));
    }

    private void processDeleteMessage(GroupChatMessage message, int position) {
        groupChatPresenter.deleteGroupChatMessage(message.getGetGroupChatMessageId(),GroupChatActivity.this);
        groupChatMessages.remove(position);
    }

    public void showPopupMessageOptions(View v, final GroupChatMessage message, final int position) {

        LayoutInflater layoutInflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.selected_message_actions_pop_up, null);
        ImageView ivMessagePopUpCopy = (ImageView) popupView.findViewById(R.id.ivMessagePopUpCopy);
        ImageView ivMessagePopUpDelete = (ImageView) popupView.findViewById(R.id.ivMessagePopUpDelete);
        ImageView ivMessagePopUpUnsend = (ImageView) popupView.findViewById(R.id.ivMessagePopUpUnsend);
        ImageView ivMessagePopUpClose = (ImageView) popupView.findViewById(R.id.ivMessagePopUpClose);


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


        ivMessagePopUpClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        ivMessagePopUpDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processDeleteMessage(v, message, position);
                sendDeleteMessageToFragment();
                popupWindow.dismiss();
            }
        });

        ivMessagePopUpCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyMessageText(message);
                popupWindow.dismiss();
                groupChatChatsterToast.notifyUserMessageCopied();
            }
        });

        ivMessagePopUpUnsend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(userCantUnsendThisMessage(message)){
                    groupChatChatsterToast.notifyUserCantUnsendMessages();
                }else{
                    if(isUsersMessage(message)){
                        unsendMessageRequestToServer(message);
                        processUnsendMessageLocally(message, position);
                        sendUnsendMessageEventToFragment(message, position);
                    }else{
                        groupChatChatsterToast.notifyUserCanOnlyUnsendOwnMessages();
                    }
                }

                popupWindow.dismiss();
            }
        });
    }

    private boolean isUsersMessage(GroupChatMessage message) {
        return groupChatPresenter.getUserId(GroupChatActivity.this) == message.getSenderId();
    }

    private boolean userCantUnsendThisMessage(GroupChatMessage message) {
        return message.getMessageText().equals(getString(R.string.message_deleted))
                && isUsersMessage(message);
    }

    private void processDeleteMessage(View v, GroupChatMessage message, int position) {
        groupChatPresenter.deleteGroupChatMessage(message.getGetGroupChatMessageId(),v.getContext());
        groupChatMessages.remove(position);
        unsendMessageRegistry.remove(position);
    }

    private void sendDeleteMessageToFragment() {
        EventBus.getDefault().post(new DeleteGroupChatMessageEvent(groupChatMessages));
    }

    private void sendUnsendMessageEventToFragment(GroupChatMessage message, int position) {
        EventBus.getDefault().post(new UnsendGroupChatMessageEvent(message.getMessageText(), position));
    }

    private void processUnsendMessageLocally(GroupChatMessage message, int position) {
        groupChatPresenter.updateMessageUnsentByUUID(message.getUuid(),GroupChatActivity.this);
        groupChatMessages.get(position).setMessageText(getString(R.string.message_deleted));
        unsendMessageRegistry.remove(position);
    }

    private void unsendMessageRequestToServer(GroupChatMessage message) {
        socket.emit(ConstantRegistry.CHATSTER_UNSEND_MESSAGE,
                groupChatPresenter.getUserId(GroupChatActivity.this),
                groupChat.get_id(),
                message.getUuid());
    }

    private void copyMessageText(GroupChatMessage message) {
        // Creates a new text clip to put on the clipboard
        clip = ClipData.newPlainText(ConstantRegistry.CHATSTER_PASTE, message.getMessageText());
        // Set the clipboard's primary clip.
        clipboard.setPrimaryClip(clip);
    }

    public void showPopupSendAttachment(View v) {
        LayoutInflater layoutInflater = (LayoutInflater) v.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.send_attachment_pop_up, null);
        ImageView ivSendAttachmentGalleryPicture = (ImageView) popupView.findViewById(R.id.ivSendAttachmentGalleryPicture);
        ImageView ivSendAttachmentTakePicture = (ImageView) popupView.findViewById(R.id.ivSendAttachmentTakePicture);
        ImageView ivSendAttachmentPopUpClose = (ImageView) popupView.findViewById(R.id.ivSendAttachmentPopUpClose);

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


        ivSendAttachmentPopUpClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        ivSendAttachmentGalleryPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                // Show only images
                intent.setType(ConstantRegistry.CHATSTER_DOCUMENT_TYPE_IMAGE);
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);//Intent.ACTION_OPEN_DOCUMENT Intent.ACTION_GET_CONTENT
                // intent.addCategory(Intent.CATEGORY_OPENABLE);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, ConstantRegistry.CHATSTER_SELECT_PICTURE),
                        ConstantRegistry.PICK_IMAGE_REQUEST);

                popupWindow.dismiss();
            }
        });

        ivSendAttachmentTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = groupChatPresenter.createImageFile(GroupChatActivity.this);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        photoURI = FileProvider.getUriForFile(GroupChatActivity.this,
                                ConstantRegistry.CHATSTER_FILE_PROVIDER,
                                photoFile);

                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, ConstantRegistry.REQUEST_IMAGE_CAPTURE);
                    }
                }

                popupWindow.dismiss();
            }
        });
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

    // region Group Info

    @OnClick(R.id.tvToolbarGroupChatTapInfo)
    public void groupInfoListener(){
        rootCoordinator.navigateToGroupChatInfoActivity(GroupChatActivity.this, groupChat.get_id());
        finish();
    }

    @OnClick(R.id.ivGroupChatBack)
    public void ivGroupChatBackListener(){
        rootCoordinator.navigateToMainActivity(GroupChatActivity.this);
        finish();
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
        cleanUpData();
        unbindButterKnife();
    }

    private void disconnectSocket() {
        if(socket != null){
            socket.disconnect();
        }
    }

    private void unbindButterKnife() {
        if(unbinder != null) {
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
        getMenuInflater().inflate(R.menu.menu_group_chat, menu);
        return true;
    }

    private boolean accessFilesPermissionIsGranted() {
        if (ContextCompat.checkSelfPermission(GroupChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }else{
            return false;
        }
    }

    private void requestPermissionWriteExternalStorage() {
        ActivityCompat.requestPermissions(GroupChatActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                ConstantRegistry.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_group_attachment) {
            if(accessFilesPermissionIsGranted()){
                showPopupSendAttachment(toolbar.getRootView());
            }else{
                requestPermissionWriteExternalStorage();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ConstantRegistry.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showPopupSendAttachment(toolbar.getRootView());
                } else {
                    showPopupDeniedPermission(toolbar.getRootView());
                }
                return;
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantRegistry.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            handlePickImageRequest(data);
        }else if (requestCode == ConstantRegistry.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            handleImageCaptureRequest();
        }else if (requestCode == ConstantRegistry.SPEECH_INPUT_REQUEST && resultCode == RESULT_OK && data != null) {
            handleSpeechToTextRequest(data);
        }
    }

    private void handlePickImageRequest(Intent data) {
        Uri uri = data.getData();
        handleOutgoingImageMessage(groupChatPresenter.getUserId(GroupChatActivity.this),groupChat.get_id(),uri);
    }

    private void handleSpeechToTextRequest(Intent data) {
        ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        processSpeechToTextResult(result);
    }

    private void processSpeechToTextResult(ArrayList<String> result) {
        if(resultNoNull(result)){
            if(resultNotEmpty(result)){
                sendSpeechToTextMessageToFragment(result);
            }else{
                groupChatChatsterToast.notifyUserCantSendEmptyMessage();
            }
        }else{
            groupChatChatsterToast.notifyUserSomethingWentWrong();
        }
    }

    private boolean resultNotEmpty(ArrayList<String> result) {
        return !result.get(0).equals(ConstantRegistry.CHATSTER_EMPTY_STRING);
    }

    private boolean resultNoNull(ArrayList<String> result) {
        return !result.get(0).equals(null);
    }

    private void sendSpeechToTextMessageToFragment(ArrayList<String> result) {
        EventBus.getDefault().post(new SpeechToTextMessageEvent(result.get(0)));
    }

    private void handleImageCaptureRequest() {
        if(photoURI != null){
            try {
                Bitmap photoBitmap = groupChatPresenter.decodeSampledBitmap(GroupChatActivity.this, photoURI);
                photoURI = groupChatPresenter.saveIncomingImageMessage(GroupChatActivity.this, photoBitmap);
                handleOutgoingImageMessage(groupChatPresenter.getUserId(GroupChatActivity.this),groupChat.get_id(),photoURI);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            groupChatChatsterToast.notifyUserSomethingWentWrong();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(hasBeenOpenedFromNotification){
            rootCoordinator.navigateToMainActivity(GroupChatActivity.this);
            finish();
        }else{
            finish();
        }
    }

    // endregion

    // region Handle Group Chat Opened

    private void handleGroupChatOpen() {
        determineActionOrigin();
        showGroupChatFragment();
    }

    private void determineActionOrigin() {
        if (getIntent().getAction() != null){
            // GroupChatActivity is being opened from GroupChatInfoActivity or ImageDetailActivity
            if (getIntent().getAction().equals(ConstantRegistry.GROUP_CHAT_ID)) {
                handleOpenFromImageDetailInfo();
            }else if (getIntent().getAction().equals(GROUP_CHATS_LIST)) {
                handleOpenFromGroupChatList();
            } else if (getIntent().getAction().equals(ConstantRegistry.READ_GROUP_CHAT_MESSAGE_REQUEST)) {
                handleReadGroupChatMessage();
            }
        }
    }

    private void handleOpenFromGroupChatList() {
        if(parcelableFromGroupChatsListHasValue()){
            groupChat = getIntent().getExtras().getParcelable(GROUP_CHATS_LIST_REQUEST);
            if(groupChat != null){
                setUpGroup();
            }else{
                groupChatChatsterToast.notifyUserSomethingWentWrong();
            }
        }
    }

    private boolean parcelableFromGroupChatsListHasValue() {
        try{
            return getIntent().getExtras().getParcelable(GROUP_CHATS_LIST_REQUEST) != null;
        }catch (NullPointerException e){
            e.printStackTrace();
            return false;
        }

    }

    private void setUpGroup() {
        configureGroupName();
        if(groupHasMessages()){
            initUnsendMessageRegistry();
            readAllGroupMessages();
        }
        connectToGroupChat();
    }

    private void configureGroupName() {
        tvToolbarGroupChatName.setText(groupChat.getGroupChatName());
    }

    private void connectToGroupChat() {
        // check if internet connection is available, if so, proceed to emit my id, contact's id, and chat name
        if(groupChatPresenter.hasInternetConnection()){
            socket.emit(ConstantRegistry.CHATSTER_OPEN_GROUP_CHAT_MESSAGE,
                    groupChatPresenter.getUserId(GroupChatActivity.this),
                    groupChat.get_id());
        }else{
            groupChatChatsterToast.notifyUserNoInternet();
        }
    }

    private boolean groupHasMessages() {
        return groupChatPresenter.getAllMessagesForGroupChatWithId(GroupChatActivity.this,
                groupChat.get_id()).size() > 0;
    }

    private void readAllGroupMessages() {
        groupChatMessages.addAll(groupChatPresenter.getAllMessagesForGroupChatWithId(GroupChatActivity.this,
                    groupChat.get_id()));
    }

    private void initUnsendMessageRegistry() {
        initializeUnsendMessageRegistry(
                groupChatPresenter.getAllMessagesForGroupChatWithId(GroupChatActivity.this,
                        groupChat.get_id()));
    }

    private void handleReadGroupChatMessage() {
        hasBeenOpenedFromNotification = true;
        if(parcelableFromGroupChatMessageHasValue()){
            GroupChatOfflineMessage groupChatOfflineMessage = getIntent().getExtras()
                    .getParcelable(ConstantRegistry.READ_GROUP_CHAT_MESSAGE_INTENT);
            groupChat = groupChatPresenter.getGroupChatById(GroupChatActivity.this,
                    groupChatOfflineMessage.getGroupChatId());
            if(groupChat != null){
                setUpGroupFromReadMessageRequest(groupChatOfflineMessage);
            }else{
                groupChatChatsterToast.notifyUserSomethingWentWrong();
            }
        }
    }

    private boolean parcelableFromGroupChatMessageHasValue() {
        try {
            return getIntent().getExtras().getParcelable(ConstantRegistry.READ_GROUP_CHAT_MESSAGE_INTENT) != null;
        }catch (NullPointerException e){
            e.printStackTrace();
            return false;
        }
    }

    private void setUpGroupFromReadMessageRequest(GroupChatOfflineMessage groupChatOfflineMessage) {
        configureGroupName();
        if(groupHasMessages()){
            initUnsendMessageRegistry();
            readAllGroupMessages();
            removeNotifications();
        }else{
            unsendMessageRegistry.put(groupChatOfflineMessage.getUuid(), 0);
        }
        connectToGroupChat();
    }

    private void removeNotifications() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancelAll();
    }

    private void handleOpenFromImageDetailInfo() {
        groupChat = groupChatPresenter.getGroupChatById(GroupChatActivity.this,
                getIntent().getStringExtra(ConstantRegistry.GROUP_CHAT_ID));
        if(groupChat != null){
            setUpGroup();
        }else{
            groupChatChatsterToast.notifyUserSomethingWentWrong();
        }
    }

    // endregion

    // region Socket.IO

    private void setUpConnectionToServer(){
        // default settings for all sockets
        IO.setDefaultOkHttpWebSocketFactory(OkHttpClientManager.setUpSecureClient());
        IO.setDefaultOkHttpCallFactory(OkHttpClientManager.setUpSecureClient());

        // set as an option
        IO.Options opts = new IO.Options();
        opts.transports = new String[] { WebSocket.NAME };
        opts.callFactory = OkHttpClientManager.setUpSecureClient();
        opts.webSocketFactory = OkHttpClientManager.setUpSecureClient();
        try {
            socket = IO.socket(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_GROUP_CHAT_PORT),opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        socket.connect();
        socket.on(ConstantRegistry.CHATSTER_HANDLE_GROUP_CHAT_MESSAGE, handleIncomingMessages);
        socket.on(ConstantRegistry.CHATSTER_UNSEND_MESSAGE, handleUnsendMessage);
        socket.on(ConstantRegistry.CHATSTER_MESSAGE_DELIVERY_STATUS, handleMessageDeliveryStatus);
        socket.on(ConstantRegistry.CHATSTER_GROUP_MESSAGE_RECEIVED, handleGroupMessageReceivedStatus);
    }

    private Emitter.Listener handleGroupMessageReceivedStatus = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String uuid = "";
                    String status = "";

                    try {
                        uuid = data.getString(ConstantRegistry.CHATSTER_MESSAGE_UUID);
                        status = data.getString(ConstantRegistry.CHATSTER_MESSAGE_STATUS);
                        if(statusIsSuccess(status)){
                            removeMessageReceivedVerification(uuid);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };


    private boolean statusIsSuccess(String status) {
        return status.equals(ConstantRegistry.SUCCESS);
    }

    private void removeMessageReceivedVerification(String uuid) {
        groupChatPresenter.deleteReceivedOnlineGroupMessageByUUID(uuid, GroupChatActivity.this);
    }

    private Emitter.Listener handleMessageDeliveryStatus = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String uuid = "";
                    String status = "";

                    try {
                        uuid = data.getString(ConstantRegistry.CHATSTER_MESSAGE_UUID);
                        status = data.getString(ConstantRegistry.CHATSTER_MESSAGE_STATUS);
                        if(statusIsSuccess(status)){
                            removeMessageFromMessageQueue(uuid);

                            for(String groupOneTimePublicKeyUUID : groupOneTimePublicKeyUUIDS){
                                groupChatPresenter.deleteOneTimeGroupKeyPairByUUID(
                                        groupOneTimePublicKeyUUID,
                                        GroupChatActivity.this
                                );
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private void removeMessageFromMessageQueue(String uuid) {
        groupChatPresenter.deleteGroupChatMessageQueueItemByUUID(uuid, GroupChatActivity.this);
    }

    private Emitter.Listener handleIncomingMessages = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    long senderId = 0;
                    String groupChatId = "";
                    String groupChatMessage = "";
                    String messageCreated = "";
                    String uuid = "";
                    String groupMemberPBKUUID = "";

                    try {
                        senderId = data.getLong(ConstantRegistry.CHATSTER_SENDER_ID);
                        groupChatId = data.getString(ConstantRegistry.CHATSTER_GROUP_CHAT_ID);
                        groupChatMessage = data.getString(ConstantRegistry.CHATSTER_MESSAGE_TEXT);
                        messageCreated = data.getString(ConstantRegistry.CHATSTER_MESSAGE_CREATED);
                        uuid = data.getString(ConstantRegistry.CHATSTER_MESSAGE_UUID);
                        groupMemberPBKUUID = data.getString(ConstantRegistry.CHATSTER_MESSAGE_PBK_UUID);
                        if(isTextMessage(data)){
                            handleIncomingTextMessage(senderId, groupChatId, groupChatMessage, messageCreated, uuid, groupMemberPBKUUID);
                        }else{
                            handleIncomingImageMessage(senderId, groupChatId, groupChatMessage, messageCreated, uuid, groupMemberPBKUUID);
                        }
                        saveMessageReceivedForLaterVerification(uuid);
                        acknowledgeMessageReceived(uuid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private void saveMessageReceivedForLaterVerification(String uuid) {
        groupChatPresenter.insertReceivedOnlineGroupMessage(uuid,GroupChatActivity.this);
    }

    private void acknowledgeMessageReceived(String uuid) {
        socket.emit(ConstantRegistry.CHATSTER_GROUP_MESSAGE_RECEIVED, uuid,
                groupChatPresenter.getUserId(GroupChatActivity.this));
    }

    private boolean isTextMessage(JSONObject data) throws JSONException {
        return data.getString(ConstantRegistry.CHATSTER_MESSAGE_TYPE).equals(ConstantRegistry.TEXT)
                || data.getString(ConstantRegistry.CHATSTER_MESSAGE_TYPE).equals(ConstantRegistry.JOINED_GROUP_CHAT);
    }

    private Emitter.Listener handleUnsendMessage = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String groupChatId = "";
                    String messageText = "";
                    String uuid = "";
                    long senderId = 0;

                    try {
                        senderId = data.getLong(ConstantRegistry.CHATSTER_SENDER_ID);
                        groupChatId = data.getString(ConstantRegistry.CHATSTER_GROUP_CHAT_ID);
                        messageText = data.getString(ConstantRegistry.CHATSTER_MESSAGE_TEXT);
                        uuid = data.getString(ConstantRegistry.CHATSTER_MESSAGE_UUID);

                        // Send an event to fragment
                        EventBus.getDefault().post(new UnsendGroupChatMessageEvent(getString(R.string.message_deleted),
                                unsendMessageRegistry.get(uuid)));
                        groupChatMessages.get(unsendMessageRegistry.get(uuid)).setMessageText(getString(R.string.message_deleted));
                        unsendMessageRegistry.remove(uuid);

                        groupChatPresenter.updateMessageUnsentByUUID(uuid,GroupChatActivity.this);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    // endregion

    // region Handle Messages

    public void sendMessageClickListener(String message){
        processMessage(message);
    }

    private void processMessage(String message) {
        if(groupChatPresenter.hasInternetConnection()){
            String uuid = groupChatPresenter.createUUID();
            GroupChatMessage groupChatMessage = processOutgoingMessageLocally(message, uuid);
            sendE2ETextMessage(
                groupChatMessage.getGroupChatId(),
                groupChatPresenter.getUserId(GroupChatActivity.this),
                groupChatMessage
            );
        }else{
            groupChatChatsterToast.notifyUserNoInternet();
        }
    }

    private void sendE2ETextMessage(String groupChatId, long userId, GroupChatMessage groupChatMessage){
        Disposable subscribeGetGroupOneTimeKeys = groupChatPresenter.getGroupOneTimeKeys(groupChatId, userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(res -> {
                    JsonArray groupMessages = new JsonArray();

                    OneTimeGroupKeyPair oneTimeGroupKeyPair = groupChatPresenter.
                            getGroupOneTimeKeyPair(GroupChatActivity.this, groupChatId, userId);

                    for(OneTimeGroupPublicKey oneTimeGroupPublicKey : res){
                        // for each of the group member pbks construct an encrypted message
                        byte[] groupMemberPublicKey = oneTimeGroupPublicKey.getOneTimeGroupPublicKey();

                        byte[] encryptSharedSecret = groupChatPresenter.generateEncryptSharedSecret(
                            groupMemberPublicKey,
                                oneTimeGroupKeyPair.getOneTimeGroupPrivateKey()
                        );

                        byte[] msgBytes = groupChatMessage.getMessageText().getBytes(StandardCharsets.UTF_8);

                        byte[] encryptedMessage = groupChatPresenter.encrypt(msgBytes, encryptSharedSecret);

                        byte[] hmac = groupChatPresenter.messageToHMAC(msgBytes, encryptSharedSecret);

                        byte[] signature = groupChatPresenter.generateSignature(
                                oneTimeGroupKeyPair.getOneTimeGroupPrivateKey(),
                            encryptedMessage
                        );

                        byte[] messageWithSignatureHMACAndPK = groupChatPresenter.appendHMACSignatureAndPKTo(
                            encryptedMessage, hmac, signature, oneTimeGroupKeyPair.getOneTimeGroupPublicKey()
                        );

                        String messageWithSignatureHMACAndPKStr = groupChatPresenter.
                                messageWithSignatureHMACAndPKToString(messageWithSignatureHMACAndPK);

                        Gson gson = new Gson();
                        JsonElement msg_type = gson.toJsonTree(ConstantRegistry.CHATSTER_GROUP_CHAT_MSG);
                        JsonElement content_type = gson.toJsonTree(ConstantRegistry.TEXT);
                        JsonElement sender_id = gson.toJsonTree(String.valueOf(groupChatPresenter.getUserId(GroupChatActivity.this)));
                        JsonElement receiver_id = gson.toJsonTree(String.valueOf(oneTimeGroupPublicKey.getUserId()));
                        JsonElement group_chat_id = gson.toJsonTree(groupChatId);
                        JsonElement message = gson.toJsonTree(messageWithSignatureHMACAndPKStr);
                        JsonElement message_uuid = gson.toJsonTree(groupChatMessage.getUuid());
                        JsonElement group_member_one_time_pbk_uuid = gson.toJsonTree(oneTimeGroupPublicKey.getUuid());
                        JsonElement item_created = gson.toJsonTree("");
                        JsonObject groupMessage = new JsonObject();
                        groupMessage.add("msg_type", msg_type);
                        groupMessage.add("content_type", content_type);
                        groupMessage.add("sender_id", sender_id);
                        groupMessage.add("receiver_id", receiver_id);
                        groupMessage.add("group_chat_id", group_chat_id);
                        groupMessage.add("message", message);
                        groupMessage.add("message_uuid", message_uuid);
                        groupMessage.add("group_member_one_time_pbk_uuid", group_member_one_time_pbk_uuid);
                        groupMessage.add("item_created", item_created);
                        groupMessages.add(groupMessage);
                    }

                    JsonObject objMain = new JsonObject();
                    objMain.add("groupChatOfflineMessages",groupMessages);

                    if(groupChatPresenter.hasInternetConnection()){
                        groupOneTimePublicKeyUUIDS.add(oneTimeGroupKeyPair.getUuid());
                        sendMessageToServer(objMain.toString(), oneTimeGroupKeyPair.getUuid());
                    }else{
                        groupChatChatsterToast.notifyUserNoInternet();
                    }
                },Throwable::printStackTrace);
    }

    private void sendMessageToServer(String messages, String senderPublicKeyUUID) {
        socket.emit(ConstantRegistry.CHATSTER_GROUP_CHAT_MESSAGE, messages, senderPublicKeyUUID);
    }

    private GroupChatMessage processOutgoingMessageLocally(String message, String uuid) {
        return handleOutgoingTextMessage(
            groupChatPresenter.getUserId(GroupChatActivity.this),
            groupChat.get_id(),
            message,
            uuid
        );
    }

    public void speechToTextGroupMessageClickListener() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, ConstantRegistry.SPEECH_INPUT_REQUEST);
        } catch (ActivityNotFoundException a) {
            groupChatChatsterToast.notifyUserSpeechToTextNotSupported();
        }
    }

    private void handleIncomingTextMessage(long senderId, String groupChatId, String message, String utc, String uuid, String groupMemberPBKUUID) {

        byte[] messageWithSignatureHMACAndPK = groupChatPresenter.messageWithSignatureHMACAndPKToByteArray(message);

        byte[] encryptedMessage = groupChatPresenter.getMessageFrom(messageWithSignatureHMACAndPK);
        byte[] hmac = groupChatPresenter.getHMACFrom(messageWithSignatureHMACAndPK);
        byte[] signature = groupChatPresenter.getSignatureFrom(messageWithSignatureHMACAndPK);
        byte[] senderPublicKey = groupChatPresenter.getPublicKeyFrom(messageWithSignatureHMACAndPK);

        byte[] receiverPrivateKey = groupChatPresenter.getGroupOneTimePrivatePreKeyByUUID(
            GroupChatActivity.this,
            groupMemberPBKUUID
        );

        byte[] decryptSharedSecret = groupChatPresenter.generateDecryptSharedSecret(senderPublicKey,receiverPrivateKey);

        boolean signatureIsCorrect = groupChatPresenter.verifySignature(senderPublicKey, encryptedMessage, signature);

        if(signatureIsCorrect){
            byte[] decryptedMessage = groupChatPresenter.decrypt(encryptedMessage, decryptSharedSecret);

            String hmacAfterDecryption = groupChatPresenter.bytesToHex(
                    groupChatPresenter.messageToHMAC(decryptedMessage,decryptSharedSecret)
            );

            if(groupChatPresenter.bytesToHex(hmac).equals(hmacAfterDecryption)){
                String msg = new String(decryptedMessage, StandardCharsets.UTF_8);

                GroupChatMessage groupChatMessage =
                        groupChatPresenter.createGroupChatTextMessage(senderId, groupChatId, msg, utc, uuid);
                groupChatMessages.add(groupChatMessage);
                updateUnsendMessageRegistry(groupChatMessage);
                saveMessageToDB(groupChatMessage);
                sendTextMessageToFragment();
            }else{
                throw new RuntimeException("GroupChatActivity =====> HMAC is incorrect");
            }
        }else{
            throw new RuntimeException("GroupChatActivity =====> signature is incorrect");
        }
    }

    private void saveMessageToDB(GroupChatMessage groupChatMessage) {
        groupChatPresenter.insertGroupChatMessage(groupChatMessage, GroupChatActivity.this);
    }

    private void updateUnsendMessageRegistry(GroupChatMessage groupChatMessage) {
        if(groupChatMessagesNotEmpty()){
            unsendMessageRegistry.put(groupChatMessage.getUuid(), atLastPosition());
        }else{
            unsendMessageRegistry.put(groupChatMessage.getUuid(), atFirstPosition());
        }
    }

    private int atFirstPosition() {
        return 0;
    }

    private int atLastPosition() {
        return groupChatMessages.size()-1;
    }

    private boolean groupChatMessagesNotEmpty() {
        return groupChatMessages.size() > 0;
    }

    private GroupChatMessage handleOutgoingTextMessage(long senderId, String groupChatId, String message, String uuid) {
        GroupChatMessage groupChatMessage = groupChatPresenter.createGroupChatTextMessage(senderId, groupChatId, message, uuid);
        groupChatMessages.add(groupChatMessage);
        updateUnsendMessageRegistry(groupChatMessage);
        saveMessageToDB(groupChatMessage);
        putMessageToMessageQueue(groupChatMessage);
        sendTextMessageToFragment();

        return groupChatMessage;
    }

    private void sendTextMessageToFragment() {
        EventBus.getDefault().post(new TextGroupChatMessageEvent(groupChatMessages));
    }

    private void putMessageToMessageQueue(GroupChatMessage groupChatMessage) {
        groupChatPresenter.insertGroupChatMessageQueueItem(groupChatMessage.getUuid(), GroupChatActivity.this);
    }

    private void handleOutgoingImageMessage(long senderId, String chatId, Uri imageUrl) {
        processOutgoingImageMessage(senderId, chatId, imageUrl);
    }

    private void processOutgoingImageMessage(long senderId, String chatId, Uri imageUrl) {
        try {
            String encodedImage = groupChatPresenter.encodeImageToString(GroupChatActivity.this, imageUrl);
            String uuid = groupChatPresenter.createUUID();
            GroupChatMessage groupChatMessage = processImageMessage(senderId,chatId,imageUrl,uuid);
            Disposable subscribeGetGroupOneTimeKeys = groupChatPresenter.getGroupOneTimeKeys(chatId, senderId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(res -> {
                        JsonArray groupMessages = new JsonArray();

                        OneTimeGroupKeyPair oneTimeGroupKeyPair = groupChatPresenter.
                                getGroupOneTimeKeyPair(GroupChatActivity.this, chatId, senderId);

                        for(OneTimeGroupPublicKey oneTimeGroupPublicKey : res){
                            // for each of the group member pbks construct an encrypted message
                            byte[] groupMemberPublicKey = oneTimeGroupPublicKey.getOneTimeGroupPublicKey();

                            byte[] encryptSharedSecret = groupChatPresenter.generateEncryptSharedSecret(
                                    groupMemberPublicKey,
                                    oneTimeGroupKeyPair.getOneTimeGroupPrivateKey()
                            );

                            byte[] msgBytes = encodedImage.getBytes(StandardCharsets.UTF_8);

                            byte[] encryptedMessage = groupChatPresenter.encrypt(msgBytes, encryptSharedSecret);

                            byte[] hmac = groupChatPresenter.messageToHMAC(msgBytes, encryptSharedSecret);

                            byte[] signature = groupChatPresenter.generateSignature(
                                    oneTimeGroupKeyPair.getOneTimeGroupPrivateKey(),
                                    encryptedMessage
                            );

                            byte[] messageWithSignatureHMACAndPK = groupChatPresenter.appendHMACSignatureAndPKTo(
                                    encryptedMessage, hmac, signature, oneTimeGroupKeyPair.getOneTimeGroupPublicKey()
                            );

                            String messageWithSignatureHMACAndPKStr = groupChatPresenter.
                                    messageWithSignatureHMACAndPKToString(messageWithSignatureHMACAndPK);

                            Gson gson = new Gson();
                            JsonElement msg_type = gson.toJsonTree(ConstantRegistry.CHATSTER_GROUP_CHAT_MSG);
                            JsonElement content_type = gson.toJsonTree(ConstantRegistry.IMAGE);
                            JsonElement sender_id = gson.toJsonTree(String.valueOf(groupChatPresenter.getUserId(GroupChatActivity.this)));
                            JsonElement receiver_id = gson.toJsonTree(String.valueOf(oneTimeGroupPublicKey.getUserId()));
                            JsonElement group_chat_id = gson.toJsonTree(chatId);
                            JsonElement message = gson.toJsonTree(messageWithSignatureHMACAndPKStr);
                            JsonElement message_uuid = gson.toJsonTree(groupChatMessage.getUuid());
                            JsonElement group_member_one_time_pbk_uuid = gson.toJsonTree(oneTimeGroupPublicKey.getUuid());
                            JsonElement item_created = gson.toJsonTree("");
                            JsonObject groupMessage = new JsonObject();
                            groupMessage.add("msg_type", msg_type);
                            groupMessage.add("content_type", content_type);
                            groupMessage.add("sender_id", sender_id);
                            groupMessage.add("receiver_id", receiver_id);
                            groupMessage.add("group_chat_id", group_chat_id);
                            groupMessage.add("message", message);
                            groupMessage.add("message_uuid", message_uuid);
                            groupMessage.add("group_member_one_time_pbk_uuid", group_member_one_time_pbk_uuid);
                            groupMessage.add("item_created", item_created);
                            groupMessages.add(groupMessage);
                        }

                        JsonObject objMain = new JsonObject();
                        objMain.add("groupChatOfflineMessages",groupMessages);

                        if(groupChatPresenter.hasInternetConnection()){
                            groupOneTimePublicKeyUUIDS.add(oneTimeGroupKeyPair.getUuid());
                            sendImageMessageToServer(objMain.toString(), oneTimeGroupKeyPair.getUuid());
                        }else{
                            groupChatChatsterToast.notifyUserNoInternet();
                        }
                    },Throwable::printStackTrace);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendImageMessageToServer(String messages, String senderPublicKeyUUID) {
        socket.emit(ConstantRegistry.CHATSTER_GROUP_CHAT_MESSAGE, messages, senderPublicKeyUUID);
    }

    private GroupChatMessage processImageMessage(long senderId, String groupChatId, Uri imageUrl, String uuid){
        GroupChatMessage groupChatMessage = groupChatPresenter.createGroupChatImageMessage(senderId, groupChatId,
                imageUrl, uuid);
        groupChatMessages.add(groupChatMessage);
        updateUnsendMessageRegistry(groupChatMessage);
        saveMessageToDB(groupChatMessage);
        putMessageToMessageQueue(groupChatMessage);
        sendImageMessageToFragment();

        return groupChatMessage;
    }

    private void sendImageMessageToFragment() {
        EventBus.getDefault().post(new ImageGroupChatMessageEvent(groupChatMessages));
    }

    private void handleIncomingImageMessage(long senderId, String groupChatId, String encryptedImage, String utc, String uuid, String groupMemberPBKUUID){

        byte[] messageWithSignatureHMACAndPK = groupChatPresenter.messageWithSignatureHMACAndPKToByteArray(encryptedImage);

        byte[] encryptedMessage = groupChatPresenter.getMessageFrom(messageWithSignatureHMACAndPK);
        byte[] hmac = groupChatPresenter.getHMACFrom(messageWithSignatureHMACAndPK);
        byte[] signature = groupChatPresenter.getSignatureFrom(messageWithSignatureHMACAndPK);
        byte[] senderPublicKey = groupChatPresenter.getPublicKeyFrom(messageWithSignatureHMACAndPK);

        byte[] receiverPrivateKey = groupChatPresenter.getGroupOneTimePrivatePreKeyByUUID(
                GroupChatActivity.this,
                groupMemberPBKUUID
        );

        byte[] decryptSharedSecret = groupChatPresenter.generateDecryptSharedSecret(senderPublicKey,receiverPrivateKey);

        boolean signatureIsCorrect = groupChatPresenter.verifySignature(senderPublicKey, encryptedMessage, signature);

        if(signatureIsCorrect){
            byte[] decryptedMessage = groupChatPresenter.decrypt(encryptedMessage, decryptSharedSecret);

            String hmacAfterDecryption = groupChatPresenter.bytesToHex(
                    groupChatPresenter.messageToHMAC(decryptedMessage,decryptSharedSecret)
            );

            if(groupChatPresenter.bytesToHex(hmac).equals(hmacAfterDecryption)){
                String msg = new String(decryptedMessage, StandardCharsets.UTF_8);

                Uri imageUrl = groupChatPresenter.
                        saveIncomingImageMessage(GroupChatActivity.this, groupChatPresenter.decodeImage(msg));

                GroupChatMessage groupChatMessage = groupChatPresenter.createGroupChatImageMessage(senderId, groupChatId,
                        imageUrl, utc, uuid);
                groupChatMessages.add(groupChatMessage);
                updateUnsendMessageRegistry(groupChatMessage);
                saveMessageToDB(groupChatMessage);
                sendImageMessageToFragment();
            }else{
                throw new RuntimeException("GroupChatActivity =====> HMAC is incorrect");
            }
        }else{
            throw new RuntimeException("GroupChatActivity =====> signature is incorrect");
        }
    }

    // endregion

    // region Initialize Unsend Group Chat Message Registry

    private void initializeUnsendMessageRegistry(ArrayList<GroupChatMessage> messages) {
        subscribeInitializeUnsendMessageRegistry = groupChatPresenter.initializeUnsendMessageRegistry(messages)
                .subscribeOn(Schedulers.io())
                .subscribe(res -> {
                    unsendMessageRegistry = res;
                }, Throwable::printStackTrace);
        disposable.add(subscribeInitializeUnsendMessageRegistry);
    }

    // endregion
}