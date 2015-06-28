package com.carpooler.ui.activities;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.carpooler.dao.DatabaseService;

/**
 * Created by raymond on 6/27/15.
 */
public class ImageViewBitmapLoader implements DatabaseService.BitmapCallback {
    private final ImageView imageView;

    public ImageViewBitmapLoader(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    public void doError(String message) {

    }

    @Override
    public void doException(Exception exception) {

    }

    @Override
    public void doSuccess(Bitmap data) {
        imageView.setImageBitmap(data);
    }
}
