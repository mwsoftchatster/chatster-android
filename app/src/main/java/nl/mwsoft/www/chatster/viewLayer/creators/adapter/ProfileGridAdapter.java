package nl.mwsoft.www.chatster.viewLayer.creators.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import nl.mwsoft.www.chatster.modelLayer.model.CreatorPost;
import nl.mwsoft.www.chatster.viewLayer.creators.CreatorsActivity;
import nl.mwsoft.www.chatster.viewLayer.creators.fragment.PostDetailFragment;

public class ProfileGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CreatorPost> creatorPosts;
    private ArrayList<SimpleExoPlayer> players = new ArrayList<>();
    private Context context;
    private CreatorsActivity creatorsActivity;

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivCreatorImagePost;

        public ImageViewHolder(View view) {
            super(view);
            context = view.getContext();
            creatorsActivity = (CreatorsActivity)context;
            ivCreatorImagePost = (ImageView) view.findViewById(R.id.ivCreatorImagePost);
            ivCreatorImagePost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CreatorPost creatorPost = creatorPosts.get(getAdapterPosition());
                    creatorsActivity
                            .loadFragment(
                                    PostDetailFragment.newInstance(creatorPost,getAdapterPosition()), true
                            );
                }
            });
        }
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        public SimpleExoPlayerView vvCreatorVideoPost;

        @SuppressLint("ClickableViewAccessibility")
        public VideoViewHolder(View view) {
            super(view);
            context = view.getContext();
            creatorsActivity = (CreatorsActivity)context;
            vvCreatorVideoPost = (SimpleExoPlayerView) view.findViewById(R.id.vvCreatorVideoPost);
            vvCreatorVideoPost.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    CreatorPost creatorPost = creatorPosts.get(getAdapterPosition());
                    creatorsActivity
                            .loadFragment(
                                    PostDetailFragment.newInstance(creatorPost,getAdapterPosition()), true
                            );
                    return false;
                }
            });
        }
    }

    public class TextViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCreatorGridTextPost;

        public TextViewHolder(View view) {
            super(view);
            context = view.getContext();
            creatorsActivity = (CreatorsActivity)context;
            tvCreatorGridTextPost = (TextView) view.findViewById(R.id.tvCreatorGridTextPost);
            tvCreatorGridTextPost.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CreatorPost creatorPost = creatorPosts.get(getAdapterPosition());
                    creatorsActivity
                            .loadFragment(
                                    PostDetailFragment.newInstance(creatorPost,getAdapterPosition()), true
                            );
                }
            });
        }
    }


    public ProfileGridAdapter(List<CreatorPost> creatorPosts) {
        this.creatorPosts = creatorPosts;
    }

    @Override
    public int getItemViewType(int position) {
        if(this.creatorPosts.get(position).getPostType().equals(ConstantRegistry.IMAGE)){
            return 1;
        }else if(this.creatorPosts.get(position).getPostType().equals(ConstantRegistry.VIDEO)){
            return 2;
        }else{
            return 3;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if(viewType == 1){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.creators_post_image_grid_item, parent, false);

            return new ProfileGridAdapter.ImageViewHolder(itemView);
        }else if(viewType == 2){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.creators_post_video_grid_item, parent, false);

            return new ProfileGridAdapter.VideoViewHolder(itemView);
        }else{
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.creator_post_text_grid_item, parent, false);

            return new ProfileGridAdapter.TextViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CreatorPost creatorPost = creatorPosts.get(position);
        if(holder.getItemViewType() == 1){
            ImageViewHolder imageViewHolder = (ImageViewHolder)holder;
            Picasso.with(context).
                    load(ConstantRegistry.IMAGE_URL_PREFIX.concat(creatorPost.getPostUrls().get(0))).
                    into(imageViewHolder.ivCreatorImagePost);
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
            videoViewHolder.vvCreatorVideoPost.requestFocus();
            videoViewHolder.vvCreatorVideoPost.hideController();
            videoViewHolder.vvCreatorVideoPost.setPlayer(player);
            MediaSource mediaSource =
                    new ExtractorMediaSource(Uri.parse(ConstantRegistry.IMAGE_URL_PREFIX.concat(creatorPost.getPostUrls().get(0))),
                            mediaDataSourceFactory, extractorsFactory, null, null);
            LoopingMediaSource loopingSource = new LoopingMediaSource(mediaSource);
            player.prepare(loopingSource);
            players.add(player);
        }else {
            TextViewHolder textViewHolder = (TextViewHolder) holder;
            textViewHolder.tvCreatorGridTextPost.setText(
                    creatorPost.getPostText()
            );
        }
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



