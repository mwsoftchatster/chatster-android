package nl.mwsoft.www.chatster.modelLayer.network.creatorFollowing;


import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.model.CreatorContact;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CreatorFollowingRequest {
    @POST("/creatorFollows")
    Call<ArrayList<CreatorContact>> getCreatorFollowing(@Query("creatorName") String creatorName,
                                                        @Query("userId") long userId);
}
