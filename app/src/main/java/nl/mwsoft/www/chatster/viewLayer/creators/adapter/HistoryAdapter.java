package nl.mwsoft.www.chatster.viewLayer.creators.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.util.dateTime.ChatsterDateTimeUtil;
import nl.mwsoft.www.chatster.modelLayer.helper.util.image.ImageCircleTransformUtil;
import nl.mwsoft.www.chatster.modelLayer.model.HistoryItem;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private Context context;
    private ArrayList<HistoryItem> historyItems;
    private ChatsterDateTimeUtil chatsterDateTimeUtil;

    public class HistoryViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivHistoryUserProfilePic;
        public ImageView ivHistoryPost;
        public TextView tvHistoryUserName;
        public TextView tvHistoryDescription;
        public TextView tvHistoryCreated;

        public HistoryViewHolder(View view) {
            super(view);
            context = view.getContext();
            ivHistoryUserProfilePic = (ImageView) view.findViewById(R.id.ivHistoryUserProfilePic);
            ivHistoryPost = (ImageView) view.findViewById(R.id.ivHistoryPost);
            tvHistoryUserName = (TextView) view.findViewById(R.id.tvHistoryUserName);
            tvHistoryDescription = (TextView) view.findViewById(R.id.tvHistoryDescription);
            tvHistoryCreated = (TextView) view.findViewById(R.id.tvHistoryCreated);
        }
    }

    public HistoryAdapter(ArrayList<HistoryItem> historyItems) {
        this.historyItems = historyItems;
        this.chatsterDateTimeUtil = new ChatsterDateTimeUtil();
    }

    @Override
    public HistoryAdapter.HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.creators_history_item, parent, false);

        return new HistoryAdapter.HistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HistoryAdapter.HistoryViewHolder holder, int position) {
        HistoryItem historyItem = historyItems.get(position);

        holder.tvHistoryCreated.setText(getPostCreated(historyItem.getCreated()));
        holder.tvHistoryUserName.setText(historyItem.getUserName());
        holder.tvHistoryDescription.setText(historyItem.getDescription());
        Picasso.with(context).
                load(ConstantRegistry.IMAGE_URL_PREFIX.concat(historyItem.getUserProfilePic())).
                transform(new ImageCircleTransformUtil()).
                into(holder.ivHistoryUserProfilePic);
        if(historyItem.getType().equals(ConstantRegistry.CREATORS_NOTIFICATION_TYPE_POST) ||
                historyItem.getType().equals(ConstantRegistry.CREATORS_NOTIFICATION_TYPE_POST_LIKE) ||
                historyItem.getType().equals(ConstantRegistry.CREATORS_NOTIFICATION_TYPE_POST_UNLIKE) ||
                historyItem.getType().equals(ConstantRegistry.CREATORS_NOTIFICATION_TYPE_POST_COMMENT)){
//            if(holder.ivHistoryPost.getVisibility() == View.GONE){
//                holder.ivHistoryPost.setVisibility(View.VISIBLE);
//            }
//            Picasso.with(context).load(ConstantRegistry.IMAGE_URL_PREFIX.concat(historyItem.getPostUrl())).into(holder.ivHistoryPost);
        }else {
            if(holder.ivHistoryPost.getVisibility() == View.VISIBLE){
                holder.ivHistoryPost.setVisibility(View.GONE);
            }
        }
    }

    @NonNull
    private String getPostCreated(String postCreated) {
        String messageCreated = "";
        String[] parts;
        String part1;
        String part2;
        String date = "";

        if(!postCreated.equals(null) && !postCreated.equals("")){
            messageCreated = postCreated;
            parts = messageCreated.split(" ");
            part1 = parts[0]; // 2017-05-27
            part2 = parts[1]; // 12:05:41
            String[] dateParts = part1.split("-");
            String[] currDate = chatsterDateTimeUtil.getDateTime().split(" ");
            String[] currDateParts = currDate[0].split("-");
            if(Integer.parseInt(currDateParts[2]) != Integer.parseInt(dateParts[2])){
                date = part1;
            }else{
                String[] newParts = part2.split(":");
                date = newParts[0].concat(":").concat(newParts[1]);
            }
        }else{
            messageCreated = chatsterDateTimeUtil.getDateTime();
            parts = messageCreated.split(" ");
            part1 = parts[0]; // 2017-05-27
            part2 = parts[1]; // 12:05:41
            String[] newParts = part2.split(":");
            date = newParts[0].concat(":").concat(newParts[1]);
        }
        return date;
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }
}
