package nl.mwsoft.www.chatster.modelLayer.network.uploadPublicKeys;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UploadPublicKeys {
    @POST("/uploadPublicKeys")
    Call<String> uploadPublicKeys(@Query("userId") long userId,
                                  @Query("oneTimePreKeyPairPbks") String oneTimePreKeyPairPbks
    );

    @POST("/uploadReRegisterPublicKeys")
    Call<String> uploadReRegisterPublicKeys(@Query("userId") long userId,
                                  @Query("oneTimePreKeyPairPbks") String oneTimePreKeyPairPbks
    );
}
