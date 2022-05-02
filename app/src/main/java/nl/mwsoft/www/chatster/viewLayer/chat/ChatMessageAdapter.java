package nl.mwsoft.www.chatster.viewLayer.chat;


import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import nl.mwsoft.www.chatster.MyApplication;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.dependencyRegistry.DependencyRegistry;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.model.Message;
import nl.mwsoft.www.chatster.presenterLayer.chat.ChatPresenter;

public class ChatMessageAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Message> messageList;
    private Context context;
    private ChatPresenter chatPresenter;

    public class TextMessageViewHolder extends RecyclerView.ViewHolder {
        public TextView tvChatMessageSenderName;
        public TextView tvChatMessageText;
        public TextView tvChatMessageCreated;

        public TextMessageViewHolder(View view) {
            super(view);
            tvChatMessageSenderName = (TextView) view.findViewById(R.id.tvChatMessageSenderName);
            tvChatMessageText = (TextView) view.findViewById(R.id.tvChatMessageText);
            tvChatMessageCreated = (TextView) view.findViewById(R.id.tvChatMessageCreated);
        }
    }

    public class ImageMessageViewHolder extends RecyclerView.ViewHolder {

        public TextView tvChatMessageImageSenderName;
        public TextView tvChatMessageImageCreated;
        public ImageView ivChatMessageImage;

        public ImageMessageViewHolder(View view) {
            super(view);

            tvChatMessageImageSenderName = (TextView) view.findViewById(R.id.tvChatMessageImageSenderName);
            ivChatMessageImage = (ImageView) view.findViewById(R.id.ivChatMessageImage);
            tvChatMessageImageCreated = (TextView) view.findViewById(R.id.tvChatMessageImageCreated);
        }
    }


    public ChatMessageAdapter(ArrayList<Message> messageList) {
        this.messageList = messageList;
        this.chatPresenter = DependencyRegistry.shared.injectChatMessageAdapter();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        context = parent.getContext();
        if(viewType == 1){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_message_item, parent, false);
            return new ChatMessageAdapter.TextMessageViewHolder(itemView);
        }else if(viewType == 2){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_message_image_item, parent, false);
            return new ChatMessageAdapter.ImageMessageViewHolder(itemView);
        }else if(viewType == 3){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_message_sender_item, parent, false);
            return new ChatMessageAdapter.TextMessageViewHolder(itemView);
        }else{
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_message_image_sender_item, parent, false);
            return new ChatMessageAdapter.ImageMessageViewHolder(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(messageList.get(position).getMsgType().equals(ConstantRegistry.TEXT)){
            if(messageList.get(position).getMessageSenderId() == chatPresenter.getUserId(MyApplication.context())){
                return 3;
            }else{
                return 1;
            }
        }else{
            if(messageList.get(position).getMessageSenderId() == chatPresenter.getUserId(MyApplication.context())){
                return 4;
            }else{
                return 2;
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);
        String messageCreated = "";
        String[] parts;
        String part1;
        String part2;
        String date = "";
        if(message.getMessageCreated() != null && !message.getMessageCreated().equals("")){
            messageCreated = message.getMessageCreated();
            parts = messageCreated.split(" ");
            part1 = parts[0]; // 2017-05-27
            part2 = parts[1]; // 12:05:41
            String[] dateParts = part1.split("-");
            String[] currDate = chatPresenter.getDateTime().split(" ");
            String[] currDateParts = currDate[0].split("-");
            if(Integer.parseInt(currDateParts[2]) != Integer.parseInt(dateParts[2])){
                date = part1;
            }else{
                String[] newParts = part2.split(":");
                date = newParts[0].concat(":").concat(newParts[1]);
            }
        }else{
            messageCreated = chatPresenter.getDateTime();
            parts = messageCreated.split(" ");
            part1 = parts[0]; // 2017-05-27
            part2 = parts[1]; // 12:05:41
            String[] newParts = part2.split(":");
            date = newParts[0].concat(":").concat(newParts[1]);
        }

        if(holder.getItemViewType() == 1 || holder.getItemViewType() == 3) {
            TextMessageViewHolder textMessageViewHolder = (TextMessageViewHolder) holder;
            if(message.getMessageSenderId() != chatPresenter.getUserId(context)){
                if(textMessageViewHolder.tvChatMessageSenderName.getVisibility() == View.GONE){
                    textMessageViewHolder.tvChatMessageSenderName.setVisibility(View.VISIBLE);
                }
                textMessageViewHolder.tvChatMessageSenderName
                        .setTypeface(textMessageViewHolder.tvChatMessageSenderName.getTypeface(), Typeface.BOLD);
                textMessageViewHolder.tvChatMessageSenderName
                        .setText(chatPresenter.getContactNameById(context, message.getMessageSenderId()));
                textMessageViewHolder.tvChatMessageText.setText(message.getMessageText());
                textMessageViewHolder.tvChatMessageCreated.setText(date);
            }else{
                textMessageViewHolder.tvChatMessageSenderName
                        .setTypeface(textMessageViewHolder.tvChatMessageSenderName.getTypeface(), Typeface.BOLD);
                textMessageViewHolder.tvChatMessageText.setText(message.getMessageText());
                textMessageViewHolder.tvChatMessageCreated.setText(date);
            }
        }else{
            ImageMessageViewHolder imageMessageViewHolder = (ImageMessageViewHolder)holder;
            if(message.getMessageSenderId() != chatPresenter.getUserId(context)){
                if(imageMessageViewHolder.tvChatMessageImageSenderName.getVisibility() == View.GONE){
                    imageMessageViewHolder.tvChatMessageImageSenderName.setVisibility(View.VISIBLE);
                }
                imageMessageViewHolder.tvChatMessageImageSenderName
                        .setTypeface(imageMessageViewHolder.tvChatMessageImageSenderName.getTypeface(), Typeface.BOLD);
                imageMessageViewHolder.tvChatMessageImageSenderName
                        .setText(chatPresenter.getContactNameById(context,message.getMessageSenderId()));
                Uri myImageURL = message.getBinaryMessageFilePath();
                Glide.with(context).load(myImageURL).into(imageMessageViewHolder.ivChatMessageImage);
                //Picasso.with(context).load(myImageURL).into(imageMessageViewHolder.ivChatMessageImage);
                imageMessageViewHolder.tvChatMessageImageCreated.setText(date);
            }else{
                imageMessageViewHolder.tvChatMessageImageSenderName
                        .setTypeface(imageMessageViewHolder.tvChatMessageImageSenderName.getTypeface(), Typeface.BOLD);
                Uri myImageURL = message.getBinaryMessageFilePath();
                Glide.with(context).load(myImageURL).into(imageMessageViewHolder.ivChatMessageImage);
                //Picasso.with(context).load(myImageURL).into(imageMessageViewHolder.ivChatMessageImage);
                imageMessageViewHolder.tvChatMessageImageCreated.setText(date);
            }
        }

        // after the message has been read by the user, mark it as has been read
        chatPresenter.updateMessageHasBeenReadByMessageId(message.getMessageId(), context);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}

