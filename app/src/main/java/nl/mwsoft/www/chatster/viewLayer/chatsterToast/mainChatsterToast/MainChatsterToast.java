package nl.mwsoft.www.chatster.viewLayer.chatsterToast.mainChatsterToast;

import android.content.Context;
import android.widget.Toast;

import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.ChatsterToast;


public class MainChatsterToast extends ChatsterToast {

    private Context context;
    private Toast toast;

    public MainChatsterToast() {

    }

    public MainChatsterToast(Context context, Toast toast) {
        super(context, toast);
        this.context = context;
        this.toast = toast;
    }

    public void notifyUserNoInternet(){
        toast.makeText(context, R.string.no_internet_connection,Toast.LENGTH_LONG).show();
    }
}
