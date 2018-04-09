package com.idejie.android.aoc.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.idejie.android.aoc.R;
import com.idejie.android.aoc.activity.NewsDetailActivity;
import com.idejie.android.aoc.application.UserApplication;
import com.idejie.android.aoc.dialog.GradeDialog;
import com.idejie.android.aoc.dialog.ProvinceTDialog;
import com.idejie.android.aoc.dialog.SortDialog;
import com.idejie.android.aoc.model.GradeModel;
import com.idejie.android.aoc.model.NewsModel;
import com.idejie.android.aoc.model.RegionModel;
import com.idejie.android.aoc.model.ScrollImageModel;
import com.idejie.android.aoc.model.SortModel;
import com.idejie.android.aoc.repository.PriceRepository;
import com.idejie.android.aoc.repository.RegionRepository;
import com.idejie.android.aoc.repository.ScrollImageRepository;
import com.idejie.android.aoc.repository.SortRepository;
import com.idejie.android.aoc.repository.UserModelRepository;
import com.idejie.android.library.fragment.LazyFragment;

import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.remoting.adapters.Adapter;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import com.youth.banner.listener.OnBannerClickListener;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;
import com.youth.banner.loader.ImageLoaderInterface;


import org.json.JSONObject;

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
public class PriceFragment extends LazyFragment implements View.OnClickListener, TextWatcher {
    public static final int REQUEST_CODE = 2000;
    public static final String SELECT_TYPE = "select_type";

    private LayoutInflater inflate;
    private View view;
    private Button btnUpload, btnCancel;
    private EditText editPrice, editAmount, editMarketName;
    private TextView textProvince, textType, textRank;
    private Handler hanProvinceDialog, hanSortDialog, hanGradeDialog;

    private Banner banner;
    public List<String> bannerTitles = new ArrayList<>();
    public List<String> bannerImgUrls = new ArrayList<>();
    private List<ScrollImageModel> scrollImageList;

    private UserApplication userApplication;

    private ArrayMap<String, List<RegionModel>> chinaMap;
    private Map<String, List<SortModel>> sortMap;

    private RegionModel selectedRegion;
    private SortModel selectedSort;
    private GradeModel selectedGrade;
    private Dialog loadingDialog;

    private SimpleDateFormat dateFormat;
    private int uploadNum = 0;

    /**
     * 初始化操作
     */

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 界面初始化
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        this.inflate = inflater;
        view = inflater.inflate(R.layout.fragment_price, container, false);
        init();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        userApplication = (UserApplication) getActivity().getApplication();
    }

    private void init() {
        //初始化各个控件
        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);
        btnUpload = (Button) view.findViewById(R.id.btn_upload);
        btnUpload.setOnClickListener(this);
        btnUpload.setEnabled(false);
        btnUpload.setBackgroundResource(R.color.gray_light);

        textProvince = (TextView) view.findViewById(R.id.province);
        textProvince.addTextChangedListener(this);
        textProvince.setOnClickListener(this);
        textType = (TextView) view.findViewById(R.id.type);
        textType.setOnClickListener(this);
        textType.addTextChangedListener(this);
        textRank = (TextView) view.findViewById(R.id.rank);
        textRank.setOnClickListener(this);
        textRank.addTextChangedListener(this);
        editPrice = (EditText) view.findViewById(R.id.edit_price);
        editPrice.setOnClickListener(this);
        editPrice.addTextChangedListener(this);
        editAmount = (EditText) view.findViewById(R.id.edit_amount);
        editAmount.setOnClickListener(this);
        editMarketName = (EditText) view.findViewById(R.id.edit_Market_name);
        editMarketName.setOnClickListener(this);

        //初始化广告栏
        banner = (Banner) view.findViewById(R.id.main_banner);
        requestBannerInfo();

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
                    textRank.setText(selectedSort.getSubName());
                    selectedGrade = null;
                } else {
                    selectedGrade = (GradeModel) msg.obj;
                    textRank.setText(selectedGrade.getName());
                }
            }
        };
    }

    private void requestBannerInfo() {
        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> filter = new HashMap<String, Object>();
        Map<String, Object> where = new HashMap<String, Object>();

        List<String> list = new ArrayList<>();
        list.add("image");
        list.add("news");

        where.put("enabled", true);
        where.put("categoryId", 7); //说明是报价的轮播图

        filter.put("where", where);
        filter.put("order", "order");
        filter.put("include", list);
        filter.put("limit", 4);

        Gson gson1 = new Gson();
        String filterStr = gson1.toJson(filter);

        params.put("filter", filterStr);
        ScrollImageRepository scrollImageRepository = ScrollImageRepository.getInstance(getContext());
        scrollImageRepository.all(params, new ListCallback<ScrollImageModel>() {
            @Override
            public void onSuccess(List<ScrollImageModel> objects) {
                scrollImageList = objects;
                initBannerData(objects);
            }

            @Override
            public void onError(Throwable t) {

            }
        });
    }

    protected void initBannerData(List<ScrollImageModel> obj) {
        for (int i = 0; i < obj.size(); i++) {
            ScrollImageModel scrollImageModel = obj.get(i);
            bannerImgUrls.add(scrollImageModel.getImage().getUrl());
            bannerTitles.add(scrollImageModel.getTitle());
        }
        banner.setDelayTime(3000);
        //banner加点
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        //记得设置标题列表哦
        banner.setImageLoader(new ImageLoaderInterface() {

            @Override
            public void displayImage(Context context, Object path, View imageView) {
                Glide
                        .with(context)
                        .load(path)
                        .centerCrop()
                        .crossFade()
                        .into((ImageView) imageView);
            }

            @Override
            public View createImageView(Context context) {
                return null;
            }
        });
        //bannerde图片的点击事件
        banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                ScrollImageModel scrollImageModel = scrollImageList.get(position - 1);

                NewsModel newsModel = scrollImageModel.getNews();
                Double d = (Double) newsModel.getId();
                newsModel.setNewsId(d.intValue());

                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("news", newsModel);
                intent.putExtras(bundle);
                intent.setClass(PriceFragment.this.getContext(), NewsDetailActivity.class);
                startActivity(intent);
            }
        });
//        banner.setOnBannerClickListener(new OnBannerClickListener() {
//            @Override
//            public void OnBannerClick(int position) {
//                ScrollImageModel scrollImageModel = scrollImageList.get(position - 1);
//
//                NewsModel newsModel = scrollImageModel.getNews();
//                Double d = (Double) newsModel.getId();
//                newsModel.setNewsId(d.intValue());
//
//                Intent intent = new Intent();
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("news", newsModel);
//                intent.putExtras(bundle);
//                intent.setClass(PriceFragment.this.getContext(), NewsDetailActivity.class);
//                startActivity(intent);
//            }
//        });
        banner.setBannerTitles(bannerTitles);
        banner.setImages(bannerImgUrls);
        banner.setDelayTime(4000);
        banner.start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) { //上传信息
            case R.id.btn_upload:
                if (userApplication.getUser() == null) {
                    Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
                    return;
                }

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式

                SharedPreferences pref = getContext().getSharedPreferences("LoadNumber", MODE_PRIVATE);
                String todayStr = df.format(new Date());

                String dateStr = pref.getString("date", todayStr);

                if (TextUtils.equals(dateStr, todayStr)) {
                    uploadNum = pref.getInt("number", 0);
                } else {
                    uploadNum = 0;
                }

                if (uploadNum >= 2) {
                    Toast.makeText(getContext(), "对不起，您今天的报价次数已达2次，谢谢您对我们工作的支持，请明天再来报价！", Toast.LENGTH_SHORT).show();
                } else {
                    upLoad(); //长传信息
                }
                break;
            case R.id.btn_cancel: //取消上传
                beEmpty();
                break;
            case R.id.province:
                if (chinaMap == null) {
                    chinaMap = new ArrayMap<>();
                    getRegionInfo();
                } else {
                    ProvinceTDialog dialog = new ProvinceTDialog(getContext(), hanProvinceDialog, chinaMap);
                    dialog.show();
                }
                break;
            case R.id.type:
                if (sortMap == null) {
                    sortMap = new HashMap<>();
                    getSorts();
                } else {
                    SortDialog sortDialog = new SortDialog(getContext(), hanSortDialog, sortMap);
                    sortDialog.show();
                }

                break;
            case R.id.rank:
                if (selectedSort == null) {
                    Toast.makeText(getContext(), "请先选好品种", Toast.LENGTH_SHORT).show();
                } else {
                    GradeDialog gradeDialog = new GradeDialog(getContext(), hanGradeDialog, selectedSort);
                    gradeDialog.show();
                }
                break;
        }
    }

    //获取城市信息
    private void getRegionInfo() {
        showLoadingDialog();
        RegionRepository regionRepository = RegionRepository.getInstance(getContext(), null);
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

                ProvinceTDialog dialog = new ProvinceTDialog(getContext(), hanProvinceDialog, chinaMap);
                dialog.show();
            }

            @Override
            public void onError(Throwable t) {
                loadingDialog.dismiss();
            }
        });
    }

    private void getSorts() {
        showLoadingDialog();
        SortRepository sortRepository = SortRepository.getInstance(getContext(), null);
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

                SortDialog sortDialog = new SortDialog(getContext(), hanSortDialog, sortMap);
                sortDialog.show();
            }

            @Override
            public void onError(Throwable t) {
                loadingDialog.dismiss();
                Log.d("test", "Throwable..Objs..." + t.toString());
            }
        });
    }

    private void showLoadingDialog() {
        if (loadingDialog == null) {
            View v2 = inflate.inflate(R.layout.loading_dialog2, null);// 得到加载view
            LinearLayout layout2 = (LinearLayout) v2.findViewById(R.id.dialog_view2);

            loadingDialog = new Dialog(getContext(), R.style.loading_dialog);
            loadingDialog.setContentView(layout2, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
        }
        loadingDialog.show();
    }

    /**
     * 上传报价
     */
    private void upLoad() {
        if (selectedRegion == null || selectedSort == null || TextUtils.isEmpty(textRank.getText()) || TextUtils.isEmpty(editPrice.getText())) {
            Toast.makeText(getContext(), "请填写必要信息", Toast.LENGTH_SHORT).show();
        } else if (!TextUtils.isDigitsOnly(editPrice.getText())) {
            Toast.makeText(getContext(), "价钱应该为数字", Toast.LENGTH_SHORT).show();
        } else if (!TextUtils.isEmpty(editAmount.getText()) && !TextUtils.isDigitsOnly(editPrice.getText())) {
            Toast.makeText(getContext(), "成交量应该为数字", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "上传中", Toast.LENGTH_SHORT).show();
            btnUpload.setBackgroundResource(R.color.gray_light);
            btnUpload.setEnabled(false);

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("regionId", selectedRegion.getId());
            params.put("sortId", selectedSort.getId());
            if (selectedGrade != null) {
                params.put("gradeId", selectedGrade.getId());
            }
            params.put("price", Double.parseDouble(editPrice.getText().toString()));
            Log.d("test", editAmount.getText().toString());
            if (TextUtils.isEmpty(editAmount.getText())) {
                params.put("turnover", 0);
            } else {
                params.put("turnover", Double.valueOf(editAmount.getText().toString()));
            }
            params.put("marketName", editMarketName.getText().toString());
            params.put("userId", userApplication.getUser().getUserId());
            params.put("priceDate", dateFormat.format(new Date()));

            Log.d("PriceFragment", userApplication.getAccessToken());
            PriceRepository priceRepository = PriceRepository.getInstance(getContext(), userApplication.getAccessToken());

            priceRepository.invokeStaticMethod("create", params, new Adapter.JsonObjectCallback() {
                @Override
                public void onSuccess(JSONObject response) {
//                    Log.d("PriceFragment", response.toString());
                    btnUpload.setBackgroundResource(R.color.colorPrimary);
                    btnUpload.setEnabled(true);
                    addScore();
                    new Thread(saveDataRunnable).start();
                    Toast.makeText(getContext(), "上传成功", Toast.LENGTH_SHORT).show();
                    beEmpty();
                }

                @Override
                public void onError(Throwable t) {
//                    Log.d("PriceFragment", "Throwable....." + t.toString());
                    Toast.makeText(getContext(), "上传失败，请重试", Toast.LENGTH_SHORT).show();
                    btnUpload.setBackgroundResource(R.color.colorPrimary);
                    btnUpload.setEnabled(true);
                    beEmpty();
                }
            });
        }
    }

    private Runnable saveDataRunnable = new Runnable() {
        @Override
        public void run() {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd"); //设置日期格式

            SharedPreferences pref = getContext().getSharedPreferences("LoadNumber", MODE_PRIVATE);

            String todayStr = df.format(new Date());

            SharedPreferences.Editor editor = pref.edit();
            editor.putString("date", todayStr);
            editor.putInt("number", uploadNum + 1);
            editor.commit();
        }
    };

    private void addScore() {
        UserModelRepository userRepository = UserModelRepository.getInstance(getContext(), userApplication.getAccessToken());
        Map<String, Object> params = new HashMap<>();
        params.put("id", userApplication.getUser().getUserId());
        params.put("score", userApplication.getUser().getScore() + 1);
        userRepository.invokeStaticMethod("upsert", params, new Adapter.JsonObjectCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d("积分", "成功+1");
                userApplication.getUser().setScore(userApplication.getUser().getScore() + 1);
                Toast.makeText(getContext(), "积分+1", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable t) {
                Log.d("积分", "失败");
            }
        });
    }

    private void beEmpty() {
        //记得把各值也清空
        editPrice.setText("");
        editAmount.setText("");
        editMarketName.setText("");
        textProvince.setText("");
        textType.setText("");
        textRank.setText("");
    }

//    @Override
//    public void displayImage(Context context, Object path, ImageView imageView) {
//        Glide
//            .with(context)
//            .load(path)
//            .centerCrop()
//            .crossFade()
//            .into(imageView);
//    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!TextUtils.isEmpty(textRank.getText())
                && !TextUtils.isEmpty(textProvince.getText())
                && !TextUtils.isEmpty(textType.getText())
                && !TextUtils.isEmpty(editPrice.getText())) {
            btnUpload.setEnabled(true);
            btnUpload.setBackgroundResource(R.color.colorPrimary);
        } else {
            btnUpload.setEnabled(false);
            btnUpload.setBackgroundResource(R.color.gray_light);
        }
    }
}
