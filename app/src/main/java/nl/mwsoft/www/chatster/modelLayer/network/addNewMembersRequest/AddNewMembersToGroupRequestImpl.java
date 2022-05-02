package nl.mwsoft.www.chatster.modelLayer.network.addNewMembersRequest;

import java.io.IOException;
import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AddNewMembersToGroupRequestImpl {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_GROUP_CHAT_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    AddNewMembersToGroupRequest service = retrofit.create(AddNewMembersToGroupRequest.class);

    public AddNewMembersToGroupRequestImpl() {

    }

    public String getAddNewMembersToGroupResponse(String groupChatId,
                                                  long groupChatAdmin,
                                                  String groupChatName,
                                                  ArrayList<Long> newMembers,
                                                  String groupChatPicPath) {

        Call<String> call = service.addNewMembersToGroupChat(groupChatId,groupChatAdmin,groupChatName,newMembers,groupChatPicPath);
        try {
            String response = call.execute().body();

            return response;
        } catch (IOException e) {
            // handle errors
        }
        return ConstantRegistry.CHATSTER_EMPTY_STRING;
    }
}
