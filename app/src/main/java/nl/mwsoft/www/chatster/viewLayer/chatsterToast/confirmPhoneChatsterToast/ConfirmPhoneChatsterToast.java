package nl.mwsoft.www.chatster.viewLayer.chatsterToast.confirmPhoneChatsterToast;

import android.content.Context;
import android.widget.Toast;

import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.ChatsterToast;


public class ConfirmPhoneChatsterToast extends ChatsterToast {

    private Context context;
    private Toast toast;


    public ConfirmPhoneChatsterToast() {

    }

    public ConfirmPhoneChatsterToast(Context context, Toast toast) {
        super(context, toast);
        this.context = context;
        this.toast = toast;
    }

    public void notifyUserNoInternet(){
        toast.makeText(context, R.string.no_internet_connection,Toast.LENGTH_LONG).show();
    }

    public void notifyUserNoPhoneNumber(){
        toast.makeText(context, R.string.enter_your_phone, Toast.LENGTH_LONG).show();
    }

    public void notifyUserIncorrectPhoneNumber(){
        toast.makeText(context, R.string.incorrect_phone,Toast.LENGTH_LONG).show();
    }

    public void notifyUserCountryNotDetermined(){
        toast.makeText(context, R.string.no_country, Toast.LENGTH_LONG).show();
    }

}
