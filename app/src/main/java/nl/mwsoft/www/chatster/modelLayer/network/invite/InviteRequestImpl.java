package nl.mwsoft.www.chatster.modelLayer.network.invite;

import java.io.IOException;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InviteRequestImpl {
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_API_USER_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    InviteRequest service = retrofit.create(InviteRequest.class);

    public InviteRequestImpl() {

    }

    public String getInviteUserResponse(String userName, String inviteeName, String inviteeEmail) {
        Call<String> call = service.inviteUser(userName,inviteeName,inviteeEmail);
        try {
            String response = call.execute().body();

            return response;
        } catch (IOException e) {
            // handle errors
        }
        return ConstantRegistry.ERROR;
    }
}
