package nl.mwsoft.www.chatster.modelLayer.network.contactLatest;


import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.model.ContactLatestInformation;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ContactLatestRequest {
    @GET("/contactLatest")
    Call<ArrayList<ContactLatestInformation>> getContactLatest(
            @Query("contacts[]") ArrayList<Long> contacts);
}
