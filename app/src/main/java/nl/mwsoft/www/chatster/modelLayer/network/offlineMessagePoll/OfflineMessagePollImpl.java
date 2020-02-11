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
package nl.mwsoft.www.chatster.modelLayer.network.offlineMessagePoll;

import java.io.IOException;
import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.model.OfflineMessageResponse;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OfflineMessagePollImpl {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_CHAT_Q_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    OfflineMessagePoll service = retrofit.create(OfflineMessagePoll.class);

    Retrofit retrofitGroup = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_GROUP_CHAT_Q_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    OfflineMessagePoll serviceGroup = retrofitGroup.create(OfflineMessagePoll.class);

    public OfflineMessagePollImpl() {

    }

    public ArrayList<OfflineMessageResponse> getOfflineMessages(long dstId) {

        Call<ArrayList<OfflineMessageResponse>> call = service.getOfflineMessages(dstId);
        try {
            ArrayList<OfflineMessageResponse> offlineMessages = call.execute().body();
            return offlineMessages;
        } catch (IOException e) {
            // log error to Firebase
        }
        return new ArrayList<>();
    }

    public ArrayList<OfflineMessageResponse> getGroupOfflineMessages(long dstId, ArrayList<String> chatIds) {

        Call<ArrayList<OfflineMessageResponse>> call = serviceGroup.getGroupOfflineMessages(dstId, chatIds);
        try {
            ArrayList<OfflineMessageResponse> offlineMessages = call.execute().body();
            return offlineMessages;
        } catch (IOException e) {
            // log error to Firebase
        }
        return new ArrayList<>();
    }
}
