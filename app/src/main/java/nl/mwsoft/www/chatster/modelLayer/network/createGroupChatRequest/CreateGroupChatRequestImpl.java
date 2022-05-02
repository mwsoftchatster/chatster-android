package nl.mwsoft.www.chatster.modelLayer.network.createGroupChatRequest;


import java.io.IOException;
import java.util.ArrayList;

import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChat;
import nl.mwsoft.www.chatster.modelLayer.helper.okHttpClientManager.OkHttpClientManager;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateGroupChatRequestImpl {


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ConstantRegistry.BASE_SERVER_URL.concat(ConstantRegistry.CHATSTER_GROUP_CHAT_PORT))
            .client(OkHttpClientManager.setUpSecureClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    CreateGroupChatRequest service = retrofit.create(CreateGroupChatRequest.class);

    public CreateGroupChatRequestImpl() {

    }

    public GroupChat getCreateGroupChatResponse(long adminId,
                                                String groupChatId,
                                                String groupChatName,
                                                ArrayList<Long> invitedGroupChatMembers) {

        Call<GroupChat> call = service.createGroupChat(adminId,groupChatId,groupChatName,invitedGroupChatMembers);
        try {
            GroupChat response = call.execute().body();

            return response;
        } catch (IOException e) {
            // handle errors
        }
        return new GroupChat();
    }
}

