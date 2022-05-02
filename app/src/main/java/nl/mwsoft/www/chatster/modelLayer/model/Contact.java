package nl.mwsoft.www.chatster.modelLayer.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Contact implements Parcelable {

    private long userId;
    private String userName;
    private String statusMessage;
    private int isChatsterContact;
    private boolean hasBeenSelected;

    public Contact(){

    }

    public Contact(long userId, String userName, String statusMessage, int isChatsterContact, boolean hasBeenSelected) {
        this.userId = userId;
        this.userName = userName;
        this.statusMessage = statusMessage;
        this.isChatsterContact = isChatsterContact;
        this.hasBeenSelected = hasBeenSelected;
    }

    protected Contact(Parcel in) {
        userId = in.readLong();
        userName = in.readString();
        statusMessage = in.readString();
        isChatsterContact = in.readInt();
        hasBeenSelected = in.readByte() != 0;
    }

    public static final Creator<Contact> CREATOR = new Creator<Contact>() {
        @Override
        public Contact createFromParcel(Parcel in) {
            return new Contact(in);
        }

        @Override
        public Contact[] newArray(int size) {
            return new Contact[size];
        }
    };

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public int getIsChatsterContact() {
        return isChatsterContact;
    }

    public void setIsChatsterContact(int isChatsterContact) {
        this.isChatsterContact = isChatsterContact;
    }

    public boolean isHasBeenSelected() {
        return hasBeenSelected;
    }

    public void setHasBeenSelected(boolean hasBeenSelected) {
        this.hasBeenSelected = hasBeenSelected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(userId);
        dest.writeString(userName);
        dest.writeString(statusMessage);
        dest.writeInt(isChatsterContact);
        dest.writeByte((byte) (hasBeenSelected ? 1 : 0));
    }
}

