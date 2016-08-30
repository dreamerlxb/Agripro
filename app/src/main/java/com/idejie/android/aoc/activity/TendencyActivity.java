package com.idejie.android.aoc.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bigkoo.pickerview.TimePickerView;
import com.idejie.android.aoc.model.PriceModel;
import com.idejie.android.aoc.R;
import com.idejie.android.aoc.repository.PriceRepository;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.loopback.callbacks.ObjectCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TendencyActivity extends Activity implements View.OnClickListener {

    private WebView chartWeb;
    private Button btnLine,btnGraph,btnMap;
    private LinearLayout lin4;
    private TimePickerView pvTime;

    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tendency);
        init();

    }

    private void init() {
        chartWeb= (WebView) findViewById(R.id.chart_webView);
        btnLine= (Button) findViewById(R.id.btn_line);
        btnLine.setOnClickListener(this);
        btnGraph= (Button) findViewById(R.id.btn_graph);
        btnGraph.setOnClickListener(this);
        btnMap= (Button) findViewById(R.id.btn_map);
        btnMap.setOnClickListener(this);
        lin4= (LinearLayout) findViewById(R.id.line_4);
        lin4.setOnClickListener(this);
        initWeb();


    }

    private void initWeb() {
        //进行webwiev的一堆设置
        //开启本地文件读取（默认为true，不设置也可以）
        chartWeb.getSettings().setAllowFileAccess(true);
        //设置编码
        chartWeb.getSettings().setDefaultTextEncodingName("utf-8");
        // 设置可以支持缩放
        chartWeb.getSettings().setSupportZoom(true);
        // 清除浏览器缓存
        chartWeb.clearCache(true);
        //开启脚本支持
        chartWeb.getSettings().setJavaScriptEnabled(true);
        chartWeb.loadUrl("file:///android_asset/echarts.html");
        //在当前页面打开链接了
        chartWeb.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        //js加上这个就好啦！
        chartWeb.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_line:
                chartWeb.loadUrl("javascript:createChart('line',[]);");
                btnLine.setBackgroundResource(R.drawable.border_green);
                btnGraph.setBackgroundResource(R.drawable.border_grey);
                btnMap.setBackgroundResource(R.drawable.border_grey);
                break;
            case R.id.btn_graph:
                btnLine.setBackgroundResource(R.drawable.border_grey);
                btnGraph.setBackgroundResource(R.drawable.border_green);
                btnMap.setBackgroundResource(R.drawable.border_grey);
                getDate();
                break;
            case R.id.btn_map:
                chartWeb.loadUrl("javascript:createChart('map',[]);");
                btnLine.setBackgroundResource(R.drawable.border_grey);
                btnGraph.setBackgroundResource(R.drawable.border_grey);
                btnMap.setBackgroundResource(R.drawable.border_green);
                break;
            case R.id.line_4:
                showTimeChoose();
                break;
        }

    }

    private void getDate() {
        Log.d("test","aaa");
        RestAdapter adapter = new RestAdapter(getApplicationContext(), "http://192.168.1.114:3001/api");
        Log.d("test", "adapter..." + adapter);
        Log.d("test", "PriceRepository.class" + PriceRepository.class);
        adapter.setAccessToken("4miVFTq2Yt3nDPPrTLLvJGSQNKH5k0x78fNyHENbwyICjii206NqmjL5ByChP6dO");
        PriceRepository productRepository = adapter.createRepository(PriceRepository.class);
//        PriceModel price = productRepository.createObject( ImmutableMap.of("price", "1.0") );
        Log.d("test","a");
        productRepository.findById(0, new ObjectCallback<PriceModel>() {
            @Override
            public void onSuccess(PriceModel object) {
                Log.d("test","findbyId..Obj..."+object.toString());
            }

            @Override
            public void onError(Throwable t) {
                Log.d("test","Throwable..Obj..."+t.toString());
            }
        });
        productRepository.findAll(new ListCallback<PriceModel>() {
            @Override
            public void onSuccess(List<PriceModel> objects) {
                Log.d("test","............."+objects.toString());
            }

            @Override
            public void onError(Throwable t) {
                Log.d("test","Throwable..Objs..."+t.toString());
            }

        });

    }

    private void showTimeChoose() {
        pvTime = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);
        //时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                Log.d("test","time...."+getTime(date));
            }
        });
        pvTime.show();
    }

    @Override
    //设置回退 在页面内回退
    //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && chartWeb.canGoBack()) {
            chartWeb.goBack(); //goBack()表示返回WebView的上一页面
            return true;
        }
//        finish();//结束退出程序
//        return false;
        return super.onKeyDown(keyCode,event);
    }
}
