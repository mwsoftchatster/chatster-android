package nl.mwsoft.www.chatster.modelLayer.network.uploadGroupPublicKeys;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UploadGroupPublicKeys {
    @POST("/uploadGroupPublicKeys")
    Call<String> uploadGroupPublicKeys(@Query("oneTimeGroupPreKeyPairPbks") String oneTimeGroupPreKeyPairPbks);
}
