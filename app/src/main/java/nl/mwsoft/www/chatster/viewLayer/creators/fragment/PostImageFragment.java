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

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.viewLayer.creators.CreatePostActivity;

public class PostImageFragment extends Fragment {

    private Uri photoUri;
    @BindView(R.id.ivPostEditingImageBack)
    ImageView ivPostEditingImageBack;
    @BindView(R.id.ivPostEditingImage)
    ImageView ivPostEditingImage;
    @BindView(R.id.ivPostEditingImageCancel)
    ImageView ivPostEditingImageCancel;
    @BindView(R.id.ivPostEditingImageNextCaption)
    ImageView ivPostEditingImageNextCaption;
    private Unbinder unbinder;
    private CreatePostActivity createPostActivity;


    public static PostImageFragment newInstance(Uri uri) {
        Bundle args = new Bundle();
        args.putParcelable(ConstantRegistry.CREATORS_PHOTO_URI,uri);
        PostImageFragment fragment = new PostImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_creators_post_editing_image, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arguments = getArguments();
        if(arguments != null){
            photoUri = arguments.getParcelable(ConstantRegistry.CREATORS_PHOTO_URI);
        }
        Picasso.with(createPostActivity).load(photoUri).into(ivPostEditingImage);
        ivPostEditingImageBack.setOnClickListener(cancelPostListener);
        ivPostEditingImageCancel.setOnClickListener(cancelPostListener);
        ivPostEditingImageNextCaption.setOnClickListener(addCaptionToPostListener);
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
            createPostActivity.loadFragment(PostCaptionFragment.newInstance(photoUri, ConstantRegistry.IMAGE));
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
