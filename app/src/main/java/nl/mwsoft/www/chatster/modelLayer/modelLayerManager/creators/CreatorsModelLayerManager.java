package nl.mwsoft.www.chatster.modelLayer.modelLayerManager.creators;


import android.content.Context;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import nl.mwsoft.www.chatster.modelLayer.database.creators.CreatorsDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorContact;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorPost;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorPostComment;
import nl.mwsoft.www.chatster.modelLayer.model.HistoryItem;
import nl.mwsoft.www.chatster.modelLayer.network.NetworkLayer;
import nl.mwsoft.www.chatster.modelLayer.network.creatorFollowers.CreatorFollowersRequestImpl;
import nl.mwsoft.www.chatster.modelLayer.network.creatorFollowing.CreatorFollowingRequestImpl;
import nl.mwsoft.www.chatster.modelLayer.network.uploadVideoPost.UploadVideoPostImpl;
import okhttp3.MultipartBody;

public class CreatorsModelLayerManager {

    private CreatorsDatabaseLayer creatorsDatabaseLayer;
    private NetworkLayer networkLayer;


    // region Constructors

    public CreatorsModelLayerManager() {
    }

    public CreatorsModelLayerManager(CreatorsDatabaseLayer creatorsDatabaseLayer, NetworkLayer networkLayer) {
        this.creatorsDatabaseLayer = creatorsDatabaseLayer;
        this.networkLayer = networkLayer;
    }

    public CreatorsModelLayerManager(CreatorsDatabaseLayer creatorsDatabaseLayer) {
        this.creatorsDatabaseLayer = creatorsDatabaseLayer;
    }

    public CreatorsModelLayerManager(NetworkLayer networkLayer) {
        this.networkLayer = networkLayer;
    }

    // endregion

    // region DB

    public String getCreatorsContactProfilePicUriById(Context context, String creatorContactId) {
        return this.creatorsDatabaseLayer.getCreatorsContactProfilePicUriById(context, creatorContactId);
    }

    public CreatorContact getCreatorContactById(Context context, String creatorContactId) {
        return this.creatorsDatabaseLayer.getCreatorContactById(context, creatorContactId);
    }

    public ArrayList<CreatorContact> getAllCreatorContacts(Context context) {
        return this.creatorsDatabaseLayer.getAllCreatorContacts(context);
    }

    public ArrayList<String> getAllCreatorContactIds(Context context) {
        return this.creatorsDatabaseLayer.getAllCreatorContactIds(context);
    }

    public void disconnectCreatorContactById(String contactId, Context context){
        this.creatorsDatabaseLayer.disconnectCreatorContactById(contactId, context);
    }

    public void insertCreatorContact(String creatorContactId, String creatorContactProfilePic,
                                     String creatorContactStatusMessage, Context context) {
        this.creatorsDatabaseLayer.insertCreatorContact(creatorContactId, creatorContactProfilePic,
                 creatorContactStatusMessage, context);
    }

    public String insertCreatorContacts(Context context){
        return this.creatorsDatabaseLayer.insertCreatorContacts(context);
    }

    public int getCreatorsPostIsLiked(Context context, String postUUID) {
        return this.creatorsDatabaseLayer.getCreatorsPostIsLiked(context, postUUID);
    }

    public boolean getCreatorsPostExists(Context context, String postUUID) {
        return this.creatorsDatabaseLayer.getCreatorsPostExists(context, postUUID);
    }

    public void insertCreatorPostIsLiked(String uuid, Context context) {
        this.creatorsDatabaseLayer.insertCreatorPostIsLiked(uuid, context);
    }

    public void updateCreatorPostIsLiked(String uuid, int status, Context context){
        this.creatorsDatabaseLayer.updateCreatorPostIsLiked(uuid, status, context);
    }


    // endregion

    // region Network

    public Observable<ArrayList<CreatorPost>> getLatestCreatorPosts(long creator, String creatorsName){
        return this.networkLayer.getLatestCreatorPosts(creator, creatorsName);
    }

    public Observable<ArrayList<CreatorPostComment>> getCreatorPostComments(String postUUID){
        return this.networkLayer.getCreatorPostComments(postUUID);
    }

    public Observable<CreatorContact> getCreatorContactProfile(String creator, long userId){
        return this.networkLayer.getCreatorContactProfile(creator,userId);
    }

    public Observable<ArrayList<CreatorPost>> discoverPosts(long userId){
        return this.networkLayer.discoverPosts(userId);
    }

    // region Creator Followers

    public Observable<ArrayList<CreatorContact>> getCreatorFollowers(String creatorName,long userId){
        return this.networkLayer.getCreatorFollowers(creatorName,userId);
    }

    // endregion

    // region Creator Followers

    public Observable<ArrayList<CreatorContact>> getCreatorFollowing(String creatorName, long userId){
        return this.networkLayer.getCreatorFollowing(creatorName, userId);
    }

    // endregion

    // region Creator History

    public Observable<ArrayList<HistoryItem>> getCreatorHistory(String creatorName, long userId){
        return this.networkLayer.getCreatorHistory(creatorName, userId);
    }

    // endregion

    // region Load More Posts

    public Observable<ArrayList<CreatorPost>> loadMorePosts(String creatorName, String lastPostCreated){
        return this.networkLayer.loadMorePosts(creatorName, lastPostCreated);
    }

    // endregion

    // region Upload Video Post

    public Observable<String> getUploadVideoPostResponse(MultipartBody.Part body, String userName, String postCapture,
                                                         String postType, String creatorProfilePic, String uuid){
        return this.networkLayer.getUploadVideoPostResponse(body,userName,
                postCapture,postType,creatorProfilePic,uuid);
    }

    // endregion

    // endregion
}
