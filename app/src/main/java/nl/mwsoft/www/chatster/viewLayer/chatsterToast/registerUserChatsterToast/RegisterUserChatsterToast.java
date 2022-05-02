package nl.mwsoft.www.chatster.viewLayer.chatsterToast.registerUserChatsterToast;

import android.content.Context;
import android.widget.Toast;

import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.ChatsterToast;


public class RegisterUserChatsterToast extends ChatsterToast {

    private Context context;
    private Toast toast;


    public RegisterUserChatsterToast() {
    }

    public RegisterUserChatsterToast(Context context, Toast toast) {
        super(context, toast);
        this.context = context;
        this.toast = toast;
    }

    public void notifyUserResult(String res) {
        toast.makeText(this.context, res, Toast.LENGTH_LONG).show();
    }

    public void notifyUserNoInternet(){
        toast.makeText(context, R.string.no_internet_connection,Toast.LENGTH_LONG).show();
    }

    public void notifyUserNameCantBeEmpty(){
        toast.makeText(context, R.string.empty_username, Toast.LENGTH_LONG).show();
    }
}
