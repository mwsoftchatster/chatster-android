package nl.mwsoft.www.chatster.modelLayer.network.getOneTimePublicKey;

import java.io.IOException;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import nl.mwsoft.www.chatster.modelLayer.model.OneTimePublicKey;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetOneTimePublicKeyImpl {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_E2E_Q_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    GetOneTimePublicKey service = retrofit.create(GetOneTimePublicKey.class);

    public GetOneTimePublicKeyImpl() {

    }

    public OneTimePublicKey getOneTimePublicKey(long contactId) {

        Call<OneTimePublicKey> call = service.getOneTimePublicKey(contactId);
        try {
            OneTimePublicKey response = call.execute().body();

            return response;
        } catch (IOException e) {
            // handle errors
            e.printStackTrace();
        }

        return new OneTimePublicKey("error");
    }
}
