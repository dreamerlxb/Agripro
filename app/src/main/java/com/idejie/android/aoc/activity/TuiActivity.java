package com.idejie.android.aoc.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.idejie.android.aoc.R;

public class TuiActivity extends AppCompatActivity {
    private ImageView backImg;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tui);
        backImg = (ImageView) findViewById(R.id.back_img);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TuiActivity.this.finish();
            }
        });

        webView = (WebView) findViewById(R.id.tui_web);

        initWeb();
    }

    private void initWeb() {
        //进行webwiev的一堆设置
        //开启本地文件读取（默认为true，不设置也可以）
        WebSettings webSettings = webView.getSettings();
        webSettings.setAllowFileAccess(true);
        //设置编码
        webSettings.setDefaultTextEncodingName("utf-8");
        // 设置可以支持缩放
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);

        //自适应屏幕
//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setUseWideViewPort(true);//将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true);// 缩放至屏幕的大小
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        //开启脚本支持
        webSettings.setJavaScriptEnabled(true);
        //不显示webview缩放按钮
        webSettings.setDisplayZoomControls(false);

        // 清除浏览器缓存
        webView.clearCache(true);

        //在当前页面打开链接了
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        //js加上这个就好啦！
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });

        webView.loadUrl("https://www.baidu.com");
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            ViewGroup parent = (ViewGroup) webView.getParent();
            if (parent != null) {
                parent.removeView(webView);
            }
            webView.removeAllViews();
            webView.destroy();
        }
    }
}
