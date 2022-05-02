package nl.mwsoft.www.chatster.modelLayer.network.creatorPostCommentsRequest;


import java.io.IOException;
import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorPostComment;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreatorPostCommentsRequestImpl {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_CREATORS_Q_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    CreatorPostCommentsRequest service = retrofit.create(CreatorPostCommentsRequest.class);

    public CreatorPostCommentsRequestImpl() {

    }

    public ArrayList<CreatorPostComment> getCreatorPostComments(String postUUID) {
        Call<ArrayList<CreatorPostComment>> call = service.getCreatorPostComments(postUUID);
        try {
            ArrayList<CreatorPostComment> response = call.execute().body();

            return response;
        } catch (IOException e) {
            // handle errors
        }
        return new ArrayList<>();
    }
}
