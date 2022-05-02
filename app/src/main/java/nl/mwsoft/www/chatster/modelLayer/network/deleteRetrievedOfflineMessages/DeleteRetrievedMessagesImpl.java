package nl.mwsoft.www.chatster.modelLayer.network.deleteRetrievedOfflineMessages;


import java.io.IOException;
import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import nl.mwsoft.www.chatster.modelLayer.model.DeleteRetrievedMessagesResponse;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DeleteRetrievedMessagesImpl {
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_CHAT_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    DeleteRetrievedMessages service = retrofit.create(DeleteRetrievedMessages.class);

    Retrofit retrofitGroup = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_GROUP_CHAT_Q_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    DeleteRetrievedMessages serviceGroup = retrofitGroup.create(DeleteRetrievedMessages.class);

    public DeleteRetrievedMessagesImpl() {

    }

    public ArrayList<DeleteRetrievedMessagesResponse> deleteRetrievedOfflineMessages(String[] uuids, long userId) {

        Call<ArrayList<DeleteRetrievedMessagesResponse>> call = service.deleteRetrievedMessages(uuids, userId);
        try {
            ArrayList<DeleteRetrievedMessagesResponse> responseMessage = call.execute().body();

            return responseMessage;
        } catch (IOException e) {
            // handle errors
        }
        return new ArrayList<>();
    }

    public ArrayList<DeleteRetrievedMessagesResponse> deleteRetrievedGroupOfflineMessages(String[] uuids, long userId) {

        Call<ArrayList<DeleteRetrievedMessagesResponse>> call = serviceGroup.deleteRetrievedGroupMessages(uuids, userId);
        try {
            ArrayList<DeleteRetrievedMessagesResponse> responseMessage = call.execute().body();

            return responseMessage;
        } catch (IOException e) {
            // handle errors
        }
        return new ArrayList<>();
    }
}
