package nl.mwsoft.www.chatster.viewLayer.welcome;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.fabric.sdk.android.Fabric;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.coordinator.RootCoordinator;
import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class WelcomeActivity extends AppCompatActivity {

    @BindView(R.id.tvWelcomeInfoTP2) TextView tvWelcomeInfoTP2;
    @BindView(R.id.btnWelcomeNext) Button btnWelcomeNext;
    @BindView(R.id.tvWelcomeInfoPP) TextView tvWelcomeInfoPP;
    private Unbinder unbinder;
    private RootCoordinator rootCoordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_welcome);
        unbinder = ButterKnife.bind(this);
        DependencyRegistry.shared.inject(this);
    }

    public void configureWith(RootCoordinator rootCoordinator){
        this.rootCoordinator = rootCoordinator;
    }

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

    // endregion

    // region OnClick Listeners

    @OnClick(R.id.tvWelcomeInfoTP2)
    public void termsAndPoliciesClickListener() {
        rootCoordinator.navigateToChatsterTermsAndPoliciesWebPage(WelcomeActivity.this);
    }

    @OnClick(R.id.btnWelcomeNext)
    public void nextClickListener() {
        showConfirmAgeDialog();
    }

    private void showConfirmAgeDialog() {
        ConfirmMinimumAgeDialogFragment confirmPhoneDialogFragment = ConfirmMinimumAgeDialogFragment.newInstance();
        confirmPhoneDialogFragment.setCancelable(true);
        confirmPhoneDialogFragment.show(getSupportFragmentManager(),ConstantRegistry.CHATSTER_VERIFY_AGE);
    }

    public void minimumAgeConfirmed() {
        btnWelcomeNext.setEnabled(false);
        rootCoordinator.navigateToPermissionsActivity(WelcomeActivity.this);
        finish();
        btnWelcomeNext.setEnabled(true);
    }

    @OnClick(R.id.tvWelcomeInfoPP)
    public void privacyPolicyClickListener() {
        rootCoordinator.navigateToPrivacyPolicyWebPage(WelcomeActivity.this);
    }

    // endregion

}
