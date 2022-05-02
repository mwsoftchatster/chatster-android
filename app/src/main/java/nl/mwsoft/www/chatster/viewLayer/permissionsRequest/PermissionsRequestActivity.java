package nl.mwsoft.www.chatster.viewLayer.permissionsRequest;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.fabric.sdk.android.Fabric;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.coordinator.RootCoordinator;
import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.permissionsRequestChatsterToast.PermissionsRequestChatsterToast;
import nl.mwsoft.www.chatster.viewLayer.permissionsRequest.adapter.PermissionsViewPagerAdapter;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PermissionsRequestActivity extends AppCompatActivity {

    private PermissionsViewPagerAdapter permissionsViewPagerAdapter;
    @BindView(R.id.vpPermissionsRequestActivity)
    ViewPager vpPermissionsRequestActivity;
    @BindView(R.id.llPermissionsRequestDots)
    LinearLayout llPermissionsRequestDots;
    private ImageView[] dots;
    @BindView(R.id.btnPermissionsRequestGrant)
    Button btnPermissionsRequestGrant;
    private int currentPosition = 0;
    private Unbinder unbinder;
    private RootCoordinator rootCoordinator;
    private ArrayList<Boolean> grantedPermissions;
    private PermissionsRequestChatsterToast permissionsRequestChatsterToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_permissions_request);
        DependencyRegistry.shared.inject(this);
        checkPermissions();
        unbinder = ButterKnife.bind(this);
        attachUI();
    }

    // region Configure RootCoordinator

    public void configureWith(RootCoordinator rootCoordinator, PermissionsRequestChatsterToast permissionsRequestChatsterToast){
        this.rootCoordinator = rootCoordinator;
        this.permissionsRequestChatsterToast = permissionsRequestChatsterToast;
    }

    // endregion

    // region Permissions

    private void checkPermissions(){
        configureInitialPermissionsValues();
        checkInitialPermissionsGrantedState();
    }

    private void checkInitialPermissionsGrantedState() {
        checkReadContactsPermissionState();
        checkMakeManageCallsPermissionState();
        checkAccessFilesPermissionState();
        handleAllPermissionsGranted();
    }

    private void handleAllPermissionsGranted() {
        if(getFirstNotGrantedPermission() == ConstantRegistry.ALL_PERMISSIONS_GRANTED){
            navigateToConfirmPhoneActivity();
        }
    }

    private void checkAccessFilesPermissionState() {
        if (accessFilesPermissionGranted()) {
            grantedPermissions.set(ConstantRegistry.ACCESS_FILES, true);
        }
    }

    private boolean accessFilesPermissionGranted() {
        return ContextCompat.checkSelfPermission(PermissionsRequestActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void checkMakeManageCallsPermissionState() {
        if (readPhoneStatePermissionGranted()) {
            grantedPermissions.set(ConstantRegistry.MAKE_MANAGE_CALLS, true);
        }
    }

    private boolean readPhoneStatePermissionGranted() {
        return ContextCompat.checkSelfPermission(PermissionsRequestActivity.this, Manifest.permission.READ_PHONE_STATE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void checkReadContactsPermissionState() {
        if (readContactsPermissionGranted()) {
            grantedPermissions.set(ConstantRegistry.READ_CONTACTS, true);
        }
    }

    private boolean readContactsPermissionGranted() {
        return ContextCompat.checkSelfPermission(PermissionsRequestActivity.this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void configureInitialPermissionsValues() {
        grantedPermissions = new ArrayList<>();
        grantedPermissions.add(false);
        grantedPermissions.add(false);
        grantedPermissions.add(false);
    }

    private int getFirstNotGrantedPermission(){
        if(!grantedPermissions.get(ConstantRegistry.READ_CONTACTS)){
            return ConstantRegistry.READ_CONTACTS;
        }else if(!grantedPermissions.get(ConstantRegistry.MAKE_MANAGE_CALLS)){
            return ConstantRegistry.MAKE_MANAGE_CALLS;
        }else if(!grantedPermissions.get(ConstantRegistry.ACCESS_FILES)){
            return ConstantRegistry.ACCESS_FILES;
        }else{
            return ConstantRegistry.ALL_PERMISSIONS_GRANTED;
        }
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
                vpPermissionsRequestActivity.setCurrentItem(getFirstNotGrantedPermission(),true);
            }
        });

    }

    private void askPermissionReadContacts() {
        if (readContactsPermissionNotGranted()) {
            handleReadContactsNotGranted();
        } else {
            handleReadContactsGranted();
        }
    }

    private boolean readContactsPermissionNotGranted() {
        return ContextCompat.checkSelfPermission(PermissionsRequestActivity.this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED;
    }

    private void handleReadContactsGranted() {
        updateGrantedPermissionsReadContacts();
        runPostPermissionGranted();
    }

    private void runPostPermissionGranted() {
        if(notAllPermissionsGranted()){
            navigateToFirstNotGrantedPermissionFragment();
        }else{
            navigateToConfirmPhoneActivity();
        }
    }

    private boolean notAllPermissionsGranted() {
        return getFirstNotGrantedPermission() != ConstantRegistry.ALL_PERMISSIONS_GRANTED;
    }

    private void handleReadContactsNotGranted() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(PermissionsRequestActivity.this,
                Manifest.permission.READ_CONTACTS)) {
            requestPermissionReadContacts();
        } else {
            requestPermissionReadContacts();
        }
    }

    private void requestPermissionReadContacts() {
        ActivityCompat.requestPermissions(PermissionsRequestActivity.this,
                new String[]{Manifest.permission.READ_CONTACTS},
                ConstantRegistry.MY_PERMISSIONS_REQUEST_READ_CONTACTS);
    }

    private void updateGrantedPermissionsReadContacts() {
        if(!grantedPermissions.get(ConstantRegistry.READ_CONTACTS)){
            grantedPermissions.set(ConstantRegistry.READ_CONTACTS, true);
        }
    }

    private void askPermissionReadPhoneState() {
        if (readPhoneStatePermissionNotGranted()) {
            handleReadPhoneStateNotGranted();
        } else {
            handleReadPhoneStateGranted();
        }
    }

    private boolean readPhoneStatePermissionNotGranted() {
        return ContextCompat.checkSelfPermission(PermissionsRequestActivity.this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED;
    }

    private void handleReadPhoneStateGranted() {
        updateGrantedPermissionsReadPhoneState();
        runPostPermissionGranted();
    }

    private void handleReadPhoneStateNotGranted() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(PermissionsRequestActivity.this,
                Manifest.permission.READ_PHONE_STATE)) {
            requestPermissionReadPhoneState();
        } else {
            requestPermissionReadPhoneState();
        }
    }

    private void requestPermissionReadPhoneState() {
        ActivityCompat.requestPermissions(PermissionsRequestActivity.this,
                new String[]{Manifest.permission.READ_PHONE_STATE},
                ConstantRegistry.MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
    }

    private void updateGrantedPermissionsReadPhoneState() {
        if(!grantedPermissions.get(ConstantRegistry.MAKE_MANAGE_CALLS)){
            grantedPermissions.set(ConstantRegistry.MAKE_MANAGE_CALLS, true);
        }
    }

    private void askPermissionWriteExternalStorage() {
        if (accessFilesNotGranted()) {
            handleAccessFilesNotGranted();
        } else {
            handleAccessFilesGranted();
        }
    }

    private boolean accessFilesNotGranted() {
        return ContextCompat.checkSelfPermission(PermissionsRequestActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED;
    }

    private void handleAccessFilesGranted() {
        updateGrantedPermissionsAccessFiles();
        runPostPermissionGranted();
    }

    private void handleAccessFilesNotGranted() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(PermissionsRequestActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestPermissionWriteExternalStorage();
        } else {
            requestPermissionWriteExternalStorage();
        }
    }

    private void requestPermissionWriteExternalStorage() {
        ActivityCompat.requestPermissions(PermissionsRequestActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                ConstantRegistry.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
    }

    private void updateGrantedPermissionsAccessFiles() {
        if(!grantedPermissions.get(ConstantRegistry.ACCESS_FILES)){
            grantedPermissions.set(ConstantRegistry.ACCESS_FILES, true);
        }
    }

    @OnClick(R.id.btnPermissionsRequestGrant)
    public void grantClickListener() {
        if(currentPosition == ConstantRegistry.READ_CONTACTS){
            askPermissionReadContacts();
        }else if(currentPosition == ConstantRegistry.MAKE_MANAGE_CALLS){
            askPermissionReadPhoneState();
        }else if(currentPosition == ConstantRegistry.ACCESS_FILES){
            askPermissionWriteExternalStorage();
        }else if(currentPosition == ConstantRegistry.PERMISSION_DENIED){
            processPermissionDenied();
        }
    }

    private void processPermissionDenied() {
        if(notAllPermissionsGranted()){
            navigateToFirstNotGrantedPermissionFragment();
        }else{
            navigateToConfirmPhoneActivity();
        }
    }

    private void navigateToConfirmPhoneActivity() {
        permissionsRequestChatsterToast.notifyUserAllPermissionsGranted();
        rootCoordinator.navigateToConfirmPhoneActivity(PermissionsRequestActivity.this);
        finish();
    }

    private void navigateToFirstNotGrantedPermissionFragment() {
        vpPermissionsRequestActivity.setCurrentItem(getFirstNotGrantedPermission(),true);
    }

    // endregion

    // region UI

    private void showDots(int currentPosition){
        resetDots();
        setCurrentDots(currentPosition);
    }

    private void setCurrentDots(int currentPosition) {
        dots = new ImageView[3];

        for(int i = 0; i < 3; i++){
            dots[i] = new ImageView(PermissionsRequestActivity.this);
            if(i == currentPosition){
                dots[i].setImageDrawable(ContextCompat.getDrawable(PermissionsRequestActivity.this, R.drawable.active_dots));
            }else{
                dots[i].setImageDrawable(ContextCompat.getDrawable(PermissionsRequestActivity.this,R.drawable.inactive_dots));
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4,0,4,0);

            llPermissionsRequestDots.addView(dots[i],params);
        }
    }

    private void resetDots() {
        if(llPermissionsRequestDots != null){
            llPermissionsRequestDots.removeAllViews();
        }
    }

    private void attachUI() {
        permissionsViewPagerAdapter = new PermissionsViewPagerAdapter(getSupportFragmentManager());
        vpPermissionsRequestActivity.setOffscreenPageLimit(3);
        vpPermissionsRequestActivity.setAdapter(permissionsViewPagerAdapter);

        showDots(0);

        vpPermissionsRequestActivity.addOnPageChangeListener(myOnPageChangeListener);
    }

    public ViewPager.OnPageChangeListener myOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            showDots(position);
            currentPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    // endregion

    // region Activity Life Cycle And Overrides

    @Override
    protected void onDestroy() {
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
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ConstantRegistry.MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGrantedPermissionsReadContacts();
                    runPostPermissionGranted();
                } else {
                    showPopupDeniedPermission(vpPermissionsRequestActivity);
                }

                return;
            }
            case ConstantRegistry.MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGrantedPermissionsReadPhoneState();
                    runPostPermissionGranted();
                } else {
                    showPopupDeniedPermission(vpPermissionsRequestActivity);
                }

                return;
            }
            case ConstantRegistry.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGrantedPermissionsAccessFiles();
                    runPostPermissionGranted();
                } else {
                    showPopupDeniedPermission(vpPermissionsRequestActivity);
                }

                return;
            }
        }
    }

    // endregion

}
