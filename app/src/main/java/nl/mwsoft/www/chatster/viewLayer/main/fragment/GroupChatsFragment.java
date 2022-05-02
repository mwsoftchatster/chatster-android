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
import nl.mwsoft.www.chatster.presenterLayer.groupChat.GroupChatPresenter;
import nl.mwsoft.www.chatster.viewLayer.groupChat.GroupChatActivity;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChat;
import nl.mwsoft.www.chatster.modelLayer.helper.recyclerView.RecyclerTouchListener;
import nl.mwsoft.www.chatster.modelLayer.helper.recyclerView.RecyclerViewClickListener;
import nl.mwsoft.www.chatster.viewLayer.main.MainActivity;
import nl.mwsoft.www.chatster.viewLayer.main.adapter.GroupChatsAdapter;

public class GroupChatsFragment extends Fragment {

    private GroupChatsAdapter groupChatsAdapter;
    private ArrayList<GroupChat> groupChats;
    private GroupChatPresenter groupChatPresenter;
    private MainActivity mainActivity;
    private RelativeLayout rlNoGroupChats;
    private RecyclerView groupChatsRecyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_chats, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        attachUI(view);
    }

    private void attachUI(View view) {
        rlNoGroupChats = view.findViewById(R.id.rlNoGroupChats);

        groupChatPresenter = DependencyRegistry.shared.injectGroupChatsFragment();

        getAllGroups(view);

        determineNoGroupsIconToBeShown();

        configureRecyclerView(view);
    }

    private void getAllGroups(View view) {
        groupChats = new ArrayList<>();

        if(groupChatPresenter.getAllGroupChats(view.getContext()) != null){
            groupChats.addAll(groupChatPresenter.getAllGroupChats(view.getContext()));
        }
    }

    private void determineNoGroupsIconToBeShown() {
        if(groupChats.size() == 0){
            if(rlNoGroupChats.getVisibility() == View.GONE){
                rlNoGroupChats.setVisibility(View.VISIBLE);
            }
        }else {
            if(rlNoGroupChats.getVisibility() == View.VISIBLE){
                rlNoGroupChats.setVisibility(View.GONE);
            }
        }
    }

    private void configureRecyclerView(View view) {
        groupChatsAdapter = new GroupChatsAdapter(groupChats);
        groupChatsRecyclerView = (RecyclerView)view.findViewById(R.id.rvGroupChats);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        groupChatsRecyclerView.setLayoutManager(mLayoutManager);
        groupChatsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        groupChatsRecyclerView.setAdapter(groupChatsAdapter);

        groupChatsRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(view.getContext(),
                groupChatsRecyclerView, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                openGroupChat(view, position);
            }

            @Override
            public void onLongClick(View view, int position) {
                GroupChat groupChat = groupChats.get(position);
                mainActivity.showDialogDeleteGroupChat(groupChat.get_id(), position,
                        groupChatsAdapter, groupChats, groupChatPresenter);
                        determineNoGroupsIconToBeShown();
            }
        }));
    }

    private void openGroupChat(View view, int position) {
        GroupChat groupChat = groupChats.get(position);
        Intent groupChatIntent = new Intent(view.getContext(), GroupChatActivity.class);
        groupChatIntent.putExtra(ConstantRegistry.GROUP_CHATS_LIST_REQUEST, groupChat);
        groupChatIntent.setAction(ConstantRegistry.GROUP_CHATS_LIST);
        startActivity(groupChatIntent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity)context;
    }
}
