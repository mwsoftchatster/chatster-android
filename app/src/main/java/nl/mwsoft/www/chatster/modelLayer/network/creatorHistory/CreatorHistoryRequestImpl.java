package nl.mwsoft.www.chatster.modelLayer.network.creatorHistory;

import java.io.IOException;
import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorContact;
import nl.mwsoft.www.chatster.modelLayer.model.HistoryItem;
import nl.mwsoft.www.chatster.modelLayer.network.creatorProfile.CreatorProfileRequest;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreatorHistoryRequestImpl {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_CREATORS_Q_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    CreatorHistoryRequest service = retrofit.create(CreatorHistoryRequest.class);

    public CreatorHistoryRequestImpl() {

    }

    public ArrayList<HistoryItem> getCreatorHistory(String creatorName, long userId) {
        Call<ArrayList<HistoryItem>> call = service.getCreatorHistory(creatorName, userId);
        try {
            ArrayList<HistoryItem> response = call.execute().body();

            return response;
        } catch (IOException e) {
            // handle errors
        }
        return new ArrayList<HistoryItem>();
    }
}
