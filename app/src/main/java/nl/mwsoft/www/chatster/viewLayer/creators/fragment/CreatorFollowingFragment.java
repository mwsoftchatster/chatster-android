package nl.mwsoft.www.chatster.viewLayer.creators.fragment;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorContact;
import nl.mwsoft.www.chatster.viewLayer.creators.CreatorsActivity;
import nl.mwsoft.www.chatster.viewLayer.creators.adapter.FollowingAdapter;

public class CreatorFollowingFragment extends Fragment {


    private ArrayList<CreatorContact> creatorFollowers;
    private FollowingAdapter followingAdapter;
    @BindView(R.id.rvCreatorsFollowing)
    RecyclerView rvCreatorsFollowing;
    @BindView(R.id.tbCreatorsFollowing)
    Toolbar tbCreatorsFollowing;
    @BindView(R.id.ivCreatorsFollowingBack)
    ImageView ivCreatorsFollowingBack;
    private Unbinder unbinder;
    private CreatorsActivity creatorsActivity;
    public static final String CREATOR_FOLLOWING = "creatorFollowing";


    public static CreatorFollowingFragment newInstance(ArrayList<CreatorContact> creatorFollowers) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(CREATOR_FOLLOWING, creatorFollowers);
        CreatorFollowingFragment fragment = new CreatorFollowingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_creator_following, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle arguments = getArguments();
        if(arguments != null){
            creatorFollowers = arguments.getParcelableArrayList(CREATOR_FOLLOWING);
        }else{
            creatorFollowers = new ArrayList<>();
        }

        ivCreatorsFollowingBack.setOnClickListener(backListener);

        followingAdapter = new FollowingAdapter(creatorFollowers);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        rvCreatorsFollowing.setLayoutManager(layoutManager);
        rvCreatorsFollowing.setItemAnimator(new DefaultItemAnimator());
        rvCreatorsFollowing.setAdapter(followingAdapter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    public View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            creatorsActivity.onBackPressed();
        }
    };

}
