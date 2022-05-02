package nl.mwsoft.www.chatster.modelLayer.modelLayerManager.image;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import java.io.File;
import java.io.IOException;

import nl.mwsoft.www.chatster.modelLayer.helper.util.image.ChatsterImageProcessingUtil;


public class ImageProcessingManager {

    private ChatsterImageProcessingUtil chatsterImageProcessingUtil;

    public ImageProcessingManager() {
    }

    public ImageProcessingManager(ChatsterImageProcessingUtil chatsterImageProcessingUtil) {
        this.chatsterImageProcessingUtil = chatsterImageProcessingUtil;
    }

    // region Image Processing

    public File createImageFile(Context context) throws IOException {
        return chatsterImageProcessingUtil.createImageFile(context);
    }

    public Bitmap decodeSampledBitmap(Context context, Uri selectedImage) throws IOException {
        return chatsterImageProcessingUtil.decodeSampledBitmap(context,selectedImage);
    }

    public Bitmap decodeImage(String data) {
        return chatsterImageProcessingUtil.decodeImage(data);
    }

    public Uri saveIncomingImageMessage(Context context, Bitmap inImage) {
        return chatsterImageProcessingUtil.saveIncomingImageMessage(context, inImage);
    }

    public String encodeImageToString(Context context, Uri imageUrl) throws IOException {
        return chatsterImageProcessingUtil.encodeImageToString(context, imageUrl);
    }

    // endregion
}
