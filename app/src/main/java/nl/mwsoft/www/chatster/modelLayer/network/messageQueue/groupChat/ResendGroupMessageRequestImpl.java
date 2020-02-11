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
package nl.mwsoft.www.chatster.modelLayer.network.messageQueue.groupChat;


import java.io.IOException;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import nl.mwsoft.www.chatster.modelLayer.model.ResendGroupMessageResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResendGroupMessageRequestImpl {
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_API_USER_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    ResendGroupMessageRequest service = retrofit.create(ResendGroupMessageRequest.class);

    public ResendGroupMessageRequestImpl() {

    }

    public ResendGroupMessageResponse getResendGroupMessageResponse(String messages) {
        Call<ResendGroupMessageResponse> call = service.resendGroupChatMessage(messages);
        try {
            ResendGroupMessageResponse response = call.execute().body();

            return response;
        } catch (IOException e) {
            return new ResendGroupMessageResponse(ConstantRegistry.NULL);
        }
    }

    public ResendGroupMessageResponse getResendGroupImageMessageResponse(MultipartBody.Part body, long senderId,
                                                                         String uuid, String groupChatId,
                                                                         String contentType) {
        Call<ResendGroupMessageResponse> call = service.resendGroupChatImageMessage(body,senderId,uuid,
                groupChatId, contentType);
        try {
            ResendGroupMessageResponse response = call.execute().body();

            return response;
        } catch (IOException e) {
            return new ResendGroupMessageResponse(ConstantRegistry.NULL);
        }
    }
}
