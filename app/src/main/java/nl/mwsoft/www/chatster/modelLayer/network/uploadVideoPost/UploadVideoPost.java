package nl.mwsoft.www.chatster.modelLayer.network.uploadVideoPost;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface UploadVideoPost {

    @Multipart
    @POST("/uploadVideoPost")
    Call<String> uploadVideoPost(
            @Part MultipartBody.Part video,
            @Query("userName") String userName,
            @Query("postCapture") String postCapture,
            @Query("postType") String postType,
            @Query("creatorProfilePic") String creatorProfilePic,
            @Query("uuid") String uuid);
}
