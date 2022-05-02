package nl.mwsoft.www.chatster.modelLayer.network.deleteGroupChatInvitation;

import java.io.IOException;
import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import nl.mwsoft.www.chatster.modelLayer.model.ConfirmPhoneResponse;
import nl.mwsoft.www.chatster.modelLayer.network.confirmPhone.PhoneConfirmation;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DeleteGroupChatInvitationImpl {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_GROUP_CHAT_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    DeleteGroupChatInvitation service = retrofit.create(DeleteGroupChatInvitation.class);

    public DeleteGroupChatInvitationImpl() {

    }

    public String deleteGroupChatInvitation(String groupChatId, long userId) {

        Call<String> call = service.deleteGroupChatInvitation(groupChatId, userId);
        try {
            String responseMessage = call.execute().body();

            return responseMessage;
        } catch (IOException e) {

        }

        return ConstantRegistry.ERROR;
    }
}
