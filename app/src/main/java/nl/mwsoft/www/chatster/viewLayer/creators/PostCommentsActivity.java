package nl.mwsoft.www.chatster.viewLayer.creators;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.net.URISyntaxException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
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
import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorPost;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorPostComment;
import nl.mwsoft.www.chatster.presenterLayer.postCommentsPresenter.PostCommentsPresenter;
import nl.mwsoft.www.chatster.viewLayer.creators.fragment.PostCommentsFragment;
import nl.mwsoft.www.chatster.viewLayer.dialog.loadingDialog.LoadingDialogFragment;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PostCommentsActivity extends AppCompatActivity {

    private Socket socket;
    private Disposable subscribeCreatorPostComments;
    private CompositeDisposable disposable;
    private ArrayList<CreatorPostComment> creatorPostComments;
    private PostCommentsPresenter postCommentsPresenter;
    private CreatorPost creatorPost;
    @BindView(R.id.tbComments)
    Toolbar tbComments;
    @BindView(R.id.ivCommentsBack)
    ImageView ivCommentsBack;
    private Unbinder unbinder;
    private LoadingDialogFragment loadingDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.content_post_comments);
        unbinder = ButterKnife.bind(this);
        ivCommentsBack.setOnClickListener(backListener);
        DependencyRegistry.shared.inject(this);

        initAttributes();

        setUpConnectionToServer();

        handlePostCommentsActivityOpened();
    }

    private void initAttributes() {
        disposable = new CompositeDisposable();
        creatorPostComments = new ArrayList<>();
    }

    private void handlePostCommentsActivityOpened() {
        if(getIntent().getExtras() != null){
            creatorPost = getIntent().getExtras().getParcelable(ConstantRegistry.CREATORS_POST);
            if(creatorPost != null){
                getCreatorPostComments(creatorPost.getUuid());
            }else{
                Toast.makeText(PostCommentsActivity.this, getString(R.string.smth_went_wrong),Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(PostCommentsActivity.this, getString(R.string.smth_went_wrong),Toast.LENGTH_LONG).show();
        }
    }

    public void configureWith(PostCommentsPresenter postCommentsPresenter){
        this.postCommentsPresenter = postCommentsPresenter;
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_comments_container, fragment);
        transaction.commit();
    }

    private void getCreatorPostComments(String postUUID){
        showLoadingDialog();

        subscribeCreatorPostComments = postCommentsPresenter.getCreatorPostComments(postUUID).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe(this::processResultCreatorPostComments, Throwable::printStackTrace);
    }

    private void processResultCreatorPostComments(ArrayList<CreatorPostComment> result){
        closeLoadingDialog();

        if(result != null && result.size() > 0){
            creatorPostComments.addAll(result);
        }

        Fragment fragment = PostCommentsFragment.newInstance(creatorPostComments, creatorPost.getUuid());
        loadFragment(fragment);
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
        socket.on(ConstantRegistry.CHATSTER_POST_COMMENT_FOR_CREATOR_POST, postCommentForCreatorPost);
    }

    private Emitter.Listener postCommentForCreatorPost = new Emitter.Listener(){
        @Override
        public void call(final Object... args){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String status = args[0].toString();

                    if(!statusIsSuccess(status)){
                        Toast.makeText(PostCommentsActivity.this, getString(R.string.smth_went_wrong),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    };

    public void postCommentForCreatorPost(String userName, String userProfilePicUrl, String postUUID, String comment){
        if(postCommentsPresenter.hasInternetConnection()){
            CreatorPostComment creatorPostComment = new CreatorPostComment();
            creatorPostComment.setCreatorsName(userName);
            creatorPostComment.setUserProfilePicUrl(userProfilePicUrl);
            creatorPostComment.setComment(comment);
            creatorPostComment.setPostUUID(postUUID);
            creatorPostComment.set_id("_id");
            socket.emit(ConstantRegistry.CHATSTER_POST_COMMENT_FOR_CREATOR_POST, userName, userProfilePicUrl, postUUID, comment);
        }else{
            Toast.makeText(PostCommentsActivity.this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
        }
    }

    public String getUserName(Context context){
        return postCommentsPresenter.getUserName(context);
    }

    public String getUserProfilePicUrl(Context context){
        return postCommentsPresenter.getUserProfilePicUrl(context);
    }

    private boolean statusIsSuccess(String status) {
        return status.equals(ConstantRegistry.SUCCESS);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindButterKnife();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

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
}
