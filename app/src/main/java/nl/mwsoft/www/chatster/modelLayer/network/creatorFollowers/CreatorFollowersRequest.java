package nl.mwsoft.www.chatster.modelLayer.network.creatorFollowers;


import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.model.CreatorContact;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CreatorFollowersRequest {
    @POST("/creatorFollowers")
    Call<ArrayList<CreatorContact>> getCreatorFollowers(@Query("creatorName") String creatorName,
                                                        @Query("userId") long userId);
}
