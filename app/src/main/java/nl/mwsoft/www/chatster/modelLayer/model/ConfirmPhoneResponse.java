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

public class ConfirmPhoneResponse {

    private long _id;
    private String name;
    private String profilePic;
    private String statusMessage;
    @Nullable
    private ArrayList<Long> chatsterContacts;
    private boolean userAlreadyExists;
    private String status;


    public ConfirmPhoneResponse() {
    }

    public ConfirmPhoneResponse(String status) {
        this.status = status;
    }

    public ConfirmPhoneResponse(long _id, String name, String profilePic, String statusMessage, ArrayList<Long> chatsterContacts, boolean userAlreadyExists, String status) {
        this._id = _id;
        this.name = name;
        this.profilePic = profilePic;
        this.statusMessage = statusMessage;
        this.chatsterContacts = chatsterContacts;
        this.userAlreadyExists = userAlreadyExists;
        this.status = status;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    @Nullable
    public ArrayList<Long> getChatsterContacts() {
        return chatsterContacts;
    }

    public void setChatsterContacts(@Nullable ArrayList<Long> chatsterContacts) {
        this.chatsterContacts = chatsterContacts;
    }

    public boolean isUserAlreadyExists() {
        return userAlreadyExists;
    }

    public void setUserAlreadyExists(boolean userAlreadyExists) {
        this.userAlreadyExists = userAlreadyExists;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
