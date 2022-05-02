package nl.mwsoft.www.chatster.modelLayer.model;


import android.os.Parcel;
import android.os.Parcelable;

public class OfflineMessage implements Parcelable {

    private String msgType;
    private String contentType;
    private long senderId;
    private String senderName;
    private long receiverId;
    private String chatName;
    private String messageData;
    private String uuid;
    private String contactPublicKeyUUID;
    private String messageCreated;

    public OfflineMessage() {
    }

    public OfflineMessage(String msgType, String contentType, long senderId, String senderName, long receiverId, String chatName, String messageData, String uuid, String contactPublicKeyUUID, String messageCreated) {
        this.msgType = msgType;
        this.contentType = contentType;
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverId = receiverId;
        this.chatName = chatName;
        this.messageData = messageData;
        this.uuid = uuid;
        this.contactPublicKeyUUID = contactPublicKeyUUID;
        this.messageCreated = messageCreated;
    }

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

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(long receiverId) {
        this.receiverId = receiverId;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public String getMessageData() {
        return messageData;
    }

    public void setMessageData(String messageData) {
        this.messageData = messageData;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContactPublicKeyUUID() {
        return contactPublicKeyUUID;
    }

    public void setContactPublicKeyUUID(String contactPublicKeyUUID) {
        this.contactPublicKeyUUID = contactPublicKeyUUID;
    }

    public String getMessageCreated() {
        return messageCreated;
    }

    public void setMessageCreated(String messageCreated) {
        this.messageCreated = messageCreated;
    }

    protected OfflineMessage(Parcel in) {
        msgType = in.readString();
        contentType = in.readString();
        senderId = in.readLong();
        senderName = in.readString();
        receiverId = in.readLong();
        chatName = in.readString();
        messageData = in.readString();
        uuid = in.readString();
        contactPublicKeyUUID = in.readString();
        messageCreated = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(msgType);
        dest.writeString(contentType);
        dest.writeLong(senderId);
        dest.writeString(senderName);
        dest.writeLong(receiverId);
        dest.writeString(chatName);
        dest.writeString(messageData);
        dest.writeString(uuid);
        dest.writeString(contactPublicKeyUUID);
        dest.writeString(messageCreated);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OfflineMessage> CREATOR = new Creator<OfflineMessage>() {
        @Override
        public OfflineMessage createFromParcel(Parcel in) {
            return new OfflineMessage(in);
        }

        @Override
        public OfflineMessage[] newArray(int size) {
            return new OfflineMessage[size];
        }
    };
}

