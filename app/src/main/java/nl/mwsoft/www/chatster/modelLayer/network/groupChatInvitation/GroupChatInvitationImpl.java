package nl.mwsoft.www.chatster.modelLayer.network.groupChatInvitation;


import java.io.IOException;
import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.model.OfflineContactResponse;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GroupChatInvitationImpl {


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_GROUP_CHAT_Q_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    GroupChatInvitation service = retrofit.create(GroupChatInvitation.class);

    public GroupChatInvitationImpl() {
    }

    public ArrayList<OfflineContactResponse> getGroupChatInvitations(long dstId) {

        Call<ArrayList<OfflineContactResponse>> call = service.getGroupChatInvitations(dstId);
        try {
            ArrayList<OfflineContactResponse> contactRequests = call.execute().body();

            return contactRequests;
        } catch (IOException e) {
            // send error to Firebase
        }
        return new ArrayList<>();
    }
}

