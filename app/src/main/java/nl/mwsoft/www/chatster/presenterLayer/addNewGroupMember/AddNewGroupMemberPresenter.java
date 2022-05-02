package nl.mwsoft.www.chatster.presenterLayer.addNewGroupMember;


import android.content.Context;

import java.util.ArrayList;

import io.reactivex.Observable;

import nl.mwsoft.www.chatster.modelLayer.model.Contact;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChat;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.contact.ContactModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.groupChat.GroupChatModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.util.UtilModelLayerManager;

public class AddNewGroupMemberPresenter {

    private GroupChatModelLayerManager groupChatModelLayerManager;
    private ContactModelLayerManager contactModelLayerManager;
    private UtilModelLayerManager utilModelLayerManager;

    public AddNewGroupMemberPresenter(){
    }

    public AddNewGroupMemberPresenter(GroupChatModelLayerManager groupChatModelLayerManager,
                                      ContactModelLayerManager contactModelLayerManager,
                                      UtilModelLayerManager utilModelLayerManager) {
        this.groupChatModelLayerManager = groupChatModelLayerManager;
        this.contactModelLayerManager = contactModelLayerManager;
        this.utilModelLayerManager = utilModelLayerManager;
    }

    public GroupChat getGroupChatById(Context context, String id) {
        return this.groupChatModelLayerManager.getGroupChatById(context, id);
    }

    public ArrayList<Contact> getAllContacts(Context context) {
        return this.contactModelLayerManager.getAllContacts(context);
    }

    public long getGroupChatAdminByGroupId(Context context, String groupChatId) {
        return this.groupChatModelLayerManager.getGroupChatAdminByGroupId(context, groupChatId);
    }

    public String getGroupChatNameById(Context context, String groupChatId) {
        return this.groupChatModelLayerManager.getGroupChatNameById(context, groupChatId);
    }

    public Observable<String> addNewGroupMember(String chatId, long groupChatAdminId, String groupChatName,
                                                ArrayList<Long> newGroupChatMembers, String groupChatPicPath){
        return this.groupChatModelLayerManager.addNewGroupMember(chatId, groupChatAdminId, groupChatName,
                newGroupChatMembers, groupChatPicPath);
    }

    public boolean hasInternetConnection(){
        return utilModelLayerManager.hasInternetConnection();
    }



}
