package nl.mwsoft.www.chatster.modelLayer.service.contactLatest;


import android.Manifest;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;

import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;
import java.util.HashMap;

import io.fabric.sdk.android.Fabric;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.modelLayer.database.contact.ContactDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.database.notification.NotificationDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.helper.util.contact.ChatsterContactsUtil;
import nl.mwsoft.www.chatster.modelLayer.helper.util.jobService.ChatsterJobServiceUtil;
import nl.mwsoft.www.chatster.modelLayer.helper.util.internet.InternetConnectionUtil;
import nl.mwsoft.www.chatster.modelLayer.model.Contact;
import nl.mwsoft.www.chatster.modelLayer.network.NetworkLayer;

public class ContactLatestJobService extends JobService {

    private CompositeDisposable disposable;
    private Disposable subscribeGetContactLatest;
    private NetworkLayer networkLayer;
    private ContactDatabaseLayer contactDatabaseLayer;
    private NotificationDatabaseLayer notificationDatabaseLayer;
    private JobParameters mParams;
    private ChatsterJobServiceUtil chatsterJobServiceUtil;
    private ArrayList<Long> myContacts;
    private ArrayList<Long> myExistingContacts;
    private HashMap<Long,Contact> phoneContacts;
    private InternetConnectionUtil internetConnectionUtil;

    public ContactLatestJobService() {
    }

    public void configureWith(NetworkLayer networkLayer, ContactDatabaseLayer contactDatabaseLayer,
                              NotificationDatabaseLayer notificationDatabaseLayer, ChatsterJobServiceUtil chatsterJobServiceUtil){
        this.networkLayer = networkLayer;
        this.contactDatabaseLayer = contactDatabaseLayer;
        this.notificationDatabaseLayer = notificationDatabaseLayer;
        this.chatsterJobServiceUtil = chatsterJobServiceUtil;
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        disposable = new CompositeDisposable();
        mParams = params;

        Fabric.with(this, new Crashlytics());
        DependencyRegistry.shared.inject(this);

        internetConnectionUtil = new InternetConnectionUtil();
        if(internetConnectionUtil.hasInternetConnection()){
            getContactLatest(contactDatabaseLayer.getAllContactIds(ContactLatestJobService.this));
        }

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }

        return true;
    }

    private void getContactLatest(ArrayList<Long> allContactIds){
        subscribeGetContactLatest =
                networkLayer.getContactLatest(allContactIds)
                        .subscribeOn(Schedulers.io())
                        .subscribe(result -> {
                            if (result.size() > 0) {
                                notificationDatabaseLayer.updateContacts(result, ContactLatestJobService.this);
                            }

                            if(readContactsPermissionIsGranted()){
                                checkForNewContacts();
                            }

                            jobFinished(mParams, false);
                            chatsterJobServiceUtil.startContactLatestJobService(ContactLatestJobService.this);
                        },throwable -> {
                            if(readContactsPermissionIsGranted()){
                                checkForNewContacts();
                            }

                            jobFinished(mParams, false);
                            chatsterJobServiceUtil.startContactLatestJobService(ContactLatestJobService.this);

                            throwable.printStackTrace();
                        });
        disposable.add(subscribeGetContactLatest);
    }

    private boolean readContactsPermissionIsGranted() {
        if (ContextCompat.checkSelfPermission(ContactLatestJobService.this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }else{
            return false;
        }
    }

    private void checkForNewContacts() {
        myContacts = new ArrayList<>();
        myExistingContacts = new ArrayList<>();
        phoneContacts = new HashMap<>();

        if(ChatsterContactsUtil.getAllContactsWithPhoneNumber(ContactLatestJobService.this) != null &&
                contactDatabaseLayer.getAllContacts(ContactLatestJobService.this) != null){
            for(Contact phoneContact : ChatsterContactsUtil.getAllContactsWithPhoneNumber(ContactLatestJobService.this)){
                myContacts.add(phoneContact.getUserId());
                phoneContacts.put(phoneContact.getUserId(), phoneContact);
            }

            for(Contact chatsterContact : contactDatabaseLayer.getAllContacts(ContactLatestJobService.this)){
                myExistingContacts.add(chatsterContact.getUserId());
            }

            for(Long userId : myContacts){
                if(!myExistingContacts.contains(userId)){
                    Contact newContact = phoneContacts.get(userId);
                    contactDatabaseLayer.insertContact(newContact.getUserId(),newContact.getUserName(),
                            newContact.getStatusMessage(), ContactLatestJobService.this);
                }
            }
        }
    }
}
