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

