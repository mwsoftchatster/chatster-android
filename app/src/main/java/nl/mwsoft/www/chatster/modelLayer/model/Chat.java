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
import androidx.annotation.Nullable;

public class Chat implements Parcelable {

    private int chatId;
    private String chatName;
    private long contactId;
    @Nullable
    private String lastActivityMessage;
    @Nullable
    private String lastActivityDate;

    public Chat(){
    }

    public Chat(int chatId, String chatName, long contactId, String lastActivityMessage, String lastActivityDate) {
        this.chatId = chatId;
        this.chatName = chatName;
        this.contactId = contactId;
        this.lastActivityMessage = lastActivityMessage;
        this.lastActivityDate = lastActivityDate;
    }

    protected Chat(Parcel in) {
        chatId = in.readInt();
        chatName = in.readString();
        contactId = in.readLong();
        lastActivityMessage = in.readString();
        lastActivityDate = in.readString();
    }

    public static final Creator<Chat> CREATOR = new Creator<Chat>() {
        @Override
        public Chat createFromParcel(Parcel in) {
            return new Chat(in);
        }

        @Override
        public Chat[] newArray(int size) {
            return new Chat[size];
        }
    };

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }

    @Nullable
    public String getLastActivityMessage() {
        return lastActivityMessage;
    }

    public void setLastActivityMessage(@Nullable String lastActivityMessage) {
        this.lastActivityMessage = lastActivityMessage;
    }

    @Nullable
    public String getLastActivityDate() {
        return lastActivityDate;
    }

    public void setLastActivityDate(@Nullable String lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(chatId);
        dest.writeString(chatName);
        dest.writeLong(contactId);
        dest.writeString(lastActivityMessage);
        dest.writeString(lastActivityDate);
    }
}
