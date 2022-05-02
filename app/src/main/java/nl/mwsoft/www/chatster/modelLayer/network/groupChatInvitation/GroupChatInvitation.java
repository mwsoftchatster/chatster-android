package nl.mwsoft.www.chatster.modelLayer.network.groupChatInvitation;


import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.model.OfflineContactResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GroupChatInvitation {
    @GET("/groupChatInvitations")
    Call<ArrayList<OfflineContactResponse>> getGroupChatInvitations(@Query("dstId") long dstId);
}
