package nl.mwsoft.www.chatster.viewLayer.groupChat;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.event.chat.SpeechToTextMessageEvent;
import nl.mwsoft.www.chatster.modelLayer.event.groupChat.DeleteGroupChatMessageEvent;
import nl.mwsoft.www.chatster.modelLayer.event.groupChat.ImageGroupChatMessageEvent;
import nl.mwsoft.www.chatster.modelLayer.event.groupChat.TextGroupChatMessageEvent;
import nl.mwsoft.www.chatster.modelLayer.event.groupChat.UnsendGroupChatMessageEvent;
import nl.mwsoft.www.chatster.modelLayer.helper.recyclerView.RecyclerTouchListener;
import nl.mwsoft.www.chatster.modelLayer.helper.recyclerView.RecyclerViewClickListener;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChatMessage;
import nl.mwsoft.www.chatster.modelLayer.model.ImageDetailRequest;

public class GroupChatFragment extends Fragment {

    private GroupChatActivity groupChatActivity;
    @BindView(R.id.rvGroupChatMessages)
    RecyclerView rvGroupChatMessages;
    @BindView(R.id.etGroupChatMessage) EditText etGroupChatMessage;
    @BindView(R.id.ivSendGroupChatMessage) ImageView ivSendGroupChatMessage;
    @BindView(R.id.ivSpeechToTextGroupMessage) ImageView ivSpeechToTextGroupMessage;
    private Unbinder unbinder;
    private ArrayList<GroupChatMessage> groupChatMessages;
    private GroupChatMessageAdapter groupChatMessageAdapter;
    public static final String GROUP_CHAT_MESSAGES = "groupChatMessages";


    public static GroupChatFragment newInstance(ArrayList<GroupChatMessage> groupChatMessages) {

        Bundle args = new Bundle();
        args.putParcelableArrayList(GROUP_CHAT_MESSAGES, groupChatMessages);
        GroupChatFragment fragment = new GroupChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_chat, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle arguments = getArguments();
        if(arguments != null){
            groupChatMessages = arguments.getParcelableArrayList(GROUP_CHAT_MESSAGES);
        }else{
            groupChatMessages = new ArrayList<>();
        }

        configureRecyclerView(view);

        if(groupChatMessages.size() > 0){
            scrollToBottom();
        }
    }

    private void configureRecyclerView(View view) {
        groupChatMessageAdapter = new GroupChatMessageAdapter(groupChatMessages);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        rvGroupChatMessages.setLayoutManager(mLayoutManager);
        rvGroupChatMessages.setItemAnimator(new DefaultItemAnimator());
        rvGroupChatMessages.setAdapter(groupChatMessageAdapter);
        rvGroupChatMessages.addOnItemTouchListener(new RecyclerTouchListener(view.getContext(),
                rvGroupChatMessages, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(groupChatMessages.get(position).getMsgType().equals(ConstantRegistry.IMAGE)){
                    showImageDetails(position);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                GroupChatMessage message = groupChatMessages.get(position);
                showMessageOptions(view, position, message);
            }
        }));
    }

    private void showMessageOptions(View view, int position, GroupChatMessage message) {
        if(message.getMsgType().equals(ConstantRegistry.TEXT)){
            groupChatActivity.showPopupMessageOptions(view, message, position);
        }else{
            groupChatActivity.showPopupImageMessageOptions(view, message, position);
        }
    }

    private void showImageDetails(int position) {
        ImageDetailRequest imageDetailRequest = new ImageDetailRequest();
        imageDetailRequest.setGroupChatId(groupChatMessages.get(position).getGroupChatId());
        imageDetailRequest.setGroupChat(true);
        imageDetailRequest.setImageUri(groupChatMessages.get(position).getBinaryMessageFilePath());
        groupChatActivity.showImageDetails(imageDetailRequest, groupChatMessages.get(position));
    }

    private void scrollToBottom() {
        rvGroupChatMessages.scrollToPosition(groupChatMessageAdapter.getItemCount() - 1);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        groupChatActivity = (GroupChatActivity) context;
    }

    @OnClick(R.id.ivSendGroupChatMessage)
    public void sendMessageClickListener(){
        String message = etGroupChatMessage.getText().toString().trim();
        etGroupChatMessage.setText("");
        groupChatActivity.sendMessageClickListener(message);
    }

    @OnClick(R.id.ivSpeechToTextGroupMessage)
    public void speechToTextMessageClickListener(){
        groupChatActivity.speechToTextGroupMessageClickListener();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TextGroupChatMessageEvent event) {
        groupChatMessages = event.getGroupChatMessages();
        groupChatMessageAdapter.notifyDataSetChanged();
        scrollToBottom();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SpeechToTextMessageEvent event) {
        if(etGroupChatMessage.getText().toString().trim().equals(ConstantRegistry.CHATSTER_EMPTY_STRING)){
            etGroupChatMessage.setText(event.getMessage());
        }else{
            etGroupChatMessage.setText(etGroupChatMessage.getText().toString().concat(Objects.requireNonNull(event.getMessage())));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ImageGroupChatMessageEvent event) {
        groupChatMessages = event.getGroupChatMessages();
        groupChatMessageAdapter.notifyDataSetChanged();
        scrollToBottom();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UnsendGroupChatMessageEvent event) {
        groupChatMessages.get(event.getMessagePosition()).setMsgType(ConstantRegistry.TEXT);
        groupChatMessages.get(event.getMessagePosition()).setBinaryMessageFilePath(Uri.EMPTY);
        groupChatMessages.get(event.getMessagePosition()).setMessageText(event.getMessageText());
        groupChatMessageAdapter.notifyItemChanged(event.getMessagePosition());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DeleteGroupChatMessageEvent event) {
        groupChatMessages = event.getGroupChatMessages();
        groupChatMessageAdapter.notifyDataSetChanged();
        scrollToBottom();
    }
}
