package nl.mwsoft.www.chatster.viewLayer.creators.fragment;


import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import nl.mwsoft.www.chatster.R;
import nl.mwsoft.www.chatster.modelLayer.constantRegistry.ConstantRegistry;
import nl.mwsoft.www.chatster.viewLayer.creators.CreatorsActivity;

public class CreateFragment extends Fragment {

    @BindView(R.id.ivCreateFromGalleryPicture)
    ImageView ivCreateFromGalleryPicture;
    @BindView(R.id.ivCreateFromTakePicture)
    ImageView ivCreateFromTakePicture;
    @BindView(R.id.ivCreateFromGalleryVideo)
    ImageView ivCreateFromGalleryVideo;
    @BindView(R.id.ivCreateFromCaptureVideo)
    ImageView ivCreateFromCaptureVideo;
    @BindView(R.id.ivCreateFromWriteText)
    ImageView ivCreateFromWriteText;
    @BindView(R.id.ivCreateBack)
    ImageView ivCreateBack;
    private CreatorsActivity creatorsActivity;
    private Unbinder unbinder;

    public static CreateFragment newInstance() {
        
        Bundle args = new Bundle();
        CreateFragment fragment = new CreateFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_creators_create, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivCreateFromGalleryPicture.setOnClickListener(imageFromGalleryListener);
        ivCreateFromTakePicture.setOnClickListener(imageFromCameraListener);
        ivCreateFromGalleryVideo.setOnClickListener(imageFromGalleryVideoListener);
        ivCreateFromCaptureVideo.setOnClickListener(imageFromCaptureVideoListener);
        ivCreateFromWriteText.setOnClickListener(ivCreateFromWriteTextListener);
        ivCreateBack.setOnClickListener(backListener);
    }

    private View.OnClickListener imageFromGalleryListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            creatorsActivity.createPostFromGallery();
        }
    };

    private View.OnClickListener imageFromCameraListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            creatorsActivity.createPostFromCamera();
        }
    };

    private View.OnClickListener imageFromGalleryVideoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            creatorsActivity.createVideoPostFromGallery();
        }
    };

    private View.OnClickListener imageFromCaptureVideoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            creatorsActivity.createVideoPostFromCapture();
        }
    };

    private View.OnClickListener ivCreateFromWriteTextListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            creatorsActivity.createTextPost(ConstantRegistry.TEXT);
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
