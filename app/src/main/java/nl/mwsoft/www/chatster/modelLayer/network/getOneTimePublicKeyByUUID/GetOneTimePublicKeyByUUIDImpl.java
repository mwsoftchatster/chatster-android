package nl.mwsoft.www.chatster.modelLayer.network.getOneTimePublicKeyByUUID;

import java.io.IOException;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import nl.mwsoft.www.chatster.modelLayer.model.OneTimePublicKey;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetOneTimePublicKeyByUUIDImpl {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_API_USER_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    GetOneTimePublicKeyByUUID service = retrofit.create(GetOneTimePublicKeyByUUID.class);

    public GetOneTimePublicKeyByUUIDImpl() {
    }

    public OneTimePublicKey getOneTimePublicKeyByUUID(long contactId, String uuid) {

        Call<OneTimePublicKey> call = service.getOneTimePublicKeyByUUID(contactId, uuid);
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
