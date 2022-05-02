package nl.mwsoft.www.chatster.modelLayer.network.checkIfGroupKeysNeeded;

import java.io.IOException;
import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import nl.mwsoft.www.chatster.modelLayer.network.checkPublicKeys.CheckPublicKeys;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CheckIfGroupKeysNeededImpl {
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_GROUP_E2E_Q_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    CheckIfGroupKeysNeeded service = retrofit.create(CheckIfGroupKeysNeeded.class);

    public CheckIfGroupKeysNeededImpl() { }

    public ArrayList<String> checkIfGroupKeysNeeded(String groupChatIds, long userId) {
        ArrayList<String> response = new ArrayList<>();
        Call<ArrayList<String>> call = service.checkIfGroupKeysNeeded(groupChatIds,userId);
        try {
            response = call.execute().body();

            return response;
        } catch (IOException e) {
            // handle errors
        }
        return response;
    }
}
