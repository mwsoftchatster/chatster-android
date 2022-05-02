package nl.mwsoft.www.chatster.viewLayer.creators.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


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
import java.util.HashMap;
import java.util.List;

import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.coordinator.RootCoordinator;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.database.contact.ContactDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.database.creators.CreatorsDatabaseLayer;
import nl.mwsoft.www.chatster.modelLayer.helper.util.dateTime.ChatsterDateTimeUtil;
import nl.mwsoft.www.chatster.modelLayer.helper.util.image.ImageCircleTransformUtil;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorPost;
import nl.mwsoft.www.chatster.viewLayer.creators.CreatorsActivity;

public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<CreatorPost> creatorPosts;
    private HashMap<String, SimpleExoPlayer> players = new HashMap<>();
    private ArrayList<SimpleExoPlayer> playersRegistry = new ArrayList<>();
    public Context context;
    public CreatorsActivity creatorsActivity;
    private ChatsterDateTimeUtil chatsterDateTimeUtil;
    private CreatorsDatabaseLayer creatorsDatabaseLayer;

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivCreatorsProfilePic;
        public ImageView ivImagePost;
        public ImageView ivLike;
        public ImageView ivPostComments;
        public TextView tvUsername;
        public TextView tvLikeCount;
        public TextView tvPostCaptionUserName;
        public TextView tvPostCaption;
        public TextView tvCommentCount;
        public TextView tvPostCreated;

        public ImageViewHolder(View view) {
            super(view);
            context = view.getContext();
            ivCreatorsProfilePic = (ImageView) view.findViewById(R.id.ivCreatorsProfilePic);
            ivImagePost = (ImageView) view.findViewById(R.id.ivImagePost);
            ivLike = (ImageView) view.findViewById(R.id.ivLike);
            ivPostComments = (ImageView) view.findViewById(R.id.ivPostComments);
            tvUsername = (TextView) view.findViewById(R.id.tvUsername);
            tvLikeCount = (TextView) view.findViewById(R.id.tvLikeCount);
            tvPostCaptionUserName = (TextView) view.findViewById(R.id.tvPostCaptionUserName);
            tvPostCaption = (TextView) view.findViewById(R.id.tvPostCaption);
            tvCommentCount = (TextView) view.findViewById(R.id.tvCommentCount);
            tvPostCreated = (TextView) view.findViewById(R.id.tvPostCreated);

            ivPostComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CreatorPost creatorPost = creatorPosts.get(getAdapterPosition());
                    RootCoordinator rootCoordinator = new RootCoordinator();
                    rootCoordinator.navigateToCommentsActivity(creatorPost, context);
                }
            });

            ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(context instanceof CreatorsActivity){
                        CreatorPost creatorPost = creatorPosts.get(getAdapterPosition());

                        if(creatorsDatabaseLayer.getCreatorsPostIsLiked(context,creatorPost.getUuid()) == 0){
                            long currentLikeCount = Long.parseLong(tvLikeCount.getText().toString());
                            long updatedLikes = currentLikeCount + 1;

                            creatorPost.setLikes(updatedLikes);
                            ((CreatorsActivity)context).likePost(((CreatorsActivity)context).getUserName(context),
                                    creatorPost, getAdapterPosition(), ((CreatorsActivity)context).getUserProfilePicUrl(context),
                                    false);
                            Picasso.with(context).load(R.drawable.liked_256).into(ivLike);
                            tvLikeCount.setText(String.valueOf(updatedLikes));
                            if(creatorsDatabaseLayer.getCreatorsPostExists(context, creatorPost.getUuid())){
                                creatorsDatabaseLayer.updateCreatorPostIsLiked(creatorPost.getUuid(),1, context);
                            }else{
                                creatorsDatabaseLayer.insertCreatorPostIsLiked(creatorPost.getUuid(), context);
                            }
                        }else{
                            long currentLikeCount = Long.parseLong(tvLikeCount.getText().toString());
                            if(currentLikeCount > 0){
                                long updatedLikes = currentLikeCount - 1;

                                creatorPost.setLikes(updatedLikes);
                                ((CreatorsActivity)context).unlikePost(((CreatorsActivity)context).getUserName(context),
                                        creatorPost, getAdapterPosition(), ((CreatorsActivity)context).getUserProfilePicUrl(context),
                                        false);
                                Picasso.with(context).load(R.drawable.like_256).into(ivLike);
                                tvLikeCount.setText(String.valueOf(updatedLikes));

                                creatorsDatabaseLayer.updateCreatorPostIsLiked(creatorPost.getUuid(),0, context);
                            }else{
                                Toast.makeText(context, R.string.zero_likes,Toast.LENGTH_LONG).show();
                            }
                        }
                    }else{
                        Toast.makeText(context, context.getString(R.string.smth_went_wrong),Toast.LENGTH_LONG).show();
                    }
                }
            });

            tvUsername.setOnClickListener(new View.OnClickListener() {
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

            ivCreatorsProfilePic.setOnClickListener(new View.OnClickListener() {
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
        }
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivCreatorsProfilePicVid;
        public SimpleExoPlayerView vvVideoPost;
        public ImageView ivLikeVid;
        public ImageView ivPostCommentsVid;
        public TextView tvUsernameVid;
        public TextView tvLikeCountVid;
        public TextView tvPostCaptionUserNameVid;
        public TextView tvPostCaptionVid;
        public TextView tvCommentCountVid;
        public TextView tvPostCreatedVid;

        @SuppressLint("ClickableViewAccessibility")
        public VideoViewHolder(View view) {
            super(view);
            ivCreatorsProfilePicVid = (ImageView) view.findViewById(R.id.ivCreatorsProfilePicVid);
            vvVideoPost = (SimpleExoPlayerView) view.findViewById(R.id.vvVideoPost);
            ivLikeVid = (ImageView) view.findViewById(R.id.ivLikeVid);
            ivPostCommentsVid = (ImageView) view.findViewById(R.id.ivPostCommentsVid);
            tvUsernameVid = (TextView) view.findViewById(R.id.tvUsernameVid);
            tvLikeCountVid = (TextView) view.findViewById(R.id.tvLikeCountVid);
            tvPostCaptionUserNameVid = (TextView) view.findViewById(R.id.tvPostCaptionUserNameVid);
            tvPostCaptionVid = (TextView) view.findViewById(R.id.tvPostCaptionVid);
            tvCommentCountVid = (TextView) view.findViewById(R.id.tvCommentCountVid);
            tvPostCreatedVid = (TextView) view.findViewById(R.id.tvPostCreatedVid);

            vvVideoPost.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    CreatorPost creatorPost = creatorPosts.get(getAdapterPosition());

                    if(players.get(creatorPost.getUuid()).getVolume() > 0.0f) {
                        players.get(creatorPost.getUuid()).setVolume(0.0f);
                    }else{
                        players.get(creatorPost.getUuid()).setVolume(1.0f);
                    }

                    return false;
                }
            });

            ivPostCommentsVid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopVideoPlayers();

                    CreatorPost creatorPost = creatorPosts.get(getAdapterPosition());

                    RootCoordinator rootCoordinator = new RootCoordinator();
                    rootCoordinator.navigateToCommentsActivity(creatorPost, context);
                }
            });

            ivLikeVid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(context instanceof CreatorsActivity){
                        CreatorPost creatorPost = creatorPosts.get(getAdapterPosition());

                        if(creatorsDatabaseLayer.getCreatorsPostIsLiked(context,creatorPost.getUuid()) == 0){
                            long currentLikeCount = Long.parseLong(tvLikeCountVid.getText().toString());
                            long updatedLikes = currentLikeCount + 1;

                            creatorPost.setLikes(updatedLikes);
                            ((CreatorsActivity)context).likePost(((CreatorsActivity)context).getUserName(context),
                                    creatorPost, getAdapterPosition(), ((CreatorsActivity)context).getUserProfilePicUrl(context),
                                    false);
                            Picasso.with(context).load(R.drawable.liked_256).into(ivLikeVid);
                            tvLikeCountVid.setText(String.valueOf(updatedLikes));
                            if(creatorsDatabaseLayer.getCreatorsPostExists(context, creatorPost.getUuid())){
                                creatorsDatabaseLayer.updateCreatorPostIsLiked(creatorPost.getUuid(),1, context);
                            }else{
                                creatorsDatabaseLayer.insertCreatorPostIsLiked(creatorPost.getUuid(), context);
                            }
                        }else{
                            long currentLikeCount = Long.parseLong(tvLikeCountVid.getText().toString());
                            if(currentLikeCount > 0){
                                long updatedLikes = currentLikeCount - 1;

                                creatorPost.setLikes(updatedLikes);
                                ((CreatorsActivity)context).unlikePost(((CreatorsActivity)context).getUserName(context),
                                        creatorPost, getAdapterPosition(), ((CreatorsActivity)context).getUserProfilePicUrl(context),
                                        false);
                                Picasso.with(context).load(R.drawable.like_256).into(ivLikeVid);
                                tvLikeCountVid.setText(String.valueOf(updatedLikes));

                                creatorsDatabaseLayer.updateCreatorPostIsLiked(creatorPost.getUuid(),0, context);
                            }else{
                                Toast.makeText(context, R.string.zero_likes,Toast.LENGTH_LONG).show();
                            }
                        }
                    }else{
                        Toast.makeText(context, context.getString(R.string.smth_went_wrong),Toast.LENGTH_LONG).show();
                    }
                }
            });

            tvUsernameVid.setOnClickListener(new View.OnClickListener() {
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

            ivCreatorsProfilePicVid.setOnClickListener(new View.OnClickListener() {
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
        }
    }

    public class TextViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivCreatorsProfilePicText;
        public TextView tvTextPost;
        public ImageView ivLikeText;
        public ImageView ivPostCommentsText;
        public TextView tvUsernameText;
        public TextView tvLikeCountText;
        public TextView tvPostCaptionUserNameText;
        public TextView tvPostCaptionText;
        public TextView tvCommentCountText;
        public TextView tvPostCreatedText;


        public TextViewHolder(View view) {
            super(view);
            ivCreatorsProfilePicText = (ImageView) view.findViewById(R.id.ivCreatorsProfilePicText);
            tvTextPost = (TextView) view.findViewById(R.id.tvTextPost);
            ivLikeText = (ImageView) view.findViewById(R.id.ivLikeText);
            ivPostCommentsText = (ImageView) view.findViewById(R.id.ivPostCommentsText);
            tvUsernameText = (TextView) view.findViewById(R.id.tvUsernameText);
            tvLikeCountText = (TextView) view.findViewById(R.id.tvLikeCountText);
            tvPostCaptionUserNameText = (TextView) view.findViewById(R.id.tvPostCaptionUserNameText);
            tvPostCaptionText = (TextView) view.findViewById(R.id.tvPostCaptionText);
            tvCommentCountText = (TextView) view.findViewById(R.id.tvCommentCountText);
            tvPostCreatedText = (TextView) view.findViewById(R.id.tvPostCreatedText);

            ivPostCommentsText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CreatorPost creatorPost = creatorPosts.get(getAdapterPosition());
                    RootCoordinator rootCoordinator = new RootCoordinator();
                    rootCoordinator.navigateToCommentsActivity(creatorPost, context);
                }
            });

            ivLikeText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(context instanceof CreatorsActivity){
                        CreatorPost creatorPost = creatorPosts.get(getAdapterPosition());

                        if(creatorsDatabaseLayer.getCreatorsPostIsLiked(context,creatorPost.getUuid()) == 0){
                            long currentLikeCount = Long.parseLong(tvLikeCountText.getText().toString());
                            long updatedLikes = currentLikeCount + 1;

                            creatorPost.setLikes(updatedLikes);
                            ((CreatorsActivity)context).likePost(((CreatorsActivity)context).getUserName(context),
                                    creatorPost, getAdapterPosition(), ((CreatorsActivity)context).getUserProfilePicUrl(context),
                                    false);
                            Picasso.with(context).load(R.drawable.liked_256).into(ivLikeText);
                            tvLikeCountText.setText(String.valueOf(updatedLikes));
                            if(creatorsDatabaseLayer.getCreatorsPostExists(context, creatorPost.getUuid())){
                                creatorsDatabaseLayer.updateCreatorPostIsLiked(creatorPost.getUuid(),1, context);
                            }else{
                                creatorsDatabaseLayer.insertCreatorPostIsLiked(creatorPost.getUuid(), context);
                            }
                        }else{
                            long currentLikeCount = Long.parseLong(tvLikeCountText.getText().toString());
                            if(currentLikeCount > 0){
                                long updatedLikes = currentLikeCount - 1;

                                creatorPost.setLikes(updatedLikes);
                                ((CreatorsActivity)context).unlikePost(((CreatorsActivity)context).getUserName(context),
                                        creatorPost, getAdapterPosition(), ((CreatorsActivity)context).getUserProfilePicUrl(context),
                                        false);
                                Picasso.with(context).load(R.drawable.like_256).into(ivLikeText);
                                tvLikeCountText.setText(String.valueOf(updatedLikes));

                                creatorsDatabaseLayer.updateCreatorPostIsLiked(creatorPost.getUuid(),0, context);
                            }else{
                                Toast.makeText(context, R.string.zero_likes,Toast.LENGTH_LONG).show();
                            }
                        }
                    }else{
                        Toast.makeText(context, context.getString(R.string.smth_went_wrong),Toast.LENGTH_LONG).show();
                    }
                }
            });

            tvUsernameText.setOnClickListener(new View.OnClickListener() {
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

            ivCreatorsProfilePicText.setOnClickListener(new View.OnClickListener() {
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
        }
    }

    public PostsAdapter(List<CreatorPost> creatorPosts, Context context) {
        this.creatorPosts = creatorPosts;
        this.chatsterDateTimeUtil = new ChatsterDateTimeUtil();
        this.creatorsDatabaseLayer = new CreatorsDatabaseLayer(new ContactDatabaseLayer());
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if(this.creatorPosts.get(position).getPostType().equals(ConstantRegistry.IMAGE)){
            return 1;
        }else if(this.creatorPosts.get(position).getPostType().equals(ConstantRegistry.VIDEO)) {
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
                    .inflate(R.layout.creators_post_image_item, parent, false);

            return new PostsAdapter.ImageViewHolder(itemView);
        }else if(viewType == 2){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.creators_post_video_item, parent, false);

            return new PostsAdapter.VideoViewHolder(itemView);
        }else {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.creators_post_text_item, parent, false);

            return new PostsAdapter.TextViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CreatorPost creatorPost = creatorPosts.get(position);

        if(holder.getItemViewType() == 1){
            ImageViewHolder imageViewHolder = (ImageViewHolder)holder;
            if(this.creatorsDatabaseLayer.getCreatorsPostIsLiked(context, creatorPost.getUuid()) != 0){
                Picasso.with(context).load(R.drawable.liked_256).into(imageViewHolder.ivLike);
            }else{
                Picasso.with(context).load(R.drawable.like_256).into(imageViewHolder.ivLike);
            }
            imageViewHolder.tvPostCreated.setText(getPostCreated(creatorPost.getPostCreated()));
            imageViewHolder.tvUsername.setText(creatorPost.getCreatorsName());
            imageViewHolder.tvLikeCount.setText(String.valueOf(creatorPost.getLikes()));
            imageViewHolder.tvPostCaption.setText(creatorPost.getPostCaption());
            imageViewHolder.tvPostCaptionUserName.setText(creatorPost.getCreatorsName());
            imageViewHolder.tvCommentCount.setText(String.valueOf(creatorPost.getComments()));
            Picasso.with(context).
                    load(ConstantRegistry.IMAGE_URL_PREFIX.concat(creatorPost.getPostUrls().get(0))).
                    into(imageViewHolder.ivImagePost);
            Picasso.with(context).
                    load(ConstantRegistry.IMAGE_URL_PREFIX.concat(creatorPost.getCreatorProfilePicUrl())).
                    transform(new ImageCircleTransformUtil()).
                    into(imageViewHolder.ivCreatorsProfilePic);

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
            playersRegistry.add(player);
            player.setPlayWhenReady(shouldAutoPlay);
            if(players.containsKey(creatorPost.getUuid())){
                players.remove(creatorPost.getUuid());
            }
            players.put(creatorPost.getUuid(), player);
            players.get(creatorPost.getUuid()).setVolume(0.0f);
            extractorsFactory = new DefaultExtractorsFactory();

            VideoViewHolder videoViewHolder = (VideoViewHolder)holder;
            if(this.creatorsDatabaseLayer.getCreatorsPostIsLiked(context, creatorPost.getUuid()) != 0){
                Picasso.with(context).load(R.drawable.liked_256).into(videoViewHolder.ivLikeVid);
            }else{
                Picasso.with(context).load(R.drawable.like_256).into(videoViewHolder.ivLikeVid);
            }
            videoViewHolder.tvPostCreatedVid.setText(getPostCreated(creatorPost.getPostCreated()));
            videoViewHolder.tvUsernameVid.setText(creatorPost.getCreatorsName());
            videoViewHolder.tvLikeCountVid.setText(String.valueOf(creatorPost.getLikes()));
            videoViewHolder.tvPostCaptionVid.setText(creatorPost.getPostCaption());
            videoViewHolder.tvPostCaptionUserNameVid.setText(creatorPost.getCreatorsName());
            videoViewHolder.tvCommentCountVid.setText(String.valueOf(creatorPost.getComments()));
            videoViewHolder.vvVideoPost.requestFocus();
            videoViewHolder.vvVideoPost.hideController();
            videoViewHolder.vvVideoPost.setPlayer(player);
            MediaSource mediaSource =
                    new ExtractorMediaSource(Uri.parse(ConstantRegistry.IMAGE_URL_PREFIX.concat(creatorPost.getPostUrls().get(0))),
                    mediaDataSourceFactory, extractorsFactory, null, null);
            LoopingMediaSource loopingSource = new LoopingMediaSource(mediaSource);
            player.prepare(loopingSource);

            Picasso.with(context).
                    load(ConstantRegistry.IMAGE_URL_PREFIX.concat(creatorPost.getCreatorProfilePicUrl())).
                    transform(new ImageCircleTransformUtil()).
                    into(videoViewHolder.ivCreatorsProfilePicVid);
        }else{
            TextViewHolder textViewHolder = (TextViewHolder)holder;
            if(this.creatorsDatabaseLayer.getCreatorsPostIsLiked(context, creatorPost.getUuid()) != 0){
                Picasso.with(context).load(R.drawable.liked_256).into(textViewHolder.ivLikeText);
            }else{
                Picasso.with(context).load(R.drawable.like_256).into(textViewHolder.ivLikeText);
            }
            textViewHolder.tvPostCreatedText.setText(getPostCreated(creatorPost.getPostCreated()));
            textViewHolder.tvUsernameText.setText(creatorPost.getCreatorsName());
            textViewHolder.tvLikeCountText.setText(String.valueOf(creatorPost.getLikes()));
            textViewHolder.tvPostCaptionText.setText(creatorPost.getPostCaption());
            textViewHolder.tvPostCaptionUserNameText.setText(creatorPost.getCreatorsName());
            textViewHolder.tvCommentCountText.setText(String.valueOf(creatorPost.getComments()));
            textViewHolder.tvTextPost.setText(creatorPost.getPostText());
            Picasso.with(context).
                    load(ConstantRegistry.IMAGE_URL_PREFIX.concat(creatorPost.getCreatorProfilePicUrl())).
                    transform(new ImageCircleTransformUtil()).
                    into(textViewHolder.ivCreatorsProfilePicText);

        }

        if(position == (getItemCount()-1)){
            // EventBus.getDefault().post(new LastItemEvent(true));
        }else{
            // EventBus.getDefault().post(new LastItemEvent(false));
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
        return creatorPosts.size();
    }

    public void stopVideoPlayers(){
        for (SimpleExoPlayer player : playersRegistry) {
            player.stop();
            player.release();
        }
    }

    public void muteVideoPlayers(){
        for (SimpleExoPlayer player : playersRegistry) {
            player.setVolume(0.0f);
        }
    }
}

