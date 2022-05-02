package nl.mwsoft.www.chatster.viewLayer.main.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChat;
import nl.mwsoft.www.chatster.modelLayer.helper.util.image.ImageCircleTransformUtil;
import nl.mwsoft.www.chatster.presenterLayer.groupChat.GroupChatPresenter;

public class GroupChatsAdapter extends RecyclerView.Adapter<GroupChatsAdapter.MyViewHolder> {

    private List<GroupChat> groupChats;
    private Context context;
    private GroupChatPresenter groupChatPresenter;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfileGroupChats;
        public TextView tvGroupChatName;
        public TextView tvLastActivityMessageGroupChats;
        public TextView tvLastActivityDateGroupChats;
        public TextView tvUnreadMsgGroupChat;

        public MyViewHolder(View view) {
            super(view);
            ivProfileGroupChats = (ImageView) view.findViewById(R.id.ivProfileGroupChats);
            tvGroupChatName = (TextView) view.findViewById(R.id.tvGroupChatName);
            tvLastActivityMessageGroupChats = (TextView) view.findViewById(R.id.tvLastActivityMessageGroupChats);
            tvLastActivityDateGroupChats = (TextView) view.findViewById(R.id.tvLastActivityDateGroupChats);
            tvUnreadMsgGroupChat = (TextView) view.findViewById(R.id.tvUnreadMsgGroupChat);
        }
    }


    public GroupChatsAdapter(List<GroupChat> groupChats) {
        this.groupChats = groupChats;
        this.groupChatPresenter = DependencyRegistry.shared.injectGroupChatsAdapter();
    }

    @Override
    public GroupChatsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_chat_list_item, parent, false);

        return new GroupChatsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GroupChatsAdapter.MyViewHolder holder, int position) {
        GroupChat groupChat = groupChats.get(position);
        if(groupChatPresenter.getGroupProfilePicUriById(context,groupChat.get_id()) != null &&
                !groupChatPresenter.getGroupProfilePicUriById(context,groupChat.get_id()).isEmpty()){

            Picasso.with(context).load(ConstantRegistry.IMAGE_URL_PREFIX.concat(groupChatPresenter.getGroupProfilePicUriById(context,groupChat.get_id())))
                    .transform(new ImageCircleTransformUtil())
                    .into(holder.ivProfileGroupChats);
        }
        holder.tvGroupChatName.setText(groupChatPresenter.getGroupChatNameById(context,groupChat.get_id()));
        holder.tvLastActivityMessageGroupChats.setText(groupChat.getGroupChatLastMessage());
        holder.tvLastActivityDateGroupChats.setText(groupChat.getGroupChatLastActivity());
        if(groupChatPresenter.getUnreadMessageCountByGroupChatId(context, groupChat.get_id()) > 0){
            int unreadMsgCount = groupChatPresenter.getUnreadMessageCountByGroupChatId(context, groupChat.get_id());
            if(holder.tvUnreadMsgGroupChat.getVisibility() == View.GONE){
                holder.tvUnreadMsgGroupChat.setVisibility(View.VISIBLE);
                holder.tvUnreadMsgGroupChat.setText(context.getString(R.string.number_unread_messages, unreadMsgCount));
            }
        }
    }

    @Override
    public int getItemCount() {
        return groupChats.size();
    }
}



