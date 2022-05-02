package nl.mwsoft.www.chatster.modelLayer.network.messageQueue.groupChat;


import java.io.IOException;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import nl.mwsoft.www.chatster.modelLayer.model.ResendGroupMessageResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ResendGroupMessageRequestImpl {
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_API_USER_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    ResendGroupMessageRequest service = retrofit.create(ResendGroupMessageRequest.class);

    public ResendGroupMessageRequestImpl() {

    }

    public ResendGroupMessageResponse getResendGroupMessageResponse(String messages,String senderPublicKeyUUID) {
        Call<ResendGroupMessageResponse> call = service.resendGroupChatMessage(messages,senderPublicKeyUUID);
        try {
            ResendGroupMessageResponse response = call.execute().body();

            return response;
        } catch (IOException e) {
            return new ResendGroupMessageResponse(ConstantRegistry.NULL);
        }
    }

    public ResendGroupMessageResponse getResendGroupImageMessageResponse(MultipartBody.Part body, long senderId,
                                                                         String uuid, String groupChatId,
                                                                         String contentType) {
        Call<ResendGroupMessageResponse> call = service.resendGroupChatImageMessage(body,senderId,uuid,
                groupChatId, contentType);
        try {
            ResendGroupMessageResponse response = call.execute().body();

            return response;
        } catch (IOException e) {
            return new ResendGroupMessageResponse(ConstantRegistry.NULL);
        }
    }
}
