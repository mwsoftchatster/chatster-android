package nl.mwsoft.www.chatster.modelLayer.network.checkPublicKeys;

import java.io.IOException;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import nl.mwsoft.www.chatster.modelLayer.network.updateToken.UpdateToken;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CheckPublicKeysImpl {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_E2E_Q_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    CheckPublicKeys service = retrofit.create(CheckPublicKeys.class);

    public CheckPublicKeysImpl() { }

    public String checkPublicKeys(long userId) {
        Call<String> call = service.checkPublicKeys(userId);
        try {
            String response = call.execute().body();

            return response;
        } catch (IOException e) {
            // handle errors
        }
        return ConstantRegistry.ERROR;
    }
}
