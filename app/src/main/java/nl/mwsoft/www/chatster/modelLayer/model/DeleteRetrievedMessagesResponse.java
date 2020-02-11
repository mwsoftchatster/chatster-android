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

public class DeleteRetrievedMessagesResponse {

    private String returnType;
    private String msgType;
    private String contentType;
    private long senderId;
    @Nullable
    private String senderName;
    @Nullable
    private String chatname;
    @Nullable
    private String groupChatId;
    private String messageText;
    private String uuid;
    private String contactPublicKeyUUID;
    private String messageCreated;

    public DeleteRetrievedMessagesResponse(String returnType, String msgType, String contentType, long senderId, String senderName, String chatname, String groupChatId, String messageText, String uuid, String contactPublicKeyUUID, String messageCreated) {
        this.returnType = returnType;
        this.msgType = msgType;
        this.contentType = contentType;
        this.senderId = senderId;
        this.senderName = senderName;
        this.chatname = chatname;
        this.groupChatId = groupChatId;
        this.messageText = messageText;
        this.uuid = uuid;
        this.contactPublicKeyUUID = contactPublicKeyUUID;
        this.messageCreated = messageCreated;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
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
    public String getChatname() {
        return chatname;
    }

    public void setChatname(@Nullable String chatname) {
        this.chatname = chatname;
    }

    @Nullable
    public String getGroupChatId() {
        return groupChatId;
    }

    public void setGroupChatId(@Nullable String groupChatId) {
        this.groupChatId = groupChatId;
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
