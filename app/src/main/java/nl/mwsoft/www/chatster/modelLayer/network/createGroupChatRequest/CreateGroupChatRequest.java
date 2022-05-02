package nl.mwsoft.www.chatster.modelLayer.network.createGroupChatRequest;

import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.model.GroupChat;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface CreateGroupChatRequest {
    @POST("/createGroupChat")
    Call<GroupChat> createGroupChat(@Query("adminId") long adminId,
                                    @Query("groupChatId") String groupChatId,
                                    @Query("groupChatName") String groupChatName,
                                    @Query("invitedGroupChatMembers[]")ArrayList<Long> invitedGroupChatMembers);
}
