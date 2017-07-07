package com.idejie.android.aoc.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.tools.LocalDisplay;
import com.zhy.view.flowlayout.TagFlowLayout;


public class PriceItemDialog extends Dialog implements View.OnClickListener{

    View localView;
    private Button btnDel;

    public PriceItemDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        int width = LocalDisplay.getScreenWidthPixels(getContext());
        Window window = getWindow();//这部分是设置dialog宽高的，宽高是我从sharedpreferences里面获取到的。之前程序启动的时候有获取
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = width;
        lp.gravity = Gravity.CENTER;
        window.setAttributes(lp);

        localView = getLayoutInflater().inflate(R.layout.price_item_confirm, null);
        btnDel = (Button) localView.findViewById(R.id.btn_del);
        btnDel.setOnClickListener(this);
        localView.setAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_bottom_to_top));

        setContentView(localView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_del:
                onItemClickListener.onItemClick(v, 0);
                break;
        }
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static interface OnItemClickListener {
        void onItemClick(View v, int index);
    }
}