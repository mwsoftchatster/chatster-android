package nl.mwsoft.www.chatster.modelLayer.network.getOneTimePublicKey;

import nl.mwsoft.www.chatster.modelLayer.model.OneTimePublicKey;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetOneTimePublicKey {
    @GET("/getOneTimePublicKey")
    Call<OneTimePublicKey> getOneTimePublicKey(@Query("contactId") long contactId);
}
