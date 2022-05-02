package nl.mwsoft.www.chatster.presenterLayer.postCommentsPresenter;


import android.content.Context;

import java.util.ArrayList;

import io.reactivex.Observable;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorPostComment;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.creators.CreatorsModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.user.UserModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.util.UtilModelLayerManager;

public class PostCommentsPresenter {

    private CreatorsModelLayerManager creatorsModelLayerManager;
    private UserModelLayerManager userModelLayerManager;
    private UtilModelLayerManager utilModelLayerManager;


    public PostCommentsPresenter() {
    }

    public PostCommentsPresenter(CreatorsModelLayerManager creatorsModelLayerManager, UserModelLayerManager userModelLayerManager, UtilModelLayerManager utilModelLayerManager) {
        this.creatorsModelLayerManager = creatorsModelLayerManager;
        this.userModelLayerManager = userModelLayerManager;
        this.utilModelLayerManager = utilModelLayerManager;
    }

    public Observable<ArrayList<CreatorPostComment>> getCreatorPostComments(String postUUID){
        return this.creatorsModelLayerManager.getCreatorPostComments(postUUID);
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

    // region Utils

    public boolean hasInternetConnection(){
        return this.utilModelLayerManager.hasInternetConnection();
    }

    // endregion

}
