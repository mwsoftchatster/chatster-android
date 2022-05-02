package nl.mwsoft.www.chatster.viewLayer.chatsterToast.permissionsRequestChatsterToast;

import android.content.Context;
import android.widget.Toast;

import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.ChatsterToast;



public class PermissionsRequestChatsterToast extends ChatsterToast {

    private Context context;
    private Toast toast;


    public PermissionsRequestChatsterToast() {

    }

    public PermissionsRequestChatsterToast(Context context, Toast toast) {
        super(context, toast);
        this.context = context;
        this.toast = toast;
    }

    public void notifyUserAllPermissionsGranted(){
        toast.makeText(context, R.string.permissions_done, Toast.LENGTH_SHORT).show();
    }


}
