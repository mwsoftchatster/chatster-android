package nl.mwsoft.www.chatster.modelLayer.network.registerUserRequest;


import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.model.RegisterUserResponse;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RegisterUserRequest {
    @POST("/createUser")
    Call<RegisterUserResponse> createUser(
            @Query("userId") long userId,
            @Query("userName") String userName,
            @Query("statusMessage") String statusMessage,
            @Query("messagingToken") String messagingToken,
            @Query("contacts[]") ArrayList<Long> contacts,
            @Query("oneTimePreKeyPairPbks") String oneTimePreKeyPairPbks);
}
