package com.idejie.android.aoc.fragment.tab;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.idejie.android.aoc.Model.NewsModel;
import com.idejie.android.aoc.R;
import com.idejie.android.aoc.repository.NewsRepository;
import com.idejie.android.library.fragment.LazyFragment;
import com.idejie.android.library.view.indicator.BannerComponent;
import com.idejie.android.library.view.indicator.Indicator;
import com.idejie.android.library.view.indicator.IndicatorViewPager;
import com.idejie.android.library.view.indicator.slidebar.ColorBar;
import com.idejie.android.library.view.indicator.slidebar.ScrollBar;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.loopback.callbacks.ObjectCallback;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import java.util.List;


/**
 * Created by shandongdaxue on 16/8/8.
 */
public class SecondLayerFragment extends LazyFragment {
    public static final String INTENT_STRING_TABNAME = "intent_String_tabName";
    public static final String INTENT_INT_POSITION = "intent_int_position";
    public static final String URL_API = "http://192.168.1.114:3001/api/";
    public static final String TOKEN="4miVFTq2Yt3nDPPrTLLvJGSQNKH5k0x78fNyHENbwyICjii206NqmjL5ByChP6dO";
    private String tabName;
    private int position;
    private BannerComponent bannerComponent;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        tabName = getArguments().getString(INTENT_STRING_TABNAME);
        position = getArguments().getInt(INTENT_INT_POSITION);
        switch (position){
            case 0:
                startYaowen();
                break;
            case 1:
                startJiance();
                break;
            case 2:
                startZhangwang();
                break;
            case 3:
                startYujing();
                break;
        }

    }
    private void startYujing() {
        setContentView(R.layout.fragment_tabmain_yujing);
        String[] images= getResources().getStringArray(R.array.url4);
        String[] titles= getResources().getStringArray(R.array.title4);
        Banner banner = (Banner) findViewById(R.id.banner4);
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        //记得设置标题列表哦
        banner.setBannerTitle(titles);
        banner.setImages(images);
        banner.setOnBannerClickListener(new Banner.OnBannerClickListener() {//设置点击事件
            @Override
            public void OnBannerClick(View view, int position) {
                Toast.makeText(getApplicationContext(),"你点击了："+position,Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startZhangwang() {
        setContentView(R.layout.fragment_tabmain_zhanwang);
        String[] images= getResources().getStringArray(R.array.url3);
        String[] titles= getResources().getStringArray(R.array.title3);
        Banner banner = (Banner) findViewById(R.id.banner3);
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        //记得设置标题列表哦
        banner.setBannerTitle(titles);
        banner.setImages(images);
        banner.setOnBannerClickListener(new Banner.OnBannerClickListener() {//设置点击事件
            @Override
            public void OnBannerClick(View view, int position) {
                Toast.makeText(getApplicationContext(),"你点击了："+position,Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startJiance() {
        setContentView(R.layout.fragment_tabmain_jiance);
        String[] images= getResources().getStringArray(R.array.url2);
        String[] titles= getResources().getStringArray(R.array.title2);
        Banner banner = (Banner) findViewById(R.id.banner2);
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        //记得设置标题列表哦
        banner.setBannerTitle(titles);
        banner.setImages(images);
        banner.setOnBannerClickListener(new Banner.OnBannerClickListener() {//设置点击事件
            @Override
            public void OnBannerClick(View view, int position) {
                Toast.makeText(getApplicationContext(),"你点击了："+position,Toast.LENGTH_LONG).show();
            }
        });
        getData();
    }

    private void startYaowen() {
        setContentView(R.layout.fragment_tabmain_yaowen);
        String[] images= getResources().getStringArray(R.array.url);
        String[] titles= getResources().getStringArray(R.array.title);
        Banner banner = (Banner) findViewById(R.id.banner1);
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        //记得设置标题列表哦
        banner.setBannerTitle(titles);
        banner.setImages(images);
        banner.setOnBannerClickListener(new Banner.OnBannerClickListener() {//设置点击事件
            @Override
            public void OnBannerClick(View view, int position) {
                Toast.makeText(getApplicationContext(),"你点击了："+position,Toast.LENGTH_LONG).show();
            }
        });
    }

    private int[] images = {R.mipmap.welcome1, R.mipmap.welcome2, R.mipmap.welcome3, R.mipmap.welcome4};

    private IndicatorViewPager.IndicatorViewPagerAdapter adapter = new IndicatorViewPager.IndicatorViewPagerAdapter() {

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = new View(container.getContext());
            }
            return convertView;
        }

        @Override
        public View getViewForPage(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = new ImageView(getApplicationContext());
                convertView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
            ImageView imageView = (ImageView) convertView;
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(images[position]);
            return convertView;
        }

//        @Override
//        public int getItemPosition(Object object) {
//            return RecyclingPagerAdapter.POSITION_NONE;
//        }

        @Override
        public int getCount() {
            return images.length;
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    public void getData() {
        RestAdapter adapter = new RestAdapter(getApplicationContext(),URL_API);
        Toast.makeText(getApplicationContext(),""+adapter,Toast.LENGTH_LONG).show();
        adapter.setAccessToken(TOKEN);
        NewsRepository newsRepository =adapter.createRepository(NewsRepository.class);
        newsRepository.findById(0, new ObjectCallback<NewsModel>() {
            @Override
            public void onSuccess(NewsModel object) {
                Log.d("news test","============"+object.toString());
            }

            @Override
            public void onError(Throwable t) {
                Log.d("news test","============"+t.toString());
            }
        });
        newsRepository.findAll(new ListCallback<NewsModel>() {
            @Override
            public void onSuccess(List<NewsModel> objects) {
                Log.d("news test all","============"+objects.toString());

            }

            @Override
            public void onError(Throwable t) {
                Log.d("news test all","============"+t.toString());
            }
        });
    }


}
