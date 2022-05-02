package nl.mwsoft.www.chatster.modelLayer.network.creatorFollowing;


import java.io.IOException;
import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorContact;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreatorFollowingRequestImpl {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_CREATORS_Q_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    CreatorFollowingRequest service = retrofit.create(CreatorFollowingRequest.class);

    public CreatorFollowingRequestImpl() {
    }

    public ArrayList<CreatorContact> getCreatorFollowing(String creatorName, long userId) {
        Call<ArrayList<CreatorContact>> call = service.getCreatorFollowing(creatorName, userId);
        try {
            ArrayList<CreatorContact> response = call.execute().body();

            return response;
        } catch (IOException e) {
            // handle errors
        }
        return new ArrayList<>();
    }
}
