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
package nl.mwsoft.www.chatster.presenterLayer.groupChat;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.model.Contact;
import nl.mwsoft.www.chatster.modelLayer.model.CreateGroupChatRequest;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChat;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChatMessage;
import nl.mwsoft.www.chatster.modelLayer.model.ReceivedOnlineGroupMessage;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.contact.ContactModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.groupChat.GroupChatModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.user.UserModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.image.ImageProcessingManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.util.UtilModelLayerManager;

public class GroupChatPresenter {

    private GroupChatModelLayerManager groupChatModelLayerManager;
    private UserModelLayerManager userModelLayerManager;
    private ContactModelLayerManager contactModelLayerManager;
    private ImageProcessingManager imageProcessingManager;
    private UtilModelLayerManager utilModelLayerManager;


    // region Constructor

    public GroupChatPresenter() {
    }

    // GroupChatMessageAdapter
    public GroupChatPresenter(ContactModelLayerManager contactModelLayerManager,
                              GroupChatModelLayerManager groupChatModelLayerManager,
                              UserModelLayerManager userModelLayerManager){
        this.groupChatModelLayerManager = groupChatModelLayerManager;
        this.userModelLayerManager = userModelLayerManager;
        this.contactModelLayerManager = contactModelLayerManager;
    }

    // CreateGroupChatActivity
    public GroupChatPresenter(ContactModelLayerManager contactModelLayerManager,
                              GroupChatModelLayerManager groupChatModelLayerManager,
                              UserModelLayerManager userModelLayerManager,
                              UtilModelLayerManager utilModelLayerManager){
        this.groupChatModelLayerManager = groupChatModelLayerManager;
        this.userModelLayerManager = userModelLayerManager;
        this.contactModelLayerManager = contactModelLayerManager;
        this.utilModelLayerManager = utilModelLayerManager;
    }

    // CreateGroupChatContactsAdapter && GroupChatInfoAdapter
    public GroupChatPresenter(ContactModelLayerManager contactModelLayerManager){
        this.contactModelLayerManager = contactModelLayerManager;
    }

    // GroupChatActivity
    public GroupChatPresenter(GroupChatModelLayerManager groupChatModelLayerManager,
                              UserModelLayerManager userModelLayerManager,
                              ImageProcessingManager imageProcessingManager,
                              ContactModelLayerManager contactModelLayerManager,
                              UtilModelLayerManager utilModelLayerManager){
        this.imageProcessingManager = imageProcessingManager;
        this.groupChatModelLayerManager = groupChatModelLayerManager;
        this.userModelLayerManager = userModelLayerManager;
        this.contactModelLayerManager = contactModelLayerManager;
        this.utilModelLayerManager = utilModelLayerManager;
    }

    // GroupChatsAdapter && GroupChatsFragment
    public GroupChatPresenter(GroupChatModelLayerManager groupChatModelLayerManager){
        this.groupChatModelLayerManager = groupChatModelLayerManager;
    }

    // endregion

    // region Image Processing

    public File createImageFile(Context context) throws IOException {
        return this.imageProcessingManager.createImageFile(context);
    }

    public Bitmap decodeImage(String data) {
        return this.imageProcessingManager.decodeImage(data);
    }

    public Uri saveIncomingImageMessage(Context context, Bitmap inImage) {
        return this.imageProcessingManager.saveIncomingImageMessage(context, inImage);
    }

    public String encodeImageToString(Context context, Uri imageUrl) throws IOException {
        return this.imageProcessingManager.encodeImageToString(context, imageUrl);
    }

    public Bitmap decodeSampledBitmap(Context context, Uri imageUrl) throws IOException {
        return this.imageProcessingManager.decodeSampledBitmap(context, imageUrl);
    }

    // endregion

    // region Create Group Chat Network

    public Observable<GroupChat> createGroupChat(CreateGroupChatRequest createGroupChatRequest) {
        return this.groupChatModelLayerManager.createGroupChat(createGroupChatRequest);
    }

    // region Group Chat DB

    public void deleteGroupChatMessageQueueItemByUUID(String uuid, Context context){
        this.groupChatModelLayerManager.deleteGroupChatMessageQueueItemByUUID(uuid, context);
    }

    public void deleteGroupChat(String groupChatId, Context context){
        this.groupChatModelLayerManager.deleteGroupChat(context, groupChatId);
    }

    public ArrayList<GroupChat> getAllGroupChats(Context context) {
        return this.groupChatModelLayerManager.getAllGroupChats(context);
    }

    public String getContactProfilePicUriById(Context context, long contactId) {
        return this.contactModelLayerManager.getContactProfilePicUriById(context, contactId);
    }

    public void updateMessageHasBeenReadByMessageId(int messageId, Context context){
        this.groupChatModelLayerManager.updateMessageHasBeenReadByMessageId( messageId, context);
    }

    public void createGroupChat(GroupChat groupChat, Context context) {
        this.groupChatModelLayerManager.createGroupChat(groupChat, context);
    }

    public void createGroupChatMember(String groupChatId, long groupChatMemberId, Context context) {
        this.groupChatModelLayerManager.createGroupChatMember(groupChatId, groupChatMemberId, context);
    }

    public ArrayList<Contact> getAllContacts(Context context) {
        return this.contactModelLayerManager.getAllContacts(context);
    }

    public long getUserId(Context context) {
        return this.userModelLayerManager.getUserId(context);
    }

    public String getContactNameById(Context context, long contactId) {
        return this.contactModelLayerManager.getContactNameById(context, contactId);
    }

    public void deleteGroupChatMessage(int messageId, Context context){
        this.groupChatModelLayerManager.deleteGroupChatMessage(messageId, context);
    }

    public void updateMessageUnsentByUUID(String uuid, Context context){
        this.groupChatModelLayerManager.updateMessageUnsentByUUID(uuid, context);
    }

    public GroupChat getGroupChatById(Context context, String id) {
        return this.groupChatModelLayerManager.getGroupChatById(context, id);
    }

    public ArrayList<GroupChatMessage> getAllMessagesForGroupChatWithId(Context context, String groupChatId) {
        return this.groupChatModelLayerManager.getAllMessagesForGroupChatWithId(context, groupChatId);
    }

    public void insertGroupChatMessage(GroupChatMessage groupChatMessage, Context context) {
        this.groupChatModelLayerManager.insertGroupChatMessage(groupChatMessage, context);
    }

    public String getGroupProfilePicUriById(Context context, String groupId) {
        return this.groupChatModelLayerManager.getGroupProfilePicUriById(context, groupId);
    }

    public String getGroupChatNameById(Context context, String groupChatId) {
        return this.groupChatModelLayerManager.getGroupChatNameById(context, groupChatId);
    }

    public int getUnreadMessageCountByGroupChatId(Context context, String groupChatId) {
        return this.groupChatModelLayerManager.getUnreadMessageCountByGroupChatId(context, groupChatId);
    }

    public void insertGroupChatMessageQueueItem(String messageUUID, Context context) {
        this.groupChatModelLayerManager.insertGroupChatMessageQueueItem(messageUUID, context);
    }

    public void insertReceivedOnlineGroupMessage(String uuid, Context context) {
        this.groupChatModelLayerManager.insertReceivedOnlineGroupMessage(uuid, context);
    }

    public void deleteReceivedOnlineGroupMessageByUUID(String uuid, Context context){
        this.groupChatModelLayerManager.deleteReceivedOnlineGroupMessageByUUID(uuid, context);
    }

    public ReceivedOnlineGroupMessage getReceivedOnlineGroupMessage(Context context) {
        return this.groupChatModelLayerManager.getReceivedOnlineGroupMessage(context);
    }

    // endregion

    // region Create Group Chat Message

    @NonNull
    public GroupChatMessage createGroupChatImageMessage(long senderId, String groupChatId, Uri imageUrl, String uuid) {
        GroupChatMessage groupChatMessage = new GroupChatMessage();
        groupChatMessage.setGroupChatId(groupChatId);
        groupChatMessage.setSenderId(senderId);
        groupChatMessage.setMsgType(ConstantRegistry.IMAGE);
        groupChatMessage.setBinaryMessageFilePath(imageUrl);
        groupChatMessage.setUuid(uuid);
        groupChatMessage.setGroupChatMessageCreated(utilModelLayerManager.getDateTime());
        return groupChatMessage;
    }

    @NonNull
    public GroupChatMessage createGroupChatImageMessage(long senderId, String groupChatId, Uri imageUrl, String utc, String uuid) {
        GroupChatMessage groupChatMessage = new GroupChatMessage();
        groupChatMessage.setGroupChatId(groupChatId);
        groupChatMessage.setSenderId(senderId);
        groupChatMessage.setMsgType(ConstantRegistry.IMAGE);
        groupChatMessage.setBinaryMessageFilePath(imageUrl);
        groupChatMessage.setUuid(uuid);
        groupChatMessage.setGroupChatMessageCreated(utilModelLayerManager.convertFromUtcToLocal(utc));
        return groupChatMessage;
    }

    @NonNull
    public GroupChatMessage createGroupChatTextMessage(long senderId, String groupChatId, String message, String utc, String uuid) {
        GroupChatMessage groupChatMessage = new GroupChatMessage();
        groupChatMessage.setGroupChatId(groupChatId);
        groupChatMessage.setSenderId(senderId);
        groupChatMessage.setMessageText(message);
        groupChatMessage.setMsgType(ConstantRegistry.TEXT);
        groupChatMessage.setUuid(uuid);
        groupChatMessage.setGroupChatMessageCreated(utilModelLayerManager.convertFromUtcToLocal(utc));
        return groupChatMessage;
    }

    @NonNull
    public GroupChatMessage createGroupChatTextMessage(long senderId, String groupChatId, String message, String uuid) {
        GroupChatMessage groupChatMessage = new GroupChatMessage();
        groupChatMessage.setGroupChatId(groupChatId);
        groupChatMessage.setSenderId(senderId);
        groupChatMessage.setMessageText(message);
        groupChatMessage.setMsgType(ConstantRegistry.TEXT);
        groupChatMessage.setUuid(uuid);
        groupChatMessage.setGroupChatMessageCreated(utilModelLayerManager.getDateTime());
        return groupChatMessage;
    }

    // endregion

    // region Initialize Unsend Message Registry

    public Observable<HashMap<String,Integer>> initializeUnsendMessageRegistry(ArrayList<GroupChatMessage> messages){
        return Observable.create(new ObservableOnSubscribe<HashMap<String,Integer>>() {
            @Override
            public void subscribe(ObservableEmitter<HashMap<String,Integer>> emitter) throws Exception {
                try {
                    HashMap<String,Integer> unsendMessageRegistry = new HashMap<>();
                    int position = 0;
                    for(GroupChatMessage message : messages){
                        unsendMessageRegistry.put(message.getUuid(), position);
                        position += 1;
                    }
                    emitter.onNext(unsendMessageRegistry);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    // endregion

    // region Util

    public boolean hasInternetConnection(){
        return utilModelLayerManager.hasInternetConnection();
    }

    public String createUUID(){
        return utilModelLayerManager.createUUID();
    }

    public String getDateTime(){
        return utilModelLayerManager.getDateTime();
    }

    // endregion
}
