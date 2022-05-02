package nl.mwsoft.www.chatster.modelLayer.model;



public class ContactLatestInformation {
    private long _id;
    private String name;
    private String statusMessage;
    private String profilePic;

    public ContactLatestInformation() {
    }

    public ContactLatestInformation(long _id, String name, String statusMessage, String profilePic) {
        this._id = _id;
        this.name = name;
        this.statusMessage = statusMessage;
        this.profilePic = profilePic;
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

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}

