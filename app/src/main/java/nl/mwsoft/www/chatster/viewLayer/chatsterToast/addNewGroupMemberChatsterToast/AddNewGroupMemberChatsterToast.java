package nl.mwsoft.www.chatster.viewLayer.chatsterToast.addNewGroupMemberChatsterToast;


import android.content.Context;
import android.widget.Toast;

import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.ChatsterToast;

public class AddNewGroupMemberChatsterToast extends ChatsterToast{

    private Context context;
    private Toast toast;

    public AddNewGroupMemberChatsterToast() {
    }

    public AddNewGroupMemberChatsterToast(Context context, Toast toast) {
        super(context, toast);
        this.context = context;
        this.toast = toast;
    }
    public void notifyUserNoInternet(){
        toast.makeText(context, R.string.no_internet_connection,Toast.LENGTH_LONG).show();
    }


    public void notifyUserResult(String res) {
        toast.makeText(this.context, res, Toast.LENGTH_LONG).show();
    }
}
