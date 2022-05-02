package nl.mwsoft.www.chatster.modelLayer.model;




public class ContactDeleteRequest {

    private long userId;
    private long contactId;

    public ContactDeleteRequest() {
    }

    public ContactDeleteRequest(long userId, long contactId) {
        this.userId = userId;
        this.contactId = contactId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }
}
