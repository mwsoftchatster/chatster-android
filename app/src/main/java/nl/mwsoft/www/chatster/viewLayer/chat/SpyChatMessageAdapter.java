package nl.mwsoft.www.chatster.viewLayer.chat;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.model.Message;

public class SpyChatMessageAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Message> messageList;
    private Context context;

    public class TextMessageViewHolder extends RecyclerView.ViewHolder {
        public TextView tvSpyChatMessageText;

        public TextMessageViewHolder(View view) {
            super(view);
            tvSpyChatMessageText = (TextView) view.findViewById(R.id.tvSpyChatMessageText);
        }
    }

    public class ImageMessageViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivSpyChatMessageImage;

        public ImageMessageViewHolder(View view) {
            super(view);

            ivSpyChatMessageImage = (ImageView) view.findViewById(R.id.ivSpyChatMessageImage);
        }
    }


    public SpyChatMessageAdapter(ArrayList<Message> messageList) {
        this.messageList = messageList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        context = parent.getContext();
        if(viewType == 1){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.spy_chat_message_item, parent, false);
            return new SpyChatMessageAdapter.TextMessageViewHolder(itemView);
        }else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.spy_chat_message_image_item, parent, false);
            return new SpyChatMessageAdapter.ImageMessageViewHolder(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(messageList.get(position).getMsgType().equals(ConstantRegistry.TEXT)){
            return 1;
        }else{
            return 2;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        if(holder.getItemViewType() == 1) {
            SpyChatMessageAdapter.TextMessageViewHolder textMessageViewHolder = (SpyChatMessageAdapter.TextMessageViewHolder) holder;
            textMessageViewHolder.tvSpyChatMessageText.setText(message.getMessageText());
        }else{
            SpyChatMessageAdapter.ImageMessageViewHolder imageMessageViewHolder = (SpyChatMessageAdapter.ImageMessageViewHolder)holder;
            Uri myImageURL = message.getBinaryMessageFilePath();
            Glide.with(context).load(myImageURL).into(imageMessageViewHolder.ivSpyChatMessageImage);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}
