package nl.mwsoft.www.chatster.modelLayer.model;


import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;

public class Message implements Parcelable{

    private int messageId;
    private int messageChatId;
    private int messageGroupChatChatId;
    private long messageSenderId;
    @Nullable
    private String messageText;
    @Nullable
    private Uri binaryMessageFilePath;
    @Nullable
    private String messageCreated;
    private String msgType;
    @Nullable
    private String messageUUID;

    public Message() {
    }

    public Message(int messageId, int messageChatId, int messageGroupChatChatId, long messageSenderId, String messageText, Uri binaryMessageFilePath, String messageCreated, String msgType, String messageUUID) {
        this.messageId = messageId;
        this.messageChatId = messageChatId;
        this.messageGroupChatChatId = messageGroupChatChatId;
        this.messageSenderId = messageSenderId;
        this.messageText = messageText;
        this.binaryMessageFilePath = binaryMessageFilePath;
        this.messageCreated = messageCreated;
        this.msgType = msgType;
        this.messageUUID = messageUUID;
    }

    protected Message(Parcel in) {
        messageId = in.readInt();
        messageChatId = in.readInt();
        messageGroupChatChatId = in.readInt();
        messageSenderId = in.readLong();
        messageText = in.readString();
        binaryMessageFilePath = in.readParcelable(Uri.class.getClassLoader());
        messageCreated = in.readString();
        msgType = in.readString();
        messageUUID = in.readString();
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {
        @Override
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        @Override
        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getMessageChatId() {
        return messageChatId;
    }

    public void setMessageChatId(int messageChatId) {
        this.messageChatId = messageChatId;
    }

    public int getMessageGroupChatChatId() {
        return messageGroupChatChatId;
    }

    public void setMessageGroupChatChatId(int messageGroupChatChatId) {
        this.messageGroupChatChatId = messageGroupChatChatId;
    }

    public long getMessageSenderId() {
        return messageSenderId;
    }

    public void setMessageSenderId(long messageSenderId) {
        this.messageSenderId = messageSenderId;
    }

    @Nullable
    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(@Nullable String messageText) {
        this.messageText = messageText;
    }

    @Nullable
    public Uri getBinaryMessageFilePath() {
        return binaryMessageFilePath;
    }

    public void setBinaryMessageFilePath(@Nullable Uri binaryMessageFilePath) {
        this.binaryMessageFilePath = binaryMessageFilePath;
    }

    @Nullable
    public String getMessageCreated() {
        return messageCreated;
    }

    public void setMessageCreated(@Nullable String messageCreated) {
        this.messageCreated = messageCreated;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    @Nullable
    public String getMessageUUID() {
        return messageUUID;
    }

    public void setMessageUUID(@Nullable String messageUUID) {
        this.messageUUID = messageUUID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(messageId);
        dest.writeInt(messageChatId);
        dest.writeInt(messageGroupChatChatId);
        dest.writeLong(messageSenderId);
        dest.writeString(messageText);
        dest.writeParcelable(binaryMessageFilePath, flags);
        dest.writeString(messageCreated);
        dest.writeString(msgType);
        dest.writeString(messageUUID);
    }
}

