package nl.mwsoft.www.chatster.modelLayer.network.discoverPosts;


import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.model.CreatorPost;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface DiscoverPosts {
    @POST("/discoverPosts")
    Call<ArrayList<CreatorPost>> discoverPosts(@Query("userId") long userId);
}
