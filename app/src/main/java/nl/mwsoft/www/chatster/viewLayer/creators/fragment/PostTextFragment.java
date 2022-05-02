package nl.mwsoft.www.chatster.viewLayer.creators.fragment;

import android.content.Context;
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

public class PostTextFragment extends Fragment {

    @BindView(R.id.ivPostEditingTextBack)
    ImageView ivPostEditingTextBack;
    @BindView(R.id.etPostEditingText)
    EditText etPostEditingText;
    @BindView(R.id.ivPostEditingTextCancel)
    ImageView ivPostEditingTextCancel;
    @BindView(R.id.ivPostEditingTextNextCaption)
    ImageView ivPostEditingTextNextCaption;
    private Unbinder unbinder;
    private CreatePostActivity createPostActivity;


    public static PostTextFragment newInstance() {
        Bundle args = new Bundle();

        PostTextFragment fragment = new PostTextFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_creators_post_editing_text, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivPostEditingTextBack.setOnClickListener(cancelPostListener);
        ivPostEditingTextCancel.setOnClickListener(cancelPostListener);
        ivPostEditingTextNextCaption.setOnClickListener(addCaptionToPostListener);
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
            createPostActivity.loadFragment(
                    PostCaptionFragment.
                            newInstance(
                                    etPostEditingText.getText().toString().trim(),
                                    ConstantRegistry.TEXT
                            )
            );
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

