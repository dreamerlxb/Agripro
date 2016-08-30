package com.idejie.android.aoc.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.fragment.MeFragment;
import com.idejie.android.aoc.fragment.IndexFragment;
import com.idejie.android.aoc.fragment.SearchFragment;
import com.idejie.android.aoc.fragment.UploadFragment;
import com.idejie.android.library.view.indicator.FixedIndicatorView;
import com.idejie.android.library.view.indicator.IndicatorViewPager;
import com.idejie.android.library.view.indicator.transition.OnTransitionTextListener;
import com.idejie.android.library.view.viewpager.SViewPager;


public class MainActivity extends AppCompatActivity {
    private IndicatorViewPager indicatorViewPager;
    private FixedIndicatorView indicator;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_main);
        SViewPager viewPager = (SViewPager) findViewById(R.id.tabmain_viewPager);
        indicator = (FixedIndicatorView) findViewById(R.id.tabmain_indicator);
        indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(Color.rgb(25,148,88), Color.BLACK));

    //        //这里可以添加一个view，作为centerView，会位于Indicator的tab的中间
    //        centerView = getLayoutInflater().inflate(R.layout.tab_main_center, indicator, false);
    //        indicator.setCenterView(centerView);
    //        centerView.setOnClickListener(onClickListener);

        indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
        indicatorViewPager.setAdapter(new MyAdapter(getSupportFragmentManager()));
        // 禁止viewpager的滑动事件
        viewPager.setCanScroll(false);
        // 设置viewpager保留界面不重新加载的页面数量
        viewPager.setOffscreenPageLimit(4);
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
            textView.setCompoundDrawablesWithIntrinsicBounds(0, tabIcons[position], 0, 0);
            return textView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            if(position==0) {
                IndexFragment mainFragment = new IndexFragment();
                Bundle bundle = new Bundle();
                bundle.putString(IndexFragment.INTENT_STRING_TABNAME, tabNames[position]);
                bundle.putInt(IndexFragment.INTENT_INT_INDEX, position);
                mainFragment.setArguments(bundle);
                return mainFragment;
            }
            else if (position==1){
                SearchFragment mainFragment = new SearchFragment();
                Bundle bundle = new Bundle();
                bundle.putString(SearchFragment.INTENT_STRING_TABNAME, "哈哈");
                bundle.putInt(SearchFragment.INTENT_INT_INDEX, position);
                mainFragment.setArguments(bundle);
                return mainFragment;
            }
            else if (position==2){
                UploadFragment mainFragment = new UploadFragment();
                Bundle bundle = new Bundle();
                bundle.putString(SearchFragment.INTENT_STRING_TABNAME, "哈哈");
                bundle.putInt(SearchFragment.INTENT_INT_INDEX, position);
                mainFragment.setArguments(bundle);
                return mainFragment;
            }else {
                MeFragment mainFragment = new MeFragment();
                Bundle bundle = new Bundle();
                bundle.putString(SearchFragment.INTENT_STRING_TABNAME, "哈哈");
                bundle.putInt(SearchFragment.INTENT_INT_INDEX, position);
                mainFragment.setArguments(bundle);
                return mainFragment;
            }

        }
    }
//    public void Login(View view) {
//        Intent intent =new Intent(MainActivity.this,LoginActivity.class);
//        Toast.makeText(getApplicationContext(),"ssss",Toast.LENGTH_LONG).show();;
//        startActivity(intent);
//
//    }
}