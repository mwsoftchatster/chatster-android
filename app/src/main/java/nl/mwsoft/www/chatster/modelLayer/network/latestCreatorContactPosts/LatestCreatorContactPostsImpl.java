package nl.mwsoft.www.chatster.modelLayer.network.latestCreatorContactPosts;


import java.io.IOException;
import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorPost;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LatestCreatorContactPostsImpl {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_CREATORS_Q_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    LatestCreatorContactsPosts service = retrofit.create(LatestCreatorContactsPosts.class);

    public LatestCreatorContactPostsImpl() {}

    public ArrayList<CreatorPost> getLatestCreatorPosts(long creator, String creatorsName) {
        Call<ArrayList<CreatorPost>> call = service.getLatestCreatorPosts(creator, creatorsName);
        try {
            ArrayList<CreatorPost> response = call.execute().body();

            return response;
        } catch (IOException e) {
            // handle errors
        }
        return new ArrayList<>();
    }
}
