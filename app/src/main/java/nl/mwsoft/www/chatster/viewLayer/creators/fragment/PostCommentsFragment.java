package nl.mwsoft.www.chatster.viewLayer.creators.fragment;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.helper.recyclerView.RecyclerTouchListener;
import nl.mwsoft.www.chatster.modelLayer.helper.recyclerView.RecyclerViewClickListener;
import nl.mwsoft.www.chatster.modelLayer.helper.util.dateTime.ChatsterDateTimeUtil;
import nl.mwsoft.www.chatster.modelLayer.model.CreatorPostComment;
import nl.mwsoft.www.chatster.viewLayer.creators.PostCommentsActivity;
import nl.mwsoft.www.chatster.viewLayer.creators.adapter.PostDetailAdapter;

public class PostCommentsFragment extends Fragment {

    private static final String COMMENTS = "comments";
    private static final String POST_UUID = "postUUID";
    private PostDetailAdapter postDetailAdapter;
    @BindView(R.id.rvCreatorPostComments)
    RecyclerView rvCreatorPostComments;
    @BindView(R.id.etComment)
    EditText etComment;
    @BindView(R.id.ivSendComment)
    ImageView ivSendComment;
    private Unbinder unbinder;
    private ArrayList<CreatorPostComment> creatorPostComments;
    private PostCommentsActivity postCommentsActivity;
    private String postUUID;
    private ChatsterDateTimeUtil chatsterDateTimeUtil;

    public static PostCommentsFragment newInstance(ArrayList<CreatorPostComment> creatorPostComments, String postUUID) {

        Bundle args = new Bundle();
        args.putParcelableArrayList(COMMENTS, creatorPostComments);
        args.putString(POST_UUID, postUUID);
        PostCommentsFragment fragment = new PostCommentsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_creators_post_comments, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        );

        Bundle arguments = getArguments();
        if(arguments != null){
            creatorPostComments = arguments.getParcelableArrayList(COMMENTS);
            postUUID = arguments.getString(POST_UUID);
        }else{
            creatorPostComments = new ArrayList<>();
        }

        chatsterDateTimeUtil = new ChatsterDateTimeUtil();
        ivSendComment.setOnClickListener(sendCommentListener);
        postDetailAdapter = new PostDetailAdapter(creatorPostComments);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        rvCreatorPostComments.setLayoutManager(mLayoutManager);
        rvCreatorPostComments.setItemAnimator(new DefaultItemAnimator());
        rvCreatorPostComments.setAdapter(postDetailAdapter);
        rvCreatorPostComments.addOnItemTouchListener(new RecyclerTouchListener(view.getContext(),
                rvCreatorPostComments, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    public View.OnClickListener sendCommentListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(etComment.getText().toString().trim().length() > 0){
                postCommentsActivity.postCommentForCreatorPost(
                        postCommentsActivity.getUserName(v.getContext()),
                        postCommentsActivity.getUserProfilePicUrl(v.getContext()),
                        postUUID,
                        etComment.getText().toString().trim());
                CreatorPostComment creatorPostComment = new CreatorPostComment();
                creatorPostComment.setCreatorsName(postCommentsActivity.getUserName(v.getContext()));
                creatorPostComment.setUserProfilePicUrl(postCommentsActivity.getUserProfilePicUrl(v.getContext()));
                creatorPostComment.setComment(etComment.getText().toString().trim());
                creatorPostComment.setPostUUID(postUUID);
                creatorPostComment.set_id("_id");
                creatorPostComment.setCommentCreated(chatsterDateTimeUtil.getUtcTime());
                creatorPostComments.add(creatorPostComment);
                postDetailAdapter.notifyDataSetChanged();
                scrollToBottom();
                etComment.setText("");
            }else{
                Toast.makeText(v.getContext(), getString(R.string.empty_comment), Toast.LENGTH_LONG).show();
            }
        }
    };
    private void scrollToBottom() {
        rvCreatorPostComments.scrollToPosition(postDetailAdapter.getItemCount() - 1);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        postCommentsActivity = (PostCommentsActivity) context;
    }
}
