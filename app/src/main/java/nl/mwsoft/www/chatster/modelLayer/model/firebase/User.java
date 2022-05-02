package nl.mwsoft.www.chatster.modelLayer.model.firebase;


public class User {

    private String messaging_token;

    public User() {
    }

    public User(String messaging_token) {
        this.messaging_token = messaging_token;
    }

    public String getMessaging_token() {
        return messaging_token;
    }

    public void setMessaging_token(String messaging_token) {
        this.messaging_token = messaging_token;
    }
}
