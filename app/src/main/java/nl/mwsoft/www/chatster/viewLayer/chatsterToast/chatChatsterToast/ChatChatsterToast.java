/*
  Copyright (C) 2017 - 2020 MWSOFT

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package nl.mwsoft.www.chatster.viewLayer.chatsterToast.chatChatsterToast;

import android.content.Context;
import android.widget.Toast;

import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.viewLayer.chatsterToast.ChatsterToast;


public class ChatChatsterToast extends ChatsterToast {

    private Context context;
    private Toast toast;


    public ChatChatsterToast() {

    }

    public ChatChatsterToast(Context context, Toast toast) {
        super(context, toast);
        this.context = context;
        this.toast = toast;
    }

    public void notifyUserNoInternet(){
        toast.makeText(context, R.string.no_internet_connection,Toast.LENGTH_LONG).show();
    }


    public void notifyUserCantSendEmptyMessage(){
        toast.makeText(context,R.string.empty_msg, Toast.LENGTH_LONG).show();
    }

    public void notifyUserSpeechToTextNotSupported(){
        toast.makeText(context, R.string.speech_not_supported, Toast.LENGTH_LONG).show();
    }

    public void notifyUserMessageCopied(){
        toast.makeText(context, R.string.message_copied, Toast.LENGTH_SHORT).show();
    }

    public void notifyUserCanOnlyUnsendOwnMessages(){
        toast.makeText(context, R.string.unsend_your_messages,Toast.LENGTH_LONG).show();
    }

    public void notifyUserCantUnsendMessages(){
        toast.makeText(context, R.string.cant_unsend_unsent,Toast.LENGTH_LONG).show();
    }

    public void notifyUserCantUnsendMessagesInThisChat(){
        toast.makeText(context, R.string.cant_unsend,Toast.LENGTH_LONG).show();
    }

}
