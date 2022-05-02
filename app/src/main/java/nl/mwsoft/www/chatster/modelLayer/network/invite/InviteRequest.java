package nl.mwsoft.www.chatster.modelLayer.network.invite;


import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface InviteRequest {
    @POST("/inviteUser")
    Call<String> inviteUser(
            @Query("userName") String userName,
            @Query("inviteeName") String inviteeName,
            @Query("inviteeEmail") String inviteeEmail);
}
