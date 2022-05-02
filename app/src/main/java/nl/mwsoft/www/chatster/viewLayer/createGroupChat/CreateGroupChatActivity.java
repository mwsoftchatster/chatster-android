package nl.mwsoft.www.chatster.viewLayer.createGroupChat;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;


import com.crashlytics.android.Crashlytics;

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
import nl.mwsoft.www.chatster.coordinator.RootCoordinator;
import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChat;
import nl.mwsoft.www.chatster.presenterLayer.groupChat.GroupChatPresenter;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.createGroupChatsterToast.CreateGroupChatsterToast;
import nl.mwsoft.www.chatster.viewLayer.dialog.loadingDialog.LoadingDialogFragment;
import nl.mwsoft.www.chatster.modelLayer.model.Contact;
import nl.mwsoft.www.chatster.modelLayer.model.CreateGroupChatRequest;
import nl.mwsoft.www.chatster.modelLayer.helper.recyclerView.RecyclerTouchListener;
import nl.mwsoft.www.chatster.modelLayer.helper.recyclerView.RecyclerViewClickListener;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CreateGroupChatActivity extends AppCompatActivity {
    @BindView(R.id.rvCreateGroupChat)
    RecyclerView rvCreateGroupChat;
    private CreateGroupChatContactsAdapter contactsAdapter;
    private ArrayList<Contact> contacts;
    private CreateGroupChatRequest createGroupChatRequest;
    @BindView(R.id.etCreateGroupName) EditText etCreateGroupName;
    @BindView(R.id.ivDoneCreateGroup) ImageView ivDoneCreateGroup;
    @BindView(R.id.toolbar) Toolbar toolbar;
    private ArrayList<Long> groupChatMembers;
    private LoadingDialogFragment loadingDialogFragment;
    private Unbinder unbinder;
    private CompositeDisposable disposable;
    private Disposable subscribeCreateGroupChat;
    private GroupChatPresenter groupChatPresenter;
    private RootCoordinator rootCoordinator;
    private CreateGroupChatsterToast createGroupChatsterToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_create_group_chat);
        unbinder = ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        DependencyRegistry.shared.inject(this);

        disposable = new CompositeDisposable();

        createGroupChatRequest = new CreateGroupChatRequest();
        groupChatMembers = new ArrayList<>();

        attachUI();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    // region Configure

    public void configureWith(GroupChatPresenter groupChatPresenter, RootCoordinator rootCoordinator,
                              CreateGroupChatsterToast createGroupChatsterToast){
        this.groupChatPresenter = groupChatPresenter;
        this.rootCoordinator = rootCoordinator;
        this.createGroupChatsterToast = createGroupChatsterToast;
    }

    // endregion

    // region UI

    private void attachUI() {
        configureContactsAdapter();
        configureRecyclerView();
    }

    private void configureRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(CreateGroupChatActivity.this);
        rvCreateGroupChat.setLayoutManager(mLayoutManager);
        rvCreateGroupChat.setItemAnimator(new DefaultItemAnimator());
        rvCreateGroupChat.setAdapter(contactsAdapter);

        rvCreateGroupChat.addOnItemTouchListener(new RecyclerTouchListener(CreateGroupChatActivity.this,
                rvCreateGroupChat, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Contact contact = contacts.get(position);
                if(contact.isHasBeenSelected()){
                    if(!groupChatMembers.contains(contact.getUserId())){
                        groupChatMembers.add(contact.getUserId());
                    }else if(groupChatMembers.contains(contact.getUserId())){
                        groupChatMembers.remove((Long)contact.getUserId());
                    }
                }else{
                    if(!groupChatMembers.contains(contact.getUserId())){
                        groupChatMembers.add(contact.getUserId());
                    }else if(groupChatMembers.contains(contact.getUserId())){
                        groupChatMembers.remove((Long)contact.getUserId());
                    }
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void configureContactsAdapter() {
        contacts = new ArrayList<>();
        if(groupChatPresenter.getAllContacts(CreateGroupChatActivity.this) != null){
            contacts = groupChatPresenter.getAllContacts(CreateGroupChatActivity.this);
        }
        contactsAdapter = new CreateGroupChatContactsAdapter(contacts);
    }

    // endregion

    // region Activity Life Cycle And Other Overrides

    @Override
    public void onDestroy() {
        super.onDestroy();
        runOnDestroyRoutine();
    }

    private void runOnDestroyRoutine() {
        cleanUpData();
        unbindButterKnife();
    }

    private void unbindButterKnife() {
        if(unbinder != null) {
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

    // region Create Group Chat

    private void uploadGroupPublicOneTimeKeys(String oneTimePreKeyPairPbks){
        showLoadingDialog();
        if(groupChatPresenter.hasInternetConnection()){
            Disposable subscribeUploadGroupPublicOneTimeKeys = groupChatPresenter.uploadGroupPublicKeys(oneTimePreKeyPairPbks)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(res -> {
                        closeLoadingDialog();
                        navigateToMain();
                    }, Throwable::printStackTrace);
        }else{
            createGroupChatsterToast.notifyUserNoInternet();
        }
    }

    private void createGroupChat(CreateGroupChatRequest createGroupChatRequest){
        showLoadingDialog();

        if(groupChatPresenter.hasInternetConnection()){
            subscribeCreateGroupChat = groupChatPresenter.createGroupChat(createGroupChatRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::processResult, Throwable::printStackTrace);
        }else{
            createGroupChatsterToast.notifyUserNoInternet();
        }

        disposable.add(subscribeCreateGroupChat);
    }

    private void processResult(GroupChat result) {
        if(hasGroupMembers(result)){
            saveGroupChatToDB(result);
            uploadGroupPublicOneTimeKeys(
                groupChatPresenter.
                    jsonifiedGroupOneTimePublicKeys(
                        CreateGroupChatActivity.this,
                        ConstantRegistry.AMOUNT_OF_GROUP_ONE_TIME_KEY_PAIRS_AT_CREATION,
                        result.get_id(),
                        result.getGroupChatAdminId()
                    )
            );
        }else{
            closeLoadingDialog();
            createGroupChatsterToast.notifyUserSomethingWentWrong();
        }
    }

    private boolean hasGroupMembers(GroupChat result) {
        return result.getGroupChatMembers().size() > 0;
    }

    private void saveGroupChatToDB(GroupChat result) {
        groupChatPresenter.createGroupChat(result, CreateGroupChatActivity.this);
        saveGroupChatMembersToDB(result);
    }

    private void saveGroupChatMembersToDB(GroupChat result) {
        for(int i = 0; i < result.getGroupChatMembers().size(); i++){
            groupChatPresenter.createGroupChatMember(result.get_id(), result.getGroupChatMembers().get(i),
                    CreateGroupChatActivity.this);
        }
    }

    private void closeLoadingDialog() {
        if(loadingDialogFragment != null){
            loadingDialogFragment.dismiss();
        }
    }

    private void navigateToMain() {
        rootCoordinator.navigateToMainActivity(CreateGroupChatActivity.this);
        finish();
    }

    private void showLoadingDialog() {
        loadingDialogFragment = new LoadingDialogFragment();
        loadingDialogFragment.setCancelable(false);
        loadingDialogFragment.show(getSupportFragmentManager(), ConstantRegistry.LOADING);
    }

    @OnClick(R.id.ivDoneCreateGroup)
    public void doneCreateGroupListener(){
        configureCreateGroupChatRequest();
        runCreateGroupChatRoutine();
    }

    private void runCreateGroupChatRoutine() {
        if(hasInvitedGroupChatMembers() && groupNameNotEmpty()){
            runCreateGroupRequestConfiguredCorrectlyRoutine();
        }else{
            runCreateGroupRequestConfiguredIncorrectlyRoutine();
        }
    }

    private void runCreateGroupRequestConfiguredIncorrectlyRoutine() {
        if(hasNoInvitedGroupMembers()){
            createGroupChatsterToast.notifyUserGroupHasNoInvitedMembers();
        }else if(groupHasNoName()){
            createGroupChatsterToast.notifyUserGroupHasNoName();
        }
    }

    private void runCreateGroupRequestConfiguredCorrectlyRoutine() {
        createGroupChat(createGroupChatRequest);
        resetGroupNameTextView();
    }

    private boolean hasNoInvitedGroupMembers() {
        return createGroupChatRequest.getInvitedGroupChatMembers().size() <= 0;
    }

    private boolean groupHasNoName() {
        return createGroupChatRequest.getGroupChatName().equals(ConstantRegistry.CHATSTER_EMPTY_STRING);
    }

    private void resetGroupNameTextView() {
        etCreateGroupName.setText(ConstantRegistry.CHATSTER_EMPTY_STRING);
    }

    private boolean groupNameNotEmpty() {
        return !createGroupChatRequest.getGroupChatName().equals(ConstantRegistry.CHATSTER_EMPTY_STRING);
    }

    private boolean hasInvitedGroupChatMembers() {
        return createGroupChatRequest.getInvitedGroupChatMembers().size() > 0;
    }

    private void configureCreateGroupChatRequest() {
        createGroupChatRequest.setAdminId(groupChatPresenter.getUserId(CreateGroupChatActivity.this));
        createGroupChatRequest.setGroupChatId(groupChatPresenter.createUUID());
        createGroupChatRequest.setGroupChatName(etCreateGroupName.getText().toString().trim());
        createGroupChatRequest.setInvitedGroupChatMembers(groupChatMembers);
        createGroupChatRequest.setGroupChatImage(ConstantRegistry.GROUP_IMAGE);
    }

    // endregion
}

