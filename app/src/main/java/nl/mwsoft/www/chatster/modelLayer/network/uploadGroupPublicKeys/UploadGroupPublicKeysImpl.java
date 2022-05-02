package nl.mwsoft.www.chatster.modelLayer.network.uploadGroupPublicKeys;

import java.io.IOException;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import nl.mwsoft.www.chatster.modelLayer.network.uploadPublicKeys.UploadPublicKeys;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadGroupPublicKeysImpl {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_GROUP_E2E_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    UploadGroupPublicKeys service = retrofit.create(UploadGroupPublicKeys.class);

    public UploadGroupPublicKeysImpl() {

    }

    public String uploadGroupPublicKeys(String oneTimePreKeyPairPbks) {

        Call<String> call = service.uploadGroupPublicKeys(oneTimePreKeyPairPbks);
        try {
            String response = call.execute().body();

            return response;
        } catch (IOException e) {
            // handle errors
            e.printStackTrace();
        }
        return "error";
    }
}
