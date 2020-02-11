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
package nl.mwsoft.www.chatster.modelLayer.modelLayerManager.user;


import android.content.Context;

import java.util.ArrayList;

import io.reactivex.Observable;
import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.modelLayer.database.user.UserDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.model.ConfirmPhoneResponse;
import nl.mwsoft.www.chatster.modelLayer.network.NetworkLayer;

public class UserModelLayerManager {

    private UserDatabaseLayer userDatabaseLayer;
    private NetworkLayer networkLayer;

    public UserModelLayerManager() {
        this.userDatabaseLayer = DependencyRegistry.shared.createUserDatabaseLayer();
        this.networkLayer = DependencyRegistry.shared.createNetworkLayer();
    }

    // region User DB

    public long getUserId(Context context) {
        return this.userDatabaseLayer.getUserId(context);
    }

    public String getUserName(Context context) {
        return this.userDatabaseLayer.getUserName(context);
    }

    public String getUserStatusMessage(Context context) {
        return this.userDatabaseLayer.getUserStatusMessage(context);
    }

    public String getUserProfilePicUri(Context context) {
        return this.userDatabaseLayer.getUserProfilePicUri(context);
    }

    public String getUserProfilePicUrl(Context context) {
        return this.userDatabaseLayer.getUserProfilePicUrl(context);
    }

    public void updateUserStatusMessage(String newStatusMessage, Context context){
        this.userDatabaseLayer.updateUserStatusMessage(newStatusMessage, context);
    }

    public void updateUser(ConfirmPhoneResponse result, Context context) {
        this.userDatabaseLayer.updateUser(result, context);
    }

    public void updateUserId(long phoneToVerify, Context context){
        this.userDatabaseLayer.updateUserId(phoneToVerify, context);
    }

    public void updateUserProfilePic(String uri, Context context){
        this.userDatabaseLayer.updateUserProfilePic(uri, context);
    }

    // endregion

    // region User Network

    // region Confirm Phone Number

    public Observable<ConfirmPhoneResponse> confirmPhoneNumber(String phoneToVerify, String messagingToken, ArrayList<Long> contacts, Context context){
        return this.networkLayer.confirmPhoneNumber(phoneToVerify, messagingToken, contacts, context);
    }

    // endregion

    // region Register User

    public Observable<String> registerUser(Context context, long userId, String userName,
                                           String  userStatusMessage,String  messagingToken, ArrayList<Long> myContactIds){
        return this.networkLayer.registerUser(context, userId, userName, userStatusMessage,messagingToken, myContactIds);
    }

    // endregion

    // region Update User Token

    public Observable<String> updateUserToken(long userId, String  messagingToken){
        return this.networkLayer.updateUserToken(userId, messagingToken);
    }

    // endregion
}
