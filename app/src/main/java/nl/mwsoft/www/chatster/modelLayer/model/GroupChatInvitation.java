package nl.mwsoft.www.chatster.modelLayer.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class GroupChatInvitation implements Parcelable {
    private String groupChatInvitationChatId;
    private String groupChatInvitationChatName;
    private String groupProfilePicPath;
    private long groupChatInvitationSenderId;
    private ArrayList<Long> groupChatInvitationGroupChatMembers;


    public GroupChatInvitation() {
        groupChatInvitationGroupChatMembers = new ArrayList<>();
    }

    public GroupChatInvitation(String groupChatInvitationChatId, String groupChatInvitationChatName, String groupProfilePicPath, long groupChatInvitationSenderId, ArrayList<Long> groupChatInvitationGroupChatMembers) {
        this.groupChatInvitationChatId = groupChatInvitationChatId;
        this.groupChatInvitationChatName = groupChatInvitationChatName;
        this.groupProfilePicPath = groupProfilePicPath;
        this.groupChatInvitationSenderId = groupChatInvitationSenderId;
        this.groupChatInvitationGroupChatMembers = groupChatInvitationGroupChatMembers;
    }

    protected GroupChatInvitation(Parcel in) {
        groupChatInvitationChatId = in.readString();
        groupChatInvitationChatName = in.readString();
        groupProfilePicPath = in.readString();
        groupChatInvitationSenderId = in.readLong();
        groupChatInvitationGroupChatMembers = in.readArrayList(Long.class.getClassLoader());
    }

    public static final Creator<GroupChatInvitation> CREATOR = new Creator<GroupChatInvitation>() {
        @Override
        public GroupChatInvitation createFromParcel(Parcel in) {
            return new GroupChatInvitation(in);
        }

        @Override
        public GroupChatInvitation[] newArray(int size) {
            return new GroupChatInvitation[size];
        }
    };

    public String getGroupChatInvitationChatId() {
        return groupChatInvitationChatId;
    }

    public void setGroupChatInvitationChatId(String groupChatInvitationChatId) {
        this.groupChatInvitationChatId = groupChatInvitationChatId;
    }

    public String getGroupChatInvitationChatName() {
        return groupChatInvitationChatName;
    }

    public void setGroupChatInvitationChatName(String groupChatInvitationChatName) {
        this.groupChatInvitationChatName = groupChatInvitationChatName;
    }

    public String getGroupProfilePicPath() {
        return groupProfilePicPath;
    }

    public void setGroupProfilePicPath(String groupProfilePicPath) {
        this.groupProfilePicPath = groupProfilePicPath;
    }

    public long getGroupChatInvitationSenderId() {
        return groupChatInvitationSenderId;
    }

    public void setGroupChatInvitationSenderId(long groupChatInvitationSenderId) {
        this.groupChatInvitationSenderId = groupChatInvitationSenderId;
    }

    public ArrayList<Long> getGroupChatInvitationGroupChatMembers() {
        return groupChatInvitationGroupChatMembers;
    }

    public void setGroupChatInvitationGroupChatMembers(ArrayList<Long> groupChatInvitationGroupChatMembers) {
        this.groupChatInvitationGroupChatMembers = groupChatInvitationGroupChatMembers;
    }

    public void addGroupChatMember(long id){
        groupChatInvitationGroupChatMembers.add(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(groupChatInvitationChatId);
        parcel.writeString(groupChatInvitationChatName);
        parcel.writeString(groupProfilePicPath);
        parcel.writeLong(groupChatInvitationSenderId);
        parcel.writeList(groupChatInvitationGroupChatMembers);
    }
}

