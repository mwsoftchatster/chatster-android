package nl.mwsoft.www.chatster.viewLayer.imageDetail;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.fabric.sdk.android.Fabric;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.coordinator.RootCoordinator;
import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.model.Chat;
import nl.mwsoft.www.chatster.modelLayer.model.ImageDetailRequest;
import nl.mwsoft.www.chatster.presenterLayer.chat.ChatPresenter;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.imageDetailChatsterToast.ImageDetailChatsterToast;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ImageDetailActivity extends AppCompatActivity  implements View.OnTouchListener {

    @BindView(R.id.ivImageDetailBack) ImageView ivImageDetailBack;
    @BindView(R.id.ivImageDetailImage) ImageView ivImageDetailImage;
    @BindView(R.id.tvImageDetailSenderName) TextView tvImageDetailSenderName;
    @BindView(R.id.imageDetailToolbar) Toolbar toolbar;
    private ImageDetailRequest imageDetailRequest;
    private Unbinder unbinder;
    private static final String TAG = "Touch";
    @SuppressWarnings("unused")
    private static final float MIN_ZOOM = 1f,MAX_ZOOM = 1f;
    // These matrices will be used to scale points of the image
    private Matrix matrix = new Matrix();
    private Matrix savedMatrix = new Matrix();

    // The 3 states (events) which the user is trying to perform
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;

    // these PointF objects are used to record the point(s) the user is touching
    private PointF start = new PointF();
    private PointF mid = new PointF();
    private float oldDist = 1f;

    private ChatPresenter chatPresenter;
    private RootCoordinator rootCoordinator;
    private ImageDetailChatsterToast imageDetailChatsterToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_image_detail);
        unbinder = ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        DependencyRegistry.shared.inject(this);

        setStatusBarColor();

        handleOpenImageDetail();
    }

    // region Configure

    public void configureWith(ChatPresenter chatPresenter, RootCoordinator rootCoordinator,
                              ImageDetailChatsterToast imageDetailChatsterToast){
        this.chatPresenter = chatPresenter;
        this.rootCoordinator = rootCoordinator;
        this.imageDetailChatsterToast = imageDetailChatsterToast;
    }

    // endregion

    // region Status Bar

    private void setStatusBarColor() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorBlack));
    }

    // endregion

    // region Activity Life Cycle And Other Overrides

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindButterKnife();
    }

    private void unbindButterKnife() {
        if(unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        runOnBackPressedRoutine();
    }

    private void runOnBackPressedRoutine() {
        if(!imageDetailRequest.isGroupChat()){
            navigateToChat();
        }else{
            navigateToGroupChat();
        }
    }

    // endregion

    // region Navigation

    @OnClick(R.id.ivImageDetailBack)
    public void ivImageDetailBackListener(){
        runOnBackPressedRoutine();
    }

    private void navigateToGroupChat() {
        rootCoordinator.navigateToGroupChatActivity(ImageDetailActivity.this, imageDetailRequest.getGroupChatId());
        finish();
    }

    private void navigateToChat() {
        Chat chat = chatPresenter.getChatById(ImageDetailActivity.this, imageDetailRequest.getChatId());
        rootCoordinator.navigateToChatActivity(ImageDetailActivity.this, chat);
        finish();
    }

    // endregion

    // region Handle Open Image Detail

    private void handleOpenImageDetail() {
        if(getIntent().getAction().equals(ConstantRegistry.IMAGE_DETAIL)){
            if(getIntent().getExtras().getParcelable(ConstantRegistry.IMAGE_DETAIL_REQUEST) != null){
                imageDetailRequest = getIntent().getExtras().getParcelable(ConstantRegistry.IMAGE_DETAIL_REQUEST);
                if(imageDetailRequest != null) {
                    tvImageDetailSenderName.setText(getString(R.string.image_detail_sender_name, imageDetailRequest.getSenderName()));
                        Picasso.with(this).load(imageDetailRequest.getImageUri())
                                .into(ivImageDetailImage);
                    ivImageDetailImage.setOnTouchListener(this);
                }else{
                    imageDetailChatsterToast.notifyUserSomethingWentWrong();
                }
            }else{
                imageDetailChatsterToast.notifyUserSomethingWentWrong();
            }
        }else{
            imageDetailChatsterToast.notifyUserSomethingWentWrong();
        }
    }

    // endregion

    // region Image Processing

    public static boolean smallerThanIdentity(Matrix m) {
        float[] values = new float[9];
        m.getValues(values);

        return ((values[0] < 1.0) || (values[4] < 1.0) || (values[8] < 1.0));
    }

    public void resetView(View v) {
        ImageView view = (ImageView)v;
        matrix = new Matrix();

        view.setScaleType(ImageView.ScaleType.MATRIX);
        view.setImageMatrix(matrix);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;

        // Handle touch events here...

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:   // first finger down only
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;

            case MotionEvent.ACTION_UP: // first finger lifted

            case MotionEvent.ACTION_POINTER_UP: // second finger lifted
                mode = NONE;
                break;

            case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down
                oldDist = spacing(event);
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    // create the transformation in the matrix  of points
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
                } else if (mode == ZOOM) {
                    // pinch zooming
                    float newDist = spacing(event);
                    if (newDist > 5f) {
                        matrix.set(savedMatrix);
                        // setting the scaling of the
                        scale = newDist / oldDist;
                        // if scale > 1 means zoom in
                        // if scale < 1 means zoom out
                        //check that zoom is not too small
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }
//        if (!smallerThanIdentity(matrix)) {
//            view.setImageMatrix(matrix);
//        }
        // display the transformation on screen
        view.setImageMatrix(matrix);

        return true; // indicate event was handled
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    // endregion

}
