package nl.mwsoft.www.chatster.modelLayer.network.latestCreatorContactPosts;


import java.util.ArrayList;


import nl.mwsoft.www.chatster.modelLayer.model.CreatorPost;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LatestCreatorContactsPosts {
    @POST("/latestPosts")
    Call<ArrayList<CreatorPost>> getLatestCreatorPosts(@Query("creator") long creator,
                                                       @Query("creatorsName") String creatorsName);
}
