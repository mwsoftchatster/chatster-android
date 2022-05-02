package nl.mwsoft.www.chatster.modelLayer.model.firebase;



public class OfflineMessage {

    private String receiver_id;

    public OfflineMessage() {
    }

    public OfflineMessage(String receiver_id) {
        this.receiver_id = receiver_id;
    }

    public String getReceiver_id() {
        return receiver_id;
    }

    public void setReceiver_id(String receiver_id) {
        this.receiver_id = receiver_id;
    }
}
