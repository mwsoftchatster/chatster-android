package nl.mwsoft.www.chatster.viewLayer.groupChat;


import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import nl.mwsoft.www.chatster.MyApplication;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.model.GroupChatMessage;
import nl.mwsoft.www.chatster.presenterLayer.groupChat.GroupChatPresenter;

public class GroupChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<GroupChatMessage> groupChatMessageList;
    private Context context;
    private GroupChatPresenter groupChatPresenter;

    public class TextMessageViewHolder extends RecyclerView.ViewHolder {
        public TextView tvGroupChatMessageSenderName;
        public TextView tvGroupChatMessageText;
        public TextView tvGroupChatMessageCreated;

        public TextMessageViewHolder(View view) {
            super(view);
            tvGroupChatMessageSenderName = (TextView) view.findViewById(R.id.tvGroupChatMessageSenderName);
            tvGroupChatMessageText = (TextView) view.findViewById(R.id.tvGroupChatMessageText);
            tvGroupChatMessageCreated = (TextView) view.findViewById(R.id.tvGroupChatMessageCreated);
        }
    }

    public class ImageMessageViewHolder extends RecyclerView.ViewHolder {

        public TextView tvGroupChatMessageImageSenderName;
        public TextView tvGroupChatMessageImageCreated;
        public ImageView ivGroupChatMessageImage;

        public ImageMessageViewHolder(View view) {
            super(view);

            tvGroupChatMessageImageSenderName = (TextView) view.findViewById(R.id.tvGroupChatMessageImageSenderName);
            ivGroupChatMessageImage = (ImageView) view.findViewById(R.id.ivGroupChatMessageImage);
            tvGroupChatMessageImageCreated = (TextView) view.findViewById(R.id.tvGroupChatMessageImageCreated);
        }
    }

    public class UserJoinedGroupMessageViewHolder extends RecyclerView.ViewHolder {
        public TextView tvUserJoinedGroupChat;

        public UserJoinedGroupMessageViewHolder(View view) {
            super(view);
            tvUserJoinedGroupChat = (TextView) view.findViewById(R.id.tvUserJoinedGroupChat);
        }
    }


    public GroupChatMessageAdapter(ArrayList<GroupChatMessage> messageList) {
        this.groupChatMessageList = messageList;
        this.groupChatPresenter = DependencyRegistry.shared.injectGroupChatMessageAdapter();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        context = parent.getContext();
        if(viewType == 1){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.group_chat_message_item, parent, false);
            return new GroupChatMessageAdapter.TextMessageViewHolder(itemView);
        }else if(viewType == 2){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.group_chat_message_image_item, parent, false);
            return new GroupChatMessageAdapter.ImageMessageViewHolder(itemView);
        }else if(viewType == 3){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.group_chat_message_sender_item, parent, false);
            return new GroupChatMessageAdapter.TextMessageViewHolder(itemView);
        }else if(viewType == 4){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.group_chat_message_image_sender_item, parent, false);
            return new GroupChatMessageAdapter.ImageMessageViewHolder(itemView);
        } else{
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.group_chat_user_joined_message_item, parent, false);
            return new GroupChatMessageAdapter.UserJoinedGroupMessageViewHolder(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(groupChatMessageList.get(position).getMsgType().equals(ConstantRegistry.TEXT)){
            if(userIsSender(position)){
                return 3;
            }else{
                return 1;
            }
        }else if(groupChatMessageList.get(position).getMsgType().equals(ConstantRegistry.IMAGE)){
            if(userIsSender(position)){
                return 4;
            }else{
                return 2;
            }
        }else{
            return 5;
        }
    }

    private boolean userIsSender(int position) {
        return groupChatMessageList.get(position).getSenderId() == groupChatPresenter.getUserId(MyApplication.context());
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        GroupChatMessage groupChatMessage = groupChatMessageList.get(position);
        String date = "";
        if(messageHasDateCreated(groupChatMessage)){
            date = getDateCreatedFromMessage(groupChatMessage);
        }else{
            date = getDateCreated();
        }
        if(holder.getItemViewType() == 1 || holder.getItemViewType() == 3) {
            GroupChatMessageAdapter.TextMessageViewHolder textMessageViewHolder =
                    (GroupChatMessageAdapter.TextMessageViewHolder) holder;
            makeSenderNameBold(textMessageViewHolder);
            setMessageTextAndDate(groupChatMessage, date, textMessageViewHolder);
            if(userIsNotSender(groupChatMessage)){
                makeSenderNameVisible(textMessageViewHolder);
                setSenderName(groupChatMessage, textMessageViewHolder);
            }
        }else if(holder.getItemViewType() == 2 || holder.getItemViewType() == 4) {
            GroupChatMessageAdapter.ImageMessageViewHolder imageMessageViewHolder =
                    (GroupChatMessageAdapter.ImageMessageViewHolder)holder;
            makeImageSenderNameBold(imageMessageViewHolder);
            setMessageImageAndDate(groupChatMessage, date, imageMessageViewHolder);
            if(userIsNotSender(groupChatMessage)) {
                makeImageSenderNameVisible(imageMessageViewHolder);
                setImageSenderName(groupChatMessage, imageMessageViewHolder);
            }
        }else{
            GroupChatMessageAdapter.UserJoinedGroupMessageViewHolder userJoinedGroupMessageViewHolder =
                    (GroupChatMessageAdapter.UserJoinedGroupMessageViewHolder) holder;
            setUserJoinedGroupMessageText(groupChatMessage, userJoinedGroupMessageViewHolder);
        }

        groupChatPresenter.updateMessageHasBeenReadByMessageId(groupChatMessage.getGetGroupChatMessageId(), context);
    }

    private void setUserJoinedGroupMessageText(GroupChatMessage groupChatMessage, UserJoinedGroupMessageViewHolder userJoinedGroupMessageViewHolder) {
        userJoinedGroupMessageViewHolder.tvUserJoinedGroupChat.setText(groupChatMessage.getMessageText());
    }

    private void setMessageImageAndDate(GroupChatMessage groupChatMessage, String date, ImageMessageViewHolder imageMessageViewHolder) {
        Uri myImageURL = groupChatMessage.getBinaryMessageFilePath();
        Glide.with(context).load(myImageURL).into(imageMessageViewHolder.ivGroupChatMessageImage);
        imageMessageViewHolder.tvGroupChatMessageImageCreated.setText(date);
    }

    private void setImageSenderName(GroupChatMessage groupChatMessage, ImageMessageViewHolder imageMessageViewHolder) {
        imageMessageViewHolder.tvGroupChatMessageImageSenderName
                .setText(groupChatPresenter.getContactNameById(context, groupChatMessage.getSenderId()));
    }

    private void makeImageSenderNameBold(ImageMessageViewHolder imageMessageViewHolder) {
        imageMessageViewHolder.tvGroupChatMessageImageSenderName
                .setTypeface(imageMessageViewHolder.tvGroupChatMessageImageSenderName.getTypeface(), Typeface.BOLD);
    }

    private void makeImageSenderNameVisible(ImageMessageViewHolder imageMessageViewHolder) {
        if(imageMessageViewHolder.tvGroupChatMessageImageSenderName.getVisibility() == View.GONE){
            imageMessageViewHolder.tvGroupChatMessageImageSenderName.setVisibility(View.VISIBLE);
        }
    }

    private void setMessageTextAndDate(GroupChatMessage groupChatMessage, String date, TextMessageViewHolder textMessageViewHolder) {
        textMessageViewHolder.tvGroupChatMessageText.setText(groupChatMessage.getMessageText());
        textMessageViewHolder.tvGroupChatMessageCreated.setText(date);
    }

    private void makeSenderNameBold(TextMessageViewHolder textMessageViewHolder) {
        textMessageViewHolder.tvGroupChatMessageSenderName
                .setTypeface(textMessageViewHolder.tvGroupChatMessageSenderName.getTypeface(), Typeface.BOLD);
    }

    private void setSenderName(GroupChatMessage groupChatMessage, TextMessageViewHolder textMessageViewHolder) {
        textMessageViewHolder.tvGroupChatMessageSenderName.setText(groupChatPresenter.getContactNameById(context,
                groupChatMessage.getSenderId()));
    }

    private void makeSenderNameVisible(TextMessageViewHolder textMessageViewHolder) {
        if(textMessageViewHolder.tvGroupChatMessageSenderName.getVisibility() == View.GONE){
            textMessageViewHolder.tvGroupChatMessageSenderName.setVisibility(View.VISIBLE);
        }
    }

    private boolean userIsNotSender(GroupChatMessage groupChatMessage) {
        return groupChatMessage.getSenderId() != groupChatPresenter.getUserId(context);
    }

    @NonNull
    private String getDateCreated() {
        String messageCreated;
        String[] parts;
        String part1;
        String part2;
        String date;
        messageCreated = groupChatPresenter.getDateTime();
        parts = messageCreated.split(" ");
        part1 = parts[0]; // 2017-05-27
        part2 = parts[1]; // 12:05:41
        String[] newParts = part2.split(":");
        date = newParts[0].concat(":").concat(newParts[1]);
        return date;
    }

    @NonNull
    private String getDateCreatedFromMessage(GroupChatMessage groupChatMessage) {
        String messageCreated;
        String[] parts;
        String part1;
        String part2;
        String date;
        messageCreated = groupChatMessage.getGroupChatMessageCreated();
        parts = messageCreated.split(" ");
        part1 = parts[0]; // 2017-05-27
        part2 = parts[1]; // 12:05:41
        String[] dateParts = part1.split("-");
        String[] currDate = groupChatPresenter.getDateTime().split(" ");
        String[] currDateParts = currDate[0].split("-");
        if(Integer.parseInt(currDateParts[2]) != Integer.parseInt(dateParts[2])){
            date = part1;
        }else{
            String[] newParts = part2.split(":");
            date = newParts[0].concat(":").concat(newParts[1]);
        }
        return date;
    }

    private boolean messageHasDateCreated(GroupChatMessage groupChatMessage) {
        return groupChatMessage.getGroupChatMessageCreated() != null && !groupChatMessage.getGroupChatMessageCreated()
                .equals(ConstantRegistry.CHATSTER_EMPTY_STRING);
    }

    @Override
    public int getItemCount() {
        return groupChatMessageList.size();
    }
}

