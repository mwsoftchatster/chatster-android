package nl.mwsoft.www.chatster.modelLayer.network.messageQueue.chat;



import java.io.IOException;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import nl.mwsoft.www.chatster.modelLayer.model.ResendMessageResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResendMessageRequestImpl {
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_API_USER_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    ResendMessageRequest service = retrofit.create(ResendMessageRequest.class);

    public ResendMessageRequestImpl() {

    }

    public ResendMessageResponse getResendMessageResponse(String message, long senderId, String senderName,
                                                          long receiverId, String chatName, String uuid,
                                                          String contactPublicKeyUUID, String userPublicKeyUUID,
                                                          String contentType) {
        Call<ResendMessageResponse> call = service.resendMessage(message,senderId,senderName,receiverId,
                chatName,uuid,contactPublicKeyUUID,userPublicKeyUUID,contentType);
        try {
            ResendMessageResponse response = call.execute().body();

            return response;
        } catch (IOException e) {
            return new ResendMessageResponse(ConstantRegistry.NULL);
        }
    }

    public ResendMessageResponse getResendImageMessageResponse(MultipartBody.Part body, long senderId, String senderName,
                                                               long receiverId, String chatName, String uuid,
                                                               String contentType) {
        Call<ResendMessageResponse> call = service.resendImageMessage(body,senderId,senderName,receiverId,
                chatName,uuid,contentType);
        try {
            ResendMessageResponse response = call.execute().body();

            return response;
        } catch (IOException e) {
            return new ResendMessageResponse(ConstantRegistry.NULL);
        }
    }
}
