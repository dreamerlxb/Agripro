package com.idejie.android.aoc.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private LinearLayout lin4,lin5;
    private TimePickerView pvTime;
    private TextView textTimeS,textTimeO;
    private Boolean ifMap=false;
    private ImageView imageBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tendency);
        init();
    }

    private void init() {
        textTimeS= (TextView) findViewById(R.id.text_timeS);
        textTimeO= (TextView) findViewById(R.id.text_timeO);
        chartWeb= (WebView) findViewById(R.id.chart_webView);
        btnLine= (Button) findViewById(R.id.btn_line);
        btnLine.setOnClickListener(this);
        btnGraph= (Button) findViewById(R.id.btn_graph);
        btnGraph.setOnClickListener(this);
        btnMap= (Button) findViewById(R.id.btn_map);
        btnMap.setOnClickListener(this);
        lin4= (LinearLayout) findViewById(R.id.line_4);
        lin4.setOnClickListener(this);
        lin5= (LinearLayout) findViewById(R.id.line_5);
        lin5.setOnClickListener(this);
        imageBack= (ImageView) findViewById(R.id.back);
        imageBack.setOnClickListener(this);
        initWeb();


    }


    private void initWeb() {
        btnLine.setBackgroundResource(R.drawable.border_green);
        //进行webwiev的一堆设置
        //开启本地文件读取（默认为true，不设置也可以）
        chartWeb.getSettings().setAllowFileAccess(true);
        //设置编码
        chartWeb.getSettings().setDefaultTextEncodingName("utf-8");
        // 设置可以支持缩放
        chartWeb.getSettings().setSupportZoom(true);
        chartWeb.getSettings().setBuiltInZoomControls(true);
        // 清除浏览器缓存
        chartWeb.clearCache(true);
        //自适应屏幕
        chartWeb.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        chartWeb.getSettings().setLoadWithOverviewMode(true);
        //开启脚本支持
        chartWeb.getSettings().setJavaScriptEnabled(true);
        chartWeb.loadUrl("file:///android_asset/test4.html");
        //不显示webview缩放按钮
        chartWeb.getSettings().setDisplayZoomControls(false);
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
                LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams)
                        chartWeb.getLayoutParams(); //取控件textView当前的布局参数
                linearParams.height = ((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 260, getResources().getDisplayMetrics()));

                chartWeb.setLayoutParams(linearParams);
                chartWeb.loadUrl("file:///android_asset/test4.html");
                btnLine.setBackgroundResource(R.drawable.border_green);
                btnGraph.setBackgroundResource(R.drawable.border_grey);
                btnMap.setBackgroundResource(R.drawable.border_grey);
                ifMap=false;
                break;
            case R.id.btn_graph:
                btnLine.setBackgroundResource(R.drawable.border_grey);
                btnGraph.setBackgroundResource(R.drawable.border_green);
                btnMap.setBackgroundResource(R.drawable.border_grey);
                ifMap=false;
                LinearLayout.LayoutParams linearParams1 =(LinearLayout.LayoutParams)
                        chartWeb.getLayoutParams(); //取控件textView当前的布局参数
                linearParams1.height = 1;// 控件的高强制设成20

                chartWeb.setLayoutParams(linearParams1);
                getDate();
                break;
            case R.id.btn_map:
                LinearLayout.LayoutParams linearParams2 =(LinearLayout.LayoutParams)
                        chartWeb.getLayoutParams(); //取控件textView当前的布局参数
                linearParams2.height = ((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 260, getResources().getDisplayMetrics()));
                chartWeb.setLayoutParams(linearParams2);
                chartWeb.loadUrl("file:///android_asset/test5.html");
                btnLine.setBackgroundResource(R.drawable.border_grey);
                btnGraph.setBackgroundResource(R.drawable.border_grey);
                btnMap.setBackgroundResource(R.drawable.border_green);
                chartWeb.setBackgroundResource(R.color.white);
                ifMap=true;
                break;
            case R.id.line_4:
                showTimeChoose(0);
                break;
            case R.id.line_5:
                showTimeChoose(1);
                break;
            case R.id.back:
                finish();
                break;
        }

    }

    private void getDate() {
        Log.d("test","aaa");
        RestAdapter adapter = new RestAdapter(getApplicationContext(), "http://192.168.1.114:3001/api");
        adapter.setAccessToken("4miVFTq2Yt3nDPPrTLLvJGSQNKH5k0x78fNyHENbwyICjii206NqmjL5ByChP6dO");
        PriceRepository productRepository = adapter.createRepository(PriceRepository.class);
        Log.d("test","a");
        productRepository.findById(1, new ObjectCallback<PriceModel>() {
            @Override
            public void onSuccess(PriceModel object) {
                Log.d("test","findbyId..Obj..."+object.toString());
                Log.d("test","Obj..."+object.getMarketName());
            }

            @Override
            public void onError(Throwable t) {
                Log.d("test","Throwable..Obj..."+t.toString());
            }
        });
        productRepository.findAll(new ListCallback<PriceModel>() {
            @Override
            public void onSuccess(List<PriceModel> objects) {
                Log.d("test",".1............"+objects.toString());
            }

            @Override
            public void onError(Throwable t) {
                Log.d("test","Throwable..Objs..."+t.toString());
            }


        });

    }


    private void showTimeChoose(final int i) {
        pvTime = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);
        //时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                Log.d("test","time...."+getTime(date));
                if (ifMap){
                    textTimeS.setText(getTime(date));
                    textTimeO.setText(getTime(date));

                }else {
                    Log.d("test","。。。。。"+textTimeS.getText());
                    if (i==0){
                        textTimeS.setText(getTime(date));
                    }else if(textTimeS.getText().equals("开始")){
                        Toast.makeText(TendencyActivity.this,"请先输入起始时间",Toast.LENGTH_SHORT).show();
                    }else {
                        int thisTime=Integer.parseInt(getTime(date).substring(0,4)+getTime(date).substring(5,7)+getTime(date).substring(8,10));
                        int lastTime=Integer.parseInt(textTimeS.getText().toString().substring(0,4)+textTimeS.getText().toString().substring(5,7)+textTimeS.getText().toString().substring(8,10));
                        Log.d("test","this..."+thisTime);
                        if (thisTime>=lastTime) {
                            textTimeO.setText(getTime(date));
                        }else {
                            Toast.makeText(TendencyActivity.this,"请选择更大的时间",Toast.LENGTH_SHORT).show();
                        }
                    }
                }



            }
        });
        pvTime.show();
    }
    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
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
