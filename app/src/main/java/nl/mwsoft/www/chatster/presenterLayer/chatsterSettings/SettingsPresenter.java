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
