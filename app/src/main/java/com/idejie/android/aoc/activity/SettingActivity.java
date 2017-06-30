package com.idejie.android.aoc.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.utils.DataCleanManager;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView modifyPwdTxt;
    private TextView cacheTxt;
    private View clearCacheView;
    private ImageView backImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        backImg = (ImageView) findViewById(R.id.back_img);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingActivity.this.finish();
            }
        });

        modifyPwdTxt = (TextView) findViewById(R.id.modify_pwd_txt);
        modifyPwdTxt.setOnClickListener(this);

        cacheTxt = (TextView) findViewById(R.id.clear_cache_txt);
        clearCacheView = findViewById(R.id.clear_cache);
        clearCacheView.setOnClickListener(this);

        try {
            cacheTxt.setText(DataCleanManager.getTotalCacheSize(this));
        } catch (Exception e) {
            Log.i("清除缓存", e.toString());
            e.printStackTrace();
        }
    }

    private Runnable clearCache = new Runnable() {
        @Override
        public void run() {
            DataCleanManager.clearAllCache(SettingActivity.this);
            try {
                String size = DataCleanManager.getTotalCacheSize(SettingActivity.this);
                Message msg = new Message();
                msg.obj = size;
                handler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            cacheTxt.setText(msg.obj.toString());
            Toast.makeText(SettingActivity.this, "缓存清除完成", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.modify_pwd_txt:
                startActivity(new Intent(this, ModifyPwdActivity.class));
                break;
            case R.id.clear_cache:
                Log.i("清除缓存", "========");
                dialog();
                break;
        }
    }

    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确任清除缓存吗？");
        builder.setTitle("提示");

//        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                new Thread(clearCache).start();
//            }
//        });
//        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
        AlertDialog alertDialog = builder.create();

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(clearCache).start();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();

        Button btn = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btn.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        Button btn2 = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        btn2.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));

    }
}
