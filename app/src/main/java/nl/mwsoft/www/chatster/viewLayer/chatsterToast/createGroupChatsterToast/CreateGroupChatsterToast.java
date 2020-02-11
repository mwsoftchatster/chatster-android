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
package nl.mwsoft.www.chatster.viewLayer.chatsterToast.createGroupChatsterToast;

import android.content.Context;
import android.widget.Toast;

import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.ChatsterToast;



public class CreateGroupChatsterToast extends ChatsterToast {

    private Context context;
    private Toast toast;


    public CreateGroupChatsterToast() {

    }

    public CreateGroupChatsterToast(Context context, Toast toast) {
        super(context, toast);
        this.context = context;
        this.toast = toast;
    }

    public void notifyUserNoInternet(){
        toast.makeText(context, R.string.no_internet_connection,Toast.LENGTH_LONG).show();
    }

    public void notifyUserGroupHasNoName(){
        toast.makeText(context, R.string.create_group_no_name, Toast.LENGTH_LONG).show();
    }

    public void notifyUserGroupHasNoInvitedMembers(){
        toast.makeText(context, R.string.create_group_no_members, Toast.LENGTH_LONG).show();
    }

}
