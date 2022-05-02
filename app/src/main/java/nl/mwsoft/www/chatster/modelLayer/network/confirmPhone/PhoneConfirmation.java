package nl.mwsoft.www.chatster.modelLayer.network.confirmPhone;


import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.model.ConfirmPhoneResponse;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PhoneConfirmation {
    @POST("/verifyPhone")
    Call<ConfirmPhoneResponse> getConfirmPhoneResponse(@Query("phoneToVerify") String phoneToVerify,
                                                       @Query("messagingToken") String messagingToken,
                                                       @Query("contacts[]") ArrayList<Long> contacts);
}
