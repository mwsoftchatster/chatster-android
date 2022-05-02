package nl.mwsoft.www.chatster.modelLayer.network.deleteGroupChatInvitation;


import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface DeleteGroupChatInvitation {
    @POST("/deleteGroupChatInvitation")
    Call<String> deleteGroupChatInvitation(@Query("groupChatId") String groupChatId,
                                           @Query("userId") long userId);
}
