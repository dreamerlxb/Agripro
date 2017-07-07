package com.idejie.android.aoc.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.activity.SearchNewsActivity;
import com.idejie.android.aoc.fragment.tab.SecondLayerFragment;
import com.idejie.android.library.fragment.LazyFragment;
import com.idejie.android.library.view.indicator.Indicator;
import com.idejie.android.library.view.indicator.IndicatorViewPager;
import com.idejie.android.library.view.indicator.slidebar.ColorBar;
import com.idejie.android.library.view.indicator.transition.OnTransitionTextListener;

import static com.idejie.android.aoc.fragment.tab.SecondLayerFragment.INTENT_INT_POSITION;

/**
 * Created by shandongdaxue on 16/8/8.
 * 首页
 */
public class IndexFragment extends LazyFragment implements View.OnClickListener {
    private IndicatorViewPager indicatorViewPager;
    private LayoutInflater inflate;
    public static final String INTENT_STRING_TABNAME = "intent_String_tabname";
    public static final String INTENT_INT_INDEX = "intent_int_index";
    private String tabName;
    private int index;
    public Context context;

    private ImageView searchImg;

    @Override
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_tabmain);
        Resources res = getResources();
        setContext(getApplicationContext());
        Bundle bundle = getArguments();
        tabName = bundle.getString(INTENT_STRING_TABNAME);
        index = bundle.getInt(INTENT_INT_INDEX);

        searchImg = (ImageView) findViewById(R.id.search_img);
        searchImg.setOnClickListener(this);
        ViewPager viewPager = (ViewPager) findViewById(R.id.fragment_tabmain_viewPager);
        Indicator indicator = (Indicator) findViewById(R.id.fragment_tabmain_indicator);

//        switch (index) {
//            case 0:
//                indicator.setScrollBar(new ColorBar(getApplicationContext(), Color.rgb(255,255,255), 5));
//                break;
//            case 1:
//                indicator.setScrollBar(new ColorBar(getApplicationContext(), Color.rgb(255,255,255), 5));
//                break;
//            case 2:
//                indicator.setScrollBar(new ColorBar(getApplicationContext(), Color.rgb(255,255,255), 5));
//                break;
//            case 3:
//                indicator.setScrollBar(new ColorBar(getApplicationContext(), Color.rgb(255,255,255), 5));
//                break;
//        }

        float unSelectSize = 16;
        float selectSize = unSelectSize * 1.2f;

        int selectColor = res.getColor(R.color.tab_top_text_2);
        int unSelectColor = res.getColor(R.color.tab_top_text_2);
        indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(selectColor, unSelectColor).setSize(selectSize, unSelectSize));

        viewPager.setOffscreenPageLimit(4);

        indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
        inflate = LayoutInflater.from(getContext());

        // 注意这里 的FragmentManager 是 getChildFragmentManager(); 因为是在Fragment里面
        // 而在activity里面用FragmentManager 是 getSupportFragmentManager()
        indicatorViewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
    }

    @Override
    protected void onResumeLazy() {
        super.onResumeLazy();
        indicatorViewPager.setCurrentItem(0, false);
    }

    @Override
    protected void onFragmentStartLazy() {
        super.onFragmentStartLazy();
        indicatorViewPager.setCurrentItem(0, false);
    }

    @Override
    protected void onFragmentStopLazy() {
        super.onFragmentStopLazy();
    }

    @Override
    protected void onPauseLazy() {
        super.onPauseLazy();
    }

    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_img:
                startActivity(new Intent(getContext(), SearchNewsActivity.class));
                break;
        }
    }

    private class MyAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {
        public MyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = inflate.inflate(R.layout.tab_top, container, false);
            }
            TextView textView = (TextView) convertView;
            if (tabName=="主页"){
               switch (position) {
                   case 0:
                       textView.setText("要闻");
                       break;
                   case 1:
                       textView.setText("监测");
                       break;
                   case 2:
                       textView.setText("展望");
                       break;
                   case 3:
                       textView.setText("预警");
                       break;
               }
            }
            return convertView;
        }
       //  每个tab对应的Fragment(要闻,检测,展望,预警)
        @Override
        public Fragment getFragmentForPage(int position) {
            SecondLayerFragment mainFragment = new SecondLayerFragment();
            Bundle bundle = new Bundle();
            bundle.putString("tabName", tabName);
            bundle.putInt(INTENT_INT_POSITION, position);
            mainFragment.setArguments(bundle);
            return mainFragment;
        }
    }
}
