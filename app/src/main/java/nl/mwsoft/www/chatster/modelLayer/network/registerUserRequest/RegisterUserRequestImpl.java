package nl.mwsoft.www.chatster.modelLayer.network.registerUserRequest;


import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.model.RegisterUserResponse;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterUserRequestImpl {


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_API_USER_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    RegisterUserRequest service = retrofit.create(RegisterUserRequest.class);

    public RegisterUserRequestImpl() {

    }

    public RegisterUserResponse getRegisterUserResponse(long userId,
                                                        String userName,
                                                        String statusMessage,
                                                        String messagingToken,
                                                        ArrayList<Long> contacts,
                                                        String oneTimePreKeyPairPbks) {
        Call<RegisterUserResponse> call = service.createUser(userId,userName,statusMessage,messagingToken,contacts,oneTimePreKeyPairPbks);
        try {
            RegisterUserResponse response = call.execute().body();

            return response;
        } catch (IOException e) {
            // handle errors
            e.printStackTrace();
        }
        return new RegisterUserResponse(ConstantRegistry.ERROR);
    }
}

