package com.idejie.android.aoc.activity;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.idejie.android.aoc.R;
import com.idejie.android.aoc.adapter.MyLoadListAdapter;
import com.idejie.android.aoc.bean.LineData;
import com.idejie.android.aoc.bean.MyUploadList;
import com.idejie.android.aoc.bean.UserId;
import com.idejie.android.aoc.dialog.SortTDialog;
import com.idejie.android.aoc.model.PriceModel;
import com.idejie.android.aoc.model.RegionModel;
import com.idejie.android.aoc.model.SortModel;
import com.idejie.android.aoc.repository.PriceRepository;
import com.idejie.android.aoc.repository.RegionRepository;
import com.idejie.android.aoc.repository.SortRepository;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.JsonArrayParser;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.loopback.callbacks.VoidCallback;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyPriceActivity extends Activity implements View.OnClickListener {

//    private String apiUrl="http://192.168.1.114:3001/api";
    private String apiUrl="http://211.87.227.214:3001/api";
    private ImageView btnBack;
    private ArrayList<MyUploadList> mlist;
    private int myId;
    private List<SortModel> sortArray;
    private List<RegionModel> regionArray;
    private ListView listView;
    private MyLoadListAdapter adapter;
    private Handler han;
    private ArrayList<PriceModel> priceArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_price);
        init();
    }

    private void init() {
        priceArray=new ArrayList<PriceModel>();
        //我的Id应该是直接获取的
        myId= Integer.parseInt(UserId.id);
        btnBack= (ImageView) findViewById(R.id.back);
        btnBack.setOnClickListener(this);
        listView= (ListView) findViewById(R.id.listView);
        mlist = new ArrayList<MyUploadList>();
        //几个操作套在里面,一个回调后进行下一个
        Log.d("test",1+"");
        getRegion();
        han= new Handler() {
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                int Jsmess =  msg.what;
                if (Jsmess==1) {
                    adapter = new MyLoadListAdapter(MyPriceActivity.this, R.layout.item_my_upload_list, mlist);
                    listView.setAdapter(adapter);
                }
            }
        };
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("test","i.........."+i);
                dialog(i);

                return false;
            }
        });
    }

    private void getUpData() {
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
                    for(int i=0;i<objects.size();i++){
                        PriceModel priceModel=objects.get(i);
                        Log.d("test",4+"");

                        if (priceModel.getUserId()==myId){
                            mlist.add(new MyUploadList(getSortName(priceModel.getSortId()),(int)priceModel.getTurnover(),
                            priceModel.getMarketName(),getRegionName(priceModel.getRegionId()),
                                    priceModel.getPriceDate().substring(0,10),(int)priceModel.getPrice(), (Integer) priceModel.getId()));
                            priceArray.add(priceModel);
                        }
                        Log.d("test",5+"");
                    }
                    Message msg=new Message();
                    msg.what=1;
                    han.sendMessage(msg);

                }
                @Override
                public void onError(Throwable t) {
                    Log.d("test","Throwable..Obj..."+t.toString());
                }
            }));

    }

    private String getRegionName(int regionId) {
        String regionName="";
        for (int i=0;i<regionArray.size();i++){
            RegionModel regionModel=regionArray.get(i);
            if ((int)regionModel.getId()==regionId){
                if (regionModel.getCity()==null){
                    return regionModel.getProvince();
                }
                return regionModel.getProvince()+"  "+regionModel.getCity();
            }
        }
        return regionName;
    }


    private String getSortName(int sortId) {
        String sort="猪肉";
        for (int i=0;i<sortArray.size();i++){
            SortModel sortModel=sortArray.get(i);
            if ((int)sortModel.getId()==sortId){
                return sortModel.getSubName();
            }
        }
        return sort;
    }
    private void getRegion() {
        RestAdapter adapter = new RestAdapter(getApplicationContext(),apiUrl);
        adapter.setAccessToken("4miVFTq2Yt3nDPPrTLLvJGSQNKH5k0x78fNyHENbwyICjii206NqmjL5ByChP6dO");
        RegionRepository regionRepository = adapter.createRepository(RegionRepository.class);
        regionRepository.findAll(new ListCallback<RegionModel>() {
            @Override
            public void onSuccess(List<RegionModel> objects) {
                Log.d("test",2+"");
                regionArray=objects;
                getSort();
            }

            @Override
            public void onError(Throwable t) {
                Log.d("test","t...."+t.toString());
            }
        });

    }
    private void getSort() {
        RestAdapter adapter = new RestAdapter(getApplicationContext(), apiUrl);
        adapter.setAccessToken("4miVFTq2Yt3nDPPrTLLvJGSQNKH5k0x78fNyHENbwyICjii206NqmjL5ByChP6dO");
        SortRepository sortRepository = adapter.createRepository(SortRepository.class);
        sortRepository.findAll(new ListCallback<SortModel>() {
            @Override
            public void onSuccess(List<SortModel> objects) {
                Log.d("test",3+"");
                sortArray=objects;
                getUpData();
            }

            @Override
            public void onError(Throwable t) {
                Log.d("test","Throwable..Objs..."+t.toString());
            }

        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
            break;
        }

    }

    protected void dialog(final int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyPriceActivity.this);
        builder.setMessage("确认删除吗？");

        builder.setTitle("提示");

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeDate(priceArray.get(i),i);
                dialog.dismiss();

            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    private void removeDate(PriceModel priceModel, final int i) {
//        RestAdapter adapter = new RestAdapter(getApplicationContext(), apiUrl);
//        adapter.setAccessToken("4miVFTq2Yt3nDPPrTLLvJGSQNKH5k0x78fNyHENbwyICjii206NqmjL5ByChP6dO");
//        PriceRepository priceRepository = adapter.createRepository(PriceRepository.class);
//        PriceModel priceModel=priceRepository.createObject(ImmutableMap.of("id",id ));
        priceModel.destroy(new VoidCallback() {
            @Override
            public void onSuccess() {
                mlist.remove(i);
                adapter = new MyLoadListAdapter(MyPriceActivity.this, R.layout.item_my_upload_list, mlist);
                listView.setAdapter(adapter);
            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }

}
