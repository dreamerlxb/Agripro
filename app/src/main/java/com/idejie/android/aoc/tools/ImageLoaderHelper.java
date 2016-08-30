package com.idejie.android.aoc.tools;

/**
 * Created by slf on 16/8/27.
 */


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.idejie.android.aoc.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class ImageLoaderHelper {

    public static final int IMG_LOAD_DELAY=200;
    private static ImageLoaderHelper imageLoaderHelper;
    private   ImageLoader imageLoader;
    // 显示图片的设置
    private DisplayImageOptions options;
    private Context context1;


    public static ImageLoaderHelper getInstance(Context context){
        if(imageLoaderHelper==null) {
            imageLoaderHelper = new ImageLoaderHelper(context);
        }
        return  imageLoaderHelper;
    }
    public ImageLoaderHelper(Context context){
        context1=context;
        init();
    }

    /**
     * 配置 imagloader 基本信息
     */
    private void init(){
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .delayBeforeLoading(IMG_LOAD_DELAY)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)	 //设置图片的解码类型
                .build();
        Log.d("test","11111");
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context1));
        Log.d("test",imageLoader.toString());
    }

    /**
     * 加载图片
     * @param url
     * @param imageView
     */
    public  void loadImage(String url,ImageView imageView){
        url=url.trim();
        imageLoader.displayImage(url,imageView,options);
    }
}
