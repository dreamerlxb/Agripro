package com.idejie.android.aoc.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.activity.TendencyActivity;
import com.idejie.android.aoc.adapter.SearchListAdapter;
import com.idejie.android.aoc.dialog.CityDialog;
import com.idejie.android.aoc.dialog.MyDialog;
import com.idejie.android.aoc.fragment.tab.SecondLayerFragment;
import com.idejie.android.aoc.tools.AutoString;
import com.idejie.android.aoc.tools.NetThread;
import com.idejie.android.library.fragment.LazyFragment;
import com.idejie.android.library.view.indicator.Indicator;
import com.idejie.android.library.view.indicator.IndicatorViewPager;
import com.idejie.android.library.view.indicator.slidebar.ColorBar;
import com.idejie.android.library.view.indicator.transition.OnTransitionTextListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

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
    private SearchListAdapter searchListAdapter;
    String url="http://api.map.baidu.com/geocoder/v2/";

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
        tendBtn = (Button) view.findViewById(R.id.btn_tendency);
        tendBtn.setOnClickListener(this);
        cityText = (TextView) view.findViewById(R.id.textCity);
        cityText.setOnClickListener(this);
        listView= (ListView) view.findViewById(R.id.listView);
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

            }
        };

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



}
