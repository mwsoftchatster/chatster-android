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
