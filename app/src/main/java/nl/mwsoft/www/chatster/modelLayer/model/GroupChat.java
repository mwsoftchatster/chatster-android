package nl.mwsoft.www.chatster.modelLayer.model;


import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class GroupChat implements Parcelable {

    private String _id;
    private long groupChatAdminId;
    private ArrayList<Long> groupChatMembers;
    private String groupChatName;
    private String groupChatStatusMessage;
    private String groupChatImage;
    @Nullable
    private String groupChatLastMessage;
    @Nullable
    private String groupChatLastActivity;

    public GroupChat() {
        groupChatMembers = new ArrayList<>();
    }

    public GroupChat(String _id, long groupChatAdminId, ArrayList<Long> groupChatMembers, String groupChatName, String groupChatStatusMessage, String groupChatImage, String groupChatLastMessage, String groupChatLastActivity) {
        this._id = _id;
        this.groupChatAdminId = groupChatAdminId;
        this.groupChatMembers = groupChatMembers;
        this.groupChatName = groupChatName;
        this.groupChatStatusMessage = groupChatStatusMessage;
        this.groupChatImage = groupChatImage;
        this.groupChatLastMessage = groupChatLastMessage;
        this.groupChatLastActivity = groupChatLastActivity;
    }

    protected GroupChat(Parcel in) {
        _id = in.readString();
        groupChatAdminId = in.readLong();
        groupChatName = in.readString();
        groupChatStatusMessage = in.readString();
        groupChatImage = in.readString();
        groupChatLastMessage = in.readString();
        groupChatLastActivity = in.readString();
    }

    public static final Creator<GroupChat> CREATOR = new Creator<GroupChat>() {
        @Override
        public GroupChat createFromParcel(Parcel in) {
            return new GroupChat(in);
        }

        @Override
        public GroupChat[] newArray(int size) {
            return new GroupChat[size];
        }
    };

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public long getGroupChatAdminId() {
        return groupChatAdminId;
    }

    public void setGroupChatAdminId(long groupChatAdminId) {
        this.groupChatAdminId = groupChatAdminId;
    }

    public ArrayList<Long> getGroupChatMembers() {
        return groupChatMembers;
    }

    public void setGroupChatMembers(ArrayList<Long> groupChatMembers) {
        this.groupChatMembers = groupChatMembers;
    }

    public String getGroupChatName() {
        return groupChatName;
    }

    public void setGroupChatName(String groupChatName) {
        this.groupChatName = groupChatName;
    }

    public String getGroupChatStatusMessage() {
        return groupChatStatusMessage;
    }

    public void setGroupChatStatusMessage(String groupChatStatusMessage) {
        this.groupChatStatusMessage = groupChatStatusMessage;
    }

    public String getGroupChatImage() {
        return groupChatImage;
    }

    public void setGroupChatImage(String groupChatImage) {
        this.groupChatImage = groupChatImage;
    }

    @Nullable
    public String getGroupChatLastMessage() {
        return groupChatLastMessage;
    }

    public void setGroupChatLastMessage(@Nullable String groupChatLastMessage) {
        this.groupChatLastMessage = groupChatLastMessage;
    }

    @Nullable
    public String getGroupChatLastActivity() {
        return groupChatLastActivity;
    }

    public void setGroupChatLastActivity(@Nullable String groupChatLastActivity) {
        this.groupChatLastActivity = groupChatLastActivity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeLong(groupChatAdminId);
        dest.writeString(groupChatName);
        dest.writeString(groupChatStatusMessage);
        dest.writeString(groupChatImage);
        dest.writeString(groupChatLastMessage);
        dest.writeString(groupChatLastActivity);
    }
}

