package nl.mwsoft.www.chatster.modelLayer.network.uploadVideoPost;

import java.io.IOException;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadVideoPostImpl {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_CREATORS_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    UploadVideoPost service = retrofit.create(UploadVideoPost.class);

    public UploadVideoPostImpl() {
    }

    public String getUploadVideoPostResponse(MultipartBody.Part body, String userName, String postCapture,
                                             String postType, String creatorProfilePic, String uuid) {
        Call<String> call = service.uploadVideoPost(body,userName,postCapture,postType,creatorProfilePic,uuid);
        try {
            String response = call.execute().body();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return ConstantRegistry.ERROR;
        }
    }
}
