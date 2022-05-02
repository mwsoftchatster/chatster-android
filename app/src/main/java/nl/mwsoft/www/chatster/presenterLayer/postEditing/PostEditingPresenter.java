package nl.mwsoft.www.chatster.presenterLayer.postEditing;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.Observable;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorContact;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.creators.CreatorsModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.image.ImageProcessingManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.user.UserModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.util.UtilModelLayerManager;
import okhttp3.MultipartBody;

public class PostEditingPresenter {

    private ImageProcessingManager imageProcessingManager;
    private CreatorsModelLayerManager creatorsModelLayerManager;
    private UserModelLayerManager userModelLayerManager;
    private UtilModelLayerManager utilModelLayerManager;

    public PostEditingPresenter() {
    }

    public PostEditingPresenter(ImageProcessingManager imageProcessingManager,
                                CreatorsModelLayerManager creatorsModelLayerManager,
                                UserModelLayerManager userModelLayerManager,
                                UtilModelLayerManager utilModelLayerManager) {
        this.imageProcessingManager = imageProcessingManager;
        this.creatorsModelLayerManager = creatorsModelLayerManager;
        this.userModelLayerManager = userModelLayerManager;
        this.utilModelLayerManager = utilModelLayerManager;
    }



    // region Image Processing

    public File createImageFile(Context context) throws IOException {
        return this.imageProcessingManager.createImageFile(context);
    }
    public Bitmap decodeSampledBitmap(Context context, Uri imageUrl) throws IOException {
        return this.imageProcessingManager.decodeSampledBitmap(context, imageUrl);
    }

    public Uri saveIncomingImageMessage(Context context, Bitmap inImage) {
        return this.imageProcessingManager.saveIncomingImageMessage(context, inImage);
    }
    public String encodeImageToString(Context context, Uri imageUrl) throws IOException {
        return this.imageProcessingManager.encodeImageToString(context, imageUrl);
    }

    //endregion

    // region Utils

    public String generateUUID(){
        return this.utilModelLayerManager.createUUID();
    }

    public boolean hasInternetConnection(){
        return this.utilModelLayerManager.hasInternetConnection();
    }

    // endregion

    // region DB

    public String getCreatorsContactProfilePicUriById(Context context, String creatorContactId) {
        return this.creatorsModelLayerManager.getCreatorsContactProfilePicUriById(context, creatorContactId);
    }

    public CreatorContact getCreatorContactByName(Context context, String creatorContactId) {
        return this.creatorsModelLayerManager.getCreatorContactById(context, creatorContactId);
    }

    public ArrayList<CreatorContact> getAllCreatorContacts(Context context) {
        return this.creatorsModelLayerManager.getAllCreatorContacts(context);
    }

    public ArrayList<String> getAllCreatorContactIds(Context context) {
        return this.creatorsModelLayerManager.getAllCreatorContactIds(context);
    }

    public void disconnectCreatorContactById(String contactId, Context context){
        this.creatorsModelLayerManager.disconnectCreatorContactById(contactId, context);
    }

    public void insertCreatorContact(String creatorContactId, String creatorContactProfilePic,
                                     String creatorContactStatusMessage, Context context) {
        this.creatorsModelLayerManager.insertCreatorContact(creatorContactId, creatorContactProfilePic,
                creatorContactStatusMessage, context);
    }

    public String insertCreatorContacts(Context context){
        return this.creatorsModelLayerManager.insertCreatorContacts(context);
    }

    public String getUserName(Context context){
        return this.userModelLayerManager.getUserName(context);
    }

    public String getUserProfilePicUrl(Context context){
        return this.userModelLayerManager.getUserProfilePicUrl(context);
    }

    public String getUserStatusMessage(Context context){
        return this.userModelLayerManager.getUserStatusMessage(context);
    }

    // endregion

    // region Upload Video Post

    public Observable<String> getUploadVideoPostResponse(MultipartBody.Part body, String userName, String postCapture,
                                                         String postType, String creatorProfilePic, String uuid){
        return this.creatorsModelLayerManager.getUploadVideoPostResponse(body,userName,
                postCapture,postType,creatorProfilePic,uuid);
    }

    // endregion
}
