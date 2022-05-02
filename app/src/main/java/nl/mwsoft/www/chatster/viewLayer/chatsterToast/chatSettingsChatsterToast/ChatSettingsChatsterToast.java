package nl.mwsoft.www.chatster.viewLayer.chatsterToast.chatSettingsChatsterToast;

import android.content.Context;
import android.widget.Toast;

import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.ChatsterToast;



public class ChatSettingsChatsterToast extends ChatsterToast {

    private Context context;
    private Toast toast;

    public ChatSettingsChatsterToast() {
    }

    public ChatSettingsChatsterToast(Context context, Toast toast) {
        super(context, toast);
        this.context = context;
        this.toast = toast;
    }

    public void notifyUserUnsendUpdated(){
        toast.makeText(context, R.string.unsend_updated,Toast.LENGTH_LONG).show();
    }

    public void notifyUserNoInternet(){
        toast.makeText(context, R.string.no_internet_connection,Toast.LENGTH_LONG).show();
    }
}
