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
import nl.mwsoft.www.chatster.modelLayer.helper.recyclerView.RecyclerTouchListener;
import nl.mwsoft.www.chatster.modelLayer.helper.recyclerView.RecyclerViewClickListener;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorContact;
import nl.mwsoft.www.chatster.viewLayer.creators.CreatorsActivity;
import nl.mwsoft.www.chatster.viewLayer.creators.adapter.FollowersAdapter;

public class CreatorsFollowersFragment extends Fragment {


    private ArrayList<CreatorContact> creatorFollowers;
    private FollowersAdapter followersAdapter;
    @BindView(R.id.rvCreatorsFollowers)
    RecyclerView rvCreatorsFollowers;
    @BindView(R.id.tbCreatorsFollowers)
    Toolbar tbCreatorsFollowers;
    @BindView(R.id.ivCreatorsFollowersBack)
    ImageView ivCreatorsFollowersBack;
    private Unbinder unbinder;
    private CreatorsActivity creatorsActivity;
    public static final String CREATOR_FOLLOWERS = "creatorFollowers";


    public static CreatorsFollowersFragment newInstance(ArrayList<CreatorContact> creatorFollowers) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(CREATOR_FOLLOWERS, creatorFollowers);
        CreatorsFollowersFragment fragment = new CreatorsFollowersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_creators_followers, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle arguments = getArguments();
        if(arguments != null){
            creatorFollowers = arguments.getParcelableArrayList(CREATOR_FOLLOWERS);
        }else{
            creatorFollowers = new ArrayList<>();
        }

        ivCreatorsFollowersBack.setOnClickListener(backListener);

        followersAdapter = new FollowersAdapter(creatorFollowers);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        rvCreatorsFollowers.setLayoutManager(layoutManager);
        rvCreatorsFollowers.setItemAnimator(new DefaultItemAnimator());
        rvCreatorsFollowers.setAdapter(followersAdapter);
        rvCreatorsFollowers.addOnItemTouchListener(new RecyclerTouchListener(view.getContext(),
                rvCreatorsFollowers, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
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
