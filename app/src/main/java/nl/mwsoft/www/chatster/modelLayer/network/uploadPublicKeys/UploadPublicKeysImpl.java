package nl.mwsoft.www.chatster.modelLayer.network.uploadPublicKeys;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadPublicKeysImpl {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_E2E_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    UploadPublicKeys service = retrofit.create(UploadPublicKeys.class);

    public UploadPublicKeysImpl() {

    }

    public String uploadPublicKeys(long userId, String oneTimePreKeyPairPbks) {

        Call<String> call = service.uploadPublicKeys(userId,oneTimePreKeyPairPbks);
        try {
            String response = call.execute().body();

            return response;
        } catch (IOException e) {
            // handle errors
            e.printStackTrace();
        }
        return "error";
    }

    public String uploadReRegisterPublicKeys(long userId, String oneTimePreKeyPairPbks) {

        Call<String> call = service.uploadReRegisterPublicKeys(userId,oneTimePreKeyPairPbks);
        try {
            String response = call.execute().body();

            return response;
        } catch (IOException e) {
            // handle errors
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
