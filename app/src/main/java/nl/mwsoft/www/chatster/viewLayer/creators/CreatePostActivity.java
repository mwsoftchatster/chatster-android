package nl.mwsoft.www.chatster.viewLayer.creators;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

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
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import nl.mwsoft.www.chatster.presenterLayer.postEditing.PostEditingPresenter;
import nl.mwsoft.www.chatster.viewLayer.creators.fragment.PostImageFragment;
import nl.mwsoft.www.chatster.viewLayer.creators.fragment.PostTextFragment;
import nl.mwsoft.www.chatster.viewLayer.creators.fragment.PostVideoFragment;
import nl.mwsoft.www.chatster.viewLayer.dialog.loadingDialog.LoadingDialogFragment;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CreatePostActivity extends AppCompatActivity {

    private Socket socket;
    private RootCoordinator rootCoordinator;
    private PostEditingPresenter postEditingPresenter;
    private Uri uri;
    private LoadingDialogFragment loadingDialogFragment;
    private Disposable subscribeUploadVideoPost;
    private CompositeDisposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_editing);

        DependencyRegistry.shared.inject(this);

        setUpConnectionToServer();

        if(getIntent().getAction() != null){
            if(getIntent().getAction().equals(ConstantRegistry.IMAGE)){
                if(getIntent().getExtras() != null){
                    uri = getIntent().getExtras().getParcelable(ConstantRegistry.CREATORS_URI);
                    if(uri != null){
                        loadFragment(PostImageFragment.newInstance(uri));
                    }else{
                        Toast.makeText(CreatePostActivity.this,getString(R.string.smth_went_wrong), Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(CreatePostActivity.this,getString(R.string.smth_went_wrong), Toast.LENGTH_LONG).show();
                }
            }else if(getIntent().getAction().equals(ConstantRegistry.VIDEO)){
                if(getIntent().getExtras() != null){
                    uri = getIntent().getExtras().getParcelable(ConstantRegistry.CREATORS_URI);
                    if(uri != null){
                        loadFragment(PostVideoFragment.newInstance(uri));
                    }else {
                        Toast.makeText(CreatePostActivity.this, getString(R.string.smth_went_wrong), Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(CreatePostActivity.this,getString(R.string.smth_went_wrong), Toast.LENGTH_LONG).show();
                }
            }else if(getIntent().getAction().equals(ConstantRegistry.TEXT)){
                loadFragment(PostTextFragment.newInstance());
            }
        }
    }

    public void configureWith(RootCoordinator rootCoordinator, PostEditingPresenter postEditingPresenter){
        this.rootCoordinator = rootCoordinator;
        this.postEditingPresenter = postEditingPresenter;
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_post_editing_container, fragment);
        transaction.commit();
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
        socket.on(ConstantRegistry.CHATSTER_SAVE_CREATORS_POST, saveCreatorPostStatus);
        socket.on(ConstantRegistry.CHATSTER_SAVE_CREATORS_TEXT_POST, saveCreatorTextPostStatus);
    }

    private Emitter.Listener saveCreatorTextPostStatus = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String status =  args[0].toString();
                    closeLoadingDialog();
                    if(statusIsSuccess(status)){
                        Toast.makeText(CreatePostActivity.this, R.string.post_successfully_uploaded,
                                Toast.LENGTH_LONG).show();
                        rootCoordinator.navigateToCreatorsActivityAfterPosting(CreatePostActivity.this);
                    }else{
                        Toast.makeText(CreatePostActivity.this, R.string.smth_went_wrong,
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    };

    private Emitter.Listener saveCreatorPostStatus = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String status =  args[0].toString();
                    closeLoadingDialog();
                    if(statusIsSuccess(status)){
                        Toast.makeText(CreatePostActivity.this, R.string.post_successfully_uploaded,
                                Toast.LENGTH_LONG).show();
                        rootCoordinator.navigateToCreatorsActivityAfterPosting(CreatePostActivity.this);
                    }else{
                        Toast.makeText(CreatePostActivity.this, R.string.smth_went_wrong,
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    };

    public void uploadPostToServer(String userName, String postCapture,String postType,String creatorProfilePic,
                                    Uri photoUri, String postUUID) {
        showLoadingDialog();
        try {
            if(postEditingPresenter.hasInternetConnection()){
                String encodedImage = postEditingPresenter.encodeImageToString(CreatePostActivity.this ,photoUri);
                socket.emit(ConstantRegistry.CHATSTER_SAVE_CREATORS_POST,
                        userName,
                        postCapture,
                        postType,
                        creatorProfilePic,
                        encodedImage,
                        postUUID);
            }else{
                closeLoadingDialog();
                Toast.makeText(CreatePostActivity.this, R.string.no_internet_connection,
                        Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            closeLoadingDialog();
            e.printStackTrace();
        }
    }

    public void uploadTextPostToServer(String userName, String postCapture,String postType,String creatorProfilePic,
                                   String postText, String postUUID) {
        showLoadingDialog();
        if(postEditingPresenter.hasInternetConnection()){
            socket.emit(ConstantRegistry.CHATSTER_SAVE_CREATORS_TEXT_POST,
                    userName,
                    postCapture,
                    postType,
                    creatorProfilePic,
                    postText,
                    postUUID);
        }else{
            closeLoadingDialog();
            Toast.makeText(CreatePostActivity.this, R.string.no_internet_connection,
                    Toast.LENGTH_LONG).show();
        }
    }

    public void uploadVideoPostToServer(Uri videoUri, String userName, String postCapture,
                                        String postType, String creatorProfilePic, String uuid){
        Toast.makeText(CreatePostActivity.this,R.string.uploading_video_post, Toast.LENGTH_SHORT).show();
        showLoadingDialog();
        try {
            File file = createFileFromBytes(getBytesFromUri(videoUri));
            if(file != null){
                RequestBody reqFile =
                        RequestBody.create(MediaType.parse(ConstantRegistry.CHATSTER_DOCUMENT_TYPE_VIDEO),
                                file);
                MultipartBody.Part body =
                        MultipartBody.Part.createFormData(ConstantRegistry.VIDEO, file.getName(), reqFile);
                if(postEditingPresenter.hasInternetConnection()){
                    subscribeUploadVideoPost = postEditingPresenter.getUploadVideoPostResponse(body,userName,postCapture,postType,creatorProfilePic,uuid).
                            subscribeOn(Schedulers.io()).
                            observeOn(AndroidSchedulers.mainThread()).
                            subscribe(res -> {
                                        closeLoadingDialog();
                                        if(res.equals(ConstantRegistry.SUCCESS)){
                                            Toast.makeText(CreatePostActivity.this, R.string.post_successfully_uploaded,
                                                    Toast.LENGTH_LONG).show();
                                            rootCoordinator.navigateToCreatorsActivityAfterPosting(CreatePostActivity.this);
                                        }else{
                                            Toast.makeText(CreatePostActivity.this,R.string.smth_went_wrong,Toast.LENGTH_LONG).show();
                                        }
                                    },
                                    throwable -> {
                                        processErrorResult();
                                    });
                }else{
                    closeLoadingDialog();
                    Toast.makeText(CreatePostActivity.this, R.string.no_internet_connection,
                            Toast.LENGTH_LONG).show();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processErrorResult(){
        closeLoadingDialog();
        Toast.makeText(CreatePostActivity.this,R.string.smth_went_wrong,Toast.LENGTH_LONG).show();
    }

    public String getUserName(Context context){
        return postEditingPresenter.getUserName(context);
    }

    public String getUserProfilePicUrl(Context context){
        return postEditingPresenter.getUserProfilePicUrl(context);
    }

    public String getUserStatusMessage(Context context){
        return postEditingPresenter.getUserStatusMessage(context);
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

    public String generateUUID(){
        return postEditingPresenter.generateUUID();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    /**
     * get bytes array from Uri.
     *
     * @param uri uri fo the file to read.
     * @return a bytes array.
     * @throws IOException
     */
    public byte[] getBytesFromUri(Uri uri) throws IOException {
        InputStream iStream = getContentResolver().openInputStream(uri);
        try {
            return getBytes(iStream);
        } finally {
            // close the stream
            try {
                iStream.close();
            } catch (IOException ignored) { /* do nothing */ }
        }
    }



    /**
     * get bytes from input stream.
     *
     * @param inputStream inputStream.
     * @return byte array read from the inputStream.
     * @throws IOException
     */
    public byte[] getBytes(InputStream inputStream) throws IOException {
        Log.d("troubleShoot", "inputStream.available() => " + inputStream.available());
        byte[] bytesResult = null;
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = inputStream.available();
        byte[] buffer = new byte[bufferSize];
        try {
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            bytesResult = byteBuffer.toByteArray();
        } finally {
            // close the stream
            try{ byteBuffer.close(); } catch (IOException ignored){ /* do nothing */ }
        }
        return bytesResult;
    }


    private File createFileFromBytes(byte[] bytes) throws IOException {
        File f = new File(
                Environment.getExternalStorageDirectory()
                        + File.separator
                        + "temp_vid.mp4");
        //write the bytes in file
        FileOutputStream fos = new FileOutputStream(f);
        try {
            fos.write(bytes);
            fos.flush();
            fos.close();
            return f;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
