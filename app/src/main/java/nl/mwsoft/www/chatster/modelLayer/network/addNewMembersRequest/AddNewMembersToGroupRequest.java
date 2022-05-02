package nl.mwsoft.www.chatster.modelLayer.network.addNewMembersRequest;


import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AddNewMembersToGroupRequest {
    @POST("/addNewMembersToGroupChat")
    Call<String> addNewMembersToGroupChat(@Query("groupChatId") String groupChatId,
                                          @Query("groupChatAdmin") long groupChatAdmin,
                                          @Query("groupChatName") String groupChatName,
                                          @Query("newMembers[]")ArrayList<Long> newMembers,
                                          @Query("groupChatPicPath") String groupChatPicPath
    );
}
