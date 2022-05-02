package nl.mwsoft.www.chatster.viewLayer.editUserStatus;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;

import com.crashlytics.android.Crashlytics;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.fabric.sdk.android.Fabric;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.coordinator.RootCoordinator;
import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.presenterLayer.chatsterSettings.SettingsPresenter;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EditUserStatusActivity extends AppCompatActivity {

    @BindView(R.id.etEditUserStatusMessage) EditText etEditUserStatusMessage;
    @BindView(R.id.btnEditUserStatusCancel) Button btnEditUserStatusCancel;
    @BindView(R.id.btnEditUserStatusSave) Button btnEditUserStatusSave;
    @BindView(R.id.toolbar) Toolbar toolbar;
    private Unbinder unbinder;
    private SettingsPresenter settingsPresenter;
    private RootCoordinator rootCoordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_edit_user_status);
        unbinder = ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        DependencyRegistry.shared.inject(this);

        handleOpenEditUserStatus();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // region Configure

    public void configureWith(SettingsPresenter settingsPresenter, RootCoordinator rootCoordinator){
        this.settingsPresenter = settingsPresenter;
        this.rootCoordinator = rootCoordinator;
    }

    // endregion

    // region Handle Open Edit User Status

    private void handleOpenEditUserStatus() {
        if(hasStatusMessage()){
            etEditUserStatusMessage.setText(settingsPresenter.getUserStatusMessage(EditUserStatusActivity.this));
        }
    }

    private boolean hasStatusMessage() {
        return settingsPresenter.getUserStatusMessage(EditUserStatusActivity.this) != null;
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
        String newStatusMessage = etEditUserStatusMessage.getText().toString().trim();
        if(statusHasChanged(newStatusMessage)){
            settingsPresenter.updateUserStatusMessage(newStatusMessage, EditUserStatusActivity.this);
        }
        finish();
    }

    private boolean statusHasChanged(String newStatusMessage) {
        return !settingsPresenter.getUserStatusMessage(EditUserStatusActivity.this).equals(newStatusMessage);
    }

    // endregion

    // region OnClick Listeners

    @OnClick(R.id.btnEditUserStatusCancel)
    public void cancelListener() {
        navigateToSettings();
    }

    @OnClick(R.id.btnEditUserStatusSave)
    public void saveListener(){
        String newStatusMessage = etEditUserStatusMessage.getText().toString().trim();
        if(statusHasChanged(newStatusMessage)){
            settingsPresenter.updateUserStatusMessage(newStatusMessage, EditUserStatusActivity.this);
        }
        navigateToSettings();
    }

    private void navigateToSettings() {
        rootCoordinator.navigateToChatsterSettingsActivity(EditUserStatusActivity.this);
        finish();
    }

    // endregion

}
