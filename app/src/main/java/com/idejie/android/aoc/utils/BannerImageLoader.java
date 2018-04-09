package com.idejie.android.aoc.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.youth.banner.loader.ImageLoader;
import com.youth.banner.loader.ImageLoaderInterface;

/**
 * Created by sxb on 2017/1/20.
 */

public class BannerImageLoader implements ImageLoaderInterface<ImageView> {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        Glide
            .with(context)
            .load(path)
            .centerCrop()
            .crossFade()
            .into(imageView);
    }

    @Override
    public ImageView createImageView(Context context) {
        return null;
    }
}
