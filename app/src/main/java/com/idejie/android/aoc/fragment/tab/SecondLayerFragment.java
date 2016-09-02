package com.idejie.android.aoc.fragment.tab;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.idejie.android.aoc.model.NewsModel;
import com.idejie.android.aoc.R;
import com.idejie.android.aoc.repository.NewsRepository;
import com.idejie.android.library.fragment.LazyFragment;
import com.idejie.android.library.view.indicator.BannerComponent;
import com.idejie.android.library.view.indicator.IndicatorViewPager;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.strongloop.android.loopback.callbacks.ObjectCallback;
import com.strongloop.android.remoting.adapters.Adapter;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by shandongdaxue on 16/8/8.
 */
public class SecondLayerFragment extends LazyFragment implements SwipeRefreshLayout.OnRefreshListener {
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
                queryYaowen(true);
            }
        });
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(this);
        queryYaowen(true);
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
//        getData();
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
        queryYaowen(true);
    }

    private void queryYaowen(boolean b) {
        if (!CommonUtil.checkNetState(getActivity())){
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(),"请检查网络连接",Toast.LENGTH_LONG).show();
        }
        RestAdapter adapter = new RestAdapter(getApplicationContext(), "http://192.168.1.114:3001/api");
        adapter.setAccessToken("4miVFTq2Yt3nDPPrTLLvJGSQNKH5k0x78fNyHENbwyICjii206NqmjL5ByChP6dO");
        NewsRepository newRepo=adapter.createRepository(NewsRepository.class);
        Map<String,Object> params =new HashMap<String,Object>();
//        Map<String,Object>filter=new HashMap<String,Object>();
//        filter.put("include","new");
//            pramas.put("filter",filter);
        newRepo.invokeStaticMethod("count", null, new Adapter.Callback() {
            @Override
            public void onSuccess(String response) {
                Toast.makeText(getContext(),response,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(getContext(),t.toString(),Toast.LENGTH_LONG).show();
            }
        });
        newRepo.findById(0,new ObjectCallback<NewsModel>(){

            @Override
            public void onSuccess(NewsModel object) {

            }

            @Override
            public void onError(Throwable t) {

            }
        });
        newRepo.findAll(new ListCallback<NewsModel>() {
            @Override
            public void onSuccess(List<NewsModel> objects) {
                Toast.makeText(getContext(),"标题:"+objects.get(0).getTitle()+"\n摘要:"+objects.get(0).getSummary()+"\n内容:"+objects.get(0).getContent()+"\n图片ID:"+objects.get(0).getImageId()+"\n标签id:"+objects.get(0).getTagId(),Toast.LENGTH_LONG).show();
                Log.d("aoc=======","==="+objects.get(0).toString());
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(getContext(),"失败了",Toast.LENGTH_LONG).show();
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


    @Override
    public void onRefresh() {
        queryYaowen(true);
    }
}
