package nl.mwsoft.www.chatster.modelLayer.network.getGroupOneTimeKeys;

import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.model.OneTimeGroupPublicKey;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetGroupOneTimeKeys {
    @GET("/getGroupOneTimeKeys")
    Call<ArrayList<OneTimeGroupPublicKey>> getGroupOneTimeKeys(@Query("groupChatId") String groupChatId,
                                                              @Query("userId") long userId);
}
