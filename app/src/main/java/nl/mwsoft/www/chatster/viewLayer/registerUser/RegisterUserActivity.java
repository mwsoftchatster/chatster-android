package nl.mwsoft.www.chatster.viewLayer.registerUser;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;

import java.net.URISyntaxException;
import java.util.ArrayList;

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
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.registerUserChatsterToast.RegisterUserChatsterToast;
import nl.mwsoft.www.chatster.viewLayer.dialog.loadingDialog.LoadingDialogFragment;
import nl.mwsoft.www.chatster.presenterLayer.registerUser.RegisterUserPresenter;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RegisterUserActivity extends AppCompatActivity {

    @BindView(R.id.etRegisterUserName)
    EditText etRegisterUserName;
    @BindView(R.id.etRegisterUserStatus)
    EditText etRegisterUserStatus;
    @BindView(R.id.btnRegisterUser)
    Button btnRegisterUser;
    @BindView(R.id.tvRegisterUserNameStatus)
    TextView tvRegisterUserNameStatus;
    private ArrayList<Long> myContactIds;
    private LoadingDialogFragment loadingDialogFragment;
    private CompositeDisposable disposable;
    private Disposable subscribeRegisterUser;
    private RegisterUserPresenter registerUserPresenter;
    private RootCoordinator rootCoordinator;
    private RegisterUserChatsterToast registerUserChatsterToast;
    private Unbinder unbinder;
    private Socket socket;
    private boolean userNameIsAvailable = false;
    private boolean userNameIsOfCorrectLength = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_register_user);
        unbinder = ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        disposable = new CompositeDisposable();

        DependencyRegistry.shared.inject(this);

        showLoadingDialog();

        getUserContacts();

        setUpConnectionToServer();

        closeLoadingDialog();

        etRegisterUserName.addTextChangedListener(userNameWatcher);
    }

    private TextWatcher userNameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            // Hide user name status view if user name is empty.
            if (s.toString().trim().length() == 0) {
                tvRegisterUserNameStatus.setText("");
                tvRegisterUserNameStatus.setVisibility(View.GONE);

                return;
            }

            if (tvRegisterUserNameStatus.getVisibility() == View.GONE) {
                tvRegisterUserNameStatus.setVisibility(View.VISIBLE);
            }

            if (s.toString().trim().length() < 3) {
                userNameIsOfCorrectLength = false;
                tvRegisterUserNameStatus.setText(R.string.min_3_char);
                tvRegisterUserNameStatus.setTextColor(getResources().getColor(R.color.colorUserNameUnAvailable));

                return;
            }

            if (s.toString().trim().length() <= 32) {
                userNameIsOfCorrectLength = true;

                if (containsCloseRoundBraces(s.toString()) || containsComma(s.toString()) ||
                        containsForwardSlash(s.toString()) || containsHashTag(s.toString()) ||
                        containsMinus(s.toString()) || containsOpenRoundBraces(s.toString()) ||
                        containsSemiColon(s.toString()) || containsSpace(s.toString()) ||
                        containsStar(s.toString()) || containsQuestionMark(s.toString())) {
                    userNameIsOfCorrectLength = false;
                    tvRegisterUserNameStatus.setText(R.string.no_spec_char);
                    tvRegisterUserNameStatus.setTextColor(getResources().getColor(R.color.colorUserNameUnAvailable));

                    return;
                }

                checkUserNameAvailability(s.toString().trim());

                return;
            }

            userNameIsOfCorrectLength = false;
            tvRegisterUserNameStatus.setText(R.string.max_32_char);
            tvRegisterUserNameStatus.setTextColor(getResources().getColor(R.color.colorUserNameUnAvailable));
        }
    };

    // region Socket.IO

    private void setUpConnectionToServer() {
        // default settings for all sockets
        IO.setDefaultOkHttpWebSocketFactory(OkHttpClientManager.setUpSecureClient());
        IO.setDefaultOkHttpCallFactory(OkHttpClientManager.setUpSecureClient());

        // set as an option
        IO.Options opts = new IO.Options();
        opts.transports = new String[]{WebSocket.NAME};
        opts.callFactory = OkHttpClientManager.setUpSecureClient();
        opts.webSocketFactory = OkHttpClientManager.setUpSecureClient();

        try {
            socket = IO.socket(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_API_USER_Q_PORT), opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        socket.connect();
        socket.on(ConstantRegistry.CHATSTER_USERNAME_AVAILABILITY, checkUserNameAvailability);
    }

    private Emitter.Listener checkUserNameAvailability = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String status = args[0].toString();
                    processUserNameAvailabilityStatus(status);
                }
            });
        }
    };

    private void processUserNameAvailabilityStatus(String status) {
        switch (status) {
            case ConstantRegistry.ERROR:
                registerUserChatsterToast.notifyUserSomethingWentWrong();
                break;
            case ConstantRegistry.UNAVAILABLE:
                userNameIsAvailable = false;
                tvRegisterUserNameStatus.setText(R.string.not_available);
                tvRegisterUserNameStatus.setTextColor(getResources().getColor(R.color.colorUserNameUnAvailable));
                etRegisterUserName.setTextColor(getResources().getColor(R.color.colorUserNameUnAvailable));
                break;
            case ConstantRegistry.AVAILABLE:
                userNameIsAvailable = true;
                tvRegisterUserNameStatus.setText(R.string.available);
                tvRegisterUserNameStatus.setTextColor(getResources().getColor(R.color.colorWhite));
                etRegisterUserName.setTextColor(getResources().getColor(R.color.colorWhite));
                break;
        }
    }

    private void checkUserNameAvailability(String userName) {
        socket.emit(ConstantRegistry.CHATSTER_USERNAME_AVAILABILITY, userName);
    }

    // endregion

    // region Get User's Contacts

    private void getUserContacts() {
        myContactIds = new ArrayList<>();
        myContactIds.addAll(registerUserPresenter.getAllContactIds(this));
    }

    // endregion

    // region Configure

    public void configureWith(
            RegisterUserPresenter registerUserPresenter,
            RootCoordinator rootCoordinator,
            RegisterUserChatsterToast registerUserChatsterToast
    ) {
        this.registerUserPresenter = registerUserPresenter;
        this.rootCoordinator = rootCoordinator;
        this.registerUserChatsterToast = registerUserChatsterToast;
    }

    // endregion

    // region Activity Life Cycle And Other Overrides

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        runOnDestroyRoutine();
    }

    private void runOnDestroyRoutine() {
        cleanUpData();
        unbindButterKnife();
    }

    private void unbindButterKnife() {
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    private void cleanUpData() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    // endregion

    // region Register User

    private void registerUser(Context context, long userId, String userName,
                              String userStatusMessage, String messagingToken, ArrayList<Long> myContactIds,
                              String oneTimePreKeyPairPbks) {
        disableRegisterButton();
        showLoadingDialog();
        hideKeyBoard();

        subscribeRegisterUser = registerUserPresenter.registerUser(
                context,
                userId,
                userName,
                userStatusMessage,
                messagingToken,
                myContactIds,
                oneTimePreKeyPairPbks
        ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(res -> {
                    enableRegisterButton();
                    closeLoadingDialog();
                    processResult(res);
                }, throwable -> {
                    enableRegisterButton();
                    closeLoadingDialog();
                    processResult(getString(R.string.smth_went_wrong));
                    throwable.printStackTrace();
                });

        disposable.add(subscribeRegisterUser);
    }

    private void navigateToMain() {
        rootCoordinator.navigateToMainActivity(RegisterUserActivity.this);
        finish();
    }

    private void resetTextViews() {
        etRegisterUserStatus.setText(ConstantRegistry.CHATSTER_EMPTY_STRING);
        etRegisterUserName.setText(ConstantRegistry.CHATSTER_EMPTY_STRING);
    }

    private void enableRegisterButton() {
        btnRegisterUser.setEnabled(true);
    }

    private void processResult(String res) {
        if (res.equals(getString(R.string.username_already_exists)) || res.equals(getString(R.string.smth_went_wrong))) {
            registerUserChatsterToast.notifyUserResult(res);
            resetTextViews();

            return;
        }

        registerUserChatsterToast.notifyUserResult(res);
        resetTextViews();
        navigateToMain();
    }

    private void closeLoadingDialog() {
        if (loadingDialogFragment != null) {
            loadingDialogFragment.dismiss();
        }
    }

    private void hideKeyBoard() {
        // Hide keyboard.
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(
                getCurrentFocus().getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN
        );
    }

    private void disableRegisterButton() {
        btnRegisterUser.setEnabled(false);
    }

    private void showLoadingDialog() {
        loadingDialogFragment = new LoadingDialogFragment();
        loadingDialogFragment.setCancelable(false);
        loadingDialogFragment.show(getSupportFragmentManager(), ConstantRegistry.LOADING);
    }

    @OnClick(R.id.btnRegisterUser)
    public void registerListener() {
        String userName = etRegisterUserName.getText().toString().trim();
        String userStatusMessage = etRegisterUserStatus.getText().toString().trim();
        if (userNameIsOfCorrectLength) {
            if (userNameIsAvailable) {
                if (!userName.isEmpty()) {
                    if (registerUserPresenter.hasInternetConnection()) {
                        registerUser(RegisterUserActivity.this,
                                registerUserPresenter.getUserId(RegisterUserActivity.this),
                                userName,
                                userStatusMessage,
                                FirebaseInstanceId.getInstance().getToken(),
                                myContactIds,
                                registerUserPresenter.jsonifiedOneTimeKeys(
                                        RegisterUserActivity.this,
                                        registerUserPresenter.getUserId(RegisterUserActivity.this),
                                        ConstantRegistry.AMOUNT_OF_ONE_TIME_KEY_PAIRS_AT_REGISTRATION));
                    } else {
                        registerUserChatsterToast.notifyUserNoInternet();
                    }
                } else {
                    registerUserChatsterToast.notifyUserNameCantBeEmpty();
                }
            } else {
                Toast.makeText(RegisterUserActivity.this, R.string.username_must_be_unique, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(RegisterUserActivity.this, R.string.username_min_3_char, Toast.LENGTH_LONG).show();
        }
    }

    // endregion

    // region String Username Processing

    private boolean containsSpace(String name) {
        return name.contains(ConstantRegistry.CHATSTER_SPACE_STRING);
    }

    private boolean containsMinus(String name) {
        return name.contains(ConstantRegistry.CHATSTER_MINUS);
    }

    private boolean containsHashTag(String name) {
        return name.contains(ConstantRegistry.CHATSTER_HASH_TAG);
    }

    private boolean containsSemiColon(String name) {
        return name.contains(ConstantRegistry.CHATSTER_SEMICOLON);
    }

    private boolean containsStar(String name) {
        return name.contains(ConstantRegistry.CHATSTER_STAR);
    }

    private boolean containsComma(String name) {
        return name.contains(ConstantRegistry.CHATSTER_COMMA);
    }

    private boolean containsForwardSlash(String name) {
        return name.contains(ConstantRegistry.CHATSTER_FORWARD_SLASH);
    }

    private boolean containsCloseRoundBraces(String name) {
        return name.contains(ConstantRegistry.CHATSTER_CLOSE_ROUND_BRACKETS);
    }

    private boolean containsOpenRoundBraces(String name) {
        return name.contains(ConstantRegistry.CHATSTER_OPEN_ROUND_BRACKETS);
    }

    private boolean containsQuestionMark(String name) {
        return name.contains(ConstantRegistry.CHATSTER_QUESTION_MARK);
    }

    // endregion
}
