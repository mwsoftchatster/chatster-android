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
package nl.mwsoft.www.chatster.presenterLayer.registerUser;


import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.contact.ContactModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.user.UserModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.util.UtilModelLayerManager;


public class RegisterUserPresenter {

    private UserModelLayerManager userModelLayerManager;
    private ContactModelLayerManager contactModelLayerManager;
    private UtilModelLayerManager utilModelLayerManager;

    public RegisterUserPresenter() {
    }

    public RegisterUserPresenter(UserModelLayerManager userModelLayerManager,
                                 ContactModelLayerManager contactModelLayerManager,
                                 UtilModelLayerManager utilModelLayerManager) {
        this.userModelLayerManager = userModelLayerManager;
        this.contactModelLayerManager = contactModelLayerManager;
        this.utilModelLayerManager = utilModelLayerManager;
    }

    public long getUserId(Context context) {
        return this.userModelLayerManager.getUserId(context);
    }

    public ArrayList<Long> getAllContactIds(Context context){
        return this.contactModelLayerManager.getAllContactIds(context);
    }

    public Observable<String> registerUser(Context context, long userId, String userName,
                                           String  userStatusMessage,String  messagingToken, ArrayList<Long> myContactIds){
        return this.userModelLayerManager.registerUser(context, userId, userName, userStatusMessage, messagingToken, myContactIds);
    }

    public boolean hasInternetConnection(){
        return utilModelLayerManager.hasInternetConnection();
    }
}
