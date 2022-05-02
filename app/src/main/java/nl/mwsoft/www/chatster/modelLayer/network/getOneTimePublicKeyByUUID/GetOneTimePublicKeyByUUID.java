package nl.mwsoft.www.chatster.modelLayer.network.getOneTimePublicKeyByUUID;

import nl.mwsoft.www.chatster.modelLayer.model.OneTimePublicKey;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetOneTimePublicKeyByUUID {
    @GET("/getOneTimePublicKeyByUUID")
    Call<OneTimePublicKey> getOneTimePublicKeyByUUID(@Query("contactId") long contactId,
                                                     @Query("uuid") String uuid);
}
