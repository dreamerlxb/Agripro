package com.idejie.android.aoc.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.idejie.android.aoc.R;
import com.idejie.android.aoc.activity.TendencyActivity;
import com.idejie.android.aoc.adapter.RiseAndFallListAdapter;
import com.idejie.android.aoc.bean.RiseAndFallBean;

import com.idejie.android.aoc.dialog.ProvinceTDialog;
import com.idejie.android.aoc.model.PriceModel;
import com.idejie.android.aoc.model.RegionModel;
import com.idejie.android.aoc.repository.PriceRepository;
import com.idejie.android.aoc.repository.RegionRepository;

import com.idejie.android.library.fragment.LazyFragment;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.remoting.adapters.Adapter;

import org.json.JSONArray;
import org.json.JSONObject;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shandongdaxue on 16/8/10.
 */
public class SearchFragment extends LazyFragment implements View.OnClickListener,AMapLocationListener{

    public static final String INTENT_STRING_TABNAME = "intent_String_tabname";
    public static final String INTENT_INT_INDEX = "intent_int_index";

    private RegionModel locationRegion;
    private ListView listView;
    private View emptyView;
    private RiseAndFallListAdapter listViewAdapter;
    private TextView regionTxt;
    private Button tendBtn;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private List<PriceModel> todayPriceList = new ArrayList<PriceModel>();
    private List<PriceModel> yesterdayPriceList = new ArrayList<PriceModel>();
    private boolean loc = false;
    private AMapLocationClientOption locationClientOption;
    private AMapLocationClient locationClient;
    private Handler hanProvinceDialog;
    private Dialog loadingDialog;
    private ArrayMap<String, List<RegionModel>> chinaMap;
    /**
     * 初始化操作
     */

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initLocationClient(){
        locationClient = new AMapLocationClient(getContext());
        locationClient.setLocationListener(this);

        locationClientOption = new AMapLocationClientOption();
        locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);

        locationClientOption.setInterval(2000);//设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。

        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        locationClientOption.setOnceLocationLatest(true);

        locationClientOption.setNeedAddress(true); //设置是否返回地址信息（默认返回地址信息）

        locationClient.setLocationOption(locationClientOption);
    }

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        Log.d("SearchFragment","onCreateViewLazy");
        setContentView(R.layout.fragment_search);

        tendBtn = (Button) findViewById(R.id.btn_tendency);
        tendBtn.setOnClickListener(this);
        regionTxt = (TextView) findViewById(R.id.location_txt);
        regionTxt.setOnClickListener(this);
        listView= (ListView) findViewById(R.id.listView);

        emptyView = findViewById(R.id.list_no_data);
        listView.setEmptyView(emptyView);
        listViewAdapter = new RiseAndFallListAdapter(getContext());
        listView.setAdapter(listViewAdapter);
        initLocationClient();

        hanProvinceDialog = new Handler() {
            public void handleMessage(Message msg) {
                locationRegion = (RegionModel) msg.obj;
                if (locationRegion.getCity() != null) {
                    regionTxt.setText(locationRegion.getCity());
                } else {
                    regionTxt.setText(locationRegion.getProvince());
                }
                initListData();
            }
        };
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_tendency:
                Intent intent=new Intent(getContext(), TendencyActivity.class);
                if (locationRegion == null){
                    intent.putExtra("defaultRegionId", 1);
                    intent.putExtra("defaultRegionName", "北京");
                } else {
                    intent.putExtra("defaultRegionId", (Integer) locationRegion.getId());
                    if (locationRegion.getCity() != null) {
                        intent.putExtra("defaultRegionName", locationRegion.getCity());
                    } else {
                        intent.putExtra("defaultRegionName", locationRegion.getProvince());
                    }
                }
                startActivity(intent);
                break;
            case R.id.location_txt:
                if (chinaMap == null) {
                    view.setEnabled(false);
                    chinaMap = new ArrayMap<>();
                    getRegionInfo();
                } else {
                    ProvinceTDialog dialog = new ProvinceTDialog(getContext(), hanProvinceDialog, chinaMap);
                    dialog.show();
                }
                break;
        }
    }
    //获取选择的城市的Id
    private void getCityId(String city,String provinceName) {
        RegionRepository regionRepository = RegionRepository.getInstance(getContext(),null);
        Map<String,Object> params = new HashMap<>();
        Map<String,Object> filter = new HashMap<>();
        Map<String,Object> where = new HashMap<>();

        if (TextUtils.isEmpty(provinceName)) { //说明是直辖市，港澳台，自治区
            where.put("province",city);
        } else {
            where.put("province",provinceName);//说明是普通省市
            where.put("city",city);
        }

        filter.put("where",where);
        params.put("filter",filter);
        regionRepository.invokeStaticMethod("all", params, new Adapter.JsonArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                if (response.length()>0){
                    JSONObject jsonObject = response.optJSONObject(0);
                    Gson gson = new Gson();
                    locationRegion = gson.fromJson(jsonObject.toString(),RegionModel.class);
                    Log.i("locationRegion",locationRegion.toString());
                    initListData();
                }
            }
            @Override
            public void onError(Throwable t) {
                Log.i("locationRegion",t.toString());
            }
        });
    }

    /**
     * 初始化列表信息
     * 同一个地区的同一品种的涨跌（只与前一天比较）
     * */
    private void initListData(){
        Date startDate = null;
        Date endDate = new Date(); //此时此刻
        final String nowStr = dateFormat.format(endDate); // "2016-10-19";//
        try {
            Date today = dateFormat.parse(nowStr); // 今天的 00:00:00
            startDate = new Date(today.getTime() - 24*60*60*1000); //开始时间为起那一天的00:00:00

            Log.d("today",dateFormat.format(today));
            Log.d("startDate",dateFormat.format(startDate));
            Log.d("endDate",dateFormat.format(endDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> filterMap = new HashMap<String, Object>();
        Map<String, Object> where = new HashMap<String, Object>();
        final Map<String, Object> priceDate = new HashMap<String, Object>();

        priceDate.put("lt",endDate);
        priceDate.put("gte",startDate);

        where.put("priceDate",priceDate);
        if (locationRegion == null) {
            where.put("regionId",1); //以北京为例
        } else {
            where.put("regionId",locationRegion.getId());
        }

        filterMap.put("where",where);

        List<String> include = new ArrayList<>();
        include.add("sort");
        include.add("grade");
        include.add("region");

        filterMap.put("include",include);

        List<String> order = new ArrayList<>();
        order.add("sortId");
        order.add("priceDate");

        filterMap.put("order",order); //按品种排序

        Gson gson1 = new Gson();
        String filterStr =  gson1.toJson(filterMap);

        params.put("filter",filterStr);

        showLoadingDialog(true);
        PriceRepository priceRepository = PriceRepository.getInstance(getContext(),null);
        priceRepository.invokeStaticMethod("all", params, new Adapter.JsonArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                showLoadingDialog(false);
                int len = response.length();
                if (len > 0) {
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
                    todayPriceList.clear();
                    yesterdayPriceList.clear();
                    for (int i = 0; i < len; i++) {
                        JSONObject jsonObject = response.optJSONObject(i);
                        PriceModel priceModel = gson.fromJson(jsonObject.toString(),PriceModel.class);

                        String priceDateStr = dateFormat.format(priceModel.getPriceDate());

                        if (TextUtils.equals(nowStr,priceDateStr)) {
                            //今天同一产品
                            todayPriceList.add(priceModel);
                        } else {
                            yesterdayPriceList.add(priceModel);
                        }
                    }
                    calculateRiseAndFall();
                } else {
                    listViewAdapter.updateItems(new ArrayList<RiseAndFallBean>());
                    listViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Throwable t) {
                showLoadingDialog(false);
            }
        });
    }
    /**
     * 计算今天的同一产品的平均价格
     * */
    private List<RiseAndFallBean> calculateToday(){
        int lastSortId1 = -1;
        double sumPrice1 = 0;
        int num1 = 0;
        PriceModel lastPrice1 = null;

        List<RiseAndFallBean> todayList = new ArrayList<>();

        int len1 = todayPriceList.size();
        for (int i = 0; i < len1; i++) {
            PriceModel priceModel = todayPriceList.get(i);
            if (priceModel.getSortId() == lastSortId1) { // 计算今天报价的相同品种的平均价格信息
                sumPrice1 += priceModel.getPrice();
                num1 += 1;
            } else {
                if (num1 > 0){
                    RiseAndFallBean temp = new RiseAndFallBean();
                    temp.setId(lastSortId1);
                    temp.setPrice(sumPrice1/num1);
                    temp.setDateStr(dateFormat.format(lastPrice1.getPriceDate()));
                    if (lastPrice1.getRegion().getCity() != null) {
                        temp.setRegion(lastPrice1.getRegion().getCity());
                    } else {
                        temp.setRegion(lastPrice1.getRegion().getProvince());
                    }
                    temp.setSort(lastPrice1.getSort().getSubName());
                    todayList.add(temp);
                }
                sumPrice1 = priceModel.getPrice();
                num1 = 1;
                lastSortId1 = priceModel.getSortId();
                lastPrice1 = priceModel;
            }

            if (i == len1-1) {
                RiseAndFallBean temp = new RiseAndFallBean();
                temp.setId(lastSortId1);
                temp.setPrice(sumPrice1/num1);
                temp.setDateStr(dateFormat.format(lastPrice1.getPriceDate()));
                if (lastPrice1.getRegion().getCity() != null) {
                    temp.setRegion(lastPrice1.getRegion().getCity());
                } else {
                    temp.setRegion(lastPrice1.getRegion().getProvince());
                }
                temp.setSort(lastPrice1.getSort().getSubName());
                todayList.add(temp);
            }
        }

        return todayList;
    }
    /**
     * 计算昨天的同一品种的平均价格
     * */
    private SparseArray<RiseAndFallBean> calculateYesterday(){
        int lastSortId2 = -1;
        double sumPrice2 = 0;
        int num2 = 0;
        PriceModel lastPrice2 = null;

        int len2 = yesterdayPriceList.size();

        SparseArray<RiseAndFallBean> yesterdayMap = new SparseArray<>(); //昨天的价格都是按照sortId排序

        for (int i = 0; i < len2; i++) { //所有的数据都是根据sortId排好序的
            if (i<len2){ //分析昨天的报价信息
                PriceModel priceModel = yesterdayPriceList.get(i);
                if (priceModel.getSortId() == lastSortId2) {
                    sumPrice2 += priceModel.getPrice();
                    num2 += 1;
                } else {
                    if (num2 > 0){
                        RiseAndFallBean temp = new RiseAndFallBean();
                        temp.setId(lastSortId2);
                        temp.setPrice(sumPrice2/num2);
                        temp.setDateStr(dateFormat.format(lastPrice2.getPriceDate()));
                        if (lastPrice2.getRegion().getCity() != null) {
                            temp.setRegion(lastPrice2.getRegion().getCity());
                        } else {
                            temp.setRegion(lastPrice2.getRegion().getProvince());
                        }
                        temp.setSort(lastPrice2.getSort().getSubName());
                        yesterdayMap.put(lastSortId2,temp);
                    }
                    sumPrice2 = priceModel.getPrice();
                    num2 = 1;
                    lastSortId2 = priceModel.getSortId();
                    lastPrice2 = priceModel;
                }

                if (i == len2-1) {
                    RiseAndFallBean temp = new RiseAndFallBean();
                    temp.setId(lastSortId2);
                    temp.setPrice(sumPrice2/num2);
                    temp.setDateStr(dateFormat.format(lastPrice2.getPriceDate()));
                    if (lastPrice2.getRegion().getCity() != null) {
                        temp.setRegion(lastPrice2.getRegion().getCity());
                    } else {
                        temp.setRegion(lastPrice2.getRegion().getProvince());
                    }
                    temp.setSort(lastPrice2.getSort().getSubName());
                    yesterdayMap.put(lastSortId2,temp);
                }
            }
        }

        return yesterdayMap;
    }
    //判断涨跌
    private void calculateRiseAndFall(){
        List<RiseAndFallBean> todayList = calculateToday();
        SparseArray<RiseAndFallBean> yesterdayMap = calculateYesterday();

        for (int i = 0; i < todayList.size(); i++) {
            RiseAndFallBean rise1 = todayList.get(i);
            RiseAndFallBean rise2 = yesterdayMap.get(rise1.getId());
            if (rise2 == null) {
                rise1.setRiseAndFall("-");
            } else {
                double d = (rise1.getPrice() - rise2.getPrice()) / rise2.getPrice();
                rise1.setRiseAndFall(String.format("%.2f %%", d));
            }
        }

        listViewAdapter.updateItems(todayList);
        listViewAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onFragmentStartLazy() {
        super.onFragmentStartLazy();
        if (!loc) {
            locationClient.startLocation();
            initListData();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                String province = aMapLocation.getProvince();
                String city = aMapLocation.getCity();
                loc = true;
                Log.i("Province",province);
                Log.i("City",city);

                regionTxt.setText(city);
                regionTxt.setEnabled(true);

                if (locationRegion != null) {
                    if (TextUtils.equals(locationRegion.getCity(),city) || TextUtils.equals(locationRegion.getProvince(),city)) {
                        initListData();
                        return;
                    }
                }
                if (TextUtils.isEmpty(province)) {
                    getCityId(city,null); //直辖市，港澳台。自治区
                } else {
                    getCityId(city,province);
                }
            }
        }
    }

    private void showLoadingDialog(boolean show) {
        if (loadingDialog == null) {
            View v2 = LayoutInflater.from(getContext()).inflate(R.layout.loading_dialog2, null);// 得到加载view
            LinearLayout layout2 = (LinearLayout) v2.findViewById(R.id.dialog_view2);

            loadingDialog = new Dialog(getContext(), R.style.loading_dialog);
            loadingDialog.setContentView(layout2, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
        }
        if (show) {
            loadingDialog.show();
        } else {
            loadingDialog.dismiss();
        }
    }

    //获取城市信息
    private void getRegionInfo() {
        showLoadingDialog(true);
        RegionRepository regionRepository = RegionRepository.getInstance(getContext(), null);
        regionRepository.findAll(new ListCallback<RegionModel>() {
            @Override
            public void onSuccess(List<RegionModel> objects) {
                showLoadingDialog(false);
                regionTxt.setEnabled(true);
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

                ProvinceTDialog dialog = new ProvinceTDialog(getContext(), hanProvinceDialog, chinaMap);
                dialog.show();
            }

            @Override
            public void onError(Throwable t) {
                regionTxt.setEnabled(true);
                showLoadingDialog(false);
            }
        });
    }
}
