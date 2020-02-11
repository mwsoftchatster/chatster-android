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


import android.os.Parcel;
import android.os.Parcelable;

public class GroupChatInvitationAcceptedResponse implements Parcelable {

    private int groupChatId;
    private String groupChatName;
    private long groupChatInvitationAcceptedSenderId;

    public GroupChatInvitationAcceptedResponse() {
    }

    public GroupChatInvitationAcceptedResponse(int groupChatId, String groupChatName, long groupChatInvitationAcceptedSenderId) {
        this.groupChatId = groupChatId;
        this.groupChatName = groupChatName;
        this.groupChatInvitationAcceptedSenderId = groupChatInvitationAcceptedSenderId;
    }

    protected GroupChatInvitationAcceptedResponse(Parcel in) {
        groupChatId = in.readInt();
        groupChatName = in.readString();
        groupChatInvitationAcceptedSenderId = in.readLong();
    }

    public static final Creator<GroupChatInvitationAcceptedResponse> CREATOR = new Creator<GroupChatInvitationAcceptedResponse>() {
        @Override
        public GroupChatInvitationAcceptedResponse createFromParcel(Parcel in) {
            return new GroupChatInvitationAcceptedResponse(in);
        }

        @Override
        public GroupChatInvitationAcceptedResponse[] newArray(int size) {
            return new GroupChatInvitationAcceptedResponse[size];
        }
    };

    public int getGroupChatId() {
        return groupChatId;
    }

    public void setGroupChatId(int groupChatId) {
        this.groupChatId = groupChatId;
    }

    public String getGroupChatName() {
        return groupChatName;
    }

    public void setGroupChatName(String groupChatName) {
        this.groupChatName = groupChatName;
    }

    public long getGroupChatInvitationAcceptedSenderId() {
        return groupChatInvitationAcceptedSenderId;
    }

    public void setGroupChatInvitationAcceptedSenderId(long groupChatInvitationAcceptedSenderId) {
        this.groupChatInvitationAcceptedSenderId = groupChatInvitationAcceptedSenderId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(groupChatId);
        dest.writeString(groupChatName);
        dest.writeLong(groupChatInvitationAcceptedSenderId);
    }
}

