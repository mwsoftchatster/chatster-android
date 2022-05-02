package nl.mwsoft.www.chatster.viewLayer.creators.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.viewLayer.creators.CreatePostActivity;

public class PostVideoFragment extends Fragment {

    private Uri videoUri;
    @BindView(R.id.ivPostEditingVideoBack)
    ImageView ivPostEditingVideoBack;
    @BindView(R.id.vvPostEditingVideo)
    VideoView vvPostEditingVideo;
    @BindView(R.id.ivPostEditingVideoCancel)
    ImageView ivPostEditingVideoCancel;
    @BindView(R.id.ivPostEditingVideoNextCaption)
    ImageView ivPostEditingVideoNextCaption;
    private Unbinder unbinder;
    private CreatePostActivity createPostActivity;


    public static PostVideoFragment newInstance(Uri uri) {
        Bundle args = new Bundle();
        args.putParcelable(ConstantRegistry.CREATORS_VIDEO_URI,uri);
        PostVideoFragment fragment = new PostVideoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_creators_post_editing_video, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arguments = getArguments();
        if(arguments != null){
            videoUri = arguments.getParcelable(ConstantRegistry.CREATORS_VIDEO_URI);
        }
        MediaController mediaController = new MediaController(view.getContext());
        vvPostEditingVideo.setMediaController(mediaController);
        // load video into VideoView
        vvPostEditingVideo.setVideoURI(videoUri);
        // vvPostEditingVideo.start();
        ivPostEditingVideoBack.setOnClickListener(cancelPostListener);
        ivPostEditingVideoCancel.setOnClickListener(cancelPostListener);
        ivPostEditingVideoNextCaption.setOnClickListener(addCaptionToPostListener);
    }

    private View.OnClickListener cancelPostListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            createPostActivity.onBackPressed();
        }
    };

    private View.OnClickListener addCaptionToPostListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            createPostActivity.loadFragment(PostCaptionFragment.newInstance(videoUri, ConstantRegistry.VIDEO));
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
