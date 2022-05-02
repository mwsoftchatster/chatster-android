package nl.mwsoft.www.chatster.modelLayer.network.confirmPhone;


import java.io.IOException;
import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import nl.mwsoft.www.chatster.modelLayer.model.ConfirmPhoneResponse;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PhoneConfirmationImpl {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_API_USER_Q_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    PhoneConfirmation service = retrofit.create(PhoneConfirmation.class);

    public PhoneConfirmationImpl() {
    }

    public ConfirmPhoneResponse getPhoneConfirmationCode(String phoneToVerify, String messagingToken, ArrayList<Long> contacts) {

        Call<ConfirmPhoneResponse> call = service.getConfirmPhoneResponse(phoneToVerify, messagingToken, contacts);
        try {
            ConfirmPhoneResponse responseMessage = call.execute().body();

            return responseMessage;
        } catch (IOException e) {
            // handle errors
            e.printStackTrace();
        }

        return new ConfirmPhoneResponse(ConstantRegistry.ERROR);
    }
}

