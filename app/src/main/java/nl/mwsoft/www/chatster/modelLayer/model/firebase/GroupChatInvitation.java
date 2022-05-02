package nl.mwsoft.www.chatster.modelLayer.model.firebase;


import java.util.List;

public class GroupChatInvitation {

    private List<String> receiver_ids;

    public GroupChatInvitation() {
    }

    public GroupChatInvitation(List<String> receiver_ids) {
        this.receiver_ids = receiver_ids;
    }

    public List<String> getReceiver_ids() {
        return receiver_ids;
    }

    public void setReceiver_ids(List<String> receiver_ids) {
        this.receiver_ids = receiver_ids;
    }
}
