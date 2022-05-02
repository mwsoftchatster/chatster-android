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

import java.util.List;

import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.util.dateTime.ChatsterDateTimeUtil;
import nl.mwsoft.www.chatster.modelLayer.helper.util.image.ImageCircleTransformUtil;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorPostComment;
import nl.mwsoft.www.chatster.modelLayer.modelLayerManager.user.UserModelLayerManager;

public class PostDetailAdapter extends RecyclerView.Adapter<PostDetailAdapter.MyViewHolder> {

    private List<CreatorPostComment> creatorPostComments;
    private Context context;
    private ChatsterDateTimeUtil chatsterDateTimeUtil;
    private UserModelLayerManager userModelLayerManager;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivCommentUserProfilePic;
        public TextView tvCommentUserName;
        public TextView tvPostComment;
        public TextView ivCommentCreated;

        public MyViewHolder(View view) {
            super(view);
            context = view.getContext();
            ivCommentUserProfilePic = (ImageView) view.findViewById(R.id.ivCommentUserProfilePic);
            tvCommentUserName = (TextView) view.findViewById(R.id.tvCommentUserName);
            tvPostComment = (TextView) view.findViewById(R.id.tvPostComment);
            ivCommentCreated = (TextView) view.findViewById(R.id.ivCommentCreated);
        }
    }


    public PostDetailAdapter(List<CreatorPostComment> creatorPostComments) {
        this.creatorPostComments = creatorPostComments;
        chatsterDateTimeUtil = new ChatsterDateTimeUtil();
        userModelLayerManager = new UserModelLayerManager();
    }

    @Override
    public PostDetailAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.creators_post_comments_item, parent, false);

        return new PostDetailAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PostDetailAdapter.MyViewHolder holder, int position) {
        CreatorPostComment creatorPostComment = creatorPostComments.get(position);
        String commentCreatedDateUtc = "";
        commentCreatedDateUtc = chatsterDateTimeUtil.convertFromUtcToLocal(creatorPostComment.getCommentCreated());
        holder.ivCommentCreated.setText(getPostCreated(commentCreatedDateUtc));

        holder.tvCommentUserName.setText(creatorPostComment.getCreatorsName());
        holder.tvPostComment.setText(creatorPostComment.getComment());
        Picasso.with(context).
                load(ConstantRegistry.IMAGE_URL_PREFIX.concat(creatorPostComment.getUserProfilePicUrl())).
                transform(new ImageCircleTransformUtil()).
                into(holder.ivCommentUserProfilePic);
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
        return creatorPostComments.size();
    }
}
