package nl.mwsoft.www.chatster.modelLayer.network.checkIfGroupKeysNeeded;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CheckIfGroupKeysNeeded {
    @POST("/checkIfGroupKeysNeeded")
    Call<ArrayList<String>> checkIfGroupKeysNeeded(@Query("groupChatIds") String groupChatIds, @Query("userId") long userId);
}
