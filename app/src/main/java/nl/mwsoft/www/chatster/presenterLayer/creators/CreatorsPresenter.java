package nl.mwsoft.www.chatster.presenterLayer.creators;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.Observable;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorContact;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorPost;
import nl.mwsoft.www.chatster.modelLayer.model.HistoryItem;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.creators.CreatorsModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.image.ImageProcessingManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.user.UserModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.util.UtilModelLayerManager;
import okhttp3.MultipartBody;


public class CreatorsPresenter {

    private ImageProcessingManager imageProcessingManager;
    private CreatorsModelLayerManager creatorsModelLayerManager;
    private UserModelLayerManager userModelLayerManager;
    private UtilModelLayerManager utilModelLayerManager;


    // region Constructor

    public CreatorsPresenter() {
    }

    public CreatorsPresenter(ImageProcessingManager imageProcessingManager,
                             CreatorsModelLayerManager creatorsModelLayerManager,
                             UserModelLayerManager userModelLayerManager,
                             UtilModelLayerManager utilModelLayerManager) {
        this.imageProcessingManager = imageProcessingManager;
        this.creatorsModelLayerManager = creatorsModelLayerManager;
        this.userModelLayerManager = userModelLayerManager;
        this.utilModelLayerManager = utilModelLayerManager;
    }

    // endregion

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

    //endregion

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

    public long getUserId(Context context){
        return this.userModelLayerManager.getUserId(context);
    }

    public String getUserProfilePicUrl(Context context){
        return this.userModelLayerManager.getUserProfilePicUrl(context);
    }

    public String getUserStatusMessage(Context context){
        return this.userModelLayerManager.getUserStatusMessage(context);
    }


    public boolean getCreatorsPostExists(Context context, String postUUID) {
        return this.creatorsModelLayerManager.getCreatorsPostExists(context, postUUID);
    }

    public int getCreatorsPostIsLiked(Context context, String postUUID) {
        return this.creatorsModelLayerManager.getCreatorsPostIsLiked(context, postUUID);
    }

    public void insertCreatorPostIsLiked(String uuid, Context context) {
        this.creatorsModelLayerManager.insertCreatorPostIsLiked(uuid, context);
    }

    public void updateCreatorPostIsLiked(String uuid, int status, Context context){
        this.creatorsModelLayerManager.updateCreatorPostIsLiked(uuid, status, context);
    }

    // endregion

    // region Network

    public Observable<ArrayList<CreatorPost>> getLatestCreatorPosts(long creator, String creatorsName){
        return this.creatorsModelLayerManager.getLatestCreatorPosts(creator, creatorsName);
    }

    public Observable<CreatorContact> getCreatorContactProfile(String creator, long userId){
        return this.creatorsModelLayerManager.getCreatorContactProfile(creator, userId);
    }

    public Observable<ArrayList<CreatorPost>> discoverPosts(long userId){
        return this.creatorsModelLayerManager.discoverPosts(userId);
    }

    // region Creator Followers

    public Observable<ArrayList<CreatorContact>> getCreatorFollowers(String creatorName, long userId){
        return this.creatorsModelLayerManager.getCreatorFollowers(creatorName, userId);
    }

    // endregion

    // region Creator Followers

    public Observable<ArrayList<CreatorContact>> getCreatorFollowing(String creatorName, long userId){
        return this.creatorsModelLayerManager.getCreatorFollowing(creatorName, userId);
    }

    // endregion

    // region Creator History

    public Observable<ArrayList<HistoryItem>> getCreatorHistory(String creatorName, long userId){
        return this.creatorsModelLayerManager.getCreatorHistory(creatorName, userId);
    }

    // endregion

    // region Load More Posts

    public Observable<ArrayList<CreatorPost>> loadMorePosts(String creatorName, String lastPostCreated){
        return this.creatorsModelLayerManager.loadMorePosts(creatorName, lastPostCreated);
    }

    // endregion

    // region Upload Video Post

    public Observable<String> getUploadVideoPostResponse(MultipartBody.Part body, String userName, String postCapture,
                                                         String postType, String creatorProfilePic, String uuid){
        return this.creatorsModelLayerManager.getUploadVideoPostResponse(body,userName,
                postCapture,postType,creatorProfilePic,uuid);
    }

    // endregion

    // endregion

    // region Utils

    public boolean hasInternetConnection(){
        return this.utilModelLayerManager.hasInternetConnection();
    }

    public String convertFromUtcToLocal(String utc){
       return this.utilModelLayerManager.convertFromUtcToLocal(utc);
    }

    // endregion

}
