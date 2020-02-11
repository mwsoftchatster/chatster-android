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
package nl.mwsoft.www.chatster.modelLayer.model;


import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;

public class GroupChatMessage implements Parcelable {

    private String msgType;
    private int getGroupChatMessageId;
    private String groupChatId;
    private long senderId;
    private String messageText;
    private String uuid;
    private Uri binaryMessageFilePath;
    @Nullable
    private String groupChatMessageCreated;

    public GroupChatMessage() {
    }

    public GroupChatMessage(String msgType, int getGroupChatMessageId, String groupChatId, long senderId, String messageText, String uuid, Uri binaryMessageFilePath, String groupChatMessageCreated) {
        this.msgType = msgType;
        this.getGroupChatMessageId = getGroupChatMessageId;
        this.groupChatId = groupChatId;
        this.senderId = senderId;
        this.messageText = messageText;
        this.uuid = uuid;
        this.binaryMessageFilePath = binaryMessageFilePath;
        this.groupChatMessageCreated = groupChatMessageCreated;
    }

    protected GroupChatMessage(Parcel in) {
        msgType = in.readString();
        getGroupChatMessageId = in.readInt();
        groupChatId = in.readString();
        senderId = in.readLong();
        messageText = in.readString();
        uuid = in.readString();
        binaryMessageFilePath = in.readParcelable(Uri.class.getClassLoader());
        groupChatMessageCreated = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(msgType);
        dest.writeInt(getGroupChatMessageId);
        dest.writeString(groupChatId);
        dest.writeLong(senderId);
        dest.writeString(messageText);
        dest.writeString(uuid);
        dest.writeParcelable(binaryMessageFilePath, flags);
        dest.writeString(groupChatMessageCreated);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GroupChatMessage> CREATOR = new Creator<GroupChatMessage>() {
        @Override
        public GroupChatMessage createFromParcel(Parcel in) {
            return new GroupChatMessage(in);
        }

        @Override
        public GroupChatMessage[] newArray(int size) {
            return new GroupChatMessage[size];
        }
    };

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public int getGetGroupChatMessageId() {
        return getGroupChatMessageId;
    }

    public void setGetGroupChatMessageId(int getGroupChatMessageId) {
        this.getGroupChatMessageId = getGroupChatMessageId;
    }

    public String getGroupChatId() {
        return groupChatId;
    }

    public void setGroupChatId(String groupChatId) {
        this.groupChatId = groupChatId;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Uri getBinaryMessageFilePath() {
        return binaryMessageFilePath;
    }

    public void setBinaryMessageFilePath(Uri binaryMessageFilePath) {
        this.binaryMessageFilePath = binaryMessageFilePath;
    }

    @Nullable
    public String getGroupChatMessageCreated() {
        return groupChatMessageCreated;
    }

    public void setGroupChatMessageCreated(@Nullable String groupChatMessageCreated) {
        this.groupChatMessageCreated = groupChatMessageCreated;
    }
}

