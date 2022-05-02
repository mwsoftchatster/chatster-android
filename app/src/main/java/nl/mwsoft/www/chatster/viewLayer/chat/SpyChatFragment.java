package nl.mwsoft.www.chatster.viewLayer.chat;

import android.content.Context;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.event.chat.DeleteMessageEvent;
import nl.mwsoft.www.chatster.modelLayer.event.chat.ImageMessageEvent;
import nl.mwsoft.www.chatster.modelLayer.event.chat.SpyModeEvent;
import nl.mwsoft.www.chatster.modelLayer.event.chat.TextMessageEvent;
import nl.mwsoft.www.chatster.modelLayer.helper.recyclerView.RecyclerTouchListener;
import nl.mwsoft.www.chatster.modelLayer.helper.recyclerView.RecyclerViewClickListener;
import nl.mwsoft.www.chatster.modelLayer.model.Message;

public class SpyChatFragment extends Fragment {

    private ChatActivity chatActivity;
    @BindView(R.id.rvSpyChat)
    RecyclerView rvSpyChat;
    @BindView(R.id.etSpyMessage)
    EditText etSpyMessage;
    @BindView(R.id.ivSendSpyMessage)
    ImageView ivSendSpyMessage;
    private Unbinder unbinder;
    private ArrayList<Message> messages;
    private SpyChatMessageAdapter spyChatMessageAdapter;
    public static final String SPY_MESSAGES = "spyMessages";


    public static SpyChatFragment newInstance(ArrayList<Message> messages) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(SPY_MESSAGES, messages);

        SpyChatFragment fragment = new SpyChatFragment();
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
        View view = inflater.inflate(R.layout.fragment_spy_chat, container, false);
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
            messages = arguments.getParcelableArrayList(SPY_MESSAGES);
        }else{
            messages = new ArrayList<>();
        }

        spyChatMessageAdapter = new SpyChatMessageAdapter(messages);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        rvSpyChat.setLayoutManager(mLayoutManager);
        rvSpyChat.setItemAnimator(new DefaultItemAnimator());
        rvSpyChat.setAdapter(spyChatMessageAdapter);
        rvSpyChat.addOnItemTouchListener(new RecyclerTouchListener(view.getContext(),
                rvSpyChat, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));

        if(messages.size() > 0){
            scrollToBottom();
        }
    }

    private void scrollToBottom() {
        rvSpyChat.scrollToPosition(spyChatMessageAdapter.getItemCount() - 1);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        chatActivity = (ChatActivity)context;
    }

    @OnClick(R.id.ivSendSpyMessage)
    public void sendMessageClickListener(){
        String message = etSpyMessage.getText().toString().trim();
        etSpyMessage.setText("");
        chatActivity.sendMessageClickListener(message);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TextMessageEvent event) {
        messages = event.getMessages();
        spyChatMessageAdapter.notifyDataSetChanged();
        scrollToBottom();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SpyModeEvent event) {
        if(event.isInSpyMode()){
        }else {
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ImageMessageEvent event) {
        messages = event.getMessages();
        spyChatMessageAdapter.notifyDataSetChanged();
        scrollToBottom();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DeleteMessageEvent event) {
        messages = event.getMessages();
        spyChatMessageAdapter.notifyDataSetChanged();
        scrollToBottom();
    }
}
