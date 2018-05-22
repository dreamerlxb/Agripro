package com.idejie.android.aoc.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.youth.banner.loader.ImageLoader;

/**
 * Created by sxb on 2017/1/20.
 */

public class BannerImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        Glide
            .with(context)
            .load(path)
            .centerCrop()
            .crossFade()
            .into(imageView);
    }
}
