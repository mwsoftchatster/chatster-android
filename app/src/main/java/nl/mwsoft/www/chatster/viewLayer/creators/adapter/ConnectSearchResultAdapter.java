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

public class ConnectSearchResultAdapter  extends RecyclerView.Adapter<ConnectSearchResultAdapter.MyViewHolder> {

    private List<CreatorContact> creatorContacts;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivProfileSearchResult;
        public ImageView ivProfileSearchResultConnect;
        public TextView tvUserNameSearchResult;
        public TextView tvUserStatusSearchResult;


        public MyViewHolder(View view) {
            super(view);
            context = view.getContext();
            ivProfileSearchResult = (ImageView) view.findViewById(R.id.ivProfileSearchResult);
            ivProfileSearchResultConnect = (ImageView) view.findViewById(R.id.ivProfileSearchResultConnect);
            tvUserNameSearchResult = (TextView) view.findViewById(R.id.tvUserNameSearchResult);
            tvUserStatusSearchResult = (TextView) view.findViewById(R.id.tvUserStatusSearchResult);

            ivProfileSearchResultConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(context instanceof CreatorsActivity){
                        CreatorContact creatorContact = creatorContacts.get(getAdapterPosition());

                        if(creatorContact.getFollowingThisCreator() == 0){
                            ((CreatorsActivity)context).followCreator(((CreatorsActivity)context).getUserId(context),
                                    ((CreatorsActivity)context).getUserName(context),
                                    ((CreatorsActivity)context).getUserProfilePicUrl(context),
                                    creatorContact.getCreatorId());
                            Picasso.with(context).load(R.drawable.following_256).into(ivProfileSearchResultConnect);
                            creatorContacts.get(getAdapterPosition()).setFollowingThisCreator(1);
                        }else{
                            ((CreatorsActivity)context).unFollowCreator(((CreatorsActivity)context).getUserId(context),
                                    ((CreatorsActivity)context).getUserName(context),
                                    ((CreatorsActivity)context).getUserProfilePicUrl(context),
                                    creatorContact.getCreatorId());
                            Picasso.with(context).load(R.drawable.connect_req_256).into(ivProfileSearchResultConnect);
                            creatorContacts.get(getAdapterPosition()).setFollowingThisCreator(0);
                        }
                    }else{
                        Toast.makeText(context, context.getString(R.string.smth_went_wrong),Toast.LENGTH_LONG).show();
                    }
                }
            });

            ivProfileSearchResult.setOnClickListener(new View.OnClickListener() {
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

            tvUserNameSearchResult.setOnClickListener(new View.OnClickListener() {
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

            tvUserStatusSearchResult.setOnClickListener(new View.OnClickListener() {
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


    public ConnectSearchResultAdapter(List<CreatorContact> creatorContacts) {
        this.creatorContacts = creatorContacts;
    }

    @Override
    public ConnectSearchResultAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_result_item, parent, false);

        return new ConnectSearchResultAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ConnectSearchResultAdapter.MyViewHolder holder, int position) {
        CreatorContact creatorContact = creatorContacts.get(position);
        holder.tvUserNameSearchResult.setText(creatorContact.getCreatorId());
        holder.tvUserStatusSearchResult.setText(creatorContact.getStatusMessage());
        Picasso.with(context).load(ConstantRegistry.IMAGE_URL_PREFIX.concat(creatorContact.getProfilePic())).
                transform(new ImageCircleTransformUtil()).
                into(holder.ivProfileSearchResult);
        if(creatorContact.getFollowingThisCreator() == 1){
            Picasso.with(context).load(R.drawable.following_256).into(holder.ivProfileSearchResultConnect);
        }else if(creatorContact.getFollowingThisCreator() == 0){
            Picasso.with(context).load(R.drawable.connect_req_256).into(holder.ivProfileSearchResultConnect);
        }
    }

    @Override
    public int getItemCount() {
        return creatorContacts.size();
    }
}
