package nl.mwsoft.www.chatster.modelLayer.network.offlineMessagePoll;


import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.model.OfflineMessageResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OfflineMessagePoll {

    @GET("/chatOfflineMessages")
    Call<ArrayList<OfflineMessageResponse>> getOfflineMessages(@Query("dstId") long dstId);

    @GET("/groupOfflineMessages")
    Call<ArrayList<OfflineMessageResponse>> getGroupOfflineMessages(@Query("dstId") long dstId,
                                                               @Query("chatIds[]") ArrayList<String> chatIds);
}
