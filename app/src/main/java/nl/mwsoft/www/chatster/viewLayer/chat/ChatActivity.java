package nl.mwsoft.www.chatster.viewLayer.chat;

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
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

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
import java.util.Objects;

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
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.coordinator.RootCoordinator;
import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.modelLayer.event.chat.DeleteMessageEvent;
import nl.mwsoft.www.chatster.modelLayer.event.chat.ImageMessageEvent;
import nl.mwsoft.www.chatster.modelLayer.event.chat.TextMessageEvent;
import nl.mwsoft.www.chatster.modelLayer.event.chat.SpeechToTextMessageEvent;
import nl.mwsoft.www.chatster.modelLayer.event.chat.UnsendMessageEvent;
import nl.mwsoft.www.chatster.modelLayer.model.OneTimeKeyPair;
import nl.mwsoft.www.chatster.presenterLayer.chat.ChatPresenter;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.chatChatsterToast.ChatChatsterToast;
import nl.mwsoft.www.chatster.viewLayer.dialog.loadingDialog.LoadingDialogFragment;
import nl.mwsoft.www.chatster.viewLayer.dialog.notRegisteredDialog.NotRegisteredDialogFragment;
import nl.mwsoft.www.chatster.viewLayer.main.MainActivity;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.model.Chat;
import nl.mwsoft.www.chatster.modelLayer.model.Contact;
import nl.mwsoft.www.chatster.modelLayer.model.ImageDetailRequest;
import nl.mwsoft.www.chatster.modelLayer.model.Message;
import nl.mwsoft.www.chatster.modelLayer.model.OfflineMessage;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ChatActivity extends AppCompatActivity implements SpyModeDialogFragment.SpyModeDialogListener,
        NotRegisteredDialogFragment.NotRegisteredDialogFragmentListener {
    @BindView(R.id.ivChatBack) ImageView ivChatBack;
    @BindView(R.id.tvChatContactName)
    TextView tvChatContactName;
    @BindView(R.id.tvChatContactStatus) TextView tvChatContactStatus;
    @BindView(R.id.chatToolbar)
    Toolbar toolbar;
    private ArrayList<Message> messages;
    private ArrayList<Message> spyModeMessages;
    private ArrayList<String> userOneTimePublicKeyUUIDS;
    private Socket socket;
    private Contact contact;
    private Chat chat;
    private ClipboardManager clipboard;
    private ClipData clip;
    private Uri photoURI;
    private HashMap<String,Integer> unsendMessageRegistry;
    private CompositeDisposable disposable;
    private Disposable subscribeInitializeUnsendMessageRegistry;
    private ChatPresenter chatPresenter;
    private RootCoordinator rootCoordinator;
    private ChatChatsterToast chatChatsterToast;
    private Unbinder unbinder;
    private boolean hasBeenOpenedFromNotification = false;
    private boolean isSwitchingToDarkMode;
    private boolean isInSpyMode;
    private LoadingDialogFragment loadingDialogFragment;
    private SpyModeDialogFragment spyModeDialogFragment;
    private String currAction = "";
    private long currActionSenderId = 0L;
    private NotRegisteredDialogFragment notRegisteredDialogFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.content_chat);
        unbinder = ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        disposable = new CompositeDisposable();

        DependencyRegistry.shared.inject(this);

        unsendMessageRegistry = new HashMap<>();

        contact = new Contact();
        chat = new Chat();

        messages = new ArrayList<>();
        spyModeMessages = new ArrayList<>();
        userOneTimePublicKeyUUIDS = new ArrayList<>();

        attachUI();

        setUpConnectionToServer();

        handleChatOpen();
    }

    // region Configure

    public void configureWith(ChatPresenter chatPresenter, RootCoordinator rootCoordinator, ChatChatsterToast chatChatsterToast){
        this.chatPresenter = chatPresenter;
        this.rootCoordinator = rootCoordinator;
        this.chatChatsterToast = chatChatsterToast;
    }

    // endregion

    // region UI

    private void attachUI() {
        // Gets a handle to the clipboard service.
        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
    }

    //endregion

    // region Show Chat Fragment

    private void showChatFragment(){
        ChatFragment chatFragment = ChatFragment.newInstance(messages);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_chat_container, chatFragment, ConstantRegistry.FRAGMENT_TAG)
                .commit();
    }

    // endregion

    // region Show Spy Chat Fragment

    private void showSpyChatFragment(){
        SpyChatFragment spyChatFragment = SpyChatFragment.newInstance(spyModeMessages);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_chat_container, spyChatFragment, ConstantRegistry.FRAGMENT_TAG)
                .commit();
    }

    // endregion

    //region Show Image Details

    public void showImageDetail(ImageDetailRequest imageDetailRequest, Message message) {
        if(userIsSender(message)){
            imageDetailRequest.setSenderName(chatPresenter.getUserName(ChatActivity.this));
        }else{
            imageDetailRequest.setSenderName(chatPresenter.getContactNameById(ChatActivity.this,
                    message.getMessageSenderId()));
        }
        rootCoordinator.navigateToImageDetailActivity(ChatActivity.this, imageDetailRequest);
        finish();
    }

    // endregion

    // region Dialogs

    public void showPopupImageMessageOptions(View v, final Message message, final int position) {

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
                deleteMessageLocally(message, position);
                popupWindow.dismiss();
            }
        });

        ivImageMessagePopUpUnsend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(userIsSender(message)){
                    sendUnsendMessageRequestToServer(message);
                    processUnsendImageLocally(message, position);
                }else{
                    chatChatsterToast.notifyUserCanOnlyUnsendOwnMessages();
                }
                popupWindow.dismiss();
            }
        });
    }

    private void processUnsendImageLocally(Message message, int position) {
        chatPresenter.updateMessageUnsentByMessageUUID(message.getMessageUUID(),ChatActivity.this);
        sendUnsendEventToFragment(position);
        updateMessageInList(position);
        removeFromUnsendRegistry(position);
    }

    private void removeFromUnsendRegistry(int position) {
        if(unsendMessageRegistry.containsValue(position)){
            unsendMessageRegistry.remove(position);
        }
    }

    private void updateMessageInList(int position) {
        messages.get(position).setMsgType(ConstantRegistry.TEXT);
        messages.get(position).setBinaryMessageFilePath(Uri.EMPTY);
        messages.get(position).setMessageText(getString(R.string.message_deleted));
    }

    private void sendUnsendEventToFragment(int position) {
        UnsendMessageEvent unsendMessageEvent =
                new UnsendMessageEvent(getString(R.string.message_deleted), position);
        EventBus.getDefault().post(unsendMessageEvent);
    }

    private void sendUnsendMessageRequestToServer(Message message) {
        socket.emit(ConstantRegistry.CHATSTER_UNSEND_MESSAGE,
                chatPresenter.getUserId(ChatActivity.this),
                chatPresenter.getUserName(ChatActivity.this),
                contact.getUserId(),
                chat.getChatName(),
                message.getMessageUUID());//eventName, senderId, senderName, contactId, chatName, uuid
    }

    private boolean userIsSender(Message message) {
        return chatPresenter.getUserId(ChatActivity.this) == message.getMessageSenderId();
    }

    private void deleteMessageLocally(Message message, int position) {
        chatPresenter.deleteChatMessageById(message.getMessageId(),ChatActivity.this);
        messages.remove(position);
        EventBus.getDefault().post(new DeleteMessageEvent(messages));
        removeFromUnsendRegistry(position);
    }

    public void showPopupMessageOptions(View v, final Message message, final int position) {
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
                deleteMessageLocally(message, position);
                popupWindow.dismiss();
            }
        });

        ivMessagePopUpCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyMessageText(message, popupWindow);
                chatChatsterToast.notifyUserMessageCopied();
            }
        });

        ivMessagePopUpUnsend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(userCantUnsendUnsentMessage(message)){
                    chatChatsterToast.notifyUserCantUnsendMessages();
                }else{
                    if(userIsSender(message)){
                        sendUnsendMessageRequestToServer(message);
                        processUnsendMessageLocally(message, position);
                    }else{
                        chatChatsterToast.notifyUserCanOnlyUnsendOwnMessages();
                    }
                }
                popupWindow.dismiss();
            }
        });
    }

    private boolean userCantUnsendUnsentMessage(Message message) {
        return message.getMessageText().equals(getString(R.string.message_deleted))
                && userIsSender(message);
    }

    private void copyMessageText(Message message, PopupWindow popupWindow) {
        // Creates a new text clip to put on the clipboard
        clip = ClipData.newPlainText(ConstantRegistry.CHATSTER_PASTE, message.getMessageText());
        // Set the clipboard's primary clip.
        clipboard.setPrimaryClip(clip);
        popupWindow.dismiss();
    }

    private void processUnsendMessageLocally(Message message, int position) {
        chatPresenter.updateMessageUnsentByMessageUUID(message.getMessageUUID(), ChatActivity.this);
        sendUnsendEventToFragment(position);
        messages.get(position).setMessageText(getString(R.string.message_deleted));
        removeFromUnsendRegistry(position);
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
                        photoFile = chatPresenter.createImageFile(ChatActivity.this);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    if (photoFile != null) {
                        photoURI = FileProvider.getUriForFile(ChatActivity.this,
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

    // region Activity Life Cycle And Other Overrides

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(isInSpyMode){
            sendSpyModeAction(ConstantRegistry.CHATSTER_SPY_CHAT_ACTION_DISCONNECT);
        }

        runOnDestroyRoutine();
    }

    private void runOnDestroyRoutine() {
        disconnectFromServer();
        cleanUpData();
        unbindButterKnife();
    }

    private void disconnectFromServer() {
        if(socket != null){
            socket.disconnect();
        }
    }

    private void unbindButterKnife() {
        if(unbinder != null){
            unbinder.unbind();
        }
    }

    private void cleanUpData() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    private boolean accessFilesPermissionIsGranted() {
        return ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionWriteExternalStorage() {
        ActivityCompat.requestPermissions(ChatActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                ConstantRegistry.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    private void setStatusBarColor(boolean isSwitchingToDarkMode) {
        if(isSwitchingToDarkMode) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorBlack));
        }else{
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_attachment) {
            if(accessFilesPermissionIsGranted()){
                showPopupSendAttachment(findViewById(android.R.id.content).getRootView());
            }else{
                requestPermissionWriteExternalStorage();
            }
        } else if (id == R.id.action_chat_settings) {
            navigateToSettings();
        } else if (id == R.id.action_spy_chat) {
            if(isInSpyMode){
                currAction = ConstantRegistry.CHATSTER_SPY_CHAT_ACTION_DISCONNECT;
                sendSpyModeAction(currAction);
                leaveSpyChat();
            }else{
                currActionSenderId = chatPresenter.getUserId(ChatActivity.this);
                currAction = ConstantRegistry.CHATSTER_SPY_CHAT_ACTION_JOIN;
                showGoInSpyModeDialog(getString(R.string.go_in_spy_mode));
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ConstantRegistry.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showPopupSendAttachment(findViewById(android.R.id.content).getRootView());
                } else {
                    showPopupDeniedPermission(findViewById(android.R.id.content).getRootView());
                }
                return;
            }
        }
    }

    private void navigateToSettings() {
        rootCoordinator.navigateToChatSettingsActivity(ChatActivity.this, chat);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ConstantRegistry.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            handlePickImageRequest(data);
        }else if (requestCode == ConstantRegistry.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            handleImageCaptureRequest();
        }else if (requestCode == ConstantRegistry.SPEECH_INPUT_REQUEST && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            handleSpeechToTextRequest(result);
        }
    }

    private void handleImageCaptureRequest() {
        if(photoURI != null){
            try {
                Bitmap photoBitmap = chatPresenter.decodeSampledBitmap(ChatActivity.this, photoURI);
                photoURI = chatPresenter.saveIncomingImageMessage(ChatActivity.this, photoBitmap);
                handleOutgoingImageMessage(chatPresenter.getUserId(ChatActivity.this), chat.getChatId(), photoURI);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            chatChatsterToast.notifyUserSomethingWentWrong();
        }
    }

    private void handleSpeechToTextRequest(ArrayList<String> result) {
        if(speechToTextResultNotNull(result)){
            if(speechToTextResultNotEmpty(result)){
                sendSpeechToTextMessageToFragment(result);
            }else{
                chatChatsterToast.notifyUserCantSendEmptyMessage();
            }
        }else{
            chatChatsterToast.notifyUserSomethingWentWrong();
        }
    }

    private void sendSpeechToTextMessageToFragment(ArrayList<String> result) {
        EventBus.getDefault().post(new SpeechToTextMessageEvent(result.get(atFirstPosition())));
    }

    private boolean speechToTextResultNotEmpty(ArrayList<String> result) {
        return !result.get(atFirstPosition()).equals(ConstantRegistry.CHATSTER_EMPTY_STRING);
    }

    private boolean speechToTextResultNotNull(ArrayList<String> result) {
        return !result.get(atFirstPosition()).equals(null);
    }

    private void handlePickImageRequest(Intent data) {
        Uri uri = data.getData();
        handleOutgoingImageMessage(chatPresenter.getUserId(ChatActivity.this), chat.getChatId(), uri);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(hasBeenOpenedFromNotification){
            if(isInSpyMode){
                sendSpyModeAction(ConstantRegistry.CHATSTER_SPY_CHAT_ACTION_DISCONNECT);
                leaveSpyChat();
            }

            rootCoordinator.navigateToMainActivity(ChatActivity.this);
            finish();
        }else{
            if(isInSpyMode){
                sendSpyModeAction(ConstantRegistry.CHATSTER_SPY_CHAT_ACTION_DISCONNECT);
                leaveSpyChat();
            }

            finish();
        }
    }

    // endregion

    // region Handle Chat Opened

    private void handleOpenedFromChatList() {
        if(parcelableFromChatListHasValue()){
            chat = getIntent().getExtras().getParcelable(ConstantRegistry.CHAT_REQUEST);
            if(contactExists()){
                contact = chatPresenter.getContactById(ChatActivity.this, chat.getContactId());
                setContactName();
                if(chatHasMessages()){
                    initUnsendMessageRegistry();
                    getAllMessagesForThisChat();
                }
                connectToChat();
            }else{
                chatChatsterToast.notifyUserSomethingWentWrong();
            }
        }else{
            chatChatsterToast.notifyUserSomethingWentWrong();
        }
    }

    private boolean parcelableFromChatListHasValue() {
        try{
            return getIntent().getExtras().getParcelable(ConstantRegistry.CHAT_REQUEST) != null;
        }catch (NullPointerException e){
            e.printStackTrace();
            return false;
        }
    }

    private void getAllMessagesForThisChat() {
        messages.addAll(chatPresenter.getAllMessagesForChatWithId(ChatActivity.this, chat.getChatId()));
    }

    private void setContactName() {
        tvChatContactName.setText(contact.getUserName());
    }

    private boolean contactExists() {
        return chatPresenter.getContactById(ChatActivity.this, chat.getContactId()) != null;
    }

    private boolean chatHasMessages() {
        return chatPresenter.getAllMessagesForChatWithId(ChatActivity.this, chat.getChatId()).size() > 0;
    }

    private void initUnsendMessageRegistry() {
        initializeUnsendMessageRegistry(chatPresenter.
                getAllMessagesForChatWithId(ChatActivity.this, chat.getChatId()));
    }

    private void handleOpenedFromContactList() {
        if(parcelableFromContactListHasValue()){
            configureContactFromContactList();
            configureChatFromContactList();
            connectToChat();
        }else{
            chatChatsterToast.notifyUserSomethingWentWrong();
        }
    }

    private boolean parcelableFromContactListHasValue() {
        try{
            return getIntent().getExtras().getParcelable(ConstantRegistry.CONTACT_REQUEST) != null;
        }catch (NullPointerException e){
            e.printStackTrace();
            return false;
        }
    }

    private void configureChatFromContactList() {
        if(chatExists()){
            chat = chatPresenter.getChatByContactId(ChatActivity.this, contact.getUserId());
            if(chatHasMessages()){
                initUnsendMessageRegistry();
                getAllMessagesForThisChat();
            }
        }else{
            setUpNewChat();
        }
    }

    private void configureContactFromContactList() {
        if (getIntent() != null){
            if(getIntent().getExtras() != null){
                contact = getIntent().getExtras().getParcelable(ConstantRegistry.CONTACT_REQUEST);
            }
        }

        if(contact != null){
            setContactName();
            if (contact.getIsChatsterContact() == 0){
                showNotRegisteredDialog(contact.getUserName());
            }
        }
    }

    private boolean chatExists() {
        return chatPresenter.getChatByContactId(ChatActivity.this, contact.getUserId()) != null;
    }

    private void setUpNewChat() {
        String myId = String.valueOf(chatPresenter.getUserId(ChatActivity.this));
        String contactId = String.valueOf(contact.getUserId());
        String chatName = myId.concat(ConstantRegistry.CHATSTER_AT).concat(contactId);
        chatPresenter.insertChat(contact.getUserId(),chatName, ChatActivity.this);
        chat = chatPresenter.getChatByContactId(ChatActivity.this, contact.getUserId());
    }

    private void handleReadMessageRequest() {
        hasBeenOpenedFromNotification = true;
        if(parcelableFromMessageNotificationHasValue()){
            OfflineMessage offlineMessage = getIntent().getExtras().getParcelable(ConstantRegistry.READ_MESSAGE_INTENT);
            configureContactFromNotification(offlineMessage);
            configureChatFromNotification(offlineMessage);
            connectToChat();

        }else{
            chatChatsterToast.notifyUserSomethingWentWrong();
        }
        removeNotifications();
    }

    private boolean parcelableFromMessageNotificationHasValue() {
        try{
            return getIntent().getExtras().getParcelable(ConstantRegistry.READ_MESSAGE_INTENT) != null;
        }catch (NullPointerException e){
            e.printStackTrace();
            return false;
        }
    }

    private void configureChatFromNotification(OfflineMessage offlineMessage) {
        if(chatExists()){
            chat = chatPresenter.getChatByContactId(ChatActivity.this, contact.getUserId());
            if(chatHasMessages()){
                initUnsendMessageRegistry();
                getAllMessagesForThisChat();
            }else {
                unsendMessageRegistry.put(offlineMessage.getUuid(), atFirstPosition());
            }
        }else{
            insertNewChat(offlineMessage);
            chat = chatPresenter.getChatByContactId(ChatActivity.this, contact.getUserId());
        }
    }

    private void configureContactFromNotification(OfflineMessage offlineMessage) {
        contact = chatPresenter.getContactById(ChatActivity.this, offlineMessage.getSenderId());
        if(contact != null){
            setContactName();
        }
    }

    private void removeNotifications() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        try{
            mNotificationManager.cancelAll();
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    private void handleMessageAcceptFromUnknown() {
        if(parcelableFromUnknownHasValue()){
            OfflineMessage offlineMessage = readParcelableFormUnknownValue();
            if(offlineMessageHasValue(offlineMessage)){
                processOfflineMessageFromUnknown(offlineMessage);
            }else{
                chatChatsterToast.notifyUserSomethingWentWrong();
            }
        }else{
            chatChatsterToast.notifyUserSomethingWentWrong();
        }
        removeNotifications();
    }

    private void processOfflineMessageFromUnknown(OfflineMessage offlineMessage) {
        addNewContact(offlineMessage);
        configureContactFromUnknown(offlineMessage);
        configureChatFromUnknown(offlineMessage);
        connectToChat();
    }

    private OfflineMessage readParcelableFormUnknownValue() {
        try {
            return getIntent().getExtras()
                    .getParcelable(ConstantRegistry.ACCEPT_MESSAGE_FROM_UNKNOWN_INTENT);
        }catch (NullPointerException e){
            e.printStackTrace();
            return null;
        }
    }

    private boolean parcelableFromUnknownHasValue() {
        try {
            return getIntent().getExtras()
                    .getParcelable(ConstantRegistry.ACCEPT_MESSAGE_FROM_UNKNOWN_INTENT) != null;
        }catch (NullPointerException e){
            e.printStackTrace();
            return false;
        }
    }

    private boolean offlineMessageHasValue(OfflineMessage offlineMessage) {
        return offlineMessage != null;
    }

    private void configureChatFromUnknown(OfflineMessage offlineMessage) {
        if(chatExists()){
            chat = chatPresenter.getChatByContactId(ChatActivity.this, contact.getUserId());
            if(chatHasMessages()){
                initUnsendMessageRegistry();
                getAllMessagesForThisChat();
            }else {
                unsendMessageRegistry.put(offlineMessage.getUuid(), atFirstPosition());
            }
        }else{
            insertNewChat(offlineMessage);
            chat = chatPresenter.getChatByContactId(ChatActivity.this, contact.getUserId());
        }
    }

    private void configureContactFromUnknown(OfflineMessage offlineMessage) {
        contact = chatPresenter.getContactById(ChatActivity.this, offlineMessage.getSenderId());
        if(contact != null){
            setContactName();
        }
    }

    private void insertNewChat(OfflineMessage offlineMessage) {
        chatPresenter.insertChat(offlineMessage.getSenderId(),offlineMessage.getChatName(),
                ChatActivity.this);
    }

    private void addNewContact(OfflineMessage offlineMessage) {
        chatPresenter.insertContact(offlineMessage.getSenderId(), offlineMessage.getSenderName(),
                ConstantRegistry.CHATSTER_HI_MY_NAME_IS.concat(offlineMessage.getSenderName()),
                ChatActivity.this);
    }

    private void connectToChat() {
        if(chatPresenter.hasInternetConnection()){
            socket.emit(ConstantRegistry.CHATSTER_OPEN_CHAT_MESSAGE,
                    chatPresenter.getUserId(ChatActivity.this),
                    contact.getUserId(),
                    chat.getChatName());
        }else{
            chatChatsterToast.notifyUserNoInternet();
        }
    }

    private void handleDeclineMessageFromUnknown() {
        removeNotifications();
        if(parcelableDeclineFromUnknownHasValue()){
            OfflineMessage offlineMessage = getIntent().getExtras()
                    .getParcelable(ConstantRegistry.DECLINE_MESSAGE_FROM_UNKNOWN_INTENT);
            chatPresenter.deleteChatMessageBySenderId(offlineMessage.getSenderId(), ChatActivity.this);
            navigateToMain();
        }
    }

    private boolean parcelableDeclineFromUnknownHasValue() {
        try{
            return getIntent().getExtras().getParcelable(ConstantRegistry.DECLINE_MESSAGE_FROM_UNKNOWN_INTENT) != null;
        }catch (NullPointerException e){
            e.printStackTrace();
            return false;
        }
    }

    private void navigateToMain() {
        Intent mainIntent = new Intent(ChatActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void handleChatOpen(){
        if(isValidAction() && getIntent() != null){
            switch(Objects.requireNonNull(getIntent().getAction())){
                case ConstantRegistry.CHATS_LIST:
                    handleOpenedFromChatList();
                    break;
                case ConstantRegistry.CONTACTS_LIST:
                    handleOpenedFromContactList();
                    break;
                case ConstantRegistry.READ_MESSAGE_REQUEST:
                    handleReadMessageRequest();
                    break;
                case ConstantRegistry.ACCEPT_MESSAGE_FROM_UNKNOWN_REQUEST:
                    handleMessageAcceptFromUnknown();
                    break;
                case ConstantRegistry.DECLINE_MESSAGE_FROM_UNKNOWN_REQUEST:
                    handleDeclineMessageFromUnknown();
                    break;
                default:
                    break;
            }

            showChatFragment();
        }else {
            chatChatsterToast.notifyUserSomethingWentWrong();
        }
    }

    private boolean isValidAction() {
        return getIntent().getAction() != null;
    }

    // endregion

    // region Loading Dialog

    private void closeLoadingDialog() {
        if(loadingDialogFragment != null){
            loadingDialogFragment.dismiss();
        }
    }

    private void showLoadingDialog() {
        loadingDialogFragment = new LoadingDialogFragment();
        loadingDialogFragment.setCancelable(false);
        loadingDialogFragment.show(getSupportFragmentManager(), ConstantRegistry.LOADING);
    }

    // endregion

    // region Handle Messages

    private void sendSpyModeAction(String action) {
        showLoadingDialog();

        socket.emit(
            ConstantRegistry.CHATSTER_SPY_CHAT_CONNECTION,
            chatPresenter.getUserId(ChatActivity.this),
            chatPresenter.getUserName(ChatActivity.this),
            contact.getUserId(),
            action
        );
    }

    private void handleIncomingTextMessage(long senderId, int chatId, String message, String utc,
                                           String uuid, String contactPublicKeyUUID) {
        byte[] messageWithSignatureHMACAndPK = chatPresenter.messageWithSignatureHMACAndPKToByteArray(message);

        byte[] encryptedMessage = chatPresenter.getMessageFrom(messageWithSignatureHMACAndPK);
        byte[] hmac = chatPresenter.getHMACFrom(messageWithSignatureHMACAndPK);
        byte[] signature = chatPresenter.getSignatureFrom(messageWithSignatureHMACAndPK);
        byte[] senderPublicKey = chatPresenter.getPublicKeyFrom(messageWithSignatureHMACAndPK);

        byte[] receiverPrivateKey = chatPresenter.getUserOneTimePrivatePreKeyByUUID(ChatActivity.this, contactPublicKeyUUID);

        byte[] decryptSharedSecret = chatPresenter.generateDecryptSharedSecret(senderPublicKey,receiverPrivateKey);

        boolean signatureIsCorrect = chatPresenter.verifySignature(senderPublicKey, encryptedMessage, signature);

        if(signatureIsCorrect){
            byte[] decryptedMessage = chatPresenter.decrypt(encryptedMessage, decryptSharedSecret);

            String hmacAfterDecryption = chatPresenter.bytesToHex(
                    chatPresenter.messageToHMAC(decryptedMessage,decryptSharedSecret)
            );
            if(chatPresenter.bytesToHex(hmac).equals(hmacAfterDecryption)){
                String msg = new String(decryptedMessage, StandardCharsets.UTF_8);

                Message chatMessage = chatPresenter.createTextMessage(senderId, chatId, msg, utc, uuid);
                messages.add(chatMessage);
                updateUnsendMessageRegistry(chatMessage);
                processIncomingTextMessageLocally(chatMessage);
                chatPresenter.deleteOneTimeKeyPairByUUID(contactPublicKeyUUID, ChatActivity.this);
            }else{
                throw new RuntimeException("ChatActivity ==> HMAC is incorrect");
            }
        }else{
            throw new RuntimeException("ChatActivity ==> Signature is incorrect");
        }
    }

    private void handleIncomingSpyTextMessage(long senderId, int chatId, String message, String utc,
                                           String uuid, String contactPublicKeyUUID) {
        byte[] messageWithSignatureHMACAndPK = chatPresenter.messageWithSignatureHMACAndPKToByteArray(message);

        byte[] encryptedMessage = chatPresenter.getMessageFrom(messageWithSignatureHMACAndPK);
        byte[] hmac = chatPresenter.getHMACFrom(messageWithSignatureHMACAndPK);
        byte[] signature = chatPresenter.getSignatureFrom(messageWithSignatureHMACAndPK);
        byte[] senderPublicKey = chatPresenter.getPublicKeyFrom(messageWithSignatureHMACAndPK);

        byte[] receiverPrivateKey = chatPresenter.getUserOneTimePrivatePreKeyByUUID(ChatActivity.this, contactPublicKeyUUID);

        byte[] decryptSharedSecret = chatPresenter.generateDecryptSharedSecret(senderPublicKey,receiverPrivateKey);

        boolean signatureIsCorrect = chatPresenter.verifySignature(senderPublicKey, encryptedMessage, signature);

        if(signatureIsCorrect){
            byte[] decryptedMessage = chatPresenter.decrypt(encryptedMessage, decryptSharedSecret);

            String hmacAfterDecryption = chatPresenter.bytesToHex(
                    chatPresenter.messageToHMAC(decryptedMessage,decryptSharedSecret)
            );
            if(chatPresenter.bytesToHex(hmac).equals(hmacAfterDecryption)){
                String msg = new String(decryptedMessage, StandardCharsets.UTF_8);

                Message chatMessage = chatPresenter.createTextMessage(senderId, chatId, msg, utc, uuid);
                spyModeMessages.add(chatMessage);
                EventBus.getDefault().post(new TextMessageEvent(spyModeMessages));
            }else{
                throw new RuntimeException("ChatActivity ==> HMAC is incorrect");
            }
        }else{
            throw new RuntimeException("ChatActivity ==> Signature is incorrect");
        }
    }

    private void processIncomingTextMessageLocally(Message chatMessage) {
        chatPresenter.insertChatMessage(chatMessage, ChatActivity.this);
        EventBus.getDefault().post(new TextMessageEvent(messages));
    }

    private void updateUnsendMessageRegistry(Message chatMessage) {
        if(messagesNotEmpty()){
            unsendMessageRegistry.put(chatMessage.getMessageUUID(), atLastPosition());
        }else{
            unsendMessageRegistry.put(chatMessage.getMessageUUID(), atFirstPosition());
        }
    }

    private int atFirstPosition() {
        return 0;
    }

    private int atLastPosition() {
        return messages.size()-1;
    }

    private boolean messagesNotEmpty() {
        return messages.size() > 0;
    }

    private void handleOutgoingImageMessage(long senderId, int chatId, Uri imageUrl) {
        try {
            String encodedImage = chatPresenter.encodeImageToString(ChatActivity.this, imageUrl);

            Disposable subscribeGetPublicKey = chatPresenter.getPublicKey(chat.getContactId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(res -> {
                        byte[] contactPublicKey = res.getOneTimePublicKey();
                        String contactPublicKeyUUID = res.getUuid();

                        OneTimeKeyPair userOneTimeKeyPair = chatPresenter.getUserOneTimeKeyPair(
                                ChatActivity.this,
                                chatPresenter.getUserId(ChatActivity.this)
                        );

                        byte[] encryptSharedSecret =
                                chatPresenter.generateEncryptSharedSecret(contactPublicKey, userOneTimeKeyPair.getOneTimePrivateKey());

                        byte[] msgBytes = encodedImage.getBytes(StandardCharsets.UTF_8);

                        byte[] encryptedMessage = chatPresenter.encrypt(msgBytes, encryptSharedSecret);

                        byte[] hmac = chatPresenter.messageToHMAC(msgBytes, encryptSharedSecret);

                        byte[] signature = chatPresenter.generateSignature(userOneTimeKeyPair.getOneTimePrivateKey(), encryptedMessage);

                        byte[] messageWithSignatureHMACAndPK = chatPresenter.appendHMACSignatureAndPKTo(
                                encryptedMessage, hmac, signature, userOneTimeKeyPair.getOneTimePublicKey()
                        );

                        String messageWithSignatureHMACAndPKStr = chatPresenter.messageWithSignatureHMACAndPKToString(messageWithSignatureHMACAndPK);

                        if(chatPresenter.hasInternetConnection()){
                            userOneTimePublicKeyUUIDS.add(userOneTimeKeyPair.getUuid());
                            Message chatMessage = processImageMessage(senderId, chatId, imageUrl, contactPublicKeyUUID, userOneTimeKeyPair.getUuid());
                            sendImageMessageToServer(messageWithSignatureHMACAndPKStr, chatMessage,
                                    contactPublicKeyUUID, userOneTimeKeyPair.getUuid());
                        }else{
                            chatChatsterToast.notifyUserNoInternet();
                        }
                    },Throwable::printStackTrace);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendImageMessageToServer(String encryptedImage, Message chatMessage,
                                          String contactPublicKeyUUID, String userPublicKeyUUID) {
        socket.emit(
                isInSpyMode ? ConstantRegistry.CHATSTER_SPY_CHAT_MESSAGE : ConstantRegistry.CHATSTER_ONE_TO_ONE_CHAT_MESSAGE,
                encryptedImage,
                chatPresenter.getUserId(ChatActivity.this),
                chatPresenter.getUserName(ChatActivity.this),
                contact.getUserId(),
                chat.getChatName(),
                ConstantRegistry.IMAGE,
                chatMessage.getMessageUUID(),
                contactPublicKeyUUID,
                userPublicKeyUUID
        );
    }

    private Message processImageMessage(long senderId, int chatId, Uri imageUrl, String contactPKUUID, String userPKUUID) {
        Message chatMessage = chatPresenter.createImageMessage(senderId, chatId, imageUrl, chatPresenter.createUUID());

        if(!isInSpyMode) {
            messages.add(chatMessage);
            updateUnsendMessageRegistry(chatMessage);
        }else{
            spyModeMessages.add(chatMessage);
        }

        processOutGoingImageMessageLocally(chatMessage, contactPKUUID, userPKUUID);

        return chatMessage;
    }

    private void processOutGoingImageMessageLocally(Message chatMessage, String contactPKUUID, String userPKUUID) {
        if(!isInSpyMode) {
            chatPresenter.insertChatMessage(chatMessage, ChatActivity.this);

            chatPresenter.insertChatMessageQueueItem(chatMessage.getMessageUUID(), contactPKUUID, userPKUUID,
                    contact.getUserId(), ChatActivity.this);

            EventBus.getDefault().post(new ImageMessageEvent(messages));
        }else{
            EventBus.getDefault().post(new ImageMessageEvent(spyModeMessages));
        }
    }

    private void handleIncomingImageMessage(long senderId, int chatId, String encryptedImage, String utc, String uuid, String contactPublicKeyUUID) {

        byte[] messageWithSignatureHMACAndPK = chatPresenter.messageWithSignatureHMACAndPKToByteArray(encryptedImage);

        byte[] encryptedMessage = chatPresenter.getMessageFrom(messageWithSignatureHMACAndPK);
        byte[] hmac = chatPresenter.getHMACFrom(messageWithSignatureHMACAndPK);
        byte[] signature = chatPresenter.getSignatureFrom(messageWithSignatureHMACAndPK);
        byte[] senderPublicKey = chatPresenter.getPublicKeyFrom(messageWithSignatureHMACAndPK);

        byte[] receiverPrivateKey = chatPresenter.getUserOneTimePrivatePreKeyByUUID(ChatActivity.this, contactPublicKeyUUID);

        byte[] decryptSharedSecret = chatPresenter.generateDecryptSharedSecret(senderPublicKey,receiverPrivateKey);

        boolean signatureIsCorrect = chatPresenter.verifySignature(senderPublicKey, encryptedMessage, signature);

        if(signatureIsCorrect){
            byte[] decryptedMessage = chatPresenter.decrypt(encryptedMessage, decryptSharedSecret);

            String hmacAfterDecryption = chatPresenter.bytesToHex(
                    chatPresenter.messageToHMAC(decryptedMessage,decryptSharedSecret)
            );
            if(chatPresenter.bytesToHex(hmac).equals(hmacAfterDecryption)){
                String msg = new String(decryptedMessage, StandardCharsets.UTF_8);

                Uri imageUrl = chatPresenter.saveIncomingImageMessage(ChatActivity.this, chatPresenter.decodeImage(msg));

                Message chatMessage = chatPresenter.createImageMessage(senderId, chatId, imageUrl, utc, uuid);
                messages.add(chatMessage);
                updateUnsendMessageRegistry(chatMessage);
                processIncomingImageMessageLocally(chatMessage);
            }else{
                throw new RuntimeException("ChatActivity ==> HMAC is incorrect");
            }
        }else{
            throw new RuntimeException("ChatActivity ==> Signature is incorrect");
        }
    }

    private void handleIncomingSpyImageMessage(long senderId, int chatId, String encryptedImage, String utc, String uuid, String contactPublicKeyUUID) {

        byte[] messageWithSignatureHMACAndPK = chatPresenter.messageWithSignatureHMACAndPKToByteArray(encryptedImage);

        byte[] encryptedMessage = chatPresenter.getMessageFrom(messageWithSignatureHMACAndPK);
        byte[] hmac = chatPresenter.getHMACFrom(messageWithSignatureHMACAndPK);
        byte[] signature = chatPresenter.getSignatureFrom(messageWithSignatureHMACAndPK);
        byte[] senderPublicKey = chatPresenter.getPublicKeyFrom(messageWithSignatureHMACAndPK);

        byte[] receiverPrivateKey = chatPresenter.getUserOneTimePrivatePreKeyByUUID(ChatActivity.this, contactPublicKeyUUID);

        byte[] decryptSharedSecret = chatPresenter.generateDecryptSharedSecret(senderPublicKey,receiverPrivateKey);

        boolean signatureIsCorrect = chatPresenter.verifySignature(senderPublicKey, encryptedMessage, signature);

        if(signatureIsCorrect){
            byte[] decryptedMessage = chatPresenter.decrypt(encryptedMessage, decryptSharedSecret);

            String hmacAfterDecryption = chatPresenter.bytesToHex(
                    chatPresenter.messageToHMAC(decryptedMessage,decryptSharedSecret)
            );
            if(chatPresenter.bytesToHex(hmac).equals(hmacAfterDecryption)){
                String msg = new String(decryptedMessage, StandardCharsets.UTF_8);

                Uri imageUrl = chatPresenter.saveIncomingImageMessage(ChatActivity.this, chatPresenter.decodeImage(msg));

                Message chatMessage = chatPresenter.createImageMessage(senderId, chatId, imageUrl, utc, uuid);
                spyModeMessages.add(chatMessage);
                EventBus.getDefault().post(new ImageMessageEvent(spyModeMessages));
            }else{
                throw new RuntimeException("ChatActivity ==> HMAC is incorrect");
            }
        }else{
            throw new RuntimeException("ChatActivity ==> Signature is incorrect");
        }
    }

    private void processIncomingImageMessageLocally(Message chatMessage) {
        chatPresenter.insertChatMessage(chatMessage, ChatActivity.this);
        EventBus.getDefault().post(new ImageMessageEvent(messages));
    }

    public void sendMessageClickListener(String message){
        if(chatPresenter.hasInternetConnection()){
            if(messageIsValid(message)){
                sendE2ETextMessage(chat.getContactId(), processTextMessage(message));
            }else{
                chatChatsterToast.notifyUserCantSendEmptyMessage();
            }
        }else{
            chatChatsterToast.notifyUserNoInternet();
        }
    }

    private boolean messageIsValid(String message) {
        return !message.equals(null) && !message.isEmpty();
    }

    private void sendE2ETextMessage(long contactId, Message chatMessage){

        Disposable subscribeGetPublicKey = chatPresenter.getPublicKey(contactId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(res -> {
                    byte[] contactPublicKey = res.getOneTimePublicKey();
                    String contactPublicKeyUUID = res.getUuid();

                    OneTimeKeyPair userOneTimeKeyPair = chatPresenter.getUserOneTimeKeyPair(
                            ChatActivity.this,
                            chatPresenter.getUserId(ChatActivity.this));

                    byte[] encryptSharedSecret =
                            chatPresenter.generateEncryptSharedSecret(contactPublicKey, userOneTimeKeyPair.getOneTimePrivateKey());

                    byte[] msgBytes = chatMessage.getMessageText().getBytes(StandardCharsets.UTF_8);

                    byte[] encryptedMessage = chatPresenter.encrypt(msgBytes, encryptSharedSecret);

                    byte[] hmac = chatPresenter.messageToHMAC(msgBytes, encryptSharedSecret);

                    byte[] signature = chatPresenter.generateSignature(userOneTimeKeyPair.getOneTimePrivateKey(), encryptedMessage);

                    byte[] messageWithSignatureHMACAndPK = chatPresenter.appendHMACSignatureAndPKTo(
                            encryptedMessage, hmac, signature, userOneTimeKeyPair.getOneTimePublicKey()
                    );

                    String messageWithSignatureHMACAndPKStr = chatPresenter.messageWithSignatureHMACAndPKToString(messageWithSignatureHMACAndPK);

                    if(chatPresenter.hasInternetConnection()){
                        userOneTimePublicKeyUUIDS.add(userOneTimeKeyPair.getUuid());

                        sendMessageToServer(messageWithSignatureHMACAndPKStr, chatMessage.getMessageUUID(),
                                contactPublicKeyUUID, userOneTimeKeyPair.getUuid());

                        if(!isInSpyMode){
                            chatPresenter.insertChatMessageQueueItem(chatMessage.getMessageUUID(), contactPublicKeyUUID, userOneTimeKeyPair.getUuid(),
                                    contact.getUserId(),  ChatActivity.this);
                        }
                    }else{
                        chatChatsterToast.notifyUserNoInternet();
                    }
                },Throwable::printStackTrace);
    }

    private void sendMessageToServer(String messageWithSignatureHMACAndPKStr, String uuid,
                                     String contactPublicKeyUUID, String userPublicKeyUUID) {
        socket.emit(
                isInSpyMode ? ConstantRegistry.CHATSTER_SPY_CHAT_MESSAGE : ConstantRegistry.CHATSTER_ONE_TO_ONE_CHAT_MESSAGE,
                messageWithSignatureHMACAndPKStr,
                chatPresenter.getUserId(ChatActivity.this),
                chatPresenter.getUserName(ChatActivity.this),
                contact.getUserId(),
                chat.getChatName(),
                ConstantRegistry.TEXT,
                uuid,
                contactPublicKeyUUID,
                userPublicKeyUUID
        );
    }

    public void speechToTextMessageClickListener(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, ConstantRegistry.SPEECH_INPUT_REQUEST);
        } catch (ActivityNotFoundException a) {
            chatChatsterToast.notifyUserSpeechToTextNotSupported();
        }
    }

    private Message processTextMessage(String message) {
        Message chatMessage = chatPresenter.createTextMessage(
                ConstantRegistry.TEXT,
                chatPresenter.getUserId(ChatActivity.this),
                chat.getChatId(),
                message,
                chatPresenter.getDateTime(),
                chatPresenter.createUUID()
        );

        if(!isInSpyMode) {
            messages.add(chatMessage);
            updateUnsendMessageRegistry(chatMessage);
        }else{
            spyModeMessages.add(chatMessage);
        }

        processTextMessageLocally(chatMessage);

        return chatMessage;
    }

    private void processTextMessageLocally(Message chatMessage) {
        if(!isInSpyMode) {
            chatPresenter.insertChatMessage(chatMessage, ChatActivity.this);
            EventBus.getDefault().post(new TextMessageEvent(messages));
        }else{
            EventBus.getDefault().post(new TextMessageEvent(spyModeMessages));
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
            socket = IO.socket(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_CHAT_PORT),opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        socket.connect();
        socket.on(ConstantRegistry.CHATSTER_HANDLE_CHAT_ONLINE_STATUS, notifyContactStatus);
        socket.on(ConstantRegistry.CHATSTER_HANDLE_CHAT_MESSAGE, handleIncomingMessages);
        socket.on(ConstantRegistry.CHATSTER_SPY_CHAT_MESSAGE, handleIncomingSpyMessages);
        socket.on(ConstantRegistry.CHATSTER_SPY_CHAT_CONNECTION, handleSpyChatConnection);
        socket.on(ConstantRegistry.CHATSTER_UNSEND_MESSAGE, handleUnsendMessage);
        socket.on(ConstantRegistry.CHATSTER_MESSAGE_DELIVERY_STATUS, handleMessageDeliveryStatus);
        socket.on(ConstantRegistry.CHATSTER_MESSAGE_RECEIVED, handleMessageReceivedStatus);
    }

    private Emitter.Listener handleMessageReceivedStatus = new Emitter.Listener(){
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
                            chatPresenter.deleteReceivedOnlineMessageByUUID(uuid,ChatActivity.this);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

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
                            chatPresenter.deleteChatMessageQueueItemByUUID(uuid, ChatActivity.this);

                            for(String userOneTimePublicKeyUUID : userOneTimePublicKeyUUIDS){
                                chatPresenter.deleteOneTimeKeyPairByUUID(userOneTimePublicKeyUUID, ChatActivity.this);
                            }
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

    private Emitter.Listener notifyContactStatus = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String status =  args[0].toString();
                    tvChatContactStatus.setText(status);
                }
            });
        }
    };

    private Emitter.Listener handleIncomingMessages = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    long senderId = 0;
                    String chatName = "";
                    String messageText = "";
                    String messageCreated = "";
                    String uuid = "";
                    String contactPublicKeyUUID = "";
                    int chatId = 0;

                    try {
                        senderId = data.getLong(ConstantRegistry.CHATSTER_SENDER_ID);
                        chatName = data.getString(ConstantRegistry.CHATSTER_MESSAGE_CHAT_NAME);
                        messageText = data.getString(ConstantRegistry.CHATSTER_MESSAGE_TEXT);
                        messageCreated = data.getString(ConstantRegistry.CHATSTER_MESSAGE_CREATED);
                        uuid = data.getString(ConstantRegistry.CHATSTER_MESSAGE_UUID);
                        contactPublicKeyUUID = data.getString(ConstantRegistry.CHATSTER_MESSAGE_CONTACT_PK_UUID);
                        chatId = chatPresenter.getChatIdByChatName(ChatActivity.this, chatName);
                        if(chatWithIdDoesNotExist(chatId)){
                            //that means the chatName needs to be reversed
                            String reversedChatName = reverseChatName(chatName);
                            chatId = chatPresenter.getChatIdByChatName(ChatActivity.this, reversedChatName);
                        }

                        if(isTextMessage(data)){
                            handleIncomingTextMessage(senderId, chatId, messageText, messageCreated, uuid, contactPublicKeyUUID);
                        }else{
                            handleIncomingImageMessage(senderId,chatId, messageText,messageCreated, uuid, contactPublicKeyUUID);
                        }

                        processReceivedOnlineMessage(uuid);

                        checkKeys(chatPresenter.getUserId(ChatActivity.this));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener handleIncomingSpyMessages = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    long senderId = 0;
                    String chatName = "";
                    String messageText = "";
                    String messageCreated = "";
                    String uuid = "";
                    String contactPublicKeyUUID = "";
                    int chatId = 0;

                    try {
                        senderId = data.getLong(ConstantRegistry.CHATSTER_SENDER_ID);
                        chatName = data.getString(ConstantRegistry.CHATSTER_MESSAGE_CHAT_NAME);
                        messageText = data.getString(ConstantRegistry.CHATSTER_MESSAGE_TEXT);
                        messageCreated = data.getString(ConstantRegistry.CHATSTER_MESSAGE_CREATED);
                        uuid = data.getString(ConstantRegistry.CHATSTER_MESSAGE_UUID);
                        contactPublicKeyUUID = data.getString(ConstantRegistry.CHATSTER_MESSAGE_CONTACT_PK_UUID);
                        chatId = chatPresenter.getChatIdByChatName(ChatActivity.this, chatName);
                        if(chatWithIdDoesNotExist(chatId)){
                            //that means the chatName needs to be reversed
                            String reversedChatName = reverseChatName(chatName);
                            chatId = chatPresenter.getChatIdByChatName(ChatActivity.this, reversedChatName);
                        }
                        if(isTextMessage(data)){
                            handleIncomingSpyTextMessage(senderId, chatId, messageText, messageCreated, uuid, contactPublicKeyUUID);
                        }else{
                            handleIncomingSpyImageMessage(senderId,chatId, messageText,messageCreated, uuid, contactPublicKeyUUID);
                        }

                        checkKeys(chatPresenter.getUserId(ChatActivity.this));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener handleSpyChatConnection = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String action = "";

                    try {
                        action = data.getString(ConstantRegistry.CHATSTER_SPY_CHAT_ACTION);

                        switch (action) {
                            case ConstantRegistry.CHATSTER_SPY_CHAT_ACTION_JOIN:
                                currActionSenderId = contact.getUserId();
                                showGoInSpyModeDialog(getString(R.string.join_spy_mode));
                                currAction = action;
                                break;
                            case ConstantRegistry.CHATSTER_SPY_CHAT_ACTION_JOINED:
                                closeLoadingDialog();
                                currAction = action;
                                goInSpyModeChat();

                                break;
                            case ConstantRegistry.CHATSTER_SPY_CHAT_ACTION_REJECTED:
                                closeLoadingDialog();
                                currAction = action;

                                break;
                            case ConstantRegistry.CHATSTER_SPY_CHAT_ACTION_DISCONNECT:
                                currAction = action;
                                leaveSpyChat();

                                break;
                            case ConstantRegistry.CHATSTER_SPY_CHAT_ACTION_SPY_IS_OFFLINE:
                                currAction = ConstantRegistry.CHATSTER_SPY_CHAT_ACTION_DISCONNECT;
                                Toast.makeText(ChatActivity.this, R.string.spy_offline, Toast.LENGTH_LONG).show();
                                leaveSpyChat();

                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private void goInSpyModeChat() {
        closeLoadingDialog();
        isSwitchingToDarkMode = true;
        isInSpyMode = true;
        tvChatContactName.setVisibility(View.INVISIBLE);
        tvChatContactStatus.setVisibility(View.INVISIBLE);
        setStatusBarColor(isSwitchingToDarkMode);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorBlack));
        showSpyChatFragment();
    }

    private void leaveSpyChat() {
        closeLoadingDialog();
        isSwitchingToDarkMode = false;
        isInSpyMode = false;
        tvChatContactName.setVisibility(View.VISIBLE);
        tvChatContactStatus.setVisibility(View.VISIBLE);
        setStatusBarColor(isSwitchingToDarkMode);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        spyModeMessages.clear();
        showChatFragment();
    }

    @NonNull
    private String reverseChatName(String chatName) {
        String[] splitChatName = chatName.split(ConstantRegistry.CHATSTER_AT);
        return splitChatName[atSecondPosition()].
                concat(ConstantRegistry.CHATSTER_AT).
                concat(splitChatName[atFirstPosition()]);
    }

    private boolean isTextMessage(JSONObject data){
        try {
            return data.getString(ConstantRegistry.CHATSTER_MESSAGE_TYPE).equals(ConstantRegistry.TEXT);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

    }

    private void processReceivedOnlineMessage(String uuid) {
        chatPresenter.insertReceivedOnlineMessage(uuid, ChatActivity.this);
        socket.emit(ConstantRegistry.CHATSTER_MESSAGE_RECEIVED,uuid);
    }

    private Emitter.Listener handleUnsendMessage = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String chatName = "";
                    String messageText = "";
                    String uuid = "";
                    long senderId = 0;
                    int chatId = 0;
                    try {
                        senderId = data.getLong(ConstantRegistry.CHATSTER_SENDER_ID);
                        messageText = data.getString(ConstantRegistry.CHATSTER_MESSAGE_TEXT);
                        uuid = data.getString(ConstantRegistry.CHATSTER_MESSAGE_UUID);

                        if(isAllowedUnsend(messageText)){
                            if(senderIsNotUser(senderId)){
                                chatName = data.getString(ConstantRegistry.CHATSTER_MESSAGE_CHAT_NAME);
                                chatId = chatPresenter.getChatIdByChatName(ChatActivity.this, chatName);
                                if(chatWithIdDoesNotExist(chatId)){
                                    //that means the chatName needs to be reversed
                                    String reversedChatName = reverseChatName(chatName);
                                    chatId = chatPresenter.getChatIdByChatName(ChatActivity.this, reversedChatName);
                                }
                                sendUnsendMessageToFragment(uuid);
                                processUnsendMessage(uuid);
                            }
                        }else{
                            if(senderIsUser(senderId)){
                                chatChatsterToast.notifyUserCantUnsendMessagesInThisChat();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private boolean isAllowedUnsend(String messageText) {
        return !messageText.equals(ConstantRegistry.CHATSTER_NOT_ALLOWED_UNSEND_MESSAGE);
    }

    private boolean senderIsNotUser(long senderId) {
        return chatPresenter.getUserId(ChatActivity.this) != senderId;
    }

    private boolean senderIsUser(long senderId) {
        return chatPresenter.getUserId(ChatActivity.this) == senderId;
    }

    private boolean chatWithIdDoesNotExist(int chatId) {
        return chatId == 0;
    }

    private int atSecondPosition() {
        return 1;
    }

    private void processUnsendMessage(String uuid) {
        messages.get(unsendMessageRegistry.get(uuid)).setMessageText(getString(R.string.message_deleted));
        unsendMessageRegistry.remove(uuid);
        chatPresenter.updateMessageUnsentByMessageUUID(uuid, ChatActivity.this);
    }

    private void sendUnsendMessageToFragment(String uuid) {
        UnsendMessageEvent unsendMessageEvent =
                new UnsendMessageEvent(getString(R.string.message_deleted),
                        unsendMessageRegistry.get(uuid));
        EventBus.getDefault().post(unsendMessageEvent);
    }

    // endregion

    // region OnClick Listeners

    @OnClick(R.id.ivChatBack)
    public void ivChatBackListener(){
        rootCoordinator.navigateToMainActivity(ChatActivity.this);
        finish();
    }

    // endregion

    // region Initialize Unsend Message Registry

    private void initializeUnsendMessageRegistry(ArrayList<Message> messages){
        subscribeInitializeUnsendMessageRegistry = chatPresenter.initializeUnsendMessageRegistry(messages)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(res -> {
                    unsendMessageRegistry = res;
                }, Throwable::printStackTrace);
        disposable.add(subscribeInitializeUnsendMessageRegistry);
    }

    // endregion

    // region Show Go In Spy Mode Dialog

    private void showGoInSpyModeDialog(String question) {
        spyModeDialogFragment =  SpyModeDialogFragment.newInstance(question);
        spyModeDialogFragment.setCancelable(true);
        spyModeDialogFragment.show(getSupportFragmentManager(), ConstantRegistry.CHATSTER_SPY_MODE_QUESTION);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if(currActionSenderId == chatPresenter.getUserId(ChatActivity.this)){
            sendSpyModeAction(currAction);
        }else{
            sendSpyModeAction(ConstantRegistry.CHATSTER_SPY_CHAT_ACTION_JOINED);
            goInSpyModeChat();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();

        if(currActionSenderId != chatPresenter.getUserId(ChatActivity.this)){
            if(currAction.equals(ConstantRegistry.CHATSTER_SPY_CHAT_ACTION_JOIN)){
                sendSpyModeAction(ConstantRegistry.CHATSTER_SPY_CHAT_ACTION_REJECTED);
            }
        }
    }

    // endregion

    // region Upload New Batch Of Keys

    private void uploadNewBatchOfKeys(long userId, String oneTimePreKeyPairPbks){
        Disposable subscribeUploadKeys = chatPresenter.uploadPublicKeys(userId, oneTimePreKeyPairPbks)
                .subscribeOn(Schedulers.io())
                .subscribe(res -> {
                    if(res.equals(ConstantRegistry.ERROR)){
                        throw new RuntimeException("Firebase =====> upload new keys error");
                    }
                }, Throwable::printStackTrace);
    }

    private void checkKeys(long userId){
        Disposable subscribeCheckKeys = chatPresenter.checkPublicKeys(userId)
                .subscribeOn(Schedulers.io())
                .subscribe(res -> {
                    if(res.equals(ConstantRegistry.YES)){
                        uploadNewBatchOfKeys(
                                userId,
                                chatPresenter.jsonifiedOneTimeKeys(
                                        ChatActivity.this,
                                        userId,
                                        ConstantRegistry.AMOUNT_OF_ONE_TIME_KEY_PAIRS_AT_REPLENISHMENT
                                )
                        );
                    }else if(res.equals(ConstantRegistry.ERROR)){
                        throw new RuntimeException("ChatActivity ===> checkKeys error");
                    }
                },Throwable::printStackTrace);
    }

    private void showNotRegisteredDialog(String contactName) {
        notRegisteredDialogFragment =  NotRegisteredDialogFragment.newInstance(contactName);
        notRegisteredDialogFragment.setCancelable(false);
        notRegisteredDialogFragment.show(getSupportFragmentManager(), ConstantRegistry.CHATSTER_CONTACT_NOT_REGISTERED);
    }

    @Override
    public void onPositiveClick(DialogFragment dialog) {
        rootCoordinator.navigateToInviteActivity(ChatActivity.this, chatPresenter.getUserName(ChatActivity.this), contact.getUserName());
        finish();
    }

    @Override
    public void onNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
        rootCoordinator.navigateToMainActivity(ChatActivity.this);
        finish();
    }

    // endregion

}
