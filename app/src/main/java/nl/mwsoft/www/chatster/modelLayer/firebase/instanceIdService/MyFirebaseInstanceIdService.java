package nl.mwsoft.www.chatster.modelLayer.firebase.instanceIdService;



import com.google.firebase.messaging.FirebaseMessagingService;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import nl.mwsoft.www.chatster.modelLayer.database.user.UserDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.network.NetworkLayer;


public class MyFirebaseInstanceIdService extends FirebaseMessagingService {


    @Override
    public void onNewToken(String s) {
        sendRegistrationToServer(s);
    }

    private void sendRegistrationToServer(String token){
        UserDatabaseLayer userDatabaseLayer = new UserDatabaseLayer();
        Disposable subscribeUpdateUserToken;
        NetworkLayer networkLayer = new NetworkLayer();
        if(userDatabaseLayer.getUserId(MyFirebaseInstanceIdService.this) != 0){
            subscribeUpdateUserToken = networkLayer.
                    updateUserToken(userDatabaseLayer.getUserId(MyFirebaseInstanceIdService.this), token).
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(res -> {

                    },throwable -> {
                        // Send error to Firebase
                    });
        }
    }
}
