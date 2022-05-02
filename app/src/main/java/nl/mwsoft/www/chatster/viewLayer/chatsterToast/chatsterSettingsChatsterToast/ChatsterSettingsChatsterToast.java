package nl.mwsoft.www.chatster.viewLayer.chatsterToast.chatsterSettingsChatsterToast;

import android.content.Context;
import android.widget.Toast;

import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.ChatsterToast;



public class ChatsterSettingsChatsterToast extends ChatsterToast {

    private Context context;
    private Toast toast;

    public ChatsterSettingsChatsterToast() {

    }

    public ChatsterSettingsChatsterToast(Context context, Toast toast) {
        super(context, toast);
        this.context = context;
        this.toast = toast;
    }

    public void notifyUserNoInternet(){
        toast.makeText(context, R.string.no_internet_connection,Toast.LENGTH_LONG).show();
    }

    public void notifyUserProfileUpdated(){
        toast.makeText(context, R.string.user_profile_updated,Toast.LENGTH_LONG).show();
    }

}
