package com.idejie.android.aoc.utils;


import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.idejie.android.aoc.R;


public class LoadingDialog {

    public static Dialog createLoadingDialog(Context context, String msg) {

        LayoutInflater inflater = LayoutInflater.from(context);

        View v2 = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
        LinearLayout layout2 = (LinearLayout) v2.findViewById(R.id.loading_dialog_view);

        Dialog loadingDialog2 = new Dialog(context, R.style.loading_dialog);
        loadingDialog2.setCanceledOnTouchOutside(false);
        loadingDialog2.setContentView(layout2,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        return loadingDialog2;
    }
}
