package nl.mwsoft.www.chatster.modelLayer.model;


import java.util.ArrayList;

public class CreateGroupChatRequest {
    private long adminId;
    private String groupChatId;
    private String groupChatName;
    private ArrayList<Long> invitedGroupChatMembers;
    private String groupChatImage;

    public CreateGroupChatRequest() {
    }

    public CreateGroupChatRequest(long adminId, String groupChatId, String groupChatName, ArrayList<Long> invitedGroupChatMembers, String groupChatImage) {
        this.adminId = adminId;
        this.groupChatId = groupChatId;
        this.groupChatName = groupChatName;
        this.invitedGroupChatMembers = invitedGroupChatMembers;
        this.groupChatImage = groupChatImage;
    }

    public long getAdminId() {
        return adminId;
    }

    public void setAdminId(long adminId) {
        this.adminId = adminId;
    }
    
    public String getGroupChatId() {
        return groupChatId;
    }

    public void setGroupChatId(String groupChatId) {
        this.groupChatId = groupChatId;
    }

    public String getGroupChatName() {
        return groupChatName;
    }

    public void setGroupChatName(String groupChatName) {
        this.groupChatName = groupChatName;
    }

    public ArrayList<Long> getInvitedGroupChatMembers() {
        return invitedGroupChatMembers;
    }

    public void setInvitedGroupChatMembers(ArrayList<Long> invitedGroupChatMembers) {
        this.invitedGroupChatMembers = invitedGroupChatMembers;
    }

    public String getGroupChatImage() {
        return groupChatImage;
    }

    public void setGroupChatImage(String groupChatImage) {
        this.groupChatImage = groupChatImage;
    }
}

