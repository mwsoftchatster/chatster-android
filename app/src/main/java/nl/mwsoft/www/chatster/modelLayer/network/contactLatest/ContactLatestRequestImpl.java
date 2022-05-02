package nl.mwsoft.www.chatster.modelLayer.network.contactLatest;



import java.io.IOException;
import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.model.ContactLatestInformation;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ContactLatestRequestImpl {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_API_USER_Q_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    ContactLatestRequest service = retrofit.create(ContactLatestRequest.class);

    public ContactLatestRequestImpl() {

    }

    public ArrayList<ContactLatestInformation> getContactLatestInformation(ArrayList<Long> contacts) {
        Call<ArrayList<ContactLatestInformation>> call = service.getContactLatest(contacts);
        try {
            ArrayList<ContactLatestInformation> response = call.execute().body();
            return response;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}

