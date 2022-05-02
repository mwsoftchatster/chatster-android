package nl.mwsoft.www.chatster.viewLayer.creators.fragment;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.event.creators.LastItemEvent;
import nl.mwsoft.www.chatster.modelLayer.event.creators.LoadMorePostsEvent;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorPost;
import nl.mwsoft.www.chatster.viewLayer.creators.CreatorsActivity;
import nl.mwsoft.www.chatster.viewLayer.creators.adapter.PostsAdapter;

public class PostsFragment extends Fragment {

    public static final String POSTS = "posts";
    private ArrayList<CreatorPost> creatorPosts;
    private PostsAdapter postsAdapter;
    @BindView(R.id.rvPosts)
    RecyclerView rvPosts;
    @BindView(R.id.ivPostsLoadMorePosts)
    ImageView ivPostsLoadMorePosts;
    @BindView(R.id.ivPostsBack)
    ImageView ivPostsBack;
    @BindView(R.id.rlNoPosts)
    RelativeLayout rlNoPosts;
    private Unbinder unbinder;
    private CreatorsActivity creatorsActivity;

    public static PostsFragment newInstance(ArrayList<CreatorPost> creatorPosts) {

        Bundle args = new Bundle();
        args.putParcelableArrayList(POSTS, creatorPosts);
        PostsFragment fragment = new PostsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_creators_posts, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle arguments = getArguments();
        if(arguments != null){
            creatorPosts = arguments.getParcelableArrayList(POSTS);
            if(creatorPosts != null){
                if(creatorPosts.size() > 0){
                    if(rlNoPosts.getVisibility() == View.VISIBLE) {
                        rlNoPosts.setVisibility(View.GONE);
                    }
                }else {
                    if(rlNoPosts.getVisibility() == View.GONE) {
                        rlNoPosts.setVisibility(View.VISIBLE);
                    }
                }
            }else{
                creatorPosts = new ArrayList<>();
                if(rlNoPosts.getVisibility() == View.GONE) {
                    rlNoPosts.setVisibility(View.VISIBLE);
                }
            }
        }else{
            creatorPosts = new ArrayList<>();
            if(rlNoPosts.getVisibility() == View.GONE) {
                rlNoPosts.setVisibility(View.VISIBLE);
            }
        }

        // ivPostsLoadMorePosts.setOnClickListener(loadMoreListener);
        ivPostsBack.setOnClickListener(backListener);
        postsAdapter = new PostsAdapter(creatorPosts,creatorsActivity);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        rvPosts.setLayoutManager(mLayoutManager);
        rvPosts.setItemAnimator(new DefaultItemAnimator());
        rvPosts.setAdapter(postsAdapter);
        rvPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                postsAdapter.muteVideoPlayers();
            }
        });
    }

    private View.OnClickListener loadMoreListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            creatorsActivity.loadMorePosts(creatorsActivity.getUserName(creatorsActivity),
                    creatorPosts.get(creatorPosts.size()-1).getPostCreated());
        }
    };

    private View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            creatorsActivity.onBackPressed();
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        creatorsActivity = (CreatorsActivity) context;
        creatorsActivity.currFragmentPosition = 1;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        postsAdapter.stopVideoPlayers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
        postsAdapter.stopVideoPlayers();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LoadMorePostsEvent event) {
        // ivPostsLoadMorePosts.setVisibility(View.GONE);
        if(event.getCreatorPosts() != null){
            creatorPosts = event.getCreatorPosts();
            if(creatorPosts.size() > 0){
                if(rlNoPosts.getVisibility() == View.VISIBLE) {
                    rlNoPosts.setVisibility(View.GONE);
                }
            }
            postsAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LastItemEvent event) {
        if(event.isLastItem()){
            // ivPostsLoadMorePosts.setVisibility(View.VISIBLE);
        }else {
            // ivPostsLoadMorePosts.setVisibility(View.GONE);
        }
    }
}
