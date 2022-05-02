package nl.mwsoft.www.chatster.modelLayer.network.discoverPosts;


import java.io.IOException;
import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorPost;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DiscoverPostsImpl {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_CREATORS_Q_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    DiscoverPosts service = retrofit.create(DiscoverPosts.class);

    public DiscoverPostsImpl() {

    }

    public ArrayList<CreatorPost> discoverPosts(long userId) {
        Call<ArrayList<CreatorPost>> call = service.discoverPosts(userId);
        try {
            ArrayList<CreatorPost> response = call.execute().body();

            return response;
        } catch (IOException e) {
            // handle errors
        }
        return new ArrayList<>();
    }
}
