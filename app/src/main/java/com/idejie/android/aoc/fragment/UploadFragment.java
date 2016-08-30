package com.idejie.android.aoc.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.tools.ImageLoaderHelper;
import com.idejie.android.library.fragment.LazyFragment;
import com.jorge.circlelibrary.ImageCycleView;


import java.util.ArrayList;

/**
 * Created by shandongdaxue on 16/8/10.
 */
public class UploadFragment extends LazyFragment implements View.OnClickListener {
    private LayoutInflater inflate;
    private Context context;
    private Activity activity;
    private View view;
    private ImageCycleView imageCycleView;
    private Button btnUpload,btnCancel;
    private EditText editPrice,editAmount,editMarketName;
    /**
     * 初始化操作
     */

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        context = getActivity();
        activity = getActivity();

    }

    /**
     * 界面初始化
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_upload, container,
                false);
        init();

        return view;
    }

    private void init() {
        //初始化广告栏
        initCycleView();
        //初始化各个控件
        btnCancel= (Button) view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);
        btnUpload= (Button) view.findViewById(R.id.btn_upload);
        btnUpload.setOnClickListener(this);
        editPrice= (EditText) view.findViewById(R.id.edit_price);
        editAmount= (EditText) view.findViewById(R.id.edit_amount);
        editMarketName= (EditText) view.findViewById(R.id.edit_Market_name);

    }

    private void initCycleView() {
        /** 找到轮播控件*/
        imageCycleView= (ImageCycleView) view.findViewById(R.id.cycleView);
        /**装在数据的集合  文字描述*/
        ArrayList<String> imageDescList=new ArrayList<>();
        /**装在数据的集合  图片地址*/
        ArrayList<String> urlList=new ArrayList<>();
        /**添加数据*/
        urlList.add("http://fashion.taiwan.cn/list/201503/W020150306794691543155.jpg");
        urlList.add("http://img.leha.com/Editor/2014-12-27/549e2d1be0d6d.jpg");
        urlList.add("http://img2.imgtn.bdimg.com/it/u=3909488552,1950939040&fm=21&gp=0.jpg");
        imageDescList.add("图片1");
        imageDescList.add("图片2");
        imageDescList.add("图片3");
        initCarsuelView(imageDescList, urlList);
    }





    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.btn_upload:
                btnUpload.setBackgroundResource(R.drawable.border_green);
                Toast.makeText(context,"上传中",Toast.LENGTH_SHORT).show();

//                beEmpty();//用于上传成功得到返回值以后再用

                break;
            case R.id.btn_cancel:
                beEmpty();
                //哎我发现取消按钮没有上一页
                break;

        }
    }

    private void beEmpty() {
        //记得把各值也清空
        editPrice.setText("");
        editAmount.setText("");
        editMarketName.setText("");

    }

    /**初始化轮播图的关键方法*/
    public void initCarsuelView(ArrayList<String> imageDescList, ArrayList<String>urlList) {
        LinearLayout.LayoutParams cParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getScreenHeight(activity) * 3 / 10);
        imageCycleView.setLayoutParams(cParams);
        ImageCycleView.ImageCycleViewListener mAdCycleViewListener = new ImageCycleView.ImageCycleViewListener() {
            @Override
            public void onImageClick(int position, View imageView) {
                /**实现点击事件*/
            }
            @Override
            public void displayImage(String imageURL, ImageView imageView) {
                /**在此方法中，显示图片，可以用自己的图片加载库，也可以用本demo中的（Imageloader）*/
//                ImageLoaderHelper imageLoaderHelper=new ImageLoaderHelper();
//                imageLoaderHelper.loadImage(imageURL, imageView);
                ImageLoaderHelper.getInstance(getApplicationContext()).loadImage(imageURL, imageView);
            }
        };
        /**设置数据*/
        imageCycleView.setImageResources(imageDescList,urlList, mAdCycleViewListener);
        imageCycleView.startImageCycle();
    }
    /**
     * 得到屏幕的高度
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context){
        if (null == context) {
            return 0;
        }
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.heightPixels;
    }



}
