/*
  Copyright (C) 2017 - 2020 MWSOFT

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
