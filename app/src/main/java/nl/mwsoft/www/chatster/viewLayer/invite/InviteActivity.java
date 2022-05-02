package nl.mwsoft.www.chatster.viewLayer.invite;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.presenterLayer.invite.InvitePresenter;
import nl.mwsoft.www.chatster.viewLayer.dialog.loadingDialog.LoadingDialogFragment;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class InviteActivity extends AppCompatActivity {

    private InviteFragment inviteFragment;
    private String userName;
    private String inviteeName;
    private String inviteeEmail;
    private InvitePresenter invitePresenter;
    private CompositeDisposable disposable;
    private Disposable subscribe;
    private LoadingDialogFragment loadingDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DependencyRegistry.shared.inject(this);

        disposable = new CompositeDisposable();

        if(getIntent().getExtras() != null){
            userName = getIntent().getExtras().getString(ConstantRegistry.USER_NAME);
            inviteeName = getIntent().getExtras().getString(ConstantRegistry.INVITEE_NAME);

            inviteFragment = InviteFragment.newInstance(userName, inviteeName);
            loadFragment(inviteFragment);
        }



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(inviteFragment.userName != null && inviteFragment.inviteeName != null && inviteFragment.inviteeEmail != null){
                    if(isValidEmail(inviteFragment.inviteeEmail)){
                        inviteUserToJoinChatster(inviteFragment.userName, inviteFragment.inviteeName, inviteFragment.inviteeEmail);
                    }else{
                        Toast.makeText(InviteActivity.this,getString(R.string.invalid_email), Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(InviteActivity.this, R.string.all_values_required, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void configureWith(InvitePresenter invitePresenter){
        this.invitePresenter = invitePresenter;
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.inviteContainer, fragment);
        transaction.commit();
    }

    public boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

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

    public void inviteUserToJoinChatster(String userName, String inviteeName, String inviteeEmail){
        showLoadingDialog();
        subscribe = invitePresenter.inviteUser(userName, inviteeName, inviteeEmail)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe((String res) -> {
                    closeLoadingDialog();
                    if(!res.equals(ConstantRegistry.ERROR)){
                        Toast.makeText(InviteActivity.this, R.string.invitation_sent,Toast.LENGTH_LONG).show();
                        onBackPressed();
                    }else{
                        Toast.makeText(InviteActivity.this, R.string.smth_went_wrong,Toast.LENGTH_LONG).show();
                    }
                }, Throwable::printStackTrace);
        disposable.add(subscribe);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }
}
