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
