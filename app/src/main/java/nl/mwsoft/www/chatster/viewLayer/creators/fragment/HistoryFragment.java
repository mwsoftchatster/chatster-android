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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.helper.recyclerView.RecyclerTouchListener;
import nl.mwsoft.www.chatster.modelLayer.helper.recyclerView.RecyclerViewClickListener;
import nl.mwsoft.www.chatster.modelLayer.model.HistoryItem;
import nl.mwsoft.www.chatster.viewLayer.creators.CreatorsActivity;
import nl.mwsoft.www.chatster.viewLayer.creators.adapter.HistoryAdapter;

public class HistoryFragment extends Fragment {

    public static final String HISTORY = "history";
    private ArrayList<HistoryItem> historyItems;
    private HistoryAdapter historyAdapter;
    @BindView(R.id.rvHistory)
    RecyclerView rvHistory;
    @BindView(R.id.ivHistoryBack)
    ImageView ivHistoryBack;
    @BindView(R.id.rlNoHistory)
    RelativeLayout rlNoHistory;
    private Unbinder unbinder;
    private CreatorsActivity creatorsActivity;

    public static HistoryFragment newInstance(ArrayList<HistoryItem> historyItems) {

        Bundle args = new Bundle();
        args.putParcelableArrayList(HISTORY, historyItems);
        HistoryFragment fragment = new HistoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_creators_history, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivHistoryBack.setOnClickListener(backListener);

        Bundle arguments = getArguments();

        if(arguments != null){
            historyItems = arguments.getParcelableArrayList(HISTORY);
            if(historyItems != null){
                if(historyItems.size() > 0){
                    if(rlNoHistory.getVisibility() == View.VISIBLE) {
                        rlNoHistory.setVisibility(View.GONE);
                    }
                }else {
                    if(rlNoHistory.getVisibility() == View.GONE) {
                        rlNoHistory.setVisibility(View.VISIBLE);
                    }
                }
            }else{
                historyItems = new ArrayList<>();
                if(rlNoHistory.getVisibility() == View.GONE) {
                    rlNoHistory.setVisibility(View.VISIBLE);
                }
            }
        }else{
            historyItems = new ArrayList<>();
            if(rlNoHistory.getVisibility() == View.GONE) {
                rlNoHistory.setVisibility(View.VISIBLE);
            }
        }

        historyAdapter = new HistoryAdapter(historyItems);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        rvHistory.setLayoutManager(mLayoutManager);
        rvHistory.setItemAnimator(new DefaultItemAnimator());
        rvHistory.setAdapter(historyAdapter);
        rvHistory.addOnItemTouchListener(new RecyclerTouchListener(view.getContext(),
                rvHistory, new RecyclerViewClickListener() {
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        creatorsActivity = (CreatorsActivity) context;
    }
}
