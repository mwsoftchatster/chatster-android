package nl.mwsoft.www.chatster.modelLayer.network.exitGroupChat;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Query;



public interface ExitGroupChatRequest {
    @DELETE("/exitGroupChat")
    Call<String> exitGroupChat(@Query("groupChatId") String groupChatId, @Query("userId") long userId);
}
