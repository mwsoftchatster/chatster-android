package nl.mwsoft.www.chatster.modelLayer.network.exitGroupChat;


import java.io.IOException;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ExitGroupChatRequestImpl {


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_GROUP_CHAT_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    ExitGroupChatRequest service = retrofit.create(ExitGroupChatRequest.class);

    public ExitGroupChatRequestImpl() {

    }

    public String getExitGroupChatResponse(String groupChatId, long userId) {

        Call<String> call = service.exitGroupChat(groupChatId, userId);
        try {
            String responseMessage = call.execute().body();

            return responseMessage;
        } catch (IOException e) {
            // handle errors
        }
        return ConstantRegistry.CHATSTER_EMPTY_STRING;
    }
}

