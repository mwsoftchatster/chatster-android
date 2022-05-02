package nl.mwsoft.www.chatster.viewLayer.chatsterToast;


import android.content.Context;
import android.widget.Toast;

import nl.mwsoft.www.chatster.R;

public abstract class ChatsterToast{

    private Context context;
    private Toast toast;

    public ChatsterToast() {
    }

    public ChatsterToast(Context context, Toast toast) {
        this.context = context;
        this.toast = toast;
    }

    public void notifyUserSomethingWentWrong(){
        toast.makeText(context, R.string.smth_went_wrong,Toast.LENGTH_LONG).show();
    }
}
