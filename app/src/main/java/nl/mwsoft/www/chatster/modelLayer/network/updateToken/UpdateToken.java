package nl.mwsoft.www.chatster.modelLayer.network.updateToken;


import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UpdateToken {
    @POST("/updateUserMessagingToken")
    Call<String> updateUserMessagingToken(@Query("userId") long userId,
            @Query("messagingToken") String messagingToken);
}
