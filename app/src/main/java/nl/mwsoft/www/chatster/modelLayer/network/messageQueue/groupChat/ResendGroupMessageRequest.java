package nl.mwsoft.www.chatster.modelLayer.network.messageQueue.groupChat;


import nl.mwsoft.www.chatster.modelLayer.model.ResendGroupMessageResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ResendGroupMessageRequest {

    @POST("/resendGroupChatMessage")
    Call<ResendGroupMessageResponse> resendGroupChatMessage(
            @Query("messages") String messages,
            @Query("senderPublicKeyUUID") String senderPublicKeyUUID);

    @Multipart
    @POST("/resendGroupChatMessage")
    Call<ResendGroupMessageResponse> resendGroupChatImageMessage(
            @Part MultipartBody.Part image,
            @Query("senderId") long senderId,
            @Query("uuid") String uuid,
            @Query("groupChatId") String groupChatId,
            @Query("contentType") String contentType);
}
