package nl.mwsoft.www.chatster.modelLayer.network.creatorHistory;

import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.model.HistoryItem;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CreatorHistoryRequest {
    @POST("/creatorHistory")
    Call<ArrayList<HistoryItem>> getCreatorHistory(@Query("creatorName") String creatorName,
                                                   @Query("userId") long userId);
}
