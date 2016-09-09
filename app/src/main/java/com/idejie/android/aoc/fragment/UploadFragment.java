package com.idejie.android.aoc.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.bean.UserId;
import com.idejie.android.aoc.dialog.CityDialog;
import com.idejie.android.aoc.dialog.GradeDialog;
import com.idejie.android.aoc.dialog.MyDialog;
import com.idejie.android.aoc.dialog.SortDetailDialog;
import com.idejie.android.aoc.dialog.SortDialog;
import com.idejie.android.aoc.model.GradeModel;
import com.idejie.android.aoc.model.PriceModel;
import com.idejie.android.aoc.model.RegionModel;
import com.idejie.android.aoc.model.SortModel;
import com.idejie.android.aoc.repository.GradeRepository;
import com.idejie.android.aoc.repository.PriceRepository;
import com.idejie.android.aoc.repository.RegionRepository;
import com.idejie.android.aoc.repository.SortRepository;
import com.idejie.android.aoc.tools.ImageLoaderHelper;
import com.idejie.android.library.fragment.LazyFragment;
import com.jorge.circlelibrary.ImageCycleView;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.loopback.callbacks.VoidCallback;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerClickListener;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by shandongdaxue on 16/8/10.
 */
public class UploadFragment extends LazyFragment implements View.OnClickListener {
    private LayoutInflater inflate;
    private Context context;
    private Activity activity;
    private View view;
    private ImageCycleView imageCycleView;
    private Button btnUpload,btnCancel;
    private EditText editPrice,editAmount,editMarketName;
    private TextView textProvince,textType,textRank;
    private String province,type,rank,price,amount,marketName;
    private LinearLayout lineProvince,lineType,lineRank;
    private Handler hanDialog,hanCityDialog,hanSortDialog,hanDetailDialog,hanGradeDialog;
    private int sorts[][];
    private List<SortModel> objectArray;
    private List<GradeModel> gradeArray;
    private int regionId,sortId,gradeId;
    private Banner banner;
    private String apiUrl="http://211.87.227.214:3001/api";
//    private String apiUrl="http://192.168.1.114:3001/api";
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
        view = inflater.inflate(R.layout.fragment_upload, container,
                false);
        init();
        return view;
    }

    private void init() {
        //初始化广告栏
        initBanner();
        //初始化各个控件
        lineProvince= (LinearLayout) view.findViewById(R.id.line_1);
        lineProvince.setOnClickListener(this);
        lineType= (LinearLayout) view.findViewById(R.id.line_2);
        lineType.setOnClickListener(this);
        lineRank= (LinearLayout) view.findViewById(R.id.line_3);
        lineRank.setOnClickListener(this);
        btnCancel= (Button) view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);
        btnUpload= (Button) view.findViewById(R.id.btn_upload);
        btnUpload.setOnClickListener(this);
        textProvince= (TextView) view.findViewById(R.id.province);
        textType= (TextView) view.findViewById(R.id.type);
        textRank= (TextView) view.findViewById(R.id.rank);
        editPrice= (EditText) view.findViewById(R.id.edit_price);
        editAmount= (EditText) view.findViewById(R.id.edit_amount);
        editMarketName= (EditText) view.findViewById(R.id.edit_Market_name);

        hanDialog = new Handler() {
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                if (msg.what==1){
                    String Jsmess = (String) msg.obj;
                    textProvince.setText(Jsmess);
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
                textProvince.setText(Jsmess);
                getCityId();
            }
        };
        hanSortDialog = new Handler() {
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                String Jsmess = (String) msg.obj;
                SortDetailDialog detailDialog=new SortDetailDialog(context,hanDetailDialog,objectArray,Jsmess);
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
        hanGradeDialog = new Handler() {
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                gradeId=msg.what;
                String Jsmess = (String) msg.obj;
                textRank.setText(Jsmess);

            }
        };


    }

    private void initBanner() {
        String[] images= getResources().getStringArray(R.array.url2);
        String[] titles= getResources().getStringArray(R.array.title2);
        banner = (Banner) view.findViewById(R.id.main_banner);
        //设置间隔
        banner.setDelayTime(3000);
        //banner加点
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        //记得设置标题列表哦
        banner.setBannerTitle(titles);
        //添加图片
        banner.setImages(images);
        //bannerde图片的点击事件
        banner.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void OnBannerClick(int position) {
                Toast.makeText(getApplicationContext(),"你点击了："+position,Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getCityId() {
        RestAdapter adapter = new RestAdapter(activity.getApplicationContext(),apiUrl);
        adapter.setAccessToken("4miVFTq2Yt3nDPPrTLLvJGSQNKH5k0x78fNyHENbwyICjii206NqmjL5ByChP6dO");
        RegionRepository regionRepository = adapter.createRepository(RegionRepository.class);
        regionRepository.findAll(new ListCallback<RegionModel>() {
            @Override
            public void onSuccess(List<RegionModel> objects) {
                for (int i=0;i<objects.size();i++){
                    if(textProvince.getText().toString().equals(objects.get(i).getCity())){
                        regionId= (int) objects.get(i).getId();
                        break;
                    }else if(objects.get(i).getProvince().equals(textProvince.getText().toString())){
                        regionId= (int) objects.get(i).getId();
                        break;
                    }
                }

            }

            @Override
            public void onError(Throwable t) {

            }
        });

    }







    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_upload:
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                int i=0;
                SharedPreferences pref=context.getSharedPreferences("LoadNumber", MODE_PRIVATE);
                String date=pref.getString("date",df.format(new Date()).substring(0,10));
                if (date.equals(df.format(new Date()).substring(0,10))){
                    i=pref.getInt("number",0);
                }else {
                    i=0;
                }
                if (i>=2){
                    Toast.makeText(context,"您今天上传次数已到达上限:0/2",Toast.LENGTH_SHORT).show();
                }else {
                    //储存账号密码
                    SharedPreferences.Editor editor=context.getSharedPreferences("LoadNumber",MODE_PRIVATE).edit();
                    editor.putString("date",df.format(new Date()).substring(0,10));
                    editor.putInt("number",i+1);
                    editor.commit();
                    upLoad();
                }
//                beEmpty();//用于上传成功得到返回值以后再用
                break;
            case R.id.btn_cancel:
                beEmpty();
                //哎我发现取消按钮没有上一页
                break;
            case R.id.line_1:
                MyDialog dialog=new MyDialog(context,hanDialog);
                dialog.show();
                break;
            case R.id.line_2:
                getSort();
                break;
            case R.id.line_3:
                if (textType.getText().equals("品种")){
                    Toast.makeText(context,"请先选好品种",Toast.LENGTH_SHORT).show();
                }else {
                    getRank();
                }

                break;

        }
    }

    private void getRank() {
        RestAdapter adapter = new RestAdapter(getApplicationContext(), apiUrl);
        adapter.setAccessToken("4miVFTq2Yt3nDPPrTLLvJGSQNKH5k0x78fNyHENbwyICjii206NqmjL5ByChP6dO");
        GradeRepository gradeRepository = adapter.createRepository(GradeRepository.class);
        Log.d("test","a");
        gradeRepository.findAll(new ListCallback<GradeModel>() {
            @Override
            public void onSuccess(List<GradeModel> objects) {
                gradeArray=objects;
                GradeDialog gradeDialog=new GradeDialog(context,hanGradeDialog,objects,sortId,textType.getText().toString());
                gradeDialog.show();
            }

            @Override
            public void onError(Throwable t) {
                Log.d("test","Throwable..Objs..."+t.toString());
            }


        });
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
                SortDialog sortDialog=new SortDialog(context,hanSortDialog,objects);
                sortDialog.show();

            }

            @Override
            public void onError(Throwable t) {
                Log.d("test","Throwable..Objs..."+t.toString());
            }


        });
    }

    private void upLoad() {
        if (textProvince.getText().equals("省市")||textType.getText().equals("品种")
                ||textRank.getText().equals("级别")||editPrice.getText().equals("元/公斤")){
            Toast.makeText(context,"请填写必要信息",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context,"上传中",Toast.LENGTH_SHORT).show();
            btnUpload.setBackgroundResource(R.drawable.border_green);
            RestAdapter adapter = new RestAdapter(activity.getApplicationContext(), apiUrl);
            adapter.setAccessToken("4miVFTq2Yt3nDPPrTLLvJGSQNKH5k0x78fNyHENbwyICjii206NqmjL5ByChP6dO");
            PriceRepository productRepository = adapter.createRepository(PriceRepository.class);
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("regionId", regionId);
            params.put("sortId", sortId);
            params.put("gradeId", gradeId);
            params.put("price", Integer.parseInt(editPrice.getText().toString()));
            params.put("turnover",Integer.parseInt(editAmount.getText().toString()) );
            params.put("marketName", editMarketName.getText().toString());
            params.put("userId", UserId.id);

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            params.put("priceDate",df.format(new Date()));
            PriceModel price = productRepository.createObject(params );
            price.save(new VoidCallback() {
                @Override
                public void onSuccess() {
                    // Pencil now exists on the server!
                    Toast.makeText(context,"上传成功",Toast.LENGTH_SHORT).show();
                    btnUpload.setBackgroundResource(R.drawable.border_grey);
                    beEmpty();
                }

                @Override
                public void onError(Throwable t) {
                    Log.d("test","Throwable....."+t.toString());
                    // save failed, handle the error
                    Toast.makeText(context,"上传失败",Toast.LENGTH_SHORT).show();
                    btnUpload.setBackgroundResource(R.drawable.border_grey);
                    beEmpty();
                }
            });
        }

    }

    private void beEmpty() {
        //记得把各值也清空
        editPrice.setText("");
        editAmount.setText("");
        editMarketName.setText("");
        textProvince.setText("省市");
        textType.setText("品种");
        textRank.setText("等级");

    }





}
