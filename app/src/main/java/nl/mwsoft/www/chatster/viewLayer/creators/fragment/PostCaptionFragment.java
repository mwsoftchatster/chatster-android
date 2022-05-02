package nl.mwsoft.www.chatster.viewLayer.creators.fragment;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.viewLayer.creators.CreatePostActivity;

public class PostCaptionFragment extends Fragment {

    private Uri uri;
    private Unbinder unbinder;
    private String postType;
    private String postText;
    private CreatePostActivity createPostActivity;
    @BindView(R.id.ivPostEditingCaptionBack)
    ImageView ivPostEditingCaptionBack;
    @BindView(R.id.etPostEditCaption)
    EditText etPostEditCaption;
    @BindView(R.id.ivPostEditingCaptionCancel)
    ImageView ivPostEditingCaptionCancel;
    @BindView(R.id.ivPostEditingCaptionUpload)
    ImageView ivPostEditingCaptionUpload;


    public static PostCaptionFragment newInstance(Uri uri, String postType) {
        Bundle args = new Bundle();
        args.putParcelable(ConstantRegistry.CREATORS_URI, uri);
        args.putString(ConstantRegistry.CREATORS_POST_TYPE, postType);
        PostCaptionFragment fragment = new PostCaptionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static PostCaptionFragment newInstance(String postText, String postType) {

        Bundle args = new Bundle();
        args.putString(ConstantRegistry.CREATORS_POST_TEXT, postText);
        args.putString(ConstantRegistry.CREATORS_POST_TYPE, postType);
        PostCaptionFragment fragment = new PostCaptionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_creators_post_editng_caption, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arguments = getArguments();
        if(arguments != null){
            postType = arguments.getString(ConstantRegistry.CREATORS_POST_TYPE);
            if(postType.equals(ConstantRegistry.IMAGE) || postType.equals(ConstantRegistry.VIDEO)){
                uri = arguments.getParcelable(ConstantRegistry.CREATORS_URI);
            }else{
                postText = arguments.getString(ConstantRegistry.CREATORS_POST_TEXT);
            }
        }

        ivPostEditingCaptionBack.setOnClickListener(backToImagePostListener);
        ivPostEditingCaptionCancel.setOnClickListener(backToImagePostListener);
        ivPostEditingCaptionUpload.setOnClickListener(uploadPostListener);
    }

    private View.OnClickListener uploadPostListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (postType) {
                case ConstantRegistry.IMAGE:
                    createPostActivity.uploadPostToServer(
                            createPostActivity.getUserName(createPostActivity),
                            etPostEditCaption.getText().toString(),
                            ConstantRegistry.IMAGE,
                            createPostActivity.getUserProfilePicUrl(createPostActivity),
                            uri,
                            createPostActivity.generateUUID()
                    );
                    break;
                case ConstantRegistry.VIDEO:
                    createPostActivity.uploadVideoPostToServer(uri,
                            createPostActivity.getUserName(createPostActivity),
                            etPostEditCaption.getText().toString(),
                            ConstantRegistry.VIDEO,
                            createPostActivity.getUserProfilePicUrl(createPostActivity),
                            createPostActivity.generateUUID());
                    break;
                default:
                    createPostActivity.uploadTextPostToServer(
                            createPostActivity.getUserName(createPostActivity),
                            etPostEditCaption.getText().toString(),
                            ConstantRegistry.TEXT,
                            createPostActivity.getUserProfilePicUrl(createPostActivity),
                            postText,
                            createPostActivity.generateUUID()
                    );
                    break;
            }
        }
    };

    private View.OnClickListener backToImagePostListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            createPostActivity.loadFragment(PostImageFragment.newInstance(uri));
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        createPostActivity = (CreatePostActivity) context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
