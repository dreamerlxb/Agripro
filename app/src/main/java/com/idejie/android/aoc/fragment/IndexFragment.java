package com.idejie.android.aoc.fragment;

import android.content.Context;
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
import android.widget.TextView;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.fragment.tab.SecondLayerFragment;
import com.idejie.android.library.fragment.LazyFragment;
import com.idejie.android.library.view.indicator.Indicator;
import com.idejie.android.library.view.indicator.IndicatorViewPager;
import com.idejie.android.library.view.indicator.slidebar.ColorBar;
import com.idejie.android.library.view.indicator.transition.OnTransitionTextListener;

/**
 * Created by shandongdaxue on 16/8/8.
 */
public class IndexFragment extends LazyFragment  {
    private IndicatorViewPager indicatorViewPager;
    private LayoutInflater inflate;
    public static final String INTENT_STRING_TABNAME = "intent_String_tabname";
    public static final String INTENT_INT_INDEX = "intent_int_index";
    private String tabName;
    private int index;
    public Context context;

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

        ViewPager viewPager = (ViewPager) findViewById(R.id.fragment_tabmain_viewPager);
        Indicator indicator = (Indicator) findViewById(R.id.fragment_tabmain_indicator);

        switch (index) {
            case 0:
                indicator.setScrollBar(new ColorBar(getApplicationContext(), Color.rgb(255,255,255), 5));
                break;
            case 1:
                indicator.setScrollBar(new ColorBar(getApplicationContext(), Color.rgb(255,255,255), 5));
                break;
            case 2:
                indicator.setScrollBar(new ColorBar(getApplicationContext(), Color.rgb(255,255,255), 5));
                break;
            case 3:
                indicator.setScrollBar(new ColorBar(getApplicationContext(), Color.rgb(255,255,255), 5));
                break;
        }

        float unSelectSize = 16;
        float selectSize = unSelectSize * 1.2f;

        int selectColor = res.getColor(R.color.tab_top_text_2);
        int unSelectColor = res.getColor(R.color.tab_top_text_1);
        indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(selectColor, unSelectColor).setSize(selectSize, unSelectSize));

        viewPager.setOffscreenPageLimit(4);

        indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
        inflate = LayoutInflater.from(getApplicationContext());

        // 注意这里 的FragmentManager 是 getChildFragmentManager(); 因为是在Fragment里面
        // 而在activity里面用FragmentManager 是 getSupportFragmentManager()
        indicatorViewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        Log.d("cccc", "Fragment 将要创建View " + this);

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

        @Override
        public Fragment getFragmentForPage(int position) {
            SecondLayerFragment mainFragment = new SecondLayerFragment();
            Bundle bundle = new Bundle();
            bundle.putString(SecondLayerFragment.INTENT_STRING_TABNAME, tabName);
            bundle.putInt(SecondLayerFragment.INTENT_INT_POSITION, position);
            mainFragment.setArguments(bundle);
            return mainFragment;
        }
    }

}
