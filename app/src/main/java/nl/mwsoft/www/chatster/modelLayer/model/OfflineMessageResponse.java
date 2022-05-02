package nl.mwsoft.www.chatster.modelLayer.model;


import androidx.annotation.Nullable;

public class OfflineMessageResponse {
    private String msgType;
    private String contentType;
    private long senderId;
    @Nullable
    private String senderName;
    @Nullable
    private long receiverId;
    @Nullable
    private String chatName;
    @Nullable
    private String messageData;
    @Nullable
    private String groupChatId;
    @Nullable
    private String messageText;
    private String uuid;
    private String contactPublicKeyUUID;
    private String messageCreated;

    public OfflineMessageResponse() {
    }

    public OfflineMessageResponse(String msgType, String contentType, long senderId, String senderName, long receiverId, String chatName, String messageData, String groupChatId, String messageText, String uuid, String contactPublicKeyUUID, String messageCreated) {
        this.msgType = msgType;
        this.contentType = contentType;
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverId = receiverId;
        this.chatName = chatName;
        this.messageData = messageData;
        this.groupChatId = groupChatId;
        this.messageText = messageText;
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

    @Nullable
    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(@Nullable String senderName) {
        this.senderName = senderName;
    }

    @Nullable
    public long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(@Nullable long receiverId) {
        this.receiverId = receiverId;
    }

    @Nullable
    public String getChatName() {
        return chatName;
    }

    public void setChatName(@Nullable String chatName) {
        this.chatName = chatName;
    }

    @Nullable
    public String getMessageData() {
        return messageData;
    }

    public void setMessageData(@Nullable String messageData) {
        this.messageData = messageData;
    }

    @Nullable
    public String getGroupChatId() {
        return groupChatId;
    }

    public void setGroupChatId(@Nullable String groupChatId) {
        this.groupChatId = groupChatId;
    }

    @Nullable
    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(@Nullable String messageText) {
        this.messageText = messageText;
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
}

