package com.idejie.android.aoc.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.ImmutableMap;
import com.idejie.android.aoc.R;
import com.idejie.android.aoc.activity.TendencyActivity;
import com.idejie.android.aoc.adapter.SearchListAdapter;
import com.idejie.android.aoc.bean.LineData;
import com.idejie.android.aoc.bean.SearchList;
import com.idejie.android.aoc.dialog.CityDialog;
import com.idejie.android.aoc.dialog.MyDialog;
import com.idejie.android.aoc.model.PriceModel;
import com.idejie.android.aoc.model.RegionModel;
import com.idejie.android.aoc.model.SortModel;
import com.idejie.android.aoc.repository.PriceRepository;
import com.idejie.android.aoc.repository.RegionRepository;
import com.idejie.android.aoc.repository.SortRepository;
import com.idejie.android.aoc.tools.Areas;
import com.idejie.android.aoc.tools.AutoString;
import com.idejie.android.aoc.tools.NetThread;
import com.idejie.android.library.fragment.LazyFragment;
import com.idejie.android.library.view.indicator.IndicatorViewPager;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.JsonArrayParser;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.loopback.callbacks.VoidCallback;

import org.json.JSONException;
import org.json.JSONObject;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shandongdaxue on 16/8/10.
 */
public class SearchFragment extends LazyFragment implements View.OnClickListener {
    private IndicatorViewPager indicatorViewPager;
    private LayoutInflater inflate;
    public static final String INTENT_STRING_TABNAME = "intent_String_tabname";
    public static final String INTENT_INT_INDEX = "intent_int_index";
    private String tabName;
    private int index;
    private Handler hanDialog,hanCityDialog;
    private Button tendBtn;
    private TextView cityText;
    private Context context;
    private Activity activity;
    private View view;
    private String lat,lon;
    private Handler han;
    private ListView listView;
    String url="http://api.map.baidu.com/geocoder/v2/";
    private int regionId;
    private ArrayList<SearchList> priceArray;
    private ArrayList<SearchList> yesterdayArray;
    private List<SortModel> sortObjects;
//    private String apiUrl="http://192.168.1.114:3001/api";
private String apiUrl="http://211.87.227.214:3001/api";
    /**
     * 初始化操作
     */

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        context = getActivity();
        activity = getActivity();

    }

    /**
     * 界面初始化
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_search, container,
                false);
        init();
        getGPSLocation();
        return view;
    }

    /**
     * 获取当前经纬度
     */
    private void getGPSLocation() {
        String provider;
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        //获取所有位置提供器
        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(activity, "No location provider", Toast.LENGTH_SHORT).show();
            return;
        }
        //检查Permission
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if(location != null){
            Log.d("test","4");
            lat = String.valueOf(location.getLatitude());
            lon = String.valueOf(location.getLongitude());
            AutoString autoString=new AutoString("ak","Lza9At4WibEALZnxBxANUFvUmuplUqGB");
            autoString.addToResult("callback","renderReverse");
            autoString.addToResult("location",lat+","+lon);
            autoString.addToResult("output","json");
            autoString.addToResult("pois","1");
            String params=autoString.getResult();
            Log.d("test","params"+params);
            //网络连接接口
            NetThread nt=new NetThread(han,url,params);
            nt.start();
            Log.d("test","Lat:"+lat+";Lon="+lon);
        }else{
            Log.d("test","5");
            LocationListener locationListener = new LocationListener(){
                public void onLocationChanged(Location location) {}
                public void onStatusChanged(String provider, int status, Bundle extras) {}
                public void onProviderEnabled(String provider) {}
                public void onProviderDisabled(String provider) {}
            };
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000, 0, locationListener);
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location != null){
                lat = String.valueOf(location.getLatitude());
                lon = String.valueOf(location.getLongitude());
            }
        }
    }



    private void init() {
        getSortObjects();
        tendBtn = (Button) view.findViewById(R.id.btn_tendency);
        tendBtn.setOnClickListener(this);
        cityText = (TextView) view.findViewById(R.id.textCity);
        cityText.setOnClickListener(this);
        listView= (ListView) view.findViewById(R.id.listView);
        priceArray=new ArrayList<SearchList>();
        yesterdayArray=new ArrayList<SearchList>();
        han = new Handler() {
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                String Jsmess = (String) msg.obj;
                Log.d("test","location........"+Jsmess);
                //获取JSON对象
                JSONObject temp = null;
                try {
                    temp = new JSONObject(Jsmess);
                    //得到数据
                    String Js2 = temp.getString("result");
                    JSONObject temp2 = new JSONObject(Js2);
                    String Js3=temp2.getString("addressComponent");
                    JSONObject temp3 = new JSONObject(Js3);
                    String city=temp3.getString("city");
                    Log.d("test","city........"+city);
                    cityText.setText(city);
                    getCityId();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        hanDialog = new Handler() {
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                if (msg.what==1){
                    String Jsmess = (String) msg.obj;
                    cityText.setText(Jsmess);
                    getCityId();
                }else {
                    CityDialog dialog=new CityDialog(context,hanCityDialog, (Integer) msg.obj);
                    dialog.show();
                }

            }
        };
        hanCityDialog = new Handler() {
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                    String Jsmess = (String) msg.obj;
                    cityText.setText(Jsmess);
                    getCityId();

            }
        };

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_tendency:
                Intent intent=new Intent(activity, TendencyActivity.class);
                startActivity(intent);
                break;
            case R.id.textCity:
                MyDialog dialog=new MyDialog(context,hanDialog);
                dialog.show();
                break;
        }
    }

    private void getCityId() {
        RestAdapter adapter = new RestAdapter(activity.getApplicationContext(),apiUrl);
        adapter.setAccessToken("4miVFTq2Yt3nDPPrTLLvJGSQNKH5k0x78fNyHENbwyICjii206NqmjL5ByChP6dO");
        RegionRepository regionRepository = adapter.createRepository(RegionRepository.class);
        regionRepository.findAll(new ListCallback<RegionModel>() {
            @Override
            public void onSuccess(List<RegionModel> objects) {
                for (int i=0;i<objects.size();i++){
                    if(cityText.getText().toString().equals(objects.get(i).getCity())){
                        regionId= (int) objects.get(i).getId();
                    } else if(objects.get(i).getProvince().equals(cityText.getText().toString())){
                    regionId= (int) objects.get(i).getId();
                }
                }
                getPrice();
            }

            @Override
            public void onError(Throwable t) {
                Log.d("test","t...."+t.toString());
            }
        });

    }

    private void getPrice() {
        RestAdapter adapter = new RestAdapter(getApplicationContext(),apiUrl);
        adapter.setAccessToken("4miVFTq2Yt3nDPPrTLLvJGSQNKH5k0x78fNyHENbwyICjii206NqmjL5ByChP6dO");
        PriceRepository productRepository = adapter.createRepository(PriceRepository.class);
        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> filterMap = new HashMap<String, Object>();
        filterMap.put("include","sort");
        params.put("filter",filterMap);
        productRepository. invokeStaticMethod("all", params, new JsonArrayParser<PriceModel>(productRepository,new ListCallback<PriceModel>() {

            @Override
            public void onSuccess(List<PriceModel> objects) {

                getYesterdayArray(objects);

            }
            @Override
            public void onError(Throwable t) {
                Log.d("test","Throwable..Obj..."+t.toString());
            }
        }));
    }

    private String getTend(SearchList searchList) {
        String tend="-";
        for(int i=0;i<yesterdayArray.size();i++){
            if (searchList.getSort().equals(yesterdayArray.get(i).getSort())){
                tend= String.valueOf(((int)searchList.getPrice()-yesterdayArray.get(i).getPrice())*100/searchList.getPrice())+"%";
                if ((searchList.getPrice()-yesterdayArray.get(i).getPrice())<0){
                    tend= String.valueOf((searchList.getPrice()-yesterdayArray.get(i).getPrice())*100/searchList.getPrice())+"%";
                }
            }
        }
        return tend;
    }

    private void getYesterdayArray(List<PriceModel> objects) {
        Date date=new Date();//取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE,-1);//把日期往后增加一天.整数往后推,负数往前移动
        date=calendar.getTime(); //这个时间就是日期往后推一天的结果
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);
        int price2=0;
        int number2=0;
        String lastDate2="";
        Log.d("test",".....110");
        for (int i=0;i<objects.size();i++){
            if (objects.get(i).getRegionId()==regionId&&dateString.equals(objects.get(i).getPriceDate().substring(0,10))){
                if (objects.get(i).getPriceDate().substring(0,10).equals(lastDate2)){
                    number2++;
                    price2= (int) (price2+objects.get(i).getPrice());
                }else {
                    number2++;
                    price2= (int) (price2+objects.get(i).getPrice());
                    yesterdayArray.add(new SearchList(price2/number2,getSort(objects.get(i).getSortId())));
                    lastDate2=objects.get(i).getPriceDate().substring(0,10);
                    number2=0;
                    price2=0;
                }

            }
        }
        Log.d("test",".....111");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
        final String dateNow=df.format(new Date());
        priceArray.clear();
        int price=0;
        int number=0;
        String lastDate="";
        Log.d("test",".....112");
        for (int i=0;i<objects.size();i++){
            Log.d("test",".....113");
            if (objects.get(i).getRegionId()==regionId&&objects.get(i).getPriceDate().substring(0,10).equals(dateNow)) {

                if (objects.get(i).getPriceDate().substring(0,10).equals(lastDate)){
                    Log.d("test",".....114");
                    number++;
                    price= (int) (price+objects.get(i).getPrice());
                }else {
                    number++;
                    price= (int) (price+objects.get(i).getPrice());
                    lastDate=objects.get(i).getPriceDate().substring(0,10);
                    SearchList searchDate = new SearchList();
                    searchDate.setArea(cityText.getText().toString());
                    searchDate.setPrice(price/number);
                    Log.d("test",".....1151");
                    searchDate.setTend(getTend(new SearchList(price/number,getSort(objects.get(i).getSortId()))));
                    Log.d("test",".....1152");
                    searchDate.setSort(getSort(objects.get(i).getSortId()));
                    priceArray.add(searchDate);
                    Log.d("test",".....115");
                    number=0;
                    price=0;
                }

            }
        }
        SearchListAdapter adapter=new SearchListAdapter(context,R.layout.item_search_list,priceArray);
        listView.setAdapter(adapter);
    }

    private String getSort(int id) {
        String sort="未知";
        for (int i=0;i<sortObjects.size();i++){
            if ((int)sortObjects.get(i).getId()==id){
                sort=sortObjects.get(i).getSubName();
                break;
            }
        }
        return sort;
    }

    private void getSortObjects() {
        RestAdapter adapter = new RestAdapter(getApplicationContext(),apiUrl);
        adapter.setAccessToken("4miVFTq2Yt3nDPPrTLLvJGSQNKH5k0x78fNyHENbwyICjii206NqmjL5ByChP6dO");
        SortRepository sortRepository = adapter.createRepository(SortRepository.class);
        Log.d("test","a");
        sortRepository.findAll(new ListCallback<SortModel>() {
            @Override
            public void onSuccess(List<SortModel> objects) {
                sortObjects=objects;
            }

            @Override
            public void onError(Throwable t) {
                Log.d("test","Throwable..Objs..."+t.toString());
            }

        });
    }

    //用于一次性上传省市信息的方法
    private void upload() {
        String[][] areas= Areas.areas;
        for (int i=0;i<areas.length;i++){
            for (int j=1;j<areas[i].length;j++){
                RestAdapter adapter = new RestAdapter(getApplicationContext(),apiUrl);
                adapter.setAccessToken("4miVFTq2Yt3nDPPrTLLvJGSQNKH5k0x78fNyHENbwyICjii206NqmjL5ByChP6dO");
                RegionRepository regionRepository = adapter.createRepository(RegionRepository.class);
                RegionModel region= regionRepository.createObject(ImmutableMap.of("Province", areas[i][0]));
//                region.setProvince(areas[i][0]);
                region.setCity(areas[i][j]);
                region.save(new VoidCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("test","上传成功");
                    }

                    @Override
                    public void onError(Throwable t) {

                    }
                });
            }

        }
    }

    @Override
    protected void onResumeLazy() {
        super.onResumeLazy();
        Log.d("cccc", "Fragment所在的Activity onResume, onResumeLazy " + this);
    }

    @Override
    protected void onFragmentStartLazy() {
        super.onFragmentStartLazy();
        Log.d("cccc", "Fragment 显示 " + this);
    }

    @Override
    protected void onFragmentStopLazy() {
        super.onFragmentStopLazy();
        Log.d("cccc", "Fragment 掩藏 " + this);
    }

    @Override
    protected void onPauseLazy() {
        super.onPauseLazy();
        Log.d("cccc", "Fragment所在的Activity onPause, onPauseLazy " + this);
    }

    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        Log.d("cccc", "Fragment View将被销毁 " + this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("cccc", "Fragment 所在的Activity onDestroy " + this);
    }


}
