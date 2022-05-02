package nl.mwsoft.www.chatster.modelLayer.network.loadMorePosts;

import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.model.CreatorPost;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LoadMorePostsRequest {
    @POST("/loadMorePosts")
    Call<ArrayList<CreatorPost>> loadMorePosts(@Query("creatorsName") String creatorsName,
                                               @Query("lastPostCreated") String lastPostCreated);
}
