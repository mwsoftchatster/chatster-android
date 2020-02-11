/*
  Copyright (C) 2017 - 2020 MWSOFT

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
