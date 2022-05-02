package nl.mwsoft.www.chatster.viewLayer.creators;


import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

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
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.event.creators.SearchCreatorsEvent;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorContact;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorPost;
import nl.mwsoft.www.chatster.modelLayer.model.HistoryItem;
import nl.mwsoft.www.chatster.presenterLayer.creators.CreatorsPresenter;
import nl.mwsoft.www.chatster.viewLayer.creators.fragment.CameraVideoFragment;
import nl.mwsoft.www.chatster.viewLayer.creators.fragment.CreateFragment;
import nl.mwsoft.www.chatster.viewLayer.creators.fragment.CreatorFollowingFragment;
import nl.mwsoft.www.chatster.viewLayer.creators.fragment.CreatorsFollowersFragment;
import nl.mwsoft.www.chatster.viewLayer.creators.fragment.DiscoverFragment;
import nl.mwsoft.www.chatster.viewLayer.creators.fragment.HistoryFragment;
import nl.mwsoft.www.chatster.viewLayer.creators.fragment.PostDetailFragment;
import nl.mwsoft.www.chatster.viewLayer.creators.fragment.PostsFragment;
import nl.mwsoft.www.chatster.viewLayer.creators.fragment.ProfileFragment;
import nl.mwsoft.www.chatster.viewLayer.dialog.loadingDialog.LoadingDialogFragment;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CreatorsActivity extends AppCompatActivity {

    private RootCoordinator rootCoordinator;
    public static int currFragmentPosition = 0;
    private Uri photoURI;
    private CreatorsPresenter creatorsPresenter;
    private Disposable subscribeLatestCreatorPosts;
    private Disposable subscribeCreatorPosts;
    private Disposable subscribeCreatorContact;
    private Disposable subscribeDiscoverPosts;
    private Disposable subscribeFollowers;
    private Disposable subscribeFollowing;
    private Disposable subscribeHistory;
    private Disposable subscribeLoadMorePosts;
    private CompositeDisposable disposable;
    private ArrayList<CreatorPost> creatorPosts;
    private ArrayList<CreatorPost> creatorProfilePosts;
    private ArrayList<CreatorPost> discoverCreatorPosts;
    private ArrayList<CreatorContact> creatorContacts;
    private ArrayList<CreatorContact> followers;
    private ArrayList<CreatorContact> following;
    private ArrayList<HistoryItem> historyItems;
    private CreatorContact creatorContact;
    private Socket socket;
    private Socket searchSocket;
    private LoadingDialogFragment loadingDialogFragment;
    private HashMap<String, CreatorContact> creatorsRegistry;
    private BottomNavigationView navigation;
    private boolean hasBeenOpenedFromNotification = false;
    public static String lastCheckedProfileCreatorName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creators);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        disposable = new CompositeDisposable();
        creatorPosts = new ArrayList<>();
        creatorProfilePosts = new ArrayList<>();
        discoverCreatorPosts = new ArrayList<>();
        creatorContacts = new ArrayList<>();
        followers = new ArrayList<>();
        following = new ArrayList<>();
        historyItems = new ArrayList<>();
        creatorContact = new CreatorContact();
        creatorsRegistry = new HashMap<>();
        DependencyRegistry.shared.inject(this);

        navigation = (BottomNavigationView) findViewById(R.id.bnCreators);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if(getIntent().getAction() != null){
            if(getIntent().getAction().equals(ConstantRegistry.READ_HISTORY_ITEM_REQUEST)){
                hasBeenOpenedFromNotification = true;
                navigation.setSelectedItemId(R.id.navigation_history);
                removeNotifications();
            }else{
                navigation.setSelectedItemId(R.id.navigation_posts);
            }
        }else{
            navigation.setSelectedItemId(R.id.navigation_posts);
        }

        setUpConnectionToSearchServer();
        setUpConnectionToServer();
    }


    public void configureWith(CreatorsPresenter creatorsPresenter, RootCoordinator rootCoordinator) {
        this.rootCoordinator = rootCoordinator;
        this.creatorsPresenter = creatorsPresenter;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_posts:
                    currFragmentPosition = 1;
                    if(creatorPosts.size() > 0){
                        fragment = PostsFragment.newInstance(creatorPosts);
                        loadFragment(fragment, false);
                    }else{
                        showLoadingDialog();
                        getLatestCreatorPosts(creatorsPresenter.getUserId(CreatorsActivity.this),
                                creatorsPresenter.getUserName(CreatorsActivity.this));
                    }
                    return true;
                case R.id.navigation_create:
                    currFragmentPosition = 2;
                    fragment = CreateFragment.newInstance();
                    loadFragment(fragment, false);
                    return true;
                case R.id.navigation_discover:
                    currFragmentPosition = 3;
                    discoverPosts(creatorsPresenter.getUserId(CreatorsActivity.this));
                    return true;
                case R.id.navigation_history:
                    currFragmentPosition = 4;
                    getCreatorHistory(creatorsPresenter.getUserName(CreatorsActivity.this),
                            creatorsPresenter.getUserId(CreatorsActivity.this));
                    return true;
                case R.id.navigation_profile:
                    currFragmentPosition = 5;
                    showLoadingDialog();
                    getCreatorContactProfile(creatorsPresenter.getUserName(CreatorsActivity.this),
                                creatorsPresenter.getUserId(CreatorsActivity.this), false);
                    return true;
            }
            return false;
        }
    };

    public void loadFragment(Fragment fragment, boolean shouldAddNullToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_creators_container, fragment);

        if(fragment instanceof PostDetailFragment){
            currFragmentPosition = 6;
        }

        if(shouldAddNullToBackStack){
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }

    public void navigateToCommentsActivity(CreatorPost creatorPost,Context context){
        rootCoordinator.navigateToCommentsActivity(creatorPost, context);
    }

    public void navigateToChatsterSettingsActivity(Context context){
        rootCoordinator.navigateToChatsterSettingsActivity(context);
    }

    private void getLatestCreatorPosts(long creator, String creatorsName){
        subscribeLatestCreatorPosts = creatorsPresenter.getLatestCreatorPosts(creator, creatorsName).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(this::processResultLatestCreatorPosts,
                        throwable -> {
                            processErrorResult();
                        });
    }

    private void getCreatorContactProfile(String creator, long userId, boolean shouldAddToBackStack){
        subscribeCreatorContact = creatorsPresenter.getCreatorContactProfile(creator,userId).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(res -> {
                    closeLoadingDialog();
                    creatorContact = res;
                    for(int i = 0; i < creatorContact.getCreatorPosts().size(); i++){
                        res.getCreatorPosts().get(i)
                                .setPostCreated(
                                        creatorsPresenter
                                                .convertFromUtcToLocal(res.getCreatorPosts().get(i).getPostCreated())
                                );
                    }
                    Fragment fragment = ProfileFragment.newInstance(creatorContact);
                    loadFragment(fragment, shouldAddToBackStack);
                },
                throwable -> {
                    processErrorResult();
                });
    }

    private void discoverPosts(long userId){
        showLoadingDialog();
        subscribeDiscoverPosts = creatorsPresenter.discoverPosts(userId).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(res -> {
                    discoverCreatorPosts.clear();

                    for(CreatorPost creatorPost: res){
                        creatorPost.setPostCreated(creatorsPresenter.convertFromUtcToLocal(creatorPost.getPostCreated()));
                        discoverCreatorPosts.add(creatorPost);
                    }

                    closeLoadingDialog();

                    Fragment fragment = DiscoverFragment.newInstance(discoverCreatorPosts, creatorContacts);
                    loadFragment(fragment, false);
                },
                throwable -> {
                    processErrorResult();
                });
    }

    public void loadMorePosts(String creatorName, String lastPostCreated){
        showLoadingDialog();
        subscribeLoadMorePosts = creatorsPresenter.loadMorePosts(creatorName, lastPostCreated).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(res -> {
                    for(CreatorPost creatorPost: res){
                        creatorPost.setPostCreated(creatorsPresenter.convertFromUtcToLocal(creatorPost.getPostCreated()));
                        creatorPosts.add(creatorPost);
                    }

                    closeLoadingDialog();
                    // EventBus.getDefault().post(new LoadMorePostsEvent(creatorPosts));
                },
                throwable -> {
                    processErrorResult();
                });
    }

    public void getFollowers(String creatorName, long userId){
        showLoadingDialog();
        subscribeFollowers = creatorsPresenter.getCreatorFollowers(creatorName, userId).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(res -> {
                    closeLoadingDialog();
                    followers.addAll(res);
                    Fragment fragment = CreatorsFollowersFragment.newInstance(res);
                    loadFragment(fragment, true);
                },
                throwable -> {
                    processErrorResult();
                });
    }

    public void getFollowing(String creatorName,long userId){
        showLoadingDialog();
        subscribeFollowing = creatorsPresenter.getCreatorFollowing(creatorName, userId).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(res -> {
                    closeLoadingDialog();
                    following.addAll(res);
                    Fragment fragment = CreatorFollowingFragment.newInstance(res);
                    loadFragment(fragment, true);
                },
                throwable -> processErrorResult());
    }

    public void getCreatorHistory(String creatorName, long userId){
        showLoadingDialog();
        subscribeHistory = creatorsPresenter.getCreatorHistory(creatorName, userId).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(res -> {
                    historyItems.clear();

                    for(HistoryItem historyItem: res){
                        historyItem.setCreated(creatorsPresenter.convertFromUtcToLocal(historyItem.getCreated()));
                        historyItems.add(historyItem);
                    }
                    closeLoadingDialog();
                    Fragment fragment = HistoryFragment.newInstance(historyItems);
                    loadFragment(fragment, false);
                },
                throwable -> {
                    processErrorResult();
                });
    }

    private void processErrorResult(){
        closeLoadingDialog();
        Toast.makeText(CreatorsActivity.this,R.string.smth_went_wrong,Toast.LENGTH_LONG).show();
    }

    private void processResultLatestCreatorPosts(ArrayList<CreatorPost> res) {
        for(CreatorPost creatorPost: res){
            creatorPost.setPostCreated(creatorsPresenter.convertFromUtcToLocal(creatorPost.getPostCreated()));
            creatorPosts.add(creatorPost);
        }

        closeLoadingDialog();

        Fragment fragment = PostsFragment.newInstance(creatorPosts);
        loadFragment(fragment, false);
    }

    private void selectVideoFromGallery(){
        Intent intent = new Intent();
        // Show only video
        intent.setType(ConstantRegistry.CHATSTER_DOCUMENT_TYPE_VIDEO);
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(Intent.createChooser(intent,ConstantRegistry.CHATSTER_SELECT_VIDEO),
                ConstantRegistry.PICK_VIDEO_REQUEST);
    }

    private void setStatusBarColor() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorBlack));
    }

    private void captureVideoPost(){
        setStatusBarColor();
        navigation.setVisibility(View.GONE);
        getFragmentManager().beginTransaction()
                .replace(R.id.frame_creators_container, CameraVideoFragment.newInstance())
                .commit();
    }

    public void createTextPost(String action){
        rootCoordinator.navigateToCreatePostActivity(CreatorsActivity.this, action);
    }

    private void selectImageFromGallery(){
        Intent intent = new Intent();
        // Show only images
        intent.setType(ConstantRegistry.CHATSTER_DOCUMENT_TYPE_IMAGE);
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);//Intent.ACTION_OPEN_DOCUMENT Intent.ACTION_GET_CONTENT
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, ConstantRegistry.CHATSTER_SELECT_PICTURE),
                ConstantRegistry.PICK_IMAGE_REQUEST);
    }

    private void takePicture(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = creatorsPresenter.createImageFile(CreatorsActivity.this);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(CreatorsActivity.this,
                        ConstantRegistry.CHATSTER_FILE_PROVIDER,
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, ConstantRegistry.REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(currFragmentPosition != 6 && currFragmentPosition != 5){
            if(hasBeenOpenedFromNotification){
                rootCoordinator.navigateToMainActivity(CreatorsActivity.this);
                finish();
            }else{
                finish();
            }
        }
    }

    private boolean accessFilesPermissionIsGranted() {
        if (ContextCompat.checkSelfPermission(CreatorsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }else{
            return false;
        }
    }

    private void requestPermissionWriteExternalStorage() {
        ActivityCompat.requestPermissions(CreatorsActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                ConstantRegistry.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ConstantRegistry.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectImageFromGallery();
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

        if (requestCode == ConstantRegistry.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            handlePickImageRequest(data.getData(), ConstantRegistry.IMAGE);
        }else if (requestCode == ConstantRegistry.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            handleImageCaptureRequest(ConstantRegistry.IMAGE);
        }else if (requestCode == ConstantRegistry.PICK_VIDEO_REQUEST && resultCode == RESULT_OK) {
            handlePickVideoRequest(data.getData(), ConstantRegistry.VIDEO);
        }else{
            Toast.makeText(CreatorsActivity.this,getString(R.string.smth_went_wrong), Toast.LENGTH_LONG).show();
        }
    }

    private void handleImageCaptureRequest(String action) {
        if(photoURI != null){
            try {
                Bitmap photoBitmap = creatorsPresenter.decodeSampledBitmap(CreatorsActivity.this, photoURI);
                photoURI = creatorsPresenter.saveIncomingImageMessage(CreatorsActivity.this, photoBitmap);
                rootCoordinator.navigateToCreatePostActivity(CreatorsActivity.this, photoURI, action);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(CreatorsActivity.this,getString(R.string.smth_went_wrong), Toast.LENGTH_LONG).show();
        }
    }

    private void handlePickImageRequest(Uri uri, String action) {
        rootCoordinator.navigateToCreatePostActivity(CreatorsActivity.this, uri, action);
    }

    private void handlePickVideoRequest(Uri uri, String action) {
        rootCoordinator.navigateToCreatePostActivity(CreatorsActivity.this, uri, action);
    }

    public void handleCaptureVideo(Uri uri, String action) {
        rootCoordinator.navigateToCreatePostActivity(CreatorsActivity.this, uri, action);
    }

    public void createPostFromGallery(){
        if(accessFilesPermissionIsGranted()){
            selectImageFromGallery();
        }else{
            requestPermissionWriteExternalStorage();
        }
    }

    public void createPostFromCamera(){
        if(accessFilesPermissionIsGranted()){
            takePicture();
        }else{
            requestPermissionWriteExternalStorage();
        }
    }

    public void createVideoPostFromGallery(){
        if(accessFilesPermissionIsGranted()){
            selectVideoFromGallery();
        }else{
            requestPermissionWriteExternalStorage();
        }
    }

    public void createVideoPostFromCapture(){
        captureVideoPost();
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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    private void setUpConnectionToSearchServer(){
        // default settings for all sockets
        IO.setDefaultOkHttpWebSocketFactory(OkHttpClientManager.setUpSecureClient());
        IO.setDefaultOkHttpCallFactory(OkHttpClientManager.setUpSecureClient());

        // set as an option
        IO.Options opts = new IO.Options();
        opts.transports = new String[] { WebSocket.NAME };
        opts.callFactory = OkHttpClientManager.setUpSecureClient();
        opts.webSocketFactory = OkHttpClientManager.setUpSecureClient();
        try {
            searchSocket = IO.socket(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_CREATORS_Q_PORT),opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        searchSocket.connect();
        searchSocket.on(ConstantRegistry.CHATSTER_SEARCH_FOR_CREATOR, searchForCreator);
    }

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
            socket = IO.socket(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_CREATORS_PORT),opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        socket.connect();
        socket.on(ConstantRegistry.CHATSTER_CONNECT_WITH_CREATOR, connectWithCreator);
        socket.on(ConstantRegistry.CHATSTER_DISCONNECT_WITH_CREATOR, disconnectWithCreator);
        socket.on(ConstantRegistry.CHATSTER_LIKE_CREATORS_POST, likeCreatorsPost);
        socket.on(ConstantRegistry.CHATSTER_UNLIKE_CREATORS_POST, unlikeCreatorsPost);
        socket.on(ConstantRegistry.CHATSTER_DELETE_CREATORS_POST, deleteCreatorsPost);
    }

    private Emitter.Listener connectWithCreator = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String status = "";
                    String creatorsName = "";

                    try {
                        creatorsName = data.getString(ConstantRegistry.CHATSTER_CREATORS_NAME);
                        status = data.getString(ConstantRegistry.CHATSTER_CREATORS_POST_STATUS);
                        if(statusIsSuccess(status)){
                            Toast.makeText(CreatorsActivity.this, getString(R.string.you_are_now_following) + creatorsName, Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(CreatorsActivity.this, getString(R.string.smth_went_wrong), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener disconnectWithCreator = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String status = "";
                    String creatorsName = "";

                    try {
                        creatorsName = data.getString(ConstantRegistry.CHATSTER_CREATORS_NAME);
                        status = data.getString(ConstantRegistry.CHATSTER_CREATORS_POST_STATUS);
                        if(statusIsSuccess(status)){
                            Toast.makeText(CreatorsActivity.this, getString(R.string.you_have_unfollowed) + creatorsName, Toast.LENGTH_LONG).show();
                        }else {
                            Toast.makeText(CreatorsActivity.this, getString(R.string.smth_went_wrong), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener likeCreatorsPost = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String uuid = "";
                    String status = "";
                    long updatedLikes = 0;

                    try {
                        uuid = data.getString(ConstantRegistry.CHATSTER_CREATORS_POST_UUID);
                        status = data.getString(ConstantRegistry.CHATSTER_CREATORS_POST_STATUS);
                        updatedLikes = data.getLong(ConstantRegistry.CHATSTER_CREATORS_POST_UPDATED_LIKES);
                        if(!statusIsSuccess(status)){
                            Toast.makeText(CreatorsActivity.this,
                                    getString(R.string.smth_went_wrong),Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener unlikeCreatorsPost = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String uuid = "";
                    String status = "";
                    long updatedLikes = 0;

                    try {
                        uuid = data.getString(ConstantRegistry.CHATSTER_CREATORS_POST_UUID);
                        status = data.getString(ConstantRegistry.CHATSTER_CREATORS_POST_STATUS);
                        updatedLikes = data.getLong(ConstantRegistry.CHATSTER_CREATORS_POST_UPDATED_LIKES);
                        if(!statusIsSuccess(status)){
                            Toast.makeText(CreatorsActivity.this,
                                    getString(R.string.smth_went_wrong),Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener deleteCreatorsPost = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    closeLoadingDialog();
                    JSONObject data = (JSONObject) args[0];
                    String uuid = "";
                    String status = "";

                    try {
                        uuid = data.getString(ConstantRegistry.CHATSTER_CREATORS_POST_UUID);
                        status = data.getString(ConstantRegistry.CHATSTER_CREATORS_POST_STATUS);
                        if(!statusIsSuccess(status)){
                            Toast.makeText(CreatorsActivity.this,
                                    getString(R.string.smth_went_wrong),Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(CreatorsActivity.this,
                                    R.string.post_is_deleted,Toast.LENGTH_LONG).show();
                        }
                        getCreatorContactProfile(creatorsPresenter.getUserName(CreatorsActivity.this),
                                creatorsPresenter.getUserId(CreatorsActivity.this), false);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    private Emitter.Listener searchForCreator = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data = (String) args[0];
                    try {
                        JSONArray jsonArray = new JSONArray(data);
                        for(int i = 0; i < jsonArray.length(); i++){
                            CreatorContact creatorContact = new CreatorContact();
                            JSONObject json = (JSONObject) jsonArray.get(i);
                            creatorContact.setCreatorId(json.getString("creatorId"));
                            creatorContact.setStatusMessage(json.getString("statusMessage"));
                            creatorContact.setProfilePic(json.getString("profilePic"));
                            creatorContact.setPosts(json.getLong("posts"));
                            creatorContact.setCreatorFollowers(json.getLong("creatorFollowers"));
                            creatorContact.setCreatorFollowing(json.getLong("creatorFollowing"));
                            creatorContact.setCreatorProfileViews(json.getLong("creatorProfileViews"));
                            creatorContact.setCreatorTotalLikes(json.getLong("creatorTotalLikes"));
                            creatorContact.setWebsite(json.getString("website"));
                            creatorContact.setFollowingThisCreator(json.getInt("followingThisCreator"));
                            if(!creatorsRegistry.containsKey(creatorContact.getCreatorId())){
                                creatorsRegistry.put(creatorContact.getCreatorId(), creatorContact);
                                creatorContacts.add(creatorContact);
                            }
                        }

                        EventBus.getDefault().post(new SearchCreatorsEvent(creatorContacts));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    public void likePost(String userName, CreatorPost creatorPost, int postPosition, String userProfilePicUrl, boolean isLikedFromPostDetails) {
        if(creatorsPresenter.hasInternetConnection()){
            if(isLikedFromPostDetails){
                creatorContact.getCreatorPosts().get(postPosition).setLikes(creatorPost.getLikes());
            }else {
                creatorPosts.get(postPosition).setLikes(creatorPost.getLikes());
            }
            socket.emit(ConstantRegistry.CHATSTER_LIKE_CREATORS_POST, userName, creatorPost.getUuid(), userProfilePicUrl);
        }else{
            closeLoadingDialog();
            Toast.makeText(CreatorsActivity.this, R.string.no_internet_connection,
                    Toast.LENGTH_LONG).show();
        }
    }

    public void unlikePost(String userName, CreatorPost creatorPost, int postPosition, String userProfilePicUrl, boolean isUnLikedFromPostDetails) {
        if(creatorsPresenter.hasInternetConnection()){
            if(isUnLikedFromPostDetails){
                creatorContact.getCreatorPosts().get(postPosition).setLikes(creatorPost.getLikes());
            }else {
                creatorPosts.get(postPosition).setLikes(creatorPost.getLikes());
            }
            socket.emit(ConstantRegistry.CHATSTER_UNLIKE_CREATORS_POST, userName, creatorPost.getUuid(), userProfilePicUrl);
        }else{
            closeLoadingDialog();
            Toast.makeText(CreatorsActivity.this, R.string.no_internet_connection,
                    Toast.LENGTH_LONG).show();
        }
    }

    public void editPost(CreatorPost creatorPost) {

    }

    public void followCreator(long userId, String userName, String photoUrl, String creatorName) {
        if(creatorsPresenter.hasInternetConnection()){
            socket.emit(ConstantRegistry.CHATSTER_CONNECT_WITH_CREATOR, userId, userName, photoUrl, creatorName);
        }else{
            Toast.makeText(CreatorsActivity.this,R.string.no_internet_connection,Toast.LENGTH_LONG).show();
        }
    }

    public void unFollowCreator(long userId, String userName, String photoUrl, String creatorName) {
        if(creatorsPresenter.hasInternetConnection()){
            socket.emit(ConstantRegistry.CHATSTER_DISCONNECT_WITH_CREATOR, userId, userName, photoUrl, creatorName);
        }else{
            Toast.makeText(CreatorsActivity.this,R.string.no_internet_connection,Toast.LENGTH_LONG).show();
        }
    }

    public void searchCreator(long userId, String userName) {
        if(creatorsPresenter.hasInternetConnection()){
            searchSocket.emit(ConstantRegistry.CHATSTER_SEARCH_FOR_CREATOR, userId, userName);
        }else{
            Toast.makeText(CreatorsActivity.this,R.string.no_internet_connection,Toast.LENGTH_LONG).show();
        }
    }

    public void navigateToCreatorsProfile(String creatorName,  long userId, boolean shouldAddToBackStack) {
        showLoadingDialog();
        getCreatorContactProfile(creatorName, userId, shouldAddToBackStack);
    }

    public void showCreatorsProfilePicPopUp(String userName) {

    }

    public void showDeletePostDialog(String postUUID) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreatorsActivity.this);
        // set title
        alertDialogBuilder.setTitle(R.string.delete_post);
        // set dialog message
        alertDialogBuilder
                .setMessage(R.string.sure_delete_post)
                .setCancelable(false)
                .setPositiveButton(R.string.yes,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, close current activity
                        showLoadingDialog();
                        socket.emit(ConstantRegistry.CHATSTER_DELETE_CREATORS_POST, postUUID);
                    }
                })
                .setNegativeButton(R.string.no,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // if this button is clicked, just close the dialog box and do nothing
                        dialog.cancel();
                        closeLoadingDialog();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public String getUserName(Context context){
        return creatorsPresenter.getUserName(context);
    }

    public long getUserId(Context context){
        return creatorsPresenter.getUserId(context);
    }

    public String getUserProfilePicUrl(Context context){
        return creatorsPresenter.getUserProfilePicUrl(context);
    }

    public String getUserStatusMessage(Context context){
        return creatorsPresenter.getUserStatusMessage(context);
    }

    public int getCreatorsPostIsLiked(Context context, String postUUID){
        return creatorsPresenter.getCreatorsPostIsLiked(context, postUUID);
    }

    public boolean getCreatorsPostExists(Context context, String postUUID) {
        return creatorsPresenter.getCreatorsPostExists(context, postUUID);
    }

    public void updateCreatorPostIsLiked(String uuid, int status, Context context){
        this.creatorsPresenter.updateCreatorPostIsLiked(uuid, status, context);
    }

    public void insertCreatorPostIsLiked(String uuid, Context context) {
        this.creatorsPresenter.insertCreatorPostIsLiked(uuid, context);
    }

    private boolean statusIsSuccess(String status) {
        return status.equals(ConstantRegistry.SUCCESS);
    }

    private void showLoadingDialog() {
        loadingDialogFragment = new LoadingDialogFragment();
        loadingDialogFragment.setCancelable(false);
        loadingDialogFragment.show(getSupportFragmentManager(), ConstantRegistry.LOADING);
    }

    private void closeLoadingDialog() {
        if(loadingDialogFragment != null){
            loadingDialogFragment.dismiss();
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
}
