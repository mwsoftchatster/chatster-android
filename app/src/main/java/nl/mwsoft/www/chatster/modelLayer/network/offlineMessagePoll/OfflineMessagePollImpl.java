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
