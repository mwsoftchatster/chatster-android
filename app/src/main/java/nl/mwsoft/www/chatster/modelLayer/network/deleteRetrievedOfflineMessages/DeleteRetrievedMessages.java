package nl.mwsoft.www.chatster.modelLayer.network.deleteRetrievedOfflineMessages;


import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.model.DeleteRetrievedMessagesResponse;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;



public interface DeleteRetrievedMessages {
    @POST("/deleteRetrievedMessages")
    Call<ArrayList<DeleteRetrievedMessagesResponse>> deleteRetrievedMessages(@Query("uuids") String[] uuids,
                                                                             @Query("dstId") long userId);

    @POST("/deleteRetrievedGroupMessages")
    Call<ArrayList<DeleteRetrievedMessagesResponse>> deleteRetrievedGroupMessages(@Query("uuids") String[] uuids,
                                                                             @Query("dstId") long userId);
}


