package nl.mwsoft.www.chatster.viewLayer.creators.fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.util.dateTime.ChatsterDateTimeUtil;
import nl.mwsoft.www.chatster.modelLayer.helper.util.image.ImageCircleTransformUtil;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorPost;
import nl.mwsoft.www.chatster.viewLayer.creators.CreatorsActivity;


public class PostDetailFragment extends Fragment {

    private static final String POST = "post";
    private static final String POSITION = "position";
    private CreatorPost creatorPost;
    @BindView(R.id.ivPostDetailsComments)
    ImageView ivPostDetailsComments;
    @BindView(R.id.ivPostDetailsBack)
    ImageView ivPostDetailsBack;
    @BindView(R.id.ivPostDetailsDeletePost)
    ImageView ivPostDetailsDeletePost;
    @BindView(R.id.ivPostDetailsLike)
    ImageView ivPostDetailsLike;
    @BindView(R.id.tvPostDetailsLikeCount)
    TextView tvPostDetailsLikeCount;
    @BindView(R.id.tvPostDetailsCommentCount)
    TextView tvPostDetailsCommentCount;
    @BindView(R.id.tvPostDetailsUserName)
    TextView tvPostDetailsUserName;
    @BindView(R.id.tvPostDetailsCreated)
    TextView tvPostDetailsCreated;
    @BindView(R.id.tvPostDetailsCaptionUserName)
    TextView tvPostDetailsCaptionUserName;
    @BindView(R.id.tvPostDetailsCaption)
    TextView tvPostDetailsCaption;
    @BindView(R.id.ivPostDetailsImagePost)
    ImageView ivPostDetailsImagePost;
    @BindView(R.id.vvPostDetailsVideoPost)
    SimpleExoPlayerView vvPostDetailsVideoPost;
    @BindView(R.id.tvPostDetailsTextPost)
    TextView tvPostDetailsTextPost;
    @BindView(R.id.ivPostDetailsProfilePic)
    ImageView ivPostDetailsProfilePic;
    private CreatorsActivity creatorsActivity;
    private Unbinder unbinder;
    private int postPosition = 0;
    private ChatsterDateTimeUtil chatsterDateTimeUtil;
    private SimpleExoPlayer player;

    public static PostDetailFragment newInstance(CreatorPost creatorPost, int position) {
        Bundle args = new Bundle();
        args.putParcelable(POST, creatorPost);
        args.putInt(POSITION, position);
        PostDetailFragment fragment = new PostDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_creators_post_details, container, false);
        unbinder = ButterKnife.bind(this, view);
        chatsterDateTimeUtil = new ChatsterDateTimeUtil();
        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivPostDetailsComments.setOnClickListener(commentsListener);
        ivPostDetailsBack.setOnClickListener(detailsBackListener);
        ivPostDetailsLike.setOnClickListener(likePostListener);
        Bundle arguments = getArguments();
        if(arguments != null){
            creatorPost = arguments.getParcelable(POST);
            postPosition = arguments.getInt(POSITION);
            if(creatorPost != null) {
                if(creatorPost.getCreatorsName().equals(creatorsActivity.getUserName(view.getContext()))){
                    if(ivPostDetailsDeletePost.getVisibility() == View.INVISIBLE) {
                        ivPostDetailsDeletePost.setVisibility(View.VISIBLE);
                    }
                    ivPostDetailsDeletePost.setOnClickListener(deletePostListener);
                }else{
                    if(ivPostDetailsDeletePost.getVisibility() == View.VISIBLE) {
                        ivPostDetailsDeletePost.setVisibility(View.INVISIBLE);
                    }
                }
                tvPostDetailsLikeCount.setText(String.valueOf(creatorPost.getLikes()));
                tvPostDetailsCommentCount.setText(String.valueOf(creatorPost.getComments()));
                tvPostDetailsUserName.setText(creatorPost.getCreatorsName());
                tvPostDetailsCreated.setText(getPostCreated(creatorPost.getPostCreated()));
                tvPostDetailsCaptionUserName.setText(creatorPost.getCreatorsName());
                tvPostDetailsCaption.setText(creatorPost.getPostCaption());
                if(creatorPost.getPostType().equals(ConstantRegistry.IMAGE)){
                    vvPostDetailsVideoPost.setVisibility(View.INVISIBLE);
                    ivPostDetailsImagePost.setVisibility(View.VISIBLE);
                    Picasso.with(view.getContext()).
                            load(ConstantRegistry.IMAGE_URL_PREFIX.concat(creatorPost.getPostUrls().get(0))).into(ivPostDetailsImagePost);
                }else if(creatorPost.getPostType().equals(ConstantRegistry.VIDEO)){
                    Timeline.Window window;
                    DataSource.Factory mediaDataSourceFactory;
                    DefaultTrackSelector trackSelector;
                    boolean shouldAutoPlay;
                    BandwidthMeter bandwidthMeter;
                    DefaultExtractorsFactory extractorsFactory;
                    shouldAutoPlay = true;
                    bandwidthMeter = new DefaultBandwidthMeter();
                    mediaDataSourceFactory =
                            new DefaultDataSourceFactory(creatorsActivity, Util.getUserAgent(creatorsActivity, "mediaPlayerSample"),
                                    (TransferListener<? super DataSource>) bandwidthMeter);
                    window = new Timeline.Window();

                    TrackSelection.Factory videoTrackSelectionFactory =
                            new AdaptiveTrackSelection.Factory(bandwidthMeter);

                    trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

                    player = ExoPlayerFactory.newSimpleInstance(creatorsActivity, trackSelector);
                    player.setPlayWhenReady(shouldAutoPlay);
                    extractorsFactory = new DefaultExtractorsFactory();
                    ivPostDetailsImagePost.setVisibility(View.INVISIBLE);
                    vvPostDetailsVideoPost.setVisibility(View.VISIBLE);
                    vvPostDetailsVideoPost.requestFocus();
                    vvPostDetailsVideoPost.hideController();
                    vvPostDetailsVideoPost.setPlayer(player);
                    vvPostDetailsVideoPost.setOnTouchListener(videoPostListener);
                    MediaSource mediaSource =
                            new ExtractorMediaSource(Uri.parse(ConstantRegistry.IMAGE_URL_PREFIX.concat(creatorPost.getPostUrls().get(0))),
                                    mediaDataSourceFactory, extractorsFactory, null, null);
                    LoopingMediaSource loopingSource = new LoopingMediaSource(mediaSource);
                    player.prepare(loopingSource);
                }else{
                    vvPostDetailsVideoPost.setVisibility(View.INVISIBLE);
                    ivPostDetailsImagePost.setVisibility(View.INVISIBLE);
                    tvPostDetailsTextPost.setVisibility(View.VISIBLE);
                    tvPostDetailsTextPost.setText(creatorPost.getPostText());
                }

                Picasso.with(view.getContext()).
                        load(ConstantRegistry.IMAGE_URL_PREFIX.concat(creatorPost.getCreatorProfilePicUrl())).
                        transform(new ImageCircleTransformUtil()).
                        into(ivPostDetailsProfilePic);
                if(creatorsActivity.getCreatorsPostIsLiked(view.getContext(), creatorPost.getUuid()) != 0){
                    Picasso.with(view.getContext()).load(R.drawable.liked_256).into(ivPostDetailsLike);
                }else{
                    Picasso.with(view.getContext()).load(R.drawable.like_256).into(ivPostDetailsLike);
                }
            }else{
                creatorPost = new CreatorPost();
            }
        }else{
            Toast.makeText(creatorsActivity, view.getContext().getString(R.string.smth_went_wrong),Toast.LENGTH_LONG).show();
        }
    }

    private View.OnTouchListener videoPostListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(player.getVolume() > 0.0f) {
                player.setVolume(0.0f);
            }else{
                player.setVolume(1.0f);
            }

            return false;
        }
    };

    private View.OnClickListener commentsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            creatorsActivity.navigateToCommentsActivity(creatorPost, creatorsActivity);
        }
    };

    private View.OnClickListener detailsBackListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            creatorsActivity.onBackPressed();
        }
    };

    private View.OnClickListener deletePostListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            creatorsActivity.showDeletePostDialog(creatorPost.getUuid());
        }
    };

    private View.OnClickListener likePostListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(creatorsActivity.getCreatorsPostIsLiked(v.getContext(),creatorPost.getUuid()) == 0){
                long currentLikeCount = Long.parseLong(tvPostDetailsLikeCount.getText().toString());
                long updatedLikes = currentLikeCount + 1;

                creatorPost.setLikes(updatedLikes);
                creatorsActivity.likePost(creatorsActivity.getUserName(creatorsActivity),creatorPost, postPosition,
                        creatorsActivity.getUserProfilePicUrl(creatorsActivity), true);
                Picasso.with(v.getContext()).load(R.drawable.liked_256).into(ivPostDetailsLike);
                tvPostDetailsLikeCount.setText(String.valueOf(updatedLikes));
                if(creatorsActivity.getCreatorsPostExists(v.getContext(), creatorPost.getUuid())){
                    creatorsActivity.updateCreatorPostIsLiked(creatorPost.getUuid(),1, v.getContext());
                }else{
                    creatorsActivity.insertCreatorPostIsLiked(creatorPost.getUuid(), v.getContext());
                }
            }else{
                long currentLikeCount = Long.parseLong(tvPostDetailsLikeCount.getText().toString());
                if(currentLikeCount > 0){
                    long updatedLikes = currentLikeCount - 1;

                    creatorPost.setLikes(updatedLikes);
                    creatorsActivity.unlikePost(creatorsActivity.getUserName(creatorsActivity),creatorPost, postPosition,
                            creatorsActivity.getUserProfilePicUrl(creatorsActivity), true);
                    Picasso.with(v.getContext()).load(R.drawable.like_256).into(ivPostDetailsLike);
                    tvPostDetailsLikeCount.setText(String.valueOf(updatedLikes));

                    creatorsActivity.updateCreatorPostIsLiked(creatorPost.getUuid(),0, v.getContext());
                }else{
                    Toast.makeText(v.getContext(), R.string.zero_likes,Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    private View.OnClickListener editPostListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            creatorsActivity.editPost(creatorPost);
        }
    };

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
    public void onAttach(Context context) {
        super.onAttach(context);
        creatorsActivity = (CreatorsActivity) context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if(player != null){
            player.stop();
            player.release();
        }
    }
}
