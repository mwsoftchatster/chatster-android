package nl.mwsoft.www.chatster.viewLayer.creators.fragment;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.modelLayer.helper.recyclerView.RecyclerTouchListener;
import nl.mwsoft.www.chatster.modelLayer.helper.recyclerView.RecyclerViewClickListener;
import nl.mwsoft.www.chatster.modelLayer.helper.util.image.ImageCircleTransformUtil;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorContact;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorPost;
import nl.mwsoft.www.chatster.viewLayer.creators.CreatorsActivity;
import nl.mwsoft.www.chatster.viewLayer.creators.adapter.ProfileGridAdapter;

public class ProfileFragment extends Fragment {

    public static final String POSTS = "posts";
    private ArrayList<CreatorPost> creatorPosts;
    private ProfileGridAdapter profileGridAdapter;
    private CreatorContact creatorContact;
    @BindView(R.id.rvCreatorPosts)
    RecyclerView rvCreatorPosts;
    @BindView(R.id.ivProfileBack)
    ImageView ivProfileBack;
    @BindView(R.id.ivCreatorProfileConnectionStatus)
    ImageView ivCreatorProfileConnectionStatus;
    @BindView(R.id.ivCreatorProfilePic)
    ImageView ivCreatorProfilePic;
    @BindView(R.id.ivCreatorProfileDashboard)
    ImageView ivCreatorProfileDashboard;
    @BindView(R.id.tvCreatorName)
    TextView tvCreatorName;
    @BindView(R.id.tvCreatorStatus)
    TextView tvCreatorStatus;
    @BindView(R.id.tvCreatorWebsite)
    TextView tvCreatorWebsite;
    @BindView(R.id.rlCreatorDashboard)
    RelativeLayout rlCreatorDashboard;
    @BindView(R.id.tvCreatorFollowersCount)
    TextView tvCreatorFollowersCount;
    @BindView(R.id.tvCreatorFollowingCount)
    TextView tvCreatorFollowingCount;
    @BindView(R.id.tvCreatorTotalLikesCount)
    TextView tvCreatorTotalLikesCount;
    @BindView(R.id.tvCreatorTotalProfileViewsCount)
    TextView tvCreatorTotalProfileViewsCount;
    @BindView(R.id.tvCreatorTotalPostsCount)
    TextView tvCreatorTotalPostsCount;
    @BindView(R.id.rlProfileNoPosts)
    RelativeLayout rlProfileNoPosts;

    private Unbinder unbinder;
    private CreatorsActivity creatorsActivity;

    public static ProfileFragment newInstance(CreatorContact creatorContact) {
        
        Bundle args = new Bundle();
        args.putParcelable(ConstantRegistry.CREATORS_PROFILE, creatorContact);
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_creators_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arguments = getArguments();
        if(arguments != null){
            creatorContact = arguments.getParcelable(ConstantRegistry.CREATORS_PROFILE);
            if(creatorContact != null) {
                configureCreatorProfile();
                creatorPosts = new ArrayList<>();
                creatorPosts.addAll(creatorContact.getCreatorPosts());
                if(creatorPosts != null){
                    if(creatorPosts.size() > 0){
                        if(rlProfileNoPosts.getVisibility() == View.VISIBLE) {
                            rlProfileNoPosts.setVisibility(View.GONE);
                        }
                    }else {
                        if(rlProfileNoPosts.getVisibility() == View.GONE) {
                            rlProfileNoPosts.setVisibility(View.VISIBLE);
                        }
                    }
                }else{
                    if(rlProfileNoPosts.getVisibility() == View.GONE) {
                        rlProfileNoPosts.setVisibility(View.VISIBLE);
                    }
                }
                configureCreatorPostsRecyclerView(view);
            }else{
                Toast.makeText(creatorsActivity, creatorsActivity.getString(R.string.smth_went_wrong),Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(creatorsActivity, creatorsActivity.getString(R.string.smth_went_wrong),Toast.LENGTH_LONG).show();
        }
    }

    private void configureCreatorProfile() {
        ivProfileBack.setOnClickListener(backListener);
        if(creatorContact.getCreatorId().equals(creatorsActivity.getUserName(creatorsActivity))){
            Picasso.with(creatorsActivity).load(R.drawable.edit_creator_profile_256).into(ivCreatorProfileConnectionStatus);
        }else{
            if(creatorContact.getFollowingThisCreator() == 1){
                Picasso.with(creatorsActivity).load(R.drawable.following_256)
                        .into(ivCreatorProfileConnectionStatus);
            }else if(creatorContact.getFollowingThisCreator() == 0){
                Picasso.with(creatorsActivity).load(R.drawable.connect_req_256)
                        .into(ivCreatorProfileConnectionStatus);
            }
        }
        ivCreatorProfileConnectionStatus.setOnClickListener(connectListener);
        Picasso.with(creatorsActivity).
                load(ConstantRegistry.IMAGE_URL_PREFIX.concat(creatorContact.getProfilePic())).
                transform(new ImageCircleTransformUtil()).
                into(ivCreatorProfilePic);
        Glide.with(creatorsActivity)
                .load(ConstantRegistry.IMAGE_URL_PREFIX.concat(creatorContact.getProfilePic()))
                .into(ivCreatorProfilePic);
        ivCreatorProfileDashboard.setOnClickListener(dashboardListener);
        tvCreatorName.setText(creatorContact.getCreatorId());
        tvCreatorStatus.setText(creatorContact.getStatusMessage());
        tvCreatorWebsite.setText(creatorContact.getWebsite());
        tvCreatorFollowersCount.setText(String.valueOf(creatorContact.getCreatorFollowers()));
        tvCreatorFollowersCount.setOnClickListener(followersListener);
        tvCreatorFollowingCount.setText(String.valueOf(creatorContact.getCreatorFollowing()));
        tvCreatorFollowingCount.setOnClickListener(followingListener);
        tvCreatorTotalLikesCount.setText(String.valueOf(creatorContact.getCreatorTotalLikes()));
        tvCreatorTotalProfileViewsCount.setText(String.valueOf(creatorContact.getCreatorProfileViews()));
        tvCreatorTotalPostsCount.setText(String.valueOf(creatorContact.getPosts()));
    }

    private void configureCreatorPostsRecyclerView(View view) {
        profileGridAdapter = new ProfileGridAdapter(creatorPosts);
        StaggeredGridLayoutManager mLayoutManager =
                new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rvCreatorPosts.setLayoutManager(mLayoutManager);
        rvCreatorPosts.setItemAnimator(new DefaultItemAnimator());
        rvCreatorPosts.setAdapter(profileGridAdapter);
        rvCreatorPosts.addOnItemTouchListener(new RecyclerTouchListener(view.getContext(),
                rvCreatorPosts, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            creatorsActivity.onBackPressed();
        }
    };

    private View.OnClickListener followersListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            creatorsActivity.getFollowers(creatorContact.getCreatorId(), creatorsActivity.getUserId(creatorsActivity));
        }
    };

    private View.OnClickListener followingListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            creatorsActivity.getFollowing(creatorContact.getCreatorId(), creatorsActivity.getUserId(creatorsActivity));
        }
    };

    private View.OnClickListener connectListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(creatorsActivity.getUserName(creatorsActivity).equals(creatorContact.getCreatorId())){
                creatorsActivity.navigateToChatsterSettingsActivity(creatorsActivity);
            }else{
                if(creatorContact.getFollowingThisCreator() == 0){
                    creatorsActivity.followCreator(creatorsActivity.getUserId(creatorsActivity),
                            creatorsActivity.getUserName(creatorsActivity),
                            creatorsActivity.getUserProfilePicUrl(creatorsActivity),
                            creatorContact.getCreatorId());
                    Picasso.with(creatorsActivity).load(R.drawable.following_256).into(ivCreatorProfileConnectionStatus);
                    creatorContact.setFollowingThisCreator(1);
                }else{
                    creatorsActivity.unFollowCreator(creatorsActivity.getUserId(creatorsActivity),
                            creatorsActivity.getUserName(creatorsActivity),
                            creatorsActivity.getUserProfilePicUrl(creatorsActivity),
                            creatorContact.getCreatorId());
                    Picasso.with(creatorsActivity).load(R.drawable.connect_req_256).into(ivCreatorProfileConnectionStatus);
                    creatorContact.setFollowingThisCreator(0);
                }
            }
        }
    };

    private View.OnClickListener dashboardListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(rlCreatorDashboard.getVisibility() == View.GONE){
                rlCreatorDashboard.setVisibility(View.VISIBLE);
            }else{
                rlCreatorDashboard.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        profileGridAdapter.stopVideoPlayers();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        creatorsActivity = (CreatorsActivity) context;
        creatorsActivity.currFragmentPosition = 5;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        profileGridAdapter.stopVideoPlayers();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
