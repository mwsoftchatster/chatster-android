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


import androidx.annotation.Nullable;

import java.util.ArrayList;

public class OfflineContactResponse {

    private String msgType;
    @Nullable
    private long userId;
    @Nullable
    private String userName;
    @Nullable
    private String statusMessage;
    @Nullable
    private String requestMessage;
    @Nullable
    private int contactRequestId;
    @Nullable
    private int reqId;
    @Nullable
    private String groupChatInvitationChatId;
    @Nullable
    private long groupChatInvitationSenderId;
    @Nullable
    private String groupChatInvitationChatName;
    @Nullable
    private ArrayList<Long> groupChatInvitationGroupChatMembers;
    @Nullable
    private String groupProfilePicPath;

    public OfflineContactResponse() {
    }

    public OfflineContactResponse(String msgType, long userId, String userName, String statusMessage, String requestMessage, int contactRequestId, int reqId, String groupChatInvitationChatId, long groupChatInvitationSenderId, String groupChatInvitationChatName, ArrayList<Long> groupChatInvitationGroupChatMembers, String groupProfilePicPath) {
        this.msgType = msgType;
        this.userId = userId;
        this.userName = userName;
        this.statusMessage = statusMessage;
        this.requestMessage = requestMessage;
        this.contactRequestId = contactRequestId;
        this.reqId = reqId;
        this.groupChatInvitationChatId = groupChatInvitationChatId;
        this.groupChatInvitationSenderId = groupChatInvitationSenderId;
        this.groupChatInvitationChatName = groupChatInvitationChatName;
        this.groupChatInvitationGroupChatMembers = groupChatInvitationGroupChatMembers;
        this.groupProfilePicPath = groupProfilePicPath;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    @Nullable
    public long getUserId() {
        return userId;
    }

    public void setUserId(@Nullable long userId) {
        this.userId = userId;
    }

    @Nullable
    public String getUserName() {
        return userName;
    }

    public void setUserName(@Nullable String userName) {
        this.userName = userName;
    }

    @Nullable
    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(@Nullable String statusMessage) {
        this.statusMessage = statusMessage;
    }

    @Nullable
    public String getRequestMessage() {
        return requestMessage;
    }

    public void setRequestMessage(@Nullable String requestMessage) {
        this.requestMessage = requestMessage;
    }

    @Nullable
    public int getContactRequestId() {
        return contactRequestId;
    }

    public void setContactRequestId(@Nullable int contactRequestId) {
        this.contactRequestId = contactRequestId;
    }

    @Nullable
    public int getReqId() {
        return reqId;
    }

    public void setReqId(@Nullable int reqId) {
        this.reqId = reqId;
    }

    @Nullable
    public String getGroupChatInvitationChatId() {
        return groupChatInvitationChatId;
    }

    public void setGroupChatInvitationChatId(@Nullable String groupChatInvitationChatId) {
        this.groupChatInvitationChatId = groupChatInvitationChatId;
    }

    @Nullable
    public long getGroupChatInvitationSenderId() {
        return groupChatInvitationSenderId;
    }

    public void setGroupChatInvitationSenderId(@Nullable long groupChatInvitationSenderId) {
        this.groupChatInvitationSenderId = groupChatInvitationSenderId;
    }

    @Nullable
    public String getGroupChatInvitationChatName() {
        return groupChatInvitationChatName;
    }

    public void setGroupChatInvitationChatName(@Nullable String groupChatInvitationChatName) {
        this.groupChatInvitationChatName = groupChatInvitationChatName;
    }

    @Nullable
    public ArrayList<Long> getGroupChatInvitationGroupChatMembers() {
        return groupChatInvitationGroupChatMembers;
    }

    public void setGroupChatInvitationGroupChatMembers(@Nullable ArrayList<Long> groupChatInvitationGroupChatMembers) {
        this.groupChatInvitationGroupChatMembers = groupChatInvitationGroupChatMembers;
    }

    @Nullable
    public String getGroupProfilePicPath() {
        return groupProfilePicPath;
    }

    public void setGroupProfilePicPath(@Nullable String groupProfilePicPath) {
        this.groupProfilePicPath = groupProfilePicPath;
    }
}

