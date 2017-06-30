package com.idejie.android.aoc.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.idejie.android.aoc.R;

public class FirstScreenActivity extends AppCompatActivity {

    public static final int SKIP_GUIDE = 0x001;
    public static final int SKIP_MAIN = 0x002;

    private SharedPreferences sharedPreferences;
    private Thread thread;
    private boolean isFirst;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);

        sharedPreferences = getSharedPreferences("WelcomePage", MODE_PRIVATE);

        thread = new Thread(runnable);
        thread.start();
    }

    //此handler用于处理界面的变换（跳转activity）
    Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SKIP_GUIDE:
                    Intent guideIntent = new Intent(FirstScreenActivity.this, WelcomeActivity.class);
                    startActivity(guideIntent);
                    finish();
                    break;
                case SKIP_MAIN:
                    Intent mainIntent = new Intent(FirstScreenActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();
                    break;
            }
        }
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                //getBoolean(String key, boolean defValue) 获取键isFirst的值，若无此键则获取默认值（第一次打开程序的时候没有isFirst这个键）
                isFirst = sharedPreferences.getBoolean("isFirst", true);
                Message msg = handler.obtainMessage();
                if(isFirst){
                    //Editor对象用于修改sharedpreference对象,修改完后必须提交事务，才能完成修改（参考数据库的事务处理）
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isFirst", false);
                    editor.commit();
                    msg.what = SKIP_GUIDE;
                }else{
                    msg.what = SKIP_MAIN;
                }
                //休眠3s后，将信息发给handler，由handler来跳转activity
                Thread.sleep(2000);
                handler.sendMessage(msg);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }
}
