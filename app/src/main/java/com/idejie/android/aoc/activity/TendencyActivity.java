package com.idejie.android.aoc.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.idejie.android.aoc.adapter.TendencyListAdapter;
import com.idejie.android.aoc.bean.LineData;
import com.idejie.android.aoc.bean.MapData;
import com.idejie.android.aoc.dialog.GradeDialog;
import com.idejie.android.aoc.dialog.ProvinceTDialog;
import com.idejie.android.aoc.dialog.SortDialog;
import com.idejie.android.aoc.fragment.tab.CommonUtil;
import com.idejie.android.aoc.model.GradeModel;
import com.idejie.android.aoc.model.PriceModel;
import com.idejie.android.aoc.R;
import com.idejie.android.aoc.model.RegionModel;
import com.idejie.android.aoc.model.SortModel;
import com.idejie.android.aoc.repository.PriceRepository;
import com.idejie.android.aoc.repository.RegionRepository;
import com.idejie.android.aoc.repository.SortRepository;
import com.idejie.android.aoc.tools.LineTableDate;
import com.idejie.android.aoc.tools.MapTableDate;
import com.idejie.android.aoc.widget.ListScrollView;
import com.strongloop.android.loopback.callbacks.JsonArrayParser;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.remoting.adapters.Adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TendencyActivity extends Activity implements View.OnClickListener {

    private ListScrollView scrollView;
    private WebView chartWeb;
    private Button btnLine, btnGraph, btnMap;

    private TimePickerView pvStartTime;
    private TimePickerView pvEndTime;

    private TextView textTimeS, textTimeO, textProvince, textType, textRank;
    private Boolean ifMap = false;
    private ImageView imageBack;
    private List<PriceModel> priceModelList;
    private Handler hanProvinceDialog, hanSortDialog, hanGradeDialog;

    private ListView listView;
    private View listViewHeader;
    private View emptyView;
    private TendencyListAdapter tendListAdapter;

    private RegionModel selectedRegion;
    private SortModel selectedSort;
    private GradeModel selectedGrade;
    private Date startDate;
    private Date endDate;

    private Dialog loadingDialog;

    private List<LineData> lineDataList;
    private List<MapData> mapDataList;

    private android.support.v4.util.ArrayMap<String, List<RegionModel>> chinaMap;
    private Map<String, List<SortModel>> sortMap;

    SimpleDateFormat dateFormat;
    private int defaultRegionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tendency);

        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        defaultRegionId = getIntent().getIntExtra("defaultRegionId", 1);
        initViews();
        initWeb();
        initHandles();
    }

    private void initViews() {

        scrollView = (ListScrollView) findViewById(R.id.scrollView);
        scrollView.scrollTo(0, 0);
        listView = (ListView) findViewById(R.id.listView);
        listViewHeader = getLayoutInflater().inflate(R.layout.tendency_list_header, null);
        emptyView = findViewById(R.id.list_no_data);
        listView.addHeaderView(listViewHeader);
        listView.setEmptyView(emptyView);

        scrollView.setListView(listView);

        textProvince = (TextView) findViewById(R.id.province);//省市
        textProvince.setOnClickListener(this);

        textType = (TextView) findViewById(R.id.type);//品种
        textType.setOnClickListener(this);

        textRank = (TextView) findViewById(R.id.rank);//级别
        textRank.setOnClickListener(this);

        textTimeS = (TextView) findViewById(R.id.text_timeS); //开始时间
        textTimeS.setOnClickListener(this);

        textTimeO = (TextView) findViewById(R.id.text_timeO); //结束时间
        textTimeO.setOnClickListener(this);

        chartWeb = (WebView) findViewById(R.id.chart_webView); //显示折线图

        btnLine = (Button) findViewById(R.id.btn_line); //点击折线图
        btnLine.setOnClickListener(this);
        btnLine.setBackgroundResource(R.drawable.border_green);

        btnGraph = (Button) findViewById(R.id.btn_graph); //点击表格
        btnGraph.setOnClickListener(this);

        btnMap = (Button) findViewById(R.id.btn_map); //点击地图
        btnMap.setOnClickListener(this);

        imageBack = (ImageView) findViewById(R.id.back);
        imageBack.setOnClickListener(this);

        initPriceLine();
    }

    @Override
    protected void onResume() {
        super.onResume();
        chartWeb.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        chartWeb.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chartWeb != null) {
            ViewGroup parent = (ViewGroup) chartWeb.getParent();
            if (parent != null) {
                parent.removeView(chartWeb);
            }
            chartWeb.removeAllViews();
            chartWeb.destroy();
        }
    }

    private void initHandles() {
        //选择省份或者直辖市
        hanProvinceDialog = new Handler() {
            public void handleMessage(Message msg) {
                selectedRegion = (RegionModel) msg.obj;
                if (selectedRegion.getCity() != null) {
                    textProvince.setText(selectedRegion.getCity());
                } else {
                    textProvince.setText(selectedRegion.getProvince());
                }
            }
        };

        hanSortDialog = new Handler() {
            public void handleMessage(Message msg) {

                selectedSort = (SortModel) msg.obj;
                textType.setText(selectedSort.getSubName());
            }
        };

        hanGradeDialog = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.obj == null) {
                    selectedGrade = null;
                    textRank.setText(selectedSort.getSubName());
                } else {
                    selectedGrade = (GradeModel) msg.obj;
                    textRank.setText(selectedGrade.getName());
                }
            }
        };
    }

    private void initWeb() {
        //进行webwiev的一堆设置
        //开启本地文件读取（默认为true，不设置也可以）
        WebSettings webSettings = chartWeb.getSettings();
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
        chartWeb.clearCache(true);

        //在当前页面打开链接了
        chartWeb.setWebViewClient(new WebViewClient() {
            @Override
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
        switch (view.getId()) {
            case R.id.btn_line: //折线图 WebView
                if (isReady()) {
                    listView.setVisibility(View.GONE);
                    chartWeb.setVisibility(View.VISIBLE);
//                    chartWeb.loadUrl("file:///android_asset/loading.html");
                    //变色
                    btnLine.setBackgroundResource(R.drawable.border_green);
                    btnGraph.setBackgroundResource(R.drawable.border_grey);
                    btnMap.setBackgroundResource(R.drawable.border_grey);

                    ifMap = false;
                    initPriceLine();
                } else {
                    Toast.makeText(TendencyActivity.this, "请填写必要参数", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_graph: //表格  listView
                if (isReady()) {
                    btnLine.setBackgroundResource(R.drawable.border_grey);
                    btnGraph.setBackgroundResource(R.drawable.border_green);
                    btnMap.setBackgroundResource(R.drawable.border_grey);
                    ifMap = false;

                    listView.setVisibility(View.VISIBLE);
                    chartWeb.setVisibility(View.GONE);

                    initListData();
                } else {
                    Toast.makeText(TendencyActivity.this, "请填写上述必要参数", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_map: //地图
                if (selectedSort == null || selectedGrade == null) {
                    Toast.makeText(TendencyActivity.this, "请填写必要参数", Toast.LENGTH_SHORT).show();
                    return;
                }

                listView.setVisibility(View.GONE);
                chartWeb.setVisibility(View.VISIBLE);

                btnLine.setBackgroundResource(R.drawable.border_grey);
                btnGraph.setBackgroundResource(R.drawable.border_grey);
                btnMap.setBackgroundResource(R.drawable.border_green);
//                chartWeb.setBackgroundResource(R.color.white);
                ifMap = true;

                initPriceMap();
                break;
            case R.id.province: //省的选取
                if (chinaMap == null) {
                    chinaMap = new android.support.v4.util.ArrayMap<>();
                    getRegionInfo();
                } else {
                    ProvinceTDialog dialog = new ProvinceTDialog(TendencyActivity.this, hanProvinceDialog, chinaMap);
                    dialog.show();
                }
                break;
            case R.id.type://获取"品种"
                if (sortMap == null) {
                    sortMap = new HashMap<>();
                    getSorts();
                } else {
                    SortDialog sortDialog = new SortDialog(this, hanSortDialog, sortMap);
                    sortDialog.show();
                }
                break;
            case R.id.rank: //等级
                if (selectedSort == null) {
                    Toast.makeText(TendencyActivity.this, "请先选好品种", Toast.LENGTH_SHORT).show();
                } else {
                    GradeDialog gradeDialog = new GradeDialog(TendencyActivity.this, hanGradeDialog, selectedSort);
                    gradeDialog.show();
                }
                break;
            case R.id.text_timeS:
                showStartTime();
                break;
            case R.id.text_timeO:
                if (startDate == null) {
                    Toast.makeText(TendencyActivity.this, "请先选择开始时间", Toast.LENGTH_SHORT).show();
                    return;
                }
                showEndTime();
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    //判断所有的必要数据都不为空
    private boolean isReady() {
        Boolean ifReady = false;
        if (!TextUtils.isEmpty(textProvince.getText()) && !TextUtils.isEmpty(textType.getText()) && !TextUtils.isEmpty(textRank.getText())) {
            ifReady = true;
        }
        return ifReady;
    }

    /**
     * 初始化列表信息
     */
    private void initListData() {
        if (endDate == null) {
            endDate = new Date();
        }
        if (startDate == null) {
            startDate = new Date(endDate.getTime() - 7 * 24 * 60 * 60 * 1000); //默认是一周内的信息
        }
        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> filterMap = new HashMap<String, Object>();
        Map<String, Object> where = new HashMap<String, Object>();
        Map<String, Object> priceDate = new HashMap<String, Object>();

        priceDate.put("lt", endDate);
        priceDate.put("gte", startDate);

        where.put("priceDate", priceDate);
        where.put("regionId", selectedRegion.getId());
        where.put("sortId", selectedSort.getId());

        if (selectedGrade != null) {
            where.put("gradeId", selectedGrade.getId());
        }

        filterMap.put("where", where);

        List<Object> list = new ArrayList<>();

        list.add("sort");
        list.add("grade");
        list.add("region");

        filterMap.put("include", list);

        filterMap.put("order", "priceDate"); //按priceDate排序

        Gson gson1 = new Gson();
        String filterStr = gson1.toJson(filterMap);

        params.put("filter", filterStr);

        PriceRepository priceRepository = PriceRepository.getInstance(this, null);
        priceRepository.invokeStaticMethod("all", params, new Adapter.JsonArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                Log.i("Tendency", response.toString());
                int len = response.length();
                if (len > 0) {
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
                    List<PriceModel> priceModels = new ArrayList<PriceModel>(len);

                    for (int i = 0; i < len; i++) {
                        JSONObject jsonObject = response.optJSONObject(i);
                        PriceModel priceModel = gson.fromJson(jsonObject.toString(), PriceModel.class);
                        priceModels.add(priceModel);
                    }
                    tendListAdapter = new TendencyListAdapter(TendencyActivity.this, priceModels);
                    listView.setAdapter(tendListAdapter);
                } else {
                    listView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.i("Tendency Error", t.toString());
            }
        });
    }

    /**
     * 获取所有的地区
     * 根据省、直辖市、自治区分配
     */
    private void getRegionInfo() {
        showLoadingDialog();
        RegionRepository regionRepository = RegionRepository.getInstance(this, null);

        regionRepository.findAll(new ListCallback<RegionModel>() {
            @Override
            public void onSuccess(List<RegionModel> objects) {
                loadingDialog.dismiss();

                for (int i = 0; i < objects.size(); i++) {
                    RegionModel regionModel = objects.get(i);
                    List<RegionModel> list = chinaMap.get(regionModel.getProvince());
                    if (list == null) {
                        list = new ArrayList<RegionModel>();
                        list.add(regionModel);
                        chinaMap.put(regionModel.getProvince(), list);
                    } else {
                        list.add(regionModel);
                    }
                }

                ProvinceTDialog dialog = new ProvinceTDialog(TendencyActivity.this, hanProvinceDialog, chinaMap);
                dialog.show();
            }

            @Override
            public void onError(Throwable t) {
                loadingDialog.dismiss();
            }
        });
    }

    /**
     * 获取所有的品种信息
     */
    private void getSorts() {
        showLoadingDialog();
        SortRepository sortRepository = SortRepository.getInstance(this, null);

        sortRepository.findAll(new ListCallback<SortModel>() {
            @Override
            public void onSuccess(List<SortModel> objects) {
                loadingDialog.dismiss();
                for (int i = 0; i < objects.size(); i++) {
                    SortModel sortModel = objects.get(i);
                    if (sortMap.containsKey(sortModel.getName())) {
                        List<SortModel> sortModels = sortMap.get(sortModel.getName());
                        sortModels.add(sortModel);
                    } else {
                        List<SortModel> sortModels = new ArrayList<SortModel>();
                        sortModels.add(sortModel);
                        sortMap.put(sortModel.getName(), sortModels);
                    }
                }

                SortDialog sortDialog = new SortDialog(TendencyActivity.this, hanSortDialog, sortMap);
                sortDialog.show();
            }

            @Override
            public void onError(Throwable t) {
                loadingDialog.dismiss();
                Log.d("TendencyActivity", "Throwable..Objs..." + t.toString());
            }
        });
    }

    /* *
     * http://211.87.227.214:3001/api/prices?filter[include]=sort&filter[include]=grade&filter[include]=region
     * http://211.87.227.214:3001/api/prices?filter={"include":["sort","grade","region"],"where":{}}  △△△△△
     * 过滤报价信息
     * 初始化报价信息
     * 默认情况选最近一周的本地信息
     * */
    private void initPriceLine() {
        if (endDate == null) {
            endDate = new Date();
        }
        if (startDate == null) {
            startDate = new Date(endDate.getTime() - 7 * 24 * 60 * 60 * 1000);
        }

        Log.i("开始时间", dateFormat.format(startDate));
        Log.i("结束时间", dateFormat.format(endDate));

        PriceRepository priceRepository = PriceRepository.getInstance(this, null);

        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> filterMap = new HashMap<String, Object>();
        Map<String, Object> where = new HashMap<String, Object>();
        Map<String, Object> priceDate = new HashMap<String, Object>();
        List<Object> includeList = new ArrayList<>();

        priceDate.put("lt", endDate);
        priceDate.put("gte", startDate);

        where.put("priceDate", priceDate);
        if (selectedRegion == null) {
            where.put("regionId", defaultRegionId);
        } else {
            where.put("regionId", selectedRegion.getId());
        }
        if (selectedSort != null) { // 品种
            where.put("sortId", selectedSort.getId());
        } else {
            Map<String, Object> sort = new HashMap<String, Object>();
            Map<String, Object> scope = new HashMap<String, Object>();
            Map<String, Object> sortWhere = new HashMap<String, Object>();
            sortWhere.put("subName","生猪");
            scope.put("where", sortWhere);
            sort.put("relation", "sort");
            sort.put("scope", scope);
            includeList.add(sort);
        }
        if (selectedGrade != null) { // 等级
            where.put("gradeId", selectedGrade.getId());
        }

        includeList.add("sort");
        includeList.add("grade");
        includeList.add("region");

        filterMap.put("where", where);
        filterMap.put("include", includeList);
        filterMap.put("order", "priceDate"); //按priceDate排序

        Gson gson = new Gson();
        String filterStr = gson.toJson(filterMap);

        params.put("filter", filterStr);

        priceRepository.invokeStaticMethod("all", params, new Adapter.JsonArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                Log.i("Tendency", response.toString());
                int len = response.length();
                if (len > 0) {
                    Gson gson1 = CommonUtil.getGSON();
                    priceModelList = new ArrayList<PriceModel>(len);
                    String lastPriceDateStr = null;
                    lineDataList = new ArrayList<LineData>();
                    double sumPrice = 0;
                    int num = 0;
                    for (int i = 0; i < len; i++) {
                        JSONObject jsonObject = response.optJSONObject(i);

                        PriceModel priceModel = gson1.fromJson(jsonObject.toString(), PriceModel.class);
                        priceModelList.add(priceModel);

                        String priceDateStr = dateFormat.format(priceModel.getPriceDate());

                        if (TextUtils.equals(lastPriceDateStr, priceDateStr)) {
                            sumPrice += priceModel.getPrice();
                            num += 1;
                        } else {
                            if (num > 0) {
                                lineDataList.add(new LineData(sumPrice / num, lastPriceDateStr));
                            }
                            sumPrice = priceModel.getPrice();
                            num = 1;
                            lastPriceDateStr = priceDateStr;
                        }

                        if (i == len - 1) {
                            lineDataList.add(new LineData(sumPrice / num, lastPriceDateStr));
                        }
                    }

                    Log.i("price", lineDataList.toString());
                    String data = gson1.toJson(lineDataList);
                    Log.i("Json->Data", data);
                    chartWeb.addJavascriptInterface(new LineTableDate(data), "lineData");
                    chartWeb.loadUrl("file:///android_asset/lineChart.html");
                }
            }

            @Override
            public void onError(Throwable t) {
//                chartWeb.loadUrl("file:///android_asset/noData.html");
            }
        });
    }

    /**
     * 初始化地图信息
     * <p>
     * 以开始时间为准
     */
    private void initPriceMap() {
        if (startDate == null) { //默认：2016-10-26 00:00:00 ~ 2016-10-27 00:00:00
            String todayStr = dateFormat.format(new Date());
            try {
                startDate = new Date(dateFormat.parse(todayStr).getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            endDate = new Date(startDate.getTime() + 24 * 60 * 60 * 1000);
        } else { //
            try {
                startDate = dateFormat.parse(textTimeS.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            endDate = new Date(startDate.getTime() + 24 * 60 * 60 * 1000);
        }

        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> filterMap = new HashMap<String, Object>();
        Map<String, Object> where = new HashMap<String, Object>();
        Map<String, Object> priceDate = new HashMap<String, Object>();


        priceDate.put("lt", endDate);
        priceDate.put("gte", startDate);

        where.put("priceDate", priceDate);
        where.put("sortId", selectedSort.getId());
        where.put("gradeId", selectedGrade.getId());

        filterMap.put("where", where);

        List<String> list = new ArrayList<>();
        list.add("sort");
        list.add("grade");
        list.add("region");

        filterMap.put("include", list);

        filterMap.put("order", "regionId"); //按地区regionId排序

        Gson gson1 = new Gson();
        String filterStr = gson1.toJson(filterMap);

        params.put("filter", filterStr);

        PriceRepository priceRepository = PriceRepository.getInstance(this, null);
        priceRepository.invokeStaticMethod("all", params, new Adapter.JsonArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                int len = response.length();
                if (len > 0) {
                    Gson gson2 = CommonUtil.getGSON();
                    priceModelList = new ArrayList<PriceModel>(len);
                    int lastRegionId = -1; //用户记录上一次的信心
                    RegionModel lastRegion = null;
                    mapDataList = new ArrayList<MapData>();
                    double sumPrice = 0;
                    int num = 0;
                    for (int i = 0; i < len; i++) {
                        JSONObject jsonObject = response.optJSONObject(i);

                        PriceModel priceModel = gson2.fromJson(jsonObject.toString(), PriceModel.class);
                        priceModelList.add(priceModel);

                        int regionId = priceModel.getRegionId();

                        if (regionId == lastRegionId) {
                            sumPrice += priceModel.getPrice();
                            num += 1;
                        } else {
                            if (num > 0) {
                                String str = lastRegion.getProvince().replace("省", "").replace("市", "");
                                mapDataList.add(new MapData(str, sumPrice / num));
                            }
                            sumPrice = priceModel.getPrice();
                            num = 1;
                            lastRegionId = regionId;
                            lastRegion = priceModel.getRegion();
                        }

                        if (i == len - 1) {
                            String str = lastRegion.getProvince().replace("省", "").replace("市", "");
                            mapDataList.add(new MapData(str, sumPrice / num));
                        }
                    }

                    Log.i("price", mapDataList.toString());
                    String data = gson2.toJson(mapDataList);
                    Log.i("Json->Data", data);
                    chartWeb.addJavascriptInterface(new MapTableDate(data), "mapData");
                    chartWeb.loadUrl("file:///android_asset/mapChart.html");
                }
            }

            @Override
            public void onError(Throwable t) {
//                chartWeb.loadUrl("file:///android_asset/noData.html");
            }
        });
    }

    private void showStartTime() {
        if (pvStartTime == null) {
            pvStartTime = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
//            pvStartTime.set
            pvStartTime.setTime(new Date());
            pvStartTime.setCyclic(false);
            pvStartTime.setCancelable(true);
            pvStartTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date) {
                    startDate = date;
                    String dateStr = dateFormat.format(date);
                    textTimeS.setText(dateStr);
                }
            });
        }
        pvStartTime.show();
    }

    private void showEndTime() {
        if (pvEndTime == null) {
            pvEndTime = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
            pvEndTime.setTime(new Date());
            pvEndTime.setCyclic(false);
            pvEndTime.setCancelable(true);
            pvEndTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date) {
                    if (startDate.getTime() > date.getTime()) {
                        Toast.makeText(TendencyActivity.this, "请选择更大的时间", Toast.LENGTH_SHORT).show();
                    } else {
                        endDate = date;
                        String dateStr = dateFormat.format(date);
                        textTimeO.setText(dateStr);
                    }
                }
            });
        }
        pvEndTime.show();
    }

//    @Override
//    //设置回退 在页面内回退
//    //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if ((keyCode == KeyEvent.KEYCODE_BACK) && chartWeb.canGoBack()) {
//            chartWeb.goBack(); //goBack()表示返回WebView的上一页面
//            return true;
//        }
//        return super.onKeyDown(keyCode,event);
//    }

    private void showLoadingDialog() {
        if (loadingDialog == null) {
            View v2 = getLayoutInflater().inflate(R.layout.loading_dialog2, null);// 得到加载view
            LinearLayout layout2 = (LinearLayout) v2.findViewById(R.id.dialog_view2);

            loadingDialog = new Dialog(this, R.style.loading_dialog);
            loadingDialog.setContentView(layout2, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
        }

        loadingDialog.show();
    }
}
