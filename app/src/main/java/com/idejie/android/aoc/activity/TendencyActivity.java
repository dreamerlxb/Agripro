package com.idejie.android.aoc.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.idejie.android.aoc.dialog.CityTDialog;
import com.idejie.android.aoc.dialog.MyTDialog;
import com.idejie.android.aoc.dialog.SortDetailTDialog;
import com.idejie.android.aoc.dialog.SortTDialog;
import com.idejie.android.aoc.model.PriceModel;
import com.idejie.android.aoc.R;
import com.idejie.android.aoc.model.RegionModel;
import com.idejie.android.aoc.model.SortModel;
import com.idejie.android.aoc.repository.PriceRepository;
import com.idejie.android.aoc.repository.RegionRepository;
import com.idejie.android.aoc.repository.SortRepository;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.JsonArrayParser;
import com.strongloop.android.loopback.callbacks.ListCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TendencyActivity extends Activity implements View.OnClickListener {

    private WebView chartWeb;
    private Button btnLine,btnGraph,btnMap;
    private LinearLayout lin4,lin5;
    private LinearLayout lineProvince,lineType,lineRank;
    private TimePickerView pvTime;
    private TextView textTimeS,textTimeO,textProvince,textType,textRank;
    private Boolean ifMap=false;
    private ImageView imageBack;
    private List<PriceModel> informations;
    private Handler hanDialog,hanCityDialog,hanSortDialog,hanDetailDialog;
    private int regionId,sortId,gradeId;
    private List<SortModel> objectArray;
    private String apiUrl="http://211.87.227.214:3001/api";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tendency);
        init();
    }

    private void init() {
        textProvince= (TextView) findViewById(R.id.province);
        textType= (TextView) findViewById(R.id.type);
        textRank= (TextView) findViewById(R.id.rank);
        textTimeS= (TextView) findViewById(R.id.text_timeS);
        textTimeO= (TextView) findViewById(R.id.text_timeO);
        chartWeb= (WebView) findViewById(R.id.chart_webView);
        btnLine= (Button) findViewById(R.id.btn_line);
        btnLine.setOnClickListener(this);
        btnGraph= (Button) findViewById(R.id.btn_graph);
        btnGraph.setOnClickListener(this);
        btnMap= (Button) findViewById(R.id.btn_map);
        btnMap.setOnClickListener(this);
        lineProvince= (LinearLayout) findViewById(R.id.line_1);
        lineProvince.setOnClickListener(this);
        lineType= (LinearLayout) findViewById(R.id.line_2);
        lineType.setOnClickListener(this);
        lineRank= (LinearLayout) findViewById(R.id.line_3);
        lineRank.setOnClickListener(this);
        lin4= (LinearLayout) findViewById(R.id.line_4);
        lin4.setOnClickListener(this);
        lin5= (LinearLayout) findViewById(R.id.line_5);
        lin5.setOnClickListener(this);
        imageBack= (ImageView) findViewById(R.id.back);
        imageBack.setOnClickListener(this);
        initWeb();
        hanDialog = new Handler() {
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                if (msg.what==1){
                    String Jsmess = (String) msg.obj;
                    textProvince.setText(Jsmess);
                    getCityId(Jsmess);
                }else {
                    CityTDialog dialog=new CityTDialog(TendencyActivity.this,hanCityDialog, (Integer) msg.obj);
                    dialog.show();
                }

            }
        };
        hanCityDialog = new Handler() {
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                String Jsmess = (String) msg.obj;
                textProvince.setText(Jsmess);

            }
        };
        hanSortDialog = new Handler() {
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                String Jsmess = (String) msg.obj;
                SortDetailTDialog detailDialog=new SortDetailTDialog(TendencyActivity.this,hanDetailDialog,objectArray,Jsmess);
                detailDialog.show();
            }
        };
        hanDetailDialog = new Handler() {
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                String Jsmess = (String) msg.obj;
                sortId=msg.what;
                textType.setText(Jsmess);
            }
        };


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
                getDate("region");
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
            case R.id.line_1:
                MyTDialog dialog=new MyTDialog(TendencyActivity.this,hanDialog);
                dialog.show();
                break;
            case R.id.line_2:
                getSort();
                break;
            case R.id.line_3:
                getRank();
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

    private void getCityId(final String name) {
        RestAdapter adapter = new RestAdapter(getApplicationContext(),apiUrl);
        adapter.setAccessToken("4miVFTq2Yt3nDPPrTLLvJGSQNKH5k0x78fNyHENbwyICjii206NqmjL5ByChP6dO");
        RegionRepository regionRepository = adapter.createRepository(RegionRepository.class);
        regionRepository.findAll(new ListCallback<RegionModel>() {
            @Override
            public void onSuccess(List<RegionModel> objects) {
                for (int i=0;i<objects.size();i++){
                    if(objects.get(i).getCity().equals(name)){
                        regionId= (int) objects.get(i).getId();
                    }
                }

            }

            @Override
            public void onError(Throwable t) {

            }
        });

    }


    private void getDate(final String name) {
        Log.d("test","aaa");
        RestAdapter adapter = new RestAdapter(getApplicationContext(), apiUrl);
        adapter.setAccessToken("4miVFTq2Yt3nDPPrTLLvJGSQNKH5k0x78fNyHENbwyICjii206NqmjL5ByChP6dO");
        PriceRepository productRepository = adapter.createRepository(PriceRepository.class);
        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("include",name);
        params.put("filter",filterMap);
        productRepository. invokeStaticMethod("all", params, new JsonArrayParser<PriceModel> (productRepository,new ListCallback<PriceModel>() {

            @Override
            public void onSuccess(List<PriceModel> objects) {
                Log.d("test",1+"");
                if (name.equals("region")){
                    informations=objects;
                    informations.get(0).getRegion().getCity();
                    //getDate("sort");
                }else if (name.equals("sort"))
                {
                    for (int i=0;i<informations.size();i++){
                        informations.get(i).setSortModel(objects.get(i).getSortModel());
                    }
                    Log.d("test","........."+informations.get(1).getSortModel().getSubName());
                    Log.d("test","........."+informations.get(1).getRegionModel().getCity());

                }
            }
            @Override
            public void onError(Throwable t) {
                Log.d("test","Throwable..Obj..."+t.toString());
            }
        }));
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

    private void getRank() {


    }


    private void getSort() {
        RestAdapter adapter = new RestAdapter(getApplicationContext(), apiUrl);
        adapter.setAccessToken("4miVFTq2Yt3nDPPrTLLvJGSQNKH5k0x78fNyHENbwyICjii206NqmjL5ByChP6dO");
        SortRepository sortRepository = adapter.createRepository(SortRepository.class);
        Log.d("test","a");
        sortRepository.findAll(new ListCallback<SortModel>() {
            @Override
            public void onSuccess(List<SortModel> objects) {
                objectArray=objects;
                SortTDialog sortDialog=new SortTDialog(TendencyActivity.this,hanSortDialog,objects);
                sortDialog.show();

            }

            @Override
            public void onError(Throwable t) {
                Log.d("test","Throwable..Objs..."+t.toString());
            }

        });
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
