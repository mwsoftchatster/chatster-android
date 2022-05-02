package nl.mwsoft.www.chatster.viewLayer.creators.adapter;


import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.util.image.ImageCircleTransformUtil;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorPost;
import nl.mwsoft.www.chatster.viewLayer.creators.CreatorsActivity;


public class DiscoverAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CreatorPost> creatorPosts;
    private ArrayList<SimpleExoPlayer> players = new ArrayList<>();
    private Context context;
    private CreatorsActivity creatorsActivity;

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivDiscoverProfilePic;
        public ImageView ivDiscoverConnect;
        public ImageView ivDiscoverLastPost;
        public TextView tvDiscoverUserName;

        public ImageViewHolder(View view) {
            super(view);
            context = view.getContext();
            creatorsActivity = (CreatorsActivity)context;
            ivDiscoverProfilePic = (ImageView) view.findViewById(R.id.ivDiscoverProfilePic);
            ivDiscoverConnect = (ImageView) view.findViewById(R.id.ivDiscoverConnect);
            ivDiscoverLastPost = (ImageView) view.findViewById(R.id.ivDiscoverLastPost);
            tvDiscoverUserName = (TextView) view.findViewById(R.id.tvDiscoverUserName);

            ivDiscoverProfilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(context instanceof CreatorsActivity){
                        CreatorPost creatorPost = creatorPosts.get(getAdapterPosition());
                        ((CreatorsActivity)context).showCreatorsProfilePicPopUp(creatorPost.getCreatorsName());
                    }else{
                        Toast.makeText(context, context.getString(R.string.smth_went_wrong),Toast.LENGTH_LONG).show();
                    }
                }
            });

            ivDiscoverConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(context instanceof CreatorsActivity){
                        CreatorPost creatorPost = creatorPosts.get(getAdapterPosition());
                        if(creatorPost.getFollowingThisCreator() == 0){
                            ((CreatorsActivity)context).followCreator(((CreatorsActivity)context).getUserId(context),
                                    ((CreatorsActivity)context).getUserName(context),
                                    ((CreatorsActivity)context).getUserProfilePicUrl(context),
                                    creatorPost.getCreatorsName());
                            Picasso.with(context).load(R.drawable.following_256).into(ivDiscoverConnect);
                            creatorPosts.get(getAdapterPosition()).setFollowingThisCreator(1);
                        }else{
                            ((CreatorsActivity)context).unFollowCreator(((CreatorsActivity)context).getUserId(context),
                                    ((CreatorsActivity)context).getUserName(context),
                                    ((CreatorsActivity)context).getUserProfilePicUrl(context),
                                    creatorPost.getCreatorsName());
                            Picasso.with(context).load(R.drawable.connect_req_256).into(ivDiscoverConnect);
                            creatorPosts.get(getAdapterPosition()).setFollowingThisCreator(0);
                        }
                    }else{
                        Toast.makeText(context, context.getString(R.string.smth_went_wrong),Toast.LENGTH_LONG).show();
                    }
                }
            });

            tvDiscoverUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(context instanceof CreatorsActivity){
                        CreatorPost creatorPost = creatorPosts.get(getAdapterPosition());
                        ((CreatorsActivity)context).navigateToCreatorsProfile( creatorPost.getCreatorsName(),
                                ((CreatorsActivity)context).getUserId(context), true );
                    }else{
                        Toast.makeText(context, context.getString(R.string.smth_went_wrong),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivDiscoverProfilePicVid;
        public ImageView ivDiscoverConnectVid;
        public SimpleExoPlayerView vvDiscoverLastPostVid;
        public TextView tvDiscoverUserNameVid;

        public VideoViewHolder(View view) {
            super(view);
            context = view.getContext();
            creatorsActivity = (CreatorsActivity)context;
            ivDiscoverProfilePicVid = (ImageView) view.findViewById(R.id.ivDiscoverProfilePicVid);
            ivDiscoverConnectVid = (ImageView) view.findViewById(R.id.ivDiscoverConnectVid);
            vvDiscoverLastPostVid = (SimpleExoPlayerView) view.findViewById(R.id.vvDiscoverLastPostVid);
            tvDiscoverUserNameVid = (TextView) view.findViewById(R.id.tvDiscoverUserNameVid);

            ivDiscoverProfilePicVid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(context instanceof CreatorsActivity){
                        CreatorPost creatorPost = creatorPosts.get(getAdapterPosition());
                        ((CreatorsActivity)context).showCreatorsProfilePicPopUp(creatorPost.getCreatorsName());
                    }else{
                        Toast.makeText(context, context.getString(R.string.smth_went_wrong),Toast.LENGTH_LONG).show();
                    }
                }
            });

            ivDiscoverConnectVid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(context instanceof CreatorsActivity){
                        CreatorPost creatorPost = creatorPosts.get(getAdapterPosition());
                        if(creatorPost.getFollowingThisCreator() == 0){
                            ((CreatorsActivity)context).followCreator(((CreatorsActivity)context).getUserId(context),
                                    ((CreatorsActivity)context).getUserName(context),
                                    ((CreatorsActivity)context).getUserProfilePicUrl(context),
                                    creatorPost.getCreatorsName());
                            Picasso.with(context).load(R.drawable.following_256).into(ivDiscoverConnectVid);
                            creatorPosts.get(getAdapterPosition()).setFollowingThisCreator(1);
                        }else{
                            ((CreatorsActivity)context).unFollowCreator(((CreatorsActivity)context).getUserId(context),
                                    ((CreatorsActivity)context).getUserName(context),
                                    ((CreatorsActivity)context).getUserProfilePicUrl(context),
                                    creatorPost.getCreatorsName());
                            Picasso.with(context).load(R.drawable.connect_req_256).into(ivDiscoverConnectVid);
                            creatorPosts.get(getAdapterPosition()).setFollowingThisCreator(0);
                        }
                    }else{
                        Toast.makeText(context, context.getString(R.string.smth_went_wrong),Toast.LENGTH_LONG).show();
                    }
                }
            });

            tvDiscoverUserNameVid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(context instanceof CreatorsActivity){
                        CreatorPost creatorPost = creatorPosts.get(getAdapterPosition());
                        ((CreatorsActivity)context).navigateToCreatorsProfile( creatorPost.getCreatorsName(),
                                ((CreatorsActivity)context).getUserId(context), true );
                    }else{
                        Toast.makeText(context, context.getString(R.string.smth_went_wrong),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public class TextViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivDiscoverTextProfilePic;
        public ImageView ivDiscoverTextConnect;
        public TextView tvDiscoverTextLastPost;
        public TextView tvDiscoverTextUserName;

        public TextViewHolder(View view) {
            super(view);
            context = view.getContext();
            creatorsActivity = (CreatorsActivity)context;
            ivDiscoverTextProfilePic = (ImageView) view.findViewById(R.id.ivDiscoverTextProfilePic);
            ivDiscoverTextConnect = (ImageView) view.findViewById(R.id.ivDiscoverTextConnect);
            tvDiscoverTextLastPost = (TextView) view.findViewById(R.id.tvDiscoverTextLastPost);
            tvDiscoverTextUserName = (TextView) view.findViewById(R.id.tvDiscoverTextUserName);

            ivDiscoverTextProfilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(context instanceof CreatorsActivity){
                        CreatorPost creatorPost = creatorPosts.get(getAdapterPosition());
                        ((CreatorsActivity)context).showCreatorsProfilePicPopUp(creatorPost.getCreatorsName());
                    }else{
                        Toast.makeText(context, context.getString(R.string.smth_went_wrong),Toast.LENGTH_LONG).show();
                    }
                }
            });

            ivDiscoverTextConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(context instanceof CreatorsActivity){
                        CreatorPost creatorPost = creatorPosts.get(getAdapterPosition());
                        if(creatorPost.getFollowingThisCreator() == 0){
                            ((CreatorsActivity)context).followCreator(((CreatorsActivity)context).getUserId(context),
                                    ((CreatorsActivity)context).getUserName(context),
                                    ((CreatorsActivity)context).getUserProfilePicUrl(context),
                                    creatorPost.getCreatorsName());
                            Picasso.with(context).load(R.drawable.following_256).into(ivDiscoverTextConnect);
                            creatorPosts.get(getAdapterPosition()).setFollowingThisCreator(1);
                        }else{
                            ((CreatorsActivity)context).unFollowCreator(((CreatorsActivity)context).getUserId(context),
                                    ((CreatorsActivity)context).getUserName(context),
                                    ((CreatorsActivity)context).getUserProfilePicUrl(context),
                                    creatorPost.getCreatorsName());
                            Picasso.with(context).load(R.drawable.connect_req_256).into(ivDiscoverTextConnect);
                            creatorPosts.get(getAdapterPosition()).setFollowingThisCreator(0);
                        }
                    }else{
                        Toast.makeText(context, context.getString(R.string.smth_went_wrong),Toast.LENGTH_LONG).show();
                    }
                }
            });

            tvDiscoverTextUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(context instanceof CreatorsActivity){
                        CreatorPost creatorPost = creatorPosts.get(getAdapterPosition());
                        ((CreatorsActivity)context).navigateToCreatorsProfile( creatorPost.getCreatorsName(),
                                ((CreatorsActivity)context).getUserId(context), true );
                    }else{
                        Toast.makeText(context, context.getString(R.string.smth_went_wrong),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public DiscoverAdapter(List<CreatorPost> creatorPosts) {
        this.creatorPosts = creatorPosts;
    }

    @Override
    public int getItemViewType(int position) {
        if(this.creatorPosts.get(position).getPostType().equals(ConstantRegistry.IMAGE)){
            return 1;
        }else if(this.creatorPosts.get(position).getPostType().equals(ConstantRegistry.VIDEO)) {
            return 2;
        } else{
            return 3;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if(viewType == 1){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.creators_discover_list_item, parent, false);

            return new DiscoverAdapter.ImageViewHolder(itemView);
        }else if(viewType == 2){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.creators_discover_video_list_item, parent, false);

            return new DiscoverAdapter.VideoViewHolder(itemView);
        }else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.creators_discover_list_text_item, parent, false);

            return new DiscoverAdapter.TextViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CreatorPost creatorPost = creatorPosts.get(position);
        if(holder.getItemViewType() == 1){
            ImageViewHolder imageViewHolder = (ImageViewHolder)holder;
            imageViewHolder.ivDiscoverConnect.setVisibility(View.INVISIBLE);
            imageViewHolder.tvDiscoverUserName.setText(creatorPost.getCreatorsName());
            Picasso.with(context).load(ConstantRegistry.IMAGE_URL_PREFIX.concat(creatorPost.getPostUrls().get(0)))
                    .into(imageViewHolder.ivDiscoverLastPost);
            Picasso.with(context).
                    load(ConstantRegistry.IMAGE_URL_PREFIX.concat(creatorPost.getCreatorProfilePicUrl())).
                    transform(new ImageCircleTransformUtil()).
                    into(imageViewHolder.ivDiscoverProfilePic);
        }else if(holder.getItemViewType() == 2){
            Timeline.Window window;
            DataSource.Factory mediaDataSourceFactory;
            DefaultTrackSelector trackSelector;
            boolean shouldAutoPlay;
            BandwidthMeter bandwidthMeter;
            SimpleExoPlayer player;
            DefaultExtractorsFactory extractorsFactory;
            shouldAutoPlay = true;
            bandwidthMeter = new DefaultBandwidthMeter();
            mediaDataSourceFactory =
                    new DefaultDataSourceFactory(context, Util.getUserAgent(context, "mediaPlayerSample"),
                            (TransferListener<? super DataSource>) bandwidthMeter);
            window = new Timeline.Window();

            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);

            trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

            player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
            player.setPlayWhenReady(shouldAutoPlay);
            player.setVolume(0.0f);
            extractorsFactory = new DefaultExtractorsFactory();

            VideoViewHolder videoViewHolder = (VideoViewHolder)holder;
            videoViewHolder.ivDiscoverConnectVid.setVisibility(View.INVISIBLE);
            videoViewHolder.tvDiscoverUserNameVid.setText(creatorPost.getCreatorsName());
            videoViewHolder.vvDiscoverLastPostVid.requestFocus();
            videoViewHolder.vvDiscoverLastPostVid.hideController();
            videoViewHolder.vvDiscoverLastPostVid.setPlayer(player);
            MediaSource mediaSource =
                    new ExtractorMediaSource(Uri.parse(ConstantRegistry.IMAGE_URL_PREFIX.concat(creatorPost.getPostUrls().get(0))),
                            mediaDataSourceFactory, extractorsFactory, null, null);
            LoopingMediaSource loopingSource = new LoopingMediaSource(mediaSource);
            player.prepare(loopingSource);

            players.add(player);

            Picasso.with(context).
                    load(ConstantRegistry.IMAGE_URL_PREFIX.concat(creatorPost.getCreatorProfilePicUrl())).
                    transform(new ImageCircleTransformUtil()).
                    into(videoViewHolder.ivDiscoverProfilePicVid);
        }else {
            TextViewHolder textViewHolder = (TextViewHolder)holder;
            textViewHolder.ivDiscoverTextConnect.setVisibility(View.INVISIBLE);
            textViewHolder.tvDiscoverTextUserName.setText(creatorPost.getCreatorsName());
            textViewHolder.tvDiscoverTextLastPost.setText(
                    creatorPost.getPostText()
            );
            Picasso.with(context).
                    load(ConstantRegistry.IMAGE_URL_PREFIX.concat(creatorPost.getCreatorProfilePicUrl())).
                    transform(new ImageCircleTransformUtil()).
                    into(textViewHolder.ivDiscoverTextProfilePic);
        }
//        if(creatorPost.getCreatorsName().equals(((CreatorsActivity)context).getUserName(context))){
//            if(holder.ivDiscoverConnect.getVisibility() == View.VISIBLE){
//                holder.ivDiscoverConnect.setVisibility(View.INVISIBLE);
//            }
//        }else{
//            if(holder.ivDiscoverConnect.getVisibility() == View.INVISIBLE){
//                holder.ivDiscoverConnect.setVisibility(View.VISIBLE);
//            }
//            if(creatorPost.getFollowingThisCreator() == 1){
//                Picasso.with(context).load(R.drawable.following_256).into(holder.ivDiscoverConnect);
//            }else if(creatorPost.getFollowingThisCreator() == 0){
//                Picasso.with(context).load(R.drawable.connect_req_256).into(holder.ivDiscoverConnect);
//            }
//        }
    }

    @Override
    public int getItemCount() {
        return creatorPosts.size();
    }

    public void stopVideoPlayers(){
        for (SimpleExoPlayer player : players){
            if(player != null){
                player.stop();
                player.release();
            }
        }
    }
}
