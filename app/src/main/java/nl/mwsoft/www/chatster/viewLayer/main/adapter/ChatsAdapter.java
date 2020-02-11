/*
  Copyright (C) 2017 - 2020 MWSOFT

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
import nl.mwsoft.www.chatster.modelLayer.model.Chat;
import nl.mwsoft.www.chatster.modelLayer.helper.util.image.ImageCircleTransformUtil;
import nl.mwsoft.www.chatster.presenterLayer.chat.ChatPresenter;

public class ChatsAdapter  extends RecyclerView.Adapter<ChatsAdapter.MyViewHolder> {

    private List<Chat> chatList;
    private Context context;
    private ChatPresenter chatPresenter;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfileChats;
        public TextView tvUserNameChats;
        public TextView tvLastActivityMessageChats;
        public TextView tvLastActivityDateChats;
        public TextView tvUnreadMsgChat;

        public MyViewHolder(View view) {
            super(view);
            ivProfileChats = (ImageView) view.findViewById(R.id.ivProfileChats);
            tvUserNameChats = (TextView) view.findViewById(R.id.tvUserNameChats);
            tvLastActivityMessageChats = (TextView) view.findViewById(R.id.tvLastActivityMessageChats);
            tvLastActivityDateChats = (TextView) view.findViewById(R.id.tvLastActivityDateChats);
            tvUnreadMsgChat = (TextView) view.findViewById(R.id.tvUnreadMsgChat);
        }
    }


    public ChatsAdapter(List<Chat> chatList) {
        this.chatList = chatList;
        this.chatPresenter = DependencyRegistry.shared.injectChatsAdapter();
    }

    @Override
    public ChatsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chats_list_item, parent, false);

        return new ChatsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ChatsAdapter.MyViewHolder holder, int position) {
        Chat chat = chatList.get(position);
        if(chatPresenter.getContactProfilePicUriById(context, chat.getContactId()) != null &&
                !chatPresenter.getContactProfilePicUriById(context, chat.getContactId()).equals(ConstantRegistry.CHATSTER_EMPTY_STRING)){

            Picasso.with(context).load(ConstantRegistry.IMAGE_URL_PREFIX.concat(chatPresenter.getContactProfilePicUriById(context, chat.getContactId())))
                    .transform(new ImageCircleTransformUtil())
                    .into(holder.ivProfileChats);
        }
        holder.tvUserNameChats.setText(chatPresenter.getContactNameById(context, chat.getContactId()));
        holder.tvLastActivityMessageChats.setText(chat.getLastActivityMessage());
        holder.tvLastActivityDateChats.setText(chat.getLastActivityDate());
        if(chatPresenter.getUnreadMessageCountByChatId(context, chat.getChatId()) > 0){
            int unreadMsgCount = chatPresenter.getUnreadMessageCountByChatId(context, chat.getChatId());
            if(holder.tvUnreadMsgChat.getVisibility() == View.GONE){
                holder.tvUnreadMsgChat.setVisibility(View.VISIBLE);
                holder.tvUnreadMsgChat.setText(context.getString(R.string.number_unread_messages, unreadMsgCount));
            }
        }
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
}

