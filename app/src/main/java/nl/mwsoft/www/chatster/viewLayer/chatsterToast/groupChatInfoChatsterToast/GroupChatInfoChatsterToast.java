package nl.mwsoft.www.chatster.viewLayer.chatsterToast.groupChatInfoChatsterToast;

import android.content.Context;
import android.widget.Toast;

import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.ChatsterToast;


public class GroupChatInfoChatsterToast extends ChatsterToast {

    private Context context;
    private Toast toast;

    public GroupChatInfoChatsterToast() {
    }

    public GroupChatInfoChatsterToast(Context context, Toast toast) {
        super(context, toast);
        this.context = context;
        this.toast = toast;
    }

    public void notifyUserGroupUpdated(){
        toast.makeText(context, R.string.group_updated,Toast.LENGTH_LONG).show();
    }

    public void notifyUserNoInternet(){
        toast.makeText(context, R.string.no_internet_connection,Toast.LENGTH_LONG).show();
    }

    public void notifyUserLeftGroup(){
        toast.makeText(context, R.string.left_group, Toast.LENGTH_LONG).show();
    }

}
