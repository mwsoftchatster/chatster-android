package nl.mwsoft.www.chatster.modelLayer.network.creatorProfile;


import java.io.IOException;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorContact;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreatorProfileRequestImpl {
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_CREATORS_Q_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    CreatorProfileRequest service = retrofit.create(CreatorProfileRequest.class);

    public CreatorProfileRequestImpl() {

    }

    public CreatorContact getCreatorContactProfile(String creatorName, long userId) {
        Call<CreatorContact> call = service.getCreatorContactProfile(creatorName, userId);
        try {
            CreatorContact response = call.execute().body();

            return response;
        } catch (IOException e) {
            // handle errors
            e.printStackTrace();
        }
        return new CreatorContact(ConstantRegistry.ERROR);
    }
}
