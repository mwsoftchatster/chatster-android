package nl.mwsoft.www.chatster.modelLayer.network.creatorFollowers;


import java.io.IOException;
import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorContact;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreatorFollowersRequestImpl {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_CREATORS_Q_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    CreatorFollowersRequest service = retrofit.create(CreatorFollowersRequest.class);

    public CreatorFollowersRequestImpl() {
    }

    public ArrayList<CreatorContact> getCreatorFollowers(String creatorName, long userId) {
        Call<ArrayList<CreatorContact>> call = service.getCreatorFollowers(creatorName, userId);
        try {
            ArrayList<CreatorContact> response = call.execute().body();

            return response;
        } catch (IOException e) {
            // handle errors
        }
        return new ArrayList<>();
    }
}
