package nl.mwsoft.www.chatster.viewLayer.main.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.presenterLayer.chat.ChatPresenter;
import nl.mwsoft.www.chatster.viewLayer.chat.ChatActivity;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.model.Chat;
import nl.mwsoft.www.chatster.modelLayer.helper.recyclerView.RecyclerTouchListener;
import nl.mwsoft.www.chatster.modelLayer.helper.recyclerView.RecyclerViewClickListener;
import nl.mwsoft.www.chatster.viewLayer.main.MainActivity;
import nl.mwsoft.www.chatster.viewLayer.main.adapter.ChatsAdapter;

public class ChatsFragment extends Fragment{

    private RecyclerView chatsRecyclerView;
    private ArrayList<Chat> chats;
    private ChatsAdapter chatsAdapter;
    private ChatPresenter chatPresenter;
    private MainActivity mainActivity;
    private RelativeLayout rlNoChats;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rlNoChats = view.findViewById(R.id.rlNoChats);

        chatPresenter = DependencyRegistry.shared.injectChatsFragment();

        readAllChats(view);

        determineNoChatsIconToBeShown();

        configureRecyclerView(view);
    }

    private void readAllChats(View view) {
        chats = new ArrayList<>();
        if(chatPresenter.getAllChats(view.getContext()) != null){
            chats.addAll(chatPresenter.getAllChats(view.getContext()));
        }
    }

    private void configureRecyclerView(View view) {
        chatsAdapter = new ChatsAdapter(chats);
        chatsRecyclerView = (RecyclerView)view.findViewById(R.id.rvChats);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        chatsRecyclerView.setLayoutManager(mLayoutManager);
        chatsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        chatsRecyclerView.setAdapter(chatsAdapter);
        chatsRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(view.getContext(),
                chatsRecyclerView, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                openChat(view, position);
            }

            @Override
            public void onLongClick(View view, int position) {
                Chat chat = chats.get(position);
                mainActivity.showDialogDeleteChat(chat.getChatId(), position, chatPresenter, chats, chatsAdapter);
                determineNoChatsIconToBeShown();
            }
        }));
    }

    private void openChat(View view, int position) {
        Chat chat = chats.get(position);
        Intent chatIntent = new Intent(view.getContext(), ChatActivity.class);
        chatIntent.putExtra(ConstantRegistry.CHAT_REQUEST, chat);
        chatIntent.setAction(ConstantRegistry.CHATS_LIST);
        startActivity(chatIntent);
    }

    private void determineNoChatsIconToBeShown() {
        if(chats.size() == 0){
            rlNoChats.setVisibility(View.VISIBLE);
        }else {
            if(rlNoChats.getVisibility() == View.VISIBLE){
                rlNoChats.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
    }
}
