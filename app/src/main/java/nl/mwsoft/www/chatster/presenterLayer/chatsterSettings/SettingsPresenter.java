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
package nl.mwsoft.www.chatster.presenterLayer.chatsterSettings;


import android.content.Context;
import android.net.Uri;

import java.io.IOException;

import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.user.UserModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.image.ImageProcessingManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.util.UtilModelLayerManager;

public class SettingsPresenter {

    private UserModelLayerManager userModelLayerManager;
    private ImageProcessingManager imageProcessingManager;
    private UtilModelLayerManager utilModelLayerManager;

    public SettingsPresenter() {
    }

    public SettingsPresenter(UserModelLayerManager userModelLayerManager){
        this.userModelLayerManager = userModelLayerManager;
    }

    public SettingsPresenter(UserModelLayerManager userModelLayerManager,
                             ImageProcessingManager imageProcessingManager,
                             UtilModelLayerManager utilModelLayerManager) {
        this.userModelLayerManager = userModelLayerManager;
        this.imageProcessingManager = imageProcessingManager;
        this.utilModelLayerManager = utilModelLayerManager;
    }

    public String encodeImageToString(Context context, Uri imageUrl) throws IOException {
        return this.imageProcessingManager.encodeImageToString(context, imageUrl);
    }

    public String getUserProfilePicUri(Context context) {
        return this.userModelLayerManager.getUserProfilePicUri(context);
    }

    public String getUserName(Context context) {
        return this.userModelLayerManager.getUserName(context);
    }

    public String getUserStatusMessage(Context context) {
        return this.userModelLayerManager.getUserStatusMessage(context);
    }

    public long getUserId(Context context) {
        return this.userModelLayerManager.getUserId(context);
    }

    public void updateUserProfilePic(String uri, Context context){
        this.userModelLayerManager.updateUserProfilePic(uri, context);
    }

    public void updateUserStatusMessage(String newStatusMessage, Context context){
        this.userModelLayerManager.updateUserStatusMessage(newStatusMessage, context);
    }

    public boolean hasInternetConnection(){
        return utilModelLayerManager.hasInternetConnection();
    }

}
