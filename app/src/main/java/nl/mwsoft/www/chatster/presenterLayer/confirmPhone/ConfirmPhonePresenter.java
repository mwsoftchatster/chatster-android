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
package nl.mwsoft.www.chatster.presenterLayer.confirmPhone;



import android.content.Context;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.model.ConfirmPhoneResponse;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.contact.ContactModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.user.UserModelLayerManager;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.util.UtilModelLayerManager;

public class ConfirmPhonePresenter {

    private UserModelLayerManager userModelLayerManager;
    private ContactModelLayerManager contactModelLayerManager;
    private UtilModelLayerManager utilModelLayerManager;

    public ConfirmPhonePresenter() {
    }

    public ConfirmPhonePresenter(UserModelLayerManager userModelLayerManager,
                                 ContactModelLayerManager contactModelLayerManager,
                                 UtilModelLayerManager utilModelLayerManager) {
        this.userModelLayerManager = userModelLayerManager;
        this.contactModelLayerManager = contactModelLayerManager;
        this.utilModelLayerManager = utilModelLayerManager;
    }

    // region DB

    public void updateUserId(long phoneToVerify, Context context){
        this.userModelLayerManager.updateUserId(phoneToVerify, context);
    }

    public void updateUser(ConfirmPhoneResponse result, Context context) {
        this.userModelLayerManager.updateUser(result, context);
    }

    public void updateContacts(ArrayList<Long> chatsterContacts, Context context){
        this.contactModelLayerManager.updateContacts(chatsterContacts, context);
    }

    public ArrayList<Long> getAllContactIds(Context context){
        return this.contactModelLayerManager.getAllContactIds(context);
    }

    // endregion

    // region Confirm Phone Number

    public Observable<ConfirmPhoneResponse> confirmPhoneNumber(String phoneToVerify, String messagingToken,ArrayList<Long> contacts, Context context){
        return this.userModelLayerManager.confirmPhoneNumber(phoneToVerify, messagingToken, contacts, context);
    }

    // endregion

    // region Insert Contacts From Phone Contacts List

    public Observable<String> insertContactsFromPhone(Context context){
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                try {
                    String result = contactModelLayerManager.insertContacts(context);
                    emitter.onNext(result);
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });
    }

    // endregion

    // region process provided phone number

    public String removeFirstChar(String s){
        return s.substring(1);
    }

    public String processProvidedPhoneNumber(String phoneNo){
        if(phoneNo.contains(ConstantRegistry.CHATSTER_OPEN_ROUND_BRACKETS)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_OPEN_ROUND_BRACKETS, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }

        if(phoneNo.contains(ConstantRegistry.CHATSTER_CLOSE_ROUND_BRACKETS)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_CLOSE_ROUND_BRACKETS, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }

        if(phoneNo.contains(ConstantRegistry.CHATSTER_FORWARD_SLASH)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_FORWARD_SLASH, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }

        if(phoneNo.contains(ConstantRegistry.CHATSTER_LETTER_N)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_LETTER_N, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }

        if(phoneNo.contains(ConstantRegistry.CHATSTER_COMMA)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_COMMA, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }

        if(phoneNo.contains(ConstantRegistry.CHATSTER_POINT)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_POINT, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }

        if(phoneNo.contains(ConstantRegistry.CHATSTER_STAR)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_STAR, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }

        if(phoneNo.contains(ConstantRegistry.CHATSTER_SEMICOLON)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_SEMICOLON, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }

        if(phoneNo.contains(ConstantRegistry.CHATSTER_HASH_TAG)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_HASH_TAG, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }

        if(phoneNo.contains(ConstantRegistry.CHATSTER_MINUS)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_MINUS, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }

        if(phoneNo.contains(ConstantRegistry.CHATSTER_PLUS)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_PLUS, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }

        if(phoneNo.contains(ConstantRegistry.CHATSTER_SPACE_STRING)){
            phoneNo = phoneNo.replace(ConstantRegistry.CHATSTER_SPACE_STRING, ConstantRegistry.CHATSTER_EMPTY_STRING);
        }

        if(phoneNo.length() > 0){
            if(phoneNo.startsWith(ConstantRegistry.CHATSTER_ZERO)){
                phoneNo = removeFirstChar(phoneNo);
            }
        }

        return phoneNo;
    }

    // endregion

    // region Util

    public boolean hasInternetConnection(){
        return utilModelLayerManager.hasInternetConnection();
    }

    // endregion

}
