package nl.mwsoft.www.chatster.viewLayer.addNewGroupMember;


import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
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
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.presenterLayer.addNewGroupMember.AddNewGroupMemberPresenter;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.addNewGroupMemberChatsterToast.AddNewGroupMemberChatsterToast;
import nl.mwsoft.www.chatster.viewLayer.createGroupChat.CreateGroupChatContactsAdapter;
import nl.mwsoft.www.chatster.viewLayer.dialog.loadingDialog.LoadingDialogFragment;
import nl.mwsoft.www.chatster.modelLayer.model.Contact;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChat;
import nl.mwsoft.www.chatster.modelLayer.helper.recyclerView.RecyclerTouchListener;
import nl.mwsoft.www.chatster.modelLayer.helper.recyclerView.RecyclerViewClickListener;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddNewGroupMembersActivity extends AppCompatActivity {

    @BindView(R.id.rvAddNewGroupMember)
    RecyclerView rvAddNewGroupMember;
    @BindView(R.id.ivDoneAddNewGroupMember)  ImageView ivDoneAddNewGroupMember;
    @BindView(R.id.ivAddNewMemberBack) ImageView ivAddNewMemberBack;
    @BindView(R.id.toolbar) Toolbar toolbar;
    private CreateGroupChatContactsAdapter contactsAdapter;
    private ArrayList<Contact> contacts;
    private ArrayList<Long> newMembers;
    private String chatId;
    private LoadingDialogFragment loadingDialogFragment;
    private GroupChat groupChat;
    private CompositeDisposable disposable;
    private Disposable subscribe;
    private AddNewGroupMemberPresenter addNewGroupMemberPresenter;
    private RootCoordinator rootCoordinator;
    private AddNewGroupMemberChatsterToast addNewGroupMemberChatsterToast;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_add_new_group_members);
        unbinder = ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        DependencyRegistry.shared.inject(this);

        disposable = new CompositeDisposable();

        handleOpenAddNewGroupMembers();

        attachUI();
    }

    // region Configure

    public void configureWith(AddNewGroupMemberPresenter addNewGroupMemberPresenter, RootCoordinator rootCoordinator,
                              AddNewGroupMemberChatsterToast addNewGroupMemberChatsterToast){
        this.addNewGroupMemberPresenter = addNewGroupMemberPresenter;
        this.rootCoordinator = rootCoordinator;
        this.addNewGroupMemberChatsterToast = addNewGroupMemberChatsterToast;
    }

    // endregion

    // region Handle Open Add New Members

    private void handleOpenAddNewGroupMembers() {
        chatId = getIntent().getStringExtra(ConstantRegistry.GROUP_CHAT_ID);
        initAddNewGroupMember();
    }

    private void initAddNewGroupMember() {
        if(hasGroupChatId()){
            groupChat = addNewGroupMemberPresenter.getGroupChatById(this,chatId);
        }else{
            addNewGroupMemberChatsterToast.notifyUserSomethingWentWrong();
        }
    }

    private boolean hasGroupChatId() {
        return !chatId.equals(null);
    }

    // endregion

    // region UI

    private void attachUI() {
        newMembers = new ArrayList<>();
        configureContacts();
        configureRecyclerView();
    }

    private void configureRecyclerView() {
        contactsAdapter = new CreateGroupChatContactsAdapter(contacts);
        rvAddNewGroupMember.setAdapter(contactsAdapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AddNewGroupMembersActivity.this);
        rvAddNewGroupMember.setLayoutManager(mLayoutManager);
        rvAddNewGroupMember.setItemAnimator(new DefaultItemAnimator());
        rvAddNewGroupMember.addOnItemTouchListener(new RecyclerTouchListener(AddNewGroupMembersActivity.this,
                rvAddNewGroupMember, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Contact contact = contacts.get(position);
                if(!newMembers.contains(contact.getUserId())){
                    newMembers.add(contact.getUserId());
                }else if(newMembers.contains(contact.getUserId())){
                    newMembers.remove((Long)contact.getUserId());
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void configureContacts() {
        contacts = new ArrayList<>();
        if(hasContacts()){
            contacts.addAll(addNewGroupMemberPresenter.getAllContacts(AddNewGroupMembersActivity.this));
        }
    }

    private boolean hasContacts() {
        return addNewGroupMemberPresenter.getAllContacts(AddNewGroupMembersActivity.this) != null;
    }

    // endregion

    // region Activity Life Cycle And Other Overrides

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
    public void onBackPressed() {
        super.onBackPressed();
        navigateToGroupChatInfo();
    }

    private void navigateToGroupChatInfo() {
        rootCoordinator.navigateToGroupChatInfoActivity(AddNewGroupMembersActivity.this, chatId);
        finish();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

    // endregion

    // region OnClick Listeners

    @OnClick(R.id.ivDoneAddNewGroupMember)
    public void doneAddingNewMembersListener(){
        if(addNewGroupMemberPresenter.hasInternetConnection()){
            addNewGroupMember(chatId,
                    addNewGroupMemberPresenter.getGroupChatAdminByGroupId(AddNewGroupMembersActivity.this,chatId),
                    addNewGroupMemberPresenter.getGroupChatNameById(AddNewGroupMembersActivity.this,chatId),
                    newMembers,
                    groupChat.getGroupChatImage());
        }else{
            addNewGroupMemberChatsterToast.notifyUserNoInternet();
        }
    }

    @OnClick(R.id.ivAddNewMemberBack)
    public void ivAddNewMemberBackListener(){
        navigateToGroupChatInfo();
    }

    // endregion

    // region Add New Group Member

    private void addNewGroupMember(String chatId, long groupChatAdminId, String groupChatName,
                                  ArrayList<Long> newGroupChatMembers, String groupChatPicPath){
        showLoadingDialog();
        subscribe = addNewGroupMemberPresenter.addNewGroupMember(chatId, groupChatAdminId,
                groupChatName, newGroupChatMembers, groupChatPicPath)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe((String res) -> {
                    closeLoadingDialog();
                    processResult(res);
                }, Throwable::printStackTrace);
        disposable.add(subscribe);
    }

    private void processResult(String res) {
        if(res != null){
            addNewGroupMemberChatsterToast.notifyUserResult(res);
            if(res.equals(ConstantRegistry.NEW_GROUP_MEMBERS_ADDED)){
                navigateToGroupChatInfo();
            }else{
                addNewGroupMemberChatsterToast.notifyUserSomethingWentWrong();
            }
        }else{
            addNewGroupMemberChatsterToast.notifyUserSomethingWentWrong();
        }
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

}
