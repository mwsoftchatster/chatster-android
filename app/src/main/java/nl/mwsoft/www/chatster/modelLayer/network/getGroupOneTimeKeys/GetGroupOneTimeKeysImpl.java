package nl.mwsoft.www.chatster.modelLayer.network.getGroupOneTimeKeys;

import java.io.IOException;
import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import nl.mwsoft.www.chatster.modelLayer.model.OneTimeGroupPublicKey;
import nl.mwsoft.www.chatster.modelLayer.model.OneTimePublicKey;
import nl.mwsoft.www.chatster.modelLayer.network.getOneTimePublicKey.GetOneTimePublicKey;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetGroupOneTimeKeysImpl {
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_GROUP_E2E_Q_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    GetGroupOneTimeKeys service = retrofit.create(GetGroupOneTimeKeys.class);

    public GetGroupOneTimeKeysImpl() {

    }

    public ArrayList<OneTimeGroupPublicKey> getGroupOneTimeKeys(String groupChatId, long userId) {
        ArrayList<OneTimeGroupPublicKey> response = new ArrayList<>();
        Call<ArrayList<OneTimeGroupPublicKey>> call = service.getGroupOneTimeKeys(groupChatId,userId);
        try {
            response = call.execute().body();

            return response;
        } catch (IOException e) {
            // handle errors
            e.printStackTrace();
        }

        return response;
    }
}
