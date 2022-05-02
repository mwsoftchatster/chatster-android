package nl.mwsoft.www.chatster.modelLayer.network.creatorPostCommentsRequest;


import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.model.CreatorPostComment;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CreatorPostCommentsRequest {
    @POST("/creatorPostComments")
    Call<ArrayList<CreatorPostComment>> getCreatorPostComments(@Query("postUUID") String postUUID);
}
