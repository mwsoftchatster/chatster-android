package nl.mwsoft.www.chatster.viewLayer.creators.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.util.image.ImageCircleTransformUtil;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorContact;
import nl.mwsoft.www.chatster.viewLayer.creators.CreatorsActivity;

public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.MyViewHolder> {

    private List<CreatorContact> creatorContacts;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivCreatorsFollowing;
        public ImageView ivCreatorsFollowingConnect;
        public TextView tvCreatorsFollowingUserName;
        public TextView tvCreatorsFollowingStatus;


        public MyViewHolder(View view) {
            super(view);
            context = view.getContext();
            ivCreatorsFollowing = (ImageView) view.findViewById(R.id.ivCreatorsFollowing);
            ivCreatorsFollowingConnect = (ImageView) view.findViewById(R.id.ivCreatorsFollowingConnect);
            tvCreatorsFollowingUserName = (TextView) view.findViewById(R.id.tvCreatorsFollowingUserName);
            tvCreatorsFollowingStatus = (TextView) view.findViewById(R.id.tvCreatorsFollowingStatus);

            ivCreatorsFollowingConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(context instanceof CreatorsActivity){
                        CreatorContact creatorContact = creatorContacts.get(getAdapterPosition());

                        if(creatorContact.getFollowingThisCreator() == 0){
                            ((CreatorsActivity)context).followCreator(((CreatorsActivity)context).getUserId(context),
                                    ((CreatorsActivity)context).getUserName(context),
                                    ((CreatorsActivity)context).getUserProfilePicUrl(context),
                                    creatorContact.getCreatorId());
                            Picasso.with(context).load(R.drawable.following_256).into(ivCreatorsFollowingConnect);
                            creatorContacts.get(getAdapterPosition()).setFollowingThisCreator(1);
                        }else{
                            ((CreatorsActivity)context).unFollowCreator(((CreatorsActivity)context).getUserId(context),
                                    ((CreatorsActivity)context).getUserName(context),
                                    ((CreatorsActivity)context).getUserProfilePicUrl(context),
                                    creatorContact.getCreatorId());
                            Picasso.with(context).load(R.drawable.connect_req_256).into(ivCreatorsFollowingConnect);
                            creatorContacts.get(getAdapterPosition()).setFollowingThisCreator(0);
                        }
                    }else{
                        Toast.makeText(context, context.getString(R.string.smth_went_wrong),Toast.LENGTH_LONG).show();
                    }
                }
            });

            ivCreatorsFollowing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(context instanceof CreatorsActivity){
                        CreatorContact creatorContact = creatorContacts.get(getAdapterPosition());
                        ((CreatorsActivity)context).navigateToCreatorsProfile( creatorContact.getCreatorId(),
                                ((CreatorsActivity)context).getUserId(context), true );
                    }else{
                        Toast.makeText(context, context.getString(R.string.smth_went_wrong),Toast.LENGTH_LONG).show();
                    }
                }
            });

            tvCreatorsFollowingUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(context instanceof CreatorsActivity){
                        CreatorContact creatorContact = creatorContacts.get(getAdapterPosition());
                        ((CreatorsActivity)context).navigateToCreatorsProfile( creatorContact.getCreatorId(),
                                ((CreatorsActivity)context).getUserId(context), true );
                    }else{
                        Toast.makeText(context, context.getString(R.string.smth_went_wrong),Toast.LENGTH_LONG).show();
                    }
                }
            });

            tvCreatorsFollowingStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(context instanceof CreatorsActivity){
                        CreatorContact creatorContact = creatorContacts.get(getAdapterPosition());
                        ((CreatorsActivity)context).navigateToCreatorsProfile( creatorContact.getCreatorId(),
                                ((CreatorsActivity)context).getUserId(context), true );
                    }else{
                        Toast.makeText(context, context.getString(R.string.smth_went_wrong),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }


    public FollowingAdapter(List<CreatorContact> creatorContacts) {
        this.creatorContacts = creatorContacts;
    }

    @Override
    public FollowingAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_creator_following_item, parent, false);

        return new FollowingAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FollowingAdapter.MyViewHolder holder, int position) {
        CreatorContact creatorContact = creatorContacts.get(position);
        holder.tvCreatorsFollowingUserName.setText(creatorContact.getCreatorId());
        holder.tvCreatorsFollowingStatus.setText(creatorContact.getStatusMessage());
        Picasso.with(context).load(ConstantRegistry.IMAGE_URL_PREFIX.concat(creatorContact.getProfilePic())).
                transform(new ImageCircleTransformUtil()).
                into(holder.ivCreatorsFollowing);
        if(creatorContact.getFollowingThisCreator() == 1){
            Picasso.with(context).load(R.drawable.following_256).into(holder.ivCreatorsFollowingConnect);
        }else if(creatorContact.getFollowingThisCreator() == 0){
            Picasso.with(context).load(R.drawable.connect_req_256).into(holder.ivCreatorsFollowingConnect);
        }
    }

    @Override
    public int getItemCount() {
        return creatorContacts.size();
    }
}

