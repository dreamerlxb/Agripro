package com.idejie.android.aoc.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.activity.MainActivity;
import com.idejie.android.aoc.model.SortModel;
import com.idejie.android.aoc.tools.Areas;

import java.util.List;

public class SortDialog extends Dialog implements android.view.View.OnClickListener{

    Context context;
    View localView;
    Dialog dialog;
    private Handler han;
    private RelativeLayout clearallpan;
    private LinearLayout lin;
    private List<SortModel> objects;

    public SortDialog(Context context, Handler han, List<SortModel> objects) {
        super(context);
        this.context = context;
        this.han=han;
        this.objects=objects;
        dialog=this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 这句代码换掉dialog默认背景，否则dialog的边缘发虚透明而且很宽
        // 总之达不到想要的效果
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater inflater = ((MainActivity) context).getLayoutInflater();
        localView = inflater.inflate(R.layout.dialog_sort, null);
        localView.setAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_bottom_to_top));
        setContentView(localView);
        // 这句话起全屏的作用
        getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

        initView();
        initListener();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.dismiss();
        return super.onTouchEvent(event);
    }

    private void initListener() {
        clearallpan.setOnClickListener(this);
    }

    private void initView() {
        clearallpan = (RelativeLayout) findViewById(R.id.clearallpan);
        lin= (LinearLayout) findViewById(R.id.line);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1); // , 1是可选写的
        lp.setMargins(10, 0, 10, 0);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1); // , 1是可选写的
        lp1.setMargins(0,8,0,0);
        LinearLayout linearLayout=null;
        int j=0;
        for (int i=0;i<objects.size();i++){

            if (i==0){
                linearLayout=new LinearLayout(context);
                linearLayout.setMinimumHeight(40);
                linearLayout.setLayoutParams(lp1);
                lin.addView(linearLayout);
                final Button btn=new Button(context);
                btn.setText(objects.get(i).getName());
                btn.setLayoutParams(lp);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Message mess=new Message();
                        mess.obj =btn.getText().toString();
                        han.sendMessage(mess);

                        dialog.dismiss();
                    }
                });
                linearLayout.addView(btn);
            }
            if (i>0&&!objects.get(i).getName().equals(objects.get(i-1).getName())){
                j++;
                if (j%4==0){
                    linearLayout=new LinearLayout(context);
                    linearLayout.setMinimumHeight(40);
                    linearLayout.setLayoutParams(lp1);
                    lin.addView(linearLayout);
                }
                final Button btn=new Button(context);
                btn.setText(objects.get(i).getName());
                btn.setLayoutParams(lp);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Message mess=new Message();
                        mess.obj =btn.getText().toString();
                        han.sendMessage(mess);
                        dialog.dismiss();
                    }
                });
                linearLayout.addView(btn);
            }

        }

    }

    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
}