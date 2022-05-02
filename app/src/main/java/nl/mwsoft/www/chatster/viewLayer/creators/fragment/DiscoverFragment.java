package nl.mwsoft.www.chatster.viewLayer.creators.fragment;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.event.creators.SearchCreatorsEvent;
import nl.mwsoft.www.chatster.modelLayer.helper.recyclerView.RecyclerTouchListener;
import nl.mwsoft.www.chatster.modelLayer.helper.recyclerView.RecyclerViewClickListener;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorContact;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorPost;
import nl.mwsoft.www.chatster.viewLayer.creators.CreatorsActivity;
import nl.mwsoft.www.chatster.viewLayer.creators.adapter.ConnectSearchResultAdapter;
import nl.mwsoft.www.chatster.viewLayer.creators.adapter.DiscoverAdapter;

public class DiscoverFragment  extends Fragment {

    private static final String CREATOR_POSTS = "creatorPosts";
    private ArrayList<CreatorPost> creatorPosts;
    private DiscoverAdapter discoverAdapter;
    @BindView(R.id.rvCreatorDiscover)
    RecyclerView rvCreatorDiscover;
    @BindView(R.id.ivDiscoverBack)
    ImageView ivDiscoverBack;
    @BindView(R.id.rlNoDiscoverPosts)
    RelativeLayout rlNoDiscoverPosts;
    private Unbinder unbinder;
    private CreatorsActivity creatorsActivity;

    private ArrayList<CreatorContact> creatorContacts;
    private ConnectSearchResultAdapter connectSearchResultAdapter;
    @BindView(R.id.rvCreatorConnectSearchResults)
    RecyclerView rvCreatorConnectSearchResults;
    public static final String CREATOR_CONTACTS = "creatorContacts";


    public static DiscoverFragment newInstance(ArrayList<CreatorPost> creatorPosts, ArrayList<CreatorContact> creatorContacts) {

        Bundle args = new Bundle();
        args.putParcelableArrayList(CREATOR_POSTS,creatorPosts);
        args.putParcelableArrayList(CREATOR_CONTACTS,creatorContacts);
        DiscoverFragment fragment = new DiscoverFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_creators_discover, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivDiscoverBack.setOnClickListener(backListener);

        Bundle arguments = getArguments();
        if(arguments != null){
            creatorPosts = arguments.getParcelableArrayList(CREATOR_POSTS);
            creatorContacts = arguments.getParcelableArrayList(CREATOR_CONTACTS);
            if(creatorPosts != null){
                if(creatorPosts.size() > 0){
                    if(rlNoDiscoverPosts.getVisibility() == View.VISIBLE) {
                        rlNoDiscoverPosts.setVisibility(View.GONE);
                    }
                }else {
                    if(rlNoDiscoverPosts.getVisibility() == View.GONE) {
                        rlNoDiscoverPosts.setVisibility(View.VISIBLE);
                    }
                }
            }else{
                creatorPosts = new ArrayList<>();
                creatorContacts = new ArrayList<>();
                if(rlNoDiscoverPosts.getVisibility() == View.GONE) {
                    rlNoDiscoverPosts.setVisibility(View.VISIBLE);
                }
            }
        }else{
            creatorPosts = new ArrayList<>();
            creatorContacts = new ArrayList<>();
            if(rlNoDiscoverPosts.getVisibility() == View.GONE) {
                rlNoDiscoverPosts.setVisibility(View.VISIBLE);
            }
        }


        discoverAdapter = new DiscoverAdapter(creatorPosts);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        rvCreatorDiscover.setLayoutManager(mLayoutManager);
        rvCreatorDiscover.setItemAnimator(new DefaultItemAnimator());
        rvCreatorDiscover.setAdapter(discoverAdapter);
        rvCreatorDiscover.addOnItemTouchListener(new RecyclerTouchListener(view.getContext(),
                rvCreatorDiscover, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        SearchView searchView = (SearchView) view.findViewById(R.id.searchViewConnect);
        EditText searchEditText = (EditText) searchView.findViewById(R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.colorWhite));
        searchEditText.setHintTextColor(getResources().getColor(R.color.colorWhite));
        searchView.setOnQueryTextListener(searchQueryListener);
        searchView.setOnSearchClickListener(onClickSearchIconListener);
        searchView.setOnCloseListener(onSearchClosedListener);


        connectSearchResultAdapter = new ConnectSearchResultAdapter(creatorContacts);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        rvCreatorConnectSearchResults.setLayoutManager(layoutManager);
        rvCreatorConnectSearchResults.setItemAnimator(new DefaultItemAnimator());
        rvCreatorConnectSearchResults.setAdapter(connectSearchResultAdapter);
        rvCreatorConnectSearchResults.addOnItemTouchListener(new RecyclerTouchListener(view.getContext(),
                rvCreatorConnectSearchResults, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    public SearchView.OnQueryTextListener searchQueryListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if(rlNoDiscoverPosts.getVisibility() == View.VISIBLE) {
                rlNoDiscoverPosts.setVisibility(View.GONE);
            }

            if(!newText.isEmpty()){
                creatorsActivity.searchCreator(creatorsActivity.getUserId(creatorsActivity), newText);
            }

            return false;
        }
    };

    public View.OnClickListener onClickSearchIconListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(rlNoDiscoverPosts.getVisibility() == View.VISIBLE) {
                rlNoDiscoverPosts.setVisibility(View.GONE);
            }

            if(rvCreatorDiscover.getVisibility()==View.VISIBLE){
                rvCreatorDiscover.setVisibility(View.GONE);
            }
            if(rvCreatorConnectSearchResults.getVisibility()==View.GONE){
                rvCreatorConnectSearchResults.setVisibility(View.VISIBLE);
            }
        }
    };

    public SearchView.OnCloseListener onSearchClosedListener = new SearchView.OnCloseListener() {
        @Override
        public boolean onClose() {
            if(rlNoDiscoverPosts.getVisibility() == View.VISIBLE) {
                rlNoDiscoverPosts.setVisibility(View.GONE);
            }

            if(rvCreatorDiscover.getVisibility()==View.GONE){
                rvCreatorDiscover.setVisibility(View.VISIBLE);
            }
            if(rvCreatorConnectSearchResults.getVisibility()==View.VISIBLE){
                rvCreatorConnectSearchResults.setVisibility(View.GONE);
            }
            return false;
        }
    };


    private View.OnClickListener backListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            creatorsActivity.onBackPressed();
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        discoverAdapter.stopVideoPlayers();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
        discoverAdapter.stopVideoPlayers();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        creatorsActivity = (CreatorsActivity) context;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SearchCreatorsEvent event) {
        if(event.getCreatorContacts() != null){
            creatorContacts = event.getCreatorContacts();
            if(creatorContacts.size() > 0){
                if(rlNoDiscoverPosts.getVisibility() == View.VISIBLE) {
                    rlNoDiscoverPosts.setVisibility(View.GONE);
                }
            }
            connectSearchResultAdapter.notifyDataSetChanged();
        }
    }
}

