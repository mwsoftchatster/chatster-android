package nl.mwsoft.www.chatster.viewLayer.confirmPhone;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.fabric.sdk.android.Fabric;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.coordinator.RootCoordinator;
import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.modelLayer.model.ConfirmPhoneResponse;
import nl.mwsoft.www.chatster.presenterLayer.confirmPhone.ConfirmPhonePresenter;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.confirmPhoneChatsterToast.ConfirmPhoneChatsterToast;
import nl.mwsoft.www.chatster.viewLayer.dialog.confirmPhoneDialog.ConfirmPhoneDialogFragment;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.viewLayer.dialog.loadingDialog.LoadingDialogFragment;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ConfirmPhoneActivity extends AppCompatActivity implements ConfirmPhoneDialogFragment.ConfirmPhoneDialogListener {

    @BindView(R.id.spConfirmCountryCode) Spinner spConfirmCountryCode;
    @BindView(R.id.etConfirmPhoneNumber) EditText etConfirmPhoneNumber;
    @BindView(R.id.etVerifyCode) EditText etVerifyCode;
    @BindView(R.id.btnConfirmNext) Button btnConfirmNext;
    @BindView(R.id.btnConfirmVerify) Button btnConfirmVerify;
    @BindView(R.id.tvConfirmInfoPP) TextView tvConfirmInfoPP;

    private LoadingDialogFragment loadingDialogFragment;
    private ConfirmPhoneDialogFragment confirmPhoneDialogFragment;
    public long phoneNumber = 0;
    public int countryCode = 0;
    public String phoneToVerify = "";
    public static int confirmCode = 0;
    private ArrayAdapter<CharSequence> adapter;
    private CompositeDisposable disposable;
    private Disposable subscribeConfirmPhoneNumber;
    private Disposable subscribeInsertContactsFromPhone;
    private ConfirmPhonePresenter confirmPhonePresenter;
    private RootCoordinator rootCoordinator;
    private ConfirmPhoneChatsterToast confirmPhoneChatsterToast;
    private Unbinder unbinder;
    private ConfirmPhoneResponse confirmPhoneResponse;
    private FirebaseAuth mAuth;
    private String codeSent;
    private boolean phoneVerificationIsCompleted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_confirm_phone);
        DependencyRegistry.shared.inject(this);
        disposable = new CompositeDisposable();
        mAuth = FirebaseAuth.getInstance();
        showLoadingDialog();
        insertContactsFromPhone(ConfirmPhoneActivity.this);

        attachUI();

        getCountryAndPhone();
    }

    // region Configure

    public void configureWith(ConfirmPhonePresenter confirmPhonePresenter,
                              RootCoordinator rootCoordinator, ConfirmPhoneChatsterToast confirmPhoneChatsterToast){
        this.confirmPhonePresenter = confirmPhonePresenter;
        this.rootCoordinator = rootCoordinator;
        this.confirmPhoneChatsterToast = confirmPhoneChatsterToast;
    }

    // endregion

    // region AttachUI

    private void attachUI() {
        unbinder = ButterKnife.bind(this);

        // Create an ArrayAdapter using the string array and a default spinner layout
        adapter = ArrayAdapter.createFromResource(this,
                R.array.countryCodes, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    // endregion

    // region Privacy Policy And Next Button Click Listeners

    @OnClick(R.id.tvConfirmInfoPP)
    public void privacyPolicyClickListener(){
        rootCoordinator.navigateToPrivacyPolicyWebPage(ConfirmPhoneActivity.this);
    }

    @OnClick(R.id.btnConfirmNext)
    public void nextClickListener(){
        if (phoneNotNull() && phoneHasValue()) {
            String confirmPhoneNumber = etConfirmPhoneNumber.getText().toString().trim();
            confirmPhoneNumber = confirmPhonePresenter.processProvidedPhoneNumber(confirmPhoneNumber);
            phoneNumber = Long.parseLong(confirmPhoneNumber.trim());
            String selectedCountry = spConfirmCountryCode.getSelectedItem().toString();
            selectedCountry = selectedCountry.split(ConstantRegistry.CHATSTER_COMMA)[1];
            selectedCountry = selectedCountry.replace(ConstantRegistry.CHATSTER_OPEN_ROUND_BRACKETS,
                    ConstantRegistry.CHATSTER_EMPTY_STRING);
            selectedCountry = selectedCountry.replace(ConstantRegistry.CHATSTER_CLOSE_ROUND_BRACKETS,
                    ConstantRegistry.CHATSTER_EMPTY_STRING);
            countryCode = Integer.parseInt(selectedCountry);
            phoneToVerify = ConstantRegistry.CHATSTER_EMPTY_STRING.concat(String.valueOf(countryCode))
                    .concat(String.valueOf(phoneNumber));
            showConfirmPhoneDialog();
        } else {
            confirmPhoneChatsterToast.notifyUserNoPhoneNumber();
        }
    }

    private void showConfirmPhoneDialog() {
        confirmPhoneDialogFragment = ConfirmPhoneDialogFragment.newInstance(phoneToVerify);
        confirmPhoneDialogFragment.setCancelable(true);
        confirmPhoneDialogFragment.show(getSupportFragmentManager(), ConstantRegistry.CHATSTER_PHONE_TO_VERIFY);
    }

    private boolean phoneHasValue() {
        return etConfirmPhoneNumber.getText().toString().length() > 0;
    }

    private boolean phoneNotNull() {
        return !etConfirmPhoneNumber.getText().toString().equals(null);
    }

    // endregion

    // region Read Country Name And Code

    private String getCountryPhoneCode() {
        String countryID = "";
        String countryPhoneCode = "";
        // TelephonyManager determines telephony services and states and some types of subscriber information
        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        // Returns the ISO country code equivalent for the SIM provider's country code
        // NL
        countryID = manager.getSimCountryIso().toUpperCase();

        Locale loc = new Locale("", countryID);
        // 31, NL
        String[] countryCodes = this.getResources().getStringArray(R.array.countryCodes);
        for (int i = 0; i < countryCodes.length; i++) {
            // [0]=31 & [1]=NL
            String[] countryCodesItem = countryCodes[i].split(ConstantRegistry.CHATSTER_COMMA);
            // NL == NL
            if (countryCodesItem[0].trim().equals(loc.getDisplayCountry().trim())) {
                // 31
                countryPhoneCode = countryCodesItem[1].replace(ConstantRegistry.CHATSTER_OPEN_ROUND_BRACKETS,
                        ConstantRegistry.CHATSTER_EMPTY_STRING);
                countryPhoneCode = countryPhoneCode.replace(ConstantRegistry.CHATSTER_CLOSE_ROUND_BRACKETS,
                        ConstantRegistry.CHATSTER_EMPTY_STRING);
                break;
            }
        }
        return countryPhoneCode;
    }

    private String getCountry() {
        // TelephonyManager determines telephony services and states and some types of subscriber information
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        // Returns the ISO country code(nl) equivalent of the current registered operator's MCC (Mobile Country Code)
        return tm.getNetworkCountryIso();
    }

    private void getCountryAndPhone() {
        if (hasCountry()) {
            // Netherlands,(31)
            String[] countryCodes = this.getResources().getStringArray(R.array.countryCodes);
            for (int i = 0; i < countryCodes.length; i++) {
                // [0]=Netherlands & [1]=(31)
                String[] countryCodesItem = countryCodes[i].split(ConstantRegistry.CHATSTER_COMMA);
                // 31
                String countryPhoneCode = countryCodesItem[1].replace(ConstantRegistry.CHATSTER_OPEN_ROUND_BRACKETS,
                        ConstantRegistry.CHATSTER_EMPTY_STRING);
                countryPhoneCode = countryPhoneCode.replace(ConstantRegistry.CHATSTER_CLOSE_ROUND_BRACKETS,
                        ConstantRegistry.CHATSTER_EMPTY_STRING);
                // 31 == 31
                if(countryPhoneCodeNotNull() && countryPhoneCodeHasValue()){
                    if (countryPhoneCodeDetermined(countryPhoneCode)) {
                        spConfirmCountryCode.setAdapter(adapter);
                        spConfirmCountryCode.setSelection(i);
                        break;
                    }
                }else{
                    spConfirmCountryCode.setAdapter(adapter);
                    confirmPhoneChatsterToast.notifyUserCountryNotDetermined();
                    break;
                }
            }
        }
    }

    private boolean countryPhoneCodeDetermined(String countryPhoneCode) {
        return countryPhoneCode.trim().equals(getCountryPhoneCode().trim());
    }

    private boolean countryPhoneCodeHasValue() {
        return !getCountryPhoneCode().isEmpty();
    }

    private boolean countryPhoneCodeNotNull() {
        return !getCountryPhoneCode().equals(null);
    }

    private boolean hasCountry() {
        return getCountry() != null;
    }

    // endregion

    // region Activity Life Cycle And Attach Base

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        runOnDestroyRoutine();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void runOnDestroyRoutine() {
        cleanUpData();
        unbindButterKnife();
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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    // endregion

    // region Subscribers To Confirm Phone And Insert Contacts Observables

    private void confirmPhoneNumber(String phoneToVerify, String messagingToken, ArrayList<Long> contacts, Context context){
        disableConfirmButton();
        showLoadingDialog();
        hideKeyBoard();

        subscribeConfirmPhoneNumber = confirmPhonePresenter.confirmPhoneNumber(phoneToVerify, messagingToken, contacts, context)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::processResult, Throwable::printStackTrace);
        disposable.add(subscribeConfirmPhoneNumber);
    }

    private void processResult(ConfirmPhoneResponse res) {
        closeLoadingDialog();
        enableConfirmButton();

        if(resultNotError(res.getStatus())){
            confirmPhoneResponse = res;
            sendVerificationCode(phoneToVerify);
        }else{
            confirmPhoneChatsterToast.notifyUserSomethingWentWrong();
        }
    }

    private boolean resultNotError(String status) {
        return !status.equals(ConstantRegistry.ERROR);
    }

    private void enableConfirmButton() {
        btnConfirmNext.setEnabled(true);
    }

    private void disableConfirmButton() {
        btnConfirmNext.setEnabled(false);
    }

    private void hideKeyBoard() {
        // hide keyboard
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    private void insertContactsFromPhone(Context context){
        subscribeInsertContactsFromPhone = confirmPhonePresenter.insertContactsFromPhone(context)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(res -> {
                    closeLoadingDialog();
                }, Throwable::printStackTrace);
        disposable.add(subscribeInsertContactsFromPhone);
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

    // endregion

    // region Dialog Click Listeners

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String phoneToVerify) {
        dialog.dismiss();
        processOnDialogPositiveClick(phoneToVerify);
    }

    private void processOnDialogPositiveClick(String phoneToVerify) {
        if (phoneToVerifyHasValue(phoneToVerify)) {
            if(confirmPhonePresenter.hasInternetConnection()){
                confirmPhoneNumber(phoneToVerify,
                        FirebaseInstanceId.getInstance().getToken(),
                        confirmPhonePresenter.getAllContactIds(ConfirmPhoneActivity.this),
                        ConfirmPhoneActivity.this);
            }else{
                confirmPhoneChatsterToast.notifyUserNoInternet();
            }
        } else {
            confirmPhoneChatsterToast.notifyUserNoPhoneNumber();
        }
    }

    private boolean phoneToVerifyHasValue(String phoneToVerify) {
        return phoneToVerify.length() > 0;
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    // endregion

    // region Phone Confirm Event

    private void processUserAlreadyExists(long userId, String oneTimePreKeyPairPbks){
        hideKeyBoard();

        Disposable subscribeUploadKeys = confirmPhonePresenter.uploadReRegisterPublicKeys(userId, oneTimePreKeyPairPbks)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(res -> {
                    closeLoadingDialog();

                    if(res.equals(ConstantRegistry.SUCCESS)){
                        confirmPhonePresenter.updateUserId(confirmPhoneResponse.get_id(), ConfirmPhoneActivity.this);

                        confirmPhonePresenter.updateUser(confirmPhoneResponse, ConfirmPhoneActivity.this);

                        if(confirmPhoneResponse.getChatsterContacts() != null && confirmPhoneResponse.getChatsterContacts().size() > 0){
                            confirmPhonePresenter.updateContacts(confirmPhoneResponse.getChatsterContacts(), ConfirmPhoneActivity.this);
                        }

                        Toast.makeText(ConfirmPhoneActivity.this, R.string.account_already_exists, Toast.LENGTH_LONG).show();

                        navigateToMainActivity();
                    }else{
                        Toast.makeText(ConfirmPhoneActivity.this, R.string.smth_went_wrong, Toast.LENGTH_LONG).show();
                    }
                },throwable -> {
                    closeLoadingDialog();

                    Toast.makeText(ConfirmPhoneActivity.this, R.string.smth_went_wrong, Toast.LENGTH_LONG).show();

                    throwable.printStackTrace();
                });
    }

    private void navigateToRegisterUser() {
        rootCoordinator.navigateToRegisterUserActivity(ConfirmPhoneActivity.this);
        finish();
    }

    private void navigateToMainActivity() {
        rootCoordinator.navigateToMainActivity(ConfirmPhoneActivity.this);
        finish();
    }

    // endregion

    // region Firebase Phone Authentication

    private void verifySignInCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        closeLoadingDialog();
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                    R.string.verification_sucessful, Toast.LENGTH_LONG).show();

                            if(confirmPhoneResponse.isUserAlreadyExists()){
                                showLoadingDialog();
                                processUserAlreadyExists(
                                        Long.parseLong(phoneToVerify),
                                        confirmPhonePresenter.jsonifiedOneTimeKeys(
                                                ConfirmPhoneActivity.this,
                                                Long.parseLong(phoneToVerify),
                                                ConstantRegistry.AMOUNT_OF_ONE_TIME_KEY_PAIRS_AT_REGISTRATION
                                        )
                                );
                            }else{
                                confirmPhonePresenter.updateUserId(Long.parseLong(phoneToVerify),ConfirmPhoneActivity.this);
                                navigateToRegisterUser();
                            }
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(),
                                        R.string.incorrect_verification_code, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void sendVerificationCode(String phone){
        String phoneToSend = ConstantRegistry.CHATSTER_PLUS.concat(phone);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneToSend,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                ConfirmPhoneActivity.this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    public PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            e.printStackTrace();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSent = s;
            phoneVerificationIsCompleted = true;
        }
    };


    @OnClick(R.id.btnConfirmVerify)
    public void verifyCode(){
        if(phoneVerificationIsCompleted){
            showLoadingDialog();
            String code = null;
            if(etVerifyCode != null){
                code = etVerifyCode.getText().toString().trim();
                if(code.length() > 0){
                    verifySignInCode(code);
                }else{
                    closeLoadingDialog();
                    Toast.makeText(ConfirmPhoneActivity.this, R.string.verfication_code_empty, Toast.LENGTH_LONG).show();
                }
            }else{
                closeLoadingDialog();
                Toast.makeText(ConfirmPhoneActivity.this, R.string.verfication_code_empty, Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(ConfirmPhoneActivity.this, R.string.verify_phone_first, Toast.LENGTH_LONG).show();
        }
    }
    // endregion
}

