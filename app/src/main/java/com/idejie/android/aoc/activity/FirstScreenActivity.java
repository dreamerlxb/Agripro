package com.idejie.android.aoc.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.idejie.android.aoc.R;

public class FirstScreenActivity extends AppCompatActivity {

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        new Handler() {
            public void handleMessage(Message msg) {
                startActivity(new Intent(FirstScreenActivity.this,
                        WelcomeActivity.class));
                finish();
            }
        }.sendEmptyMessageDelayed(0, 200);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }
}
