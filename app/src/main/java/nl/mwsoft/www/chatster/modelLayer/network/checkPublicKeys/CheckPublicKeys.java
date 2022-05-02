package nl.mwsoft.www.chatster.modelLayer.network.checkPublicKeys;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CheckPublicKeys {
    @POST("/checkPublicKeys")
    Call<String> checkPublicKeys(@Query("userId") long userId);
}
