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
package nl.mwsoft.www.chatster.modelLayer.network.confirmPhone;


import java.io.IOException;
import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import nl.mwsoft.www.chatster.modelLayer.model.ConfirmPhoneResponse;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhoneConfirmationImpl {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_API_USER_Q_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    PhoneConfirmation service = retrofit.create(PhoneConfirmation.class);

    public PhoneConfirmationImpl() {
    }

    public ConfirmPhoneResponse getPhoneConfirmationCode(String phoneToVerify, String messagingToken, ArrayList<Long> contacts) {

        Call<ConfirmPhoneResponse> call = service.getConfirmPhoneResponse(phoneToVerify, messagingToken, contacts);
        try {
            ConfirmPhoneResponse responseMessage = call.execute().body();

            return responseMessage;
        } catch (IOException e) {
            // handle errors
            e.printStackTrace();
        }

        return new ConfirmPhoneResponse(ConstantRegistry.ERROR);
    }
}

