package com.idejie.android.aoc.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.tools.LocalDisplay;


public class UploadDialog extends Dialog{

    View localView;
    private TextView percentTxt;

    public UploadDialog(Context context) {
        super(context, R.style.loading_dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        int width = LocalDisplay.getScreenWidthPixels(getContext());
//        Window window = getWindow();//这部分是设置dialog宽高的，宽高是我从sharedpreferences里面获取到的。之前程序启动的时候有获取
//        window.getDecorView().setPadding(0, 0, 0, 0);
//        WindowManager.LayoutParams lp = window.getAttributes();
//        lp.width = width;
//        window.setAttributes(lp);

        localView = getLayoutInflater().inflate(R.layout.upload_img_loading, null);
        percentTxt = (TextView) localView.findViewById(R.id.percent_txt);

        setContentView(localView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        getWindow().getAttributes().gravity = Gravity.CENTER;
        setCanceledOnTouchOutside(false);
    }

    public void setPercentTxt(double d) {
        percentTxt.setText(String.format("%.2f %%", d));
    }
}