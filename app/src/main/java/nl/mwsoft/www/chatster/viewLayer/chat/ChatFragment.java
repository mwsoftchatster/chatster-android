package nl.mwsoft.www.chatster.viewLayer.chat;


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
import android.widget.RelativeLayout;


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
import nl.mwsoft.www.chatster.modelLayer.event.chat.DeleteMessageEvent;
import nl.mwsoft.www.chatster.modelLayer.event.chat.ImageMessageEvent;
import nl.mwsoft.www.chatster.modelLayer.event.chat.TextMessageEvent;
import nl.mwsoft.www.chatster.modelLayer.event.chat.SpeechToTextMessageEvent;
import nl.mwsoft.www.chatster.modelLayer.event.chat.UnsendMessageEvent;
import nl.mwsoft.www.chatster.modelLayer.helper.recyclerView.RecyclerTouchListener;
import nl.mwsoft.www.chatster.modelLayer.helper.recyclerView.RecyclerViewClickListener;
import nl.mwsoft.www.chatster.modelLayer.model.ImageDetailRequest;
import nl.mwsoft.www.chatster.modelLayer.model.Message;


public class ChatFragment extends Fragment {

    private ChatActivity chatActivity;
    @BindView(R.id.rvChat)
    RecyclerView rvChat;
    @BindView(R.id.etMessage)
    EditText etMessage;
    @BindView(R.id.ivSendMessage)
    ImageView ivSendMessage;
    @BindView(R.id.ivSpeechToTextMessage)
    ImageView ivSpeechToTextMessage;
    @BindView(R.id.rlMainChat)
    RelativeLayout rlMainChat;
    private Unbinder unbinder;
    private ArrayList<Message> messages;
    private ChatMessageAdapter chatMessageAdapter;
    public static final String MESSAGES = "messages";


    public static ChatFragment newInstance(ArrayList<Message> messages) {
        
        Bundle args = new Bundle();
        args.putParcelableArrayList(MESSAGES, messages);
        ChatFragment fragment = new ChatFragment();
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
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
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
            messages = arguments.getParcelableArrayList(MESSAGES);
        }else{
            messages = new ArrayList<>();
        }

        chatMessageAdapter = new ChatMessageAdapter(messages);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        rvChat.setLayoutManager(mLayoutManager);
        rvChat.setItemAnimator(new DefaultItemAnimator());
        rvChat.setAdapter(chatMessageAdapter);
        rvChat.addOnItemTouchListener(new RecyclerTouchListener(view.getContext(),
                rvChat, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(messages.get(position).getMsgType().equals(ConstantRegistry.IMAGE)){
                    ImageDetailRequest imageDetailRequest = new ImageDetailRequest();
                    imageDetailRequest.setChatId(messages.get(position).getMessageChatId());
                    imageDetailRequest.setGroupChat(false);
                    imageDetailRequest.setImageUri(messages.get(position).getBinaryMessageFilePath());
                    chatActivity.showImageDetail(imageDetailRequest, messages.get(position));
                }
            }

            @Override
            public void onLongClick(View view, int position) {
                Message message = messages.get(position);
                if(message.getMsgType().equals(ConstantRegistry.TEXT)){
                    chatActivity.showPopupMessageOptions(view, message, position);
                }else{
                    chatActivity.showPopupImageMessageOptions(view, message, position);
                }
            }
        }));

        if(messages.size() > 0){
            scrollToBottom();
        }
    }

    private void scrollToBottom() {
        rvChat.scrollToPosition(chatMessageAdapter.getItemCount() - 1);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        chatActivity = (ChatActivity)context;
    }

    @OnClick(R.id.ivSendMessage)
    public void sendMessageClickListener(){
        String message = etMessage.getText().toString().trim();
        etMessage.setText("");
        chatActivity.sendMessageClickListener(message);
    }

    @OnClick(R.id.ivSpeechToTextMessage)
    public void speechToTextMessageClickListener(){
        chatActivity.speechToTextMessageClickListener();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TextMessageEvent event) {
        messages = event.getMessages();
        chatMessageAdapter.notifyDataSetChanged();
        scrollToBottom();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SpeechToTextMessageEvent event) {
        if(etMessage.getText().toString().trim().equals(ConstantRegistry.CHATSTER_EMPTY_STRING)){
            etMessage.setText(event.getMessage());
        }else{
            etMessage.setText(etMessage.getText().toString().concat(Objects.requireNonNull(event.getMessage())));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ImageMessageEvent event) {
        messages = event.getMessages();
        chatMessageAdapter.notifyDataSetChanged();
        scrollToBottom();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UnsendMessageEvent event) {
        messages.get(event.getMessagePosition()).setMsgType(ConstantRegistry.TEXT);
        messages.get(event.getMessagePosition()).setBinaryMessageFilePath(Uri.EMPTY);
        messages.get(event.getMessagePosition()).setMessageText(event.getMessageText());
        chatMessageAdapter.notifyItemChanged(event.getMessagePosition());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DeleteMessageEvent event) {
        messages = event.getMessages();
        chatMessageAdapter.notifyDataSetChanged();
        scrollToBottom();
    }
}
