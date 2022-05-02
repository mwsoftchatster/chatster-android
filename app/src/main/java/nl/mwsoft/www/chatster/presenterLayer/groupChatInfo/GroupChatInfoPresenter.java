package nl.mwsoft.www.chatster.presenterLayer.groupChatInfo;


import android.content.Context;
import android.net.Uri;

import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.Observable;
import nl.mwsoft.www.chatster.modelLayer.model.Contact;
import nl.mwsoft.www.chatster.modelLayer.model.ExitGroupChatReq;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChat;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.contact.ContactModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.groupChat.GroupChatModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.user.UserModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.image.ImageProcessingManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.util.UtilModelLayerManager;

public class GroupChatInfoPresenter {

    private GroupChatModelLayerManager groupChatModelLayerManager;
    private ContactModelLayerManager contactModelLayerManager;
    private UserModelLayerManager userModelLayerManager;
    private UtilModelLayerManager utilModelLayerManager;

    public ImageProcessingManager imageProcessingManager;

    public GroupChatInfoPresenter() {
    }

    public GroupChatInfoPresenter(GroupChatModelLayerManager groupChatModelLayerManager,
                                  ContactModelLayerManager contactModelLayerManager,
                                  UserModelLayerManager userModelLayerManager,
                                  ImageProcessingManager imageProcessingManager,
                                  UtilModelLayerManager utilModelLayerManager) {
        this.groupChatModelLayerManager = groupChatModelLayerManager;
        this.contactModelLayerManager = contactModelLayerManager;
        this.userModelLayerManager = userModelLayerManager;
        this.imageProcessingManager = imageProcessingManager;
        this.utilModelLayerManager = utilModelLayerManager;
    }

    public String encodeImageToString(Context context, Uri imageUrl) throws IOException {
        return this.imageProcessingManager.encodeImageToString(context, imageUrl);
    }

    // region Exit Group Chat

    public Observable<ExitGroupChatReq> exitGroupChat(ExitGroupChatReq exitGroupChatReq, Context context){
        return this.groupChatModelLayerManager.exitGroupChat(exitGroupChatReq, context);
    }

    // endregion

    // region Group Chat Info DB

    public GroupChat getGroupChatById(Context context, String id) {
        return this.groupChatModelLayerManager.getGroupChatById(context, id);
    }

    public String getGroupProfilePicUriById(Context context, String groupId) {
        return this.groupChatModelLayerManager.getGroupProfilePicUriById(context, groupId);
    }

    public long getUserId(Context context) {
        return this.userModelLayerManager.getUserId(context);
    }

    public void updateGroupProfilePic(String uri, String groupChatId, Context context){
        this.groupChatModelLayerManager.updateGroupProfilePic(uri, groupChatId, context);
    }

    public void deleteGroupChat(Context context, String groupChatId){
        this.groupChatModelLayerManager.deleteGroupChat(context, groupChatId);
    }

    public ArrayList<Contact> getAllContactsByGroupChat(Context context, String groupChatId) {
        return this.contactModelLayerManager.getAllContactsByGroupChat(context, groupChatId);
    }

    public Contact getContactById(Context context, long contactId) {
        return this.contactModelLayerManager.getContactById(context, contactId);
    }

    // endregion

    // region Util

    public boolean hasInternetConnection(){
        return utilModelLayerManager.hasInternetConnection();
    }

    // endregion
}
