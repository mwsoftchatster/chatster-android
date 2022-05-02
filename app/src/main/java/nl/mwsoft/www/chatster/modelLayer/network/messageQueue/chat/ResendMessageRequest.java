package nl.mwsoft.www.chatster.modelLayer.network.messageQueue.chat;



import nl.mwsoft.www.chatster.modelLayer.model.ResendMessageResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ResendMessageRequest {
    @POST("/resendChatMessage")
    Call<ResendMessageResponse> resendMessage(
            @Query("message") String message,
            @Query("senderId") long senderId,
            @Query("senderName") String senderName,
            @Query("receiverId") long receiverId,
            @Query("chatName") String chatName,
            @Query("uuid") String uuid,
            @Query("contactPublicKeyUUID") String contactPublicKeyUUID,
            @Query("userPublicKeyUUID") String userPublicKeyUUID,
            @Query("contentType") String contentType);

    @Multipart
    @POST("/resendChatMessage")
    Call<ResendMessageResponse> resendImageMessage(
            @Part MultipartBody.Part image,
            @Query("senderId") long senderId,
            @Query("senderName") String senderName,
            @Query("receiverId") long receiverId,
            @Query("chatName") String chatName,
            @Query("uuid") String uuid,
            @Query("contentType") String contentType);
}
