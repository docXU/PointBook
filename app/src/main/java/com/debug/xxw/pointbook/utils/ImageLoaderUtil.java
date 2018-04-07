package com.debug.xxw.pointbook.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.debug.xxw.pointbook.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * @author xxw
 */
public class ImageLoaderUtil {

    private static ImageLoader getImageLoader() {
        return ImageLoader.getInstance();
    }

    public static DisplayImageOptions getPhotoImageOption() {
        Integer extra = 1;
        return new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
                .showImageForEmptyUri(R.drawable.banner_default).showImageOnFail(R.drawable.banner_default)
                .showImageOnLoading(R.drawable.banner_default)
                .extraForDownloader(extra)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    public static void displayImage(ImageView imageView, String url, DisplayImageOptions options, ImageLoadingListener listener) {
        getImageLoader().displayImage(url, imageView, options, listener);
    }
}
