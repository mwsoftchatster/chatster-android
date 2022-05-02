package nl.mwsoft.www.chatster.modelLayer.model;


import android.os.Parcel;
import android.os.Parcelable;

public class GroupChatOfflineMessage implements Parcelable {

    private String msgType;
    private String contentType;
    private long senderId;
    private String groupChatId;
    private String groupChatMessage;
    private String uuid;
    private String groupMemberPBKUUID;
    private String messageCreated;

    public GroupChatOfflineMessage() {
    }

    public GroupChatOfflineMessage(String msgType, String contentType, long senderId, String groupChatId, String groupChatMessage, String uuid, String groupMemberPBKUUID, String messageCreated) {
        this.msgType = msgType;
        this.contentType = contentType;
        this.senderId = senderId;
        this.groupChatId = groupChatId;
        this.groupChatMessage = groupChatMessage;
        this.uuid = uuid;
        this.groupMemberPBKUUID = groupMemberPBKUUID;
        this.messageCreated = messageCreated;
    }

    protected GroupChatOfflineMessage(Parcel in) {
        msgType = in.readString();
        contentType = in.readString();
        senderId = in.readLong();
        groupChatId = in.readString();
        groupChatMessage = in.readString();
        uuid = in.readString();
        groupMemberPBKUUID = in.readString();
        messageCreated = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(msgType);
        dest.writeString(contentType);
        dest.writeLong(senderId);
        dest.writeString(groupChatId);
        dest.writeString(groupChatMessage);
        dest.writeString(uuid);
        dest.writeString(groupMemberPBKUUID);
        dest.writeString(messageCreated);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GroupChatOfflineMessage> CREATOR = new Creator<GroupChatOfflineMessage>() {
        @Override
        public GroupChatOfflineMessage createFromParcel(Parcel in) {
            return new GroupChatOfflineMessage(in);
        }

        @Override
        public GroupChatOfflineMessage[] newArray(int size) {
            return new GroupChatOfflineMessage[size];
        }
    };

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public String getGroupChatId() {
        return groupChatId;
    }

    public void setGroupChatId(String groupChatId) {
        this.groupChatId = groupChatId;
    }

    public String getGroupChatMessage() {
        return groupChatMessage;
    }

    public void setGroupChatMessage(String groupChatMessage) {
        this.groupChatMessage = groupChatMessage;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getGroupMemberPBKUUID() {
        return groupMemberPBKUUID;
    }

    public void setGroupMemberPBKUUID(String groupMemberPBKUUID) {
        this.groupMemberPBKUUID = groupMemberPBKUUID;
    }

    public String getMessageCreated() {
        return messageCreated;
    }

    public void setMessageCreated(String messageCreated) {
        this.messageCreated = messageCreated;
    }
}

