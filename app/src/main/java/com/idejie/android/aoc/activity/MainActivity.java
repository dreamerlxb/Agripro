package com.idejie.android.aoc.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.idejie.android.aoc.R;
import com.idejie.android.aoc.UserApplication;
import com.idejie.android.aoc.fragment.MeFragment;
import com.idejie.android.aoc.fragment.IndexFragment;
import com.idejie.android.aoc.fragment.SearchFragment;
import com.idejie.android.aoc.fragment.PriceFragment;
import com.idejie.android.aoc.fragment.tab.SecondLayerFragment;
import com.idejie.android.aoc.model.UserModel;
import com.idejie.android.aoc.repository.UserModelRepository;
import com.idejie.android.library.view.indicator.FixedIndicatorView;
import com.idejie.android.library.view.indicator.IndicatorViewPager;
import com.idejie.android.library.view.indicator.transition.OnTransitionTextListener;
import com.idejie.android.library.view.viewpager.SViewPager;
import com.strongloop.android.remoting.adapters.Adapter;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1000; // 请求码

    private IndicatorViewPager indicatorViewPager;
    private FixedIndicatorView indicator;
    private SecondLayerFragment secondLayerFragment;

    private UserApplication userApplication;

    private Dialog loadingDialog;
//    private PermissionsChecker mPermissionsChecker; // 权限检测器

    static final String[] PERMISSIONS = new String[]{
        Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        secondLayerFragment =new SecondLayerFragment();

        userApplication = (UserApplication) getApplication();
//        mPermissionsChecker = new PermissionsChecker(this);

        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));

        setContentView(R.layout.activity_main);
        SViewPager viewPager = (SViewPager) findViewById(R.id.tabmain_viewPager);
        indicator = (FixedIndicatorView) findViewById(R.id.tabmain_indicator);
//        Color. Color.argb(255, 17, 179, 100)
        indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(Color.argb(255, 5, 112, 67), Color.BLACK));

    //        //这里可以添加一个view，作为centerView，会位于Indicator的tab的中间
    //        centerView = getLayoutInflater().inflate(R.layout.tab_main_center, indicator, false);
    //        indicator.setCenterView(centerView);
    //        centerView.setOnClickListener(onClickListener);

        indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
        indicatorViewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        indicatorViewPager.setOnIndicatorPageChangeListener(new IndicatorViewPager.OnIndicatorPageChangeListener() {
            @Override
            public void onIndicatorPageChange(int preItem, int currentItem) {

                TextView tv = (TextView) indicator.getItemView(currentItem);
                Drawable top = tv.getCompoundDrawables()[1];
                Drawable wrappedDrawable = DrawableCompat.wrap(top);
                DrawableCompat.setTintList(wrappedDrawable, ColorStateList.valueOf(Color.argb(255, 5, 112, 67)));
                tv.setCompoundDrawables(null, wrappedDrawable, null, null);

                TextView preTv = (TextView) indicator.getItemView(preItem);
                Drawable preTop = preTv.getCompoundDrawables()[1];
                Drawable preWrappedDrawable = DrawableCompat.wrap(preTop);
                DrawableCompat.setTintList(preWrappedDrawable, ColorStateList.valueOf(Color.BLACK));
                preTv.setCompoundDrawables(null, preWrappedDrawable, null, null);
            }
        });
        // 禁止viewpager的滑动事件
        viewPager.setCanScroll(false);
        // 设置viewpager保留界面不重新加载的页面数量
        viewPager.setOffscreenPageLimit(4);

        getLoginUser();
    }

    private void getLoginUser(){
        SharedPreferences sharedPreferences = getSharedPreferences("LoginUser", MODE_PRIVATE);
        final int userId = sharedPreferences.getInt("userId", -1);
        final String accessToken = sharedPreferences.getString("accessToken", "");
        Log.i("Login User",userId+"   "+accessToken);
        if (userId != -1 && !TextUtils.isEmpty(accessToken)) {
            UserModelRepository userModelRepository = UserModelRepository.getInstance(MainActivity.this, accessToken);
            Map<String,Object> params = new HashMap<>();
            Map<String,Object> filter = new HashMap<>();
            params.put("id",userId);
            filter.put("include","avatar");
            params.put("filter",filter);
            showLoadingDialog();
            userModelRepository.invokeStaticMethod("findById", params, new Adapter.JsonObjectCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    loadingDialog.dismiss();
                    if (response != null){
                        Log.i("Login User",response.toString());
                        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
                        UserModel userModel = gson.fromJson(response.toString(),UserModel.class);
                        userModel.setUserId(userId);

                        userApplication.setAccessToken(accessToken);
                        userApplication.setUser(userModel);
                    }
                }

                @Override
                public void onError(Throwable t) {
                    loadingDialog.dismiss();
                    Log.i("Login User",t.toString());
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 缺少权限时, 进入权限配置页面
//        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
//            PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
//        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
//            finish();
//        }
    }

    private class MyAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {
        private String[] tabNames = {"主页", "查询", "报价", "我的"};
        private int[] tabIcons = {R.mipmap.ic_drawer_index, R.mipmap.ic_drawer_search, R.mipmap.ic_drawer_price,
                R.mipmap.ic_drawer_me};
        private LayoutInflater inflater;

        public MyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            inflater = LayoutInflater.from(getApplicationContext());
        }

        @Override
        public int getCount() {
            return tabNames.length;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.tab_main, container, false);
            }
            TextView textView = (TextView) convertView;
            textView.setText(tabNames[position]);
            if (position == 0) {
                Drawable top = ContextCompat.getDrawable(MainActivity.this, tabIcons[position]);
                Drawable wrappedDrawable = DrawableCompat.wrap(top);
                DrawableCompat.setTintList(wrappedDrawable, ColorStateList.valueOf(Color.argb(255, 5, 112, 67)));
                textView.setCompoundDrawablesWithIntrinsicBounds(null, wrappedDrawable, null, null);
            } else {
                textView.setCompoundDrawablesWithIntrinsicBounds(0, tabIcons[position], 0, 0);
            }
            return textView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            if(position==0) { //首页
                IndexFragment mainFragment = new IndexFragment();
                Bundle bundle = new Bundle();
                bundle.putString(IndexFragment.INTENT_STRING_TABNAME, tabNames[position]);
                bundle.putInt(IndexFragment.INTENT_INT_INDEX, position);
                mainFragment.setArguments(bundle);
                return mainFragment;
            }
            else if (position==1){ // 搜索测试
                SearchFragment mainFragment = new SearchFragment();
                Bundle bundle = new Bundle();
                bundle.putString(SearchFragment.INTENT_STRING_TABNAME, tabNames[position]);
                bundle.putInt(SearchFragment.INTENT_INT_INDEX, position);
                mainFragment.setArguments(bundle);
                return mainFragment;
            }
            else if (position==2){ //
                PriceFragment mainFragment = new PriceFragment();
                Bundle bundle = new Bundle();
                bundle.putString(SearchFragment.INTENT_STRING_TABNAME, tabNames[position]);
                bundle.putInt(SearchFragment.INTENT_INT_INDEX, position);
                mainFragment.setArguments(bundle);
                return mainFragment;
            }else { //我的
                MeFragment mainFragment = new MeFragment();
                Bundle bundle = new Bundle();
                bundle.putString(SearchFragment.INTENT_STRING_TABNAME, tabNames[position]);
                bundle.putInt(SearchFragment.INTENT_INT_INDEX, position);
                mainFragment.setArguments(bundle);
                return mainFragment;
            }

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    private void showLoadingDialog(){
        if (loadingDialog == null) {
            View v2 = getLayoutInflater().inflate(R.layout.loading_dialog2, null);// 得到加载view
            LinearLayout layout2 = (LinearLayout) v2.findViewById(R.id.dialog_view2);

            loadingDialog = new Dialog(this,R.style.loading_dialog);
            loadingDialog.setContentView(layout2,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
        }

        loadingDialog.show();
    }
}
