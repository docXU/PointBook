package com.debug.xxw.pointbook.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.debug.xxw.pointbook.R;
import com.debug.xxw.pointbook.utils.ImageLoaderUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xxw
 */
public class NineGridTestLayout extends NineGridLayout {

    protected static final int MAX_W_H_RATIO = 3;

    public NineGridTestLayout(Context context) {
        super(context);
    }

    public NineGridTestLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean displayOneImage(final RatioImageView imageView, String url, final int parentWidth) {
        if (!url.startsWith("http")) {
            url = "file://" + url;
        }
        ImageLoaderUtil.displayImage(imageView, url, ImageLoaderUtil.getPhotoImageOption(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                Log.e(this.getClass().getName(), failReason.toString());
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap bitmap) {
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();

                int newW;
                int newH;
                if (h > w * MAX_W_H_RATIO) {
                    //h:w = 5:3
                    newW = parentWidth / 2;
                    newH = newW * 5 / 3;
                } else if (h < w) {
                    //h:w = 2:3
                    newW = parentWidth * 2 / 3;
                    newH = newW * 2 / 3;
                } else {//newH:h = newW :w
                    newW = parentWidth / 2;
                    newH = h * newW / w;
                }
                setOneImageLayoutParams(imageView, newW, newH);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                Log.e(this.getClass().getName(), imageUri + "加载被取消了~");
            }
        });
        return false;
    }

    @Override
    protected void displayImage(RatioImageView imageView, String url) {

        String path = url;
        if (!url.startsWith("http")) {
            path = "file://" + url;
        }
        Picasso.with(mContext)
                .load(path)
                .error(R.mipmap.ic_launcher)
                .into(imageView);
    }

    @Override
    protected void onClickImage(int i, String url, List<String> urlList) {
        List<LocalMedia> pics = new ArrayList<>();
        for (String path : urlList) {
            String[] split = path.split("\\.");
            LocalMedia pic = new LocalMedia(path, 0, PictureMimeType.ofImage(), "image/" + split[split.length - 1]);
            pics.add(pic);
        }
        PictureSelector.create((Activity) mContext).externalPicturePreview(i, "/sava", pics);
    }
}
