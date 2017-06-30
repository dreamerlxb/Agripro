package com.idejie.android.aoc.fragment.tab;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.idejie.android.aoc.activity.NewsDetailActivity;
import com.idejie.android.aoc.model.NewsModel;
import com.idejie.android.aoc.R;
import com.idejie.android.aoc.model.ScrollImageModel;
import com.idejie.android.aoc.repository.NewsRepository;
import com.idejie.android.aoc.repository.ScrollImageRepository;
import com.idejie.android.aoc.utils.BannerImageLoader;
import com.idejie.android.library.fragment.LazyFragment;
import com.strongloop.android.remoting.adapters.Adapter;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerClickListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shandongdaxue on 16/8/8.
 */
public class SecondLayerFragment extends LazyFragment
        implements SwipeRefreshLayout.OnRefreshListener,
            HomeRecyclerAdapter.OnRecyclerViewItemClickListener,
            HomeRecyclerAdapter.OnRecyclerViewLoadMoreListener {
    public static final String INTENT_INT_POSITION = "intent_int_position";

    private static final int LOAD_COUNT = 10;

    private int categoryId; // 2:要闻 , 3:检测 ,4:展望 ,5:预警 ,6:广告

    private RecyclerView recyclerView;
    private HomeRecyclerAdapter homeRecyclerAdapter;
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Banner banner;
    private List<ScrollImageModel> scrollImageList;
    public List<String> bannerTitles = new ArrayList<>();
    public List<String> bannerImgUrls = new ArrayList<>();

    private int skip = 0;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        int position = getArguments().getInt(INTENT_INT_POSITION);
        categoryId = position + 2; // 2:要闻 , 3:检测 ,4:展望 ,5:预警 ,6:广告
        switch (position) {
            case 0:
                startYaowen();
                break;
            case 1:
                startJiance();
                break;
            case 2:
                startZhanwang();
                break;
            case 3:
                startYujing();
                break;
        }

        homeRecyclerAdapter = new HomeRecyclerAdapter(getContext());
        recyclerView.setAdapter(homeRecyclerAdapter);
        linearLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
//        homeRecyclerAdapter.setLayoutManager(linearLayoutManager);
        homeRecyclerAdapter.setOnItemClickListener(this);
        homeRecyclerAdapter.setOnLoadMoreListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);
        queryNews(categoryId, true);
        initBannerInfo();
    }

    @Override
    public void onItemClick(View view, int position) {
        NewsModel newsModel = (NewsModel) homeRecyclerAdapter.getItemObject(position);
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("news", newsModel);
        bundle.putInt("position", position);
        intent.putExtras(bundle);
        intent.setClass(this.getContext(), NewsDetailActivity.class);
        SecondLayerFragment.this.startActivityForResult(intent, 21000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 22000 && requestCode == 21000) {
            int position = data.getIntExtra("position", -1);
            if (position != -1) {
                NewsModel newsModel = (NewsModel) homeRecyclerAdapter.getItemObject(position);
                int likeCount = data.getIntExtra("likeCount", newsModel.getLikersCount());
                int commentCount = data.getIntExtra("commentCount", newsModel.getCommentsCount());
                newsModel.setCommentsCount(commentCount);
                newsModel.setLikersCount(likeCount);
                homeRecyclerAdapter.notifyItemChanged(position);
            }
        }
    }

    // 点击轮播图时的操作
    private void clickBanner(int position) {
        ScrollImageModel scrollImageModel = scrollImageList.get(position - 1);
        NewsModel newsModel = scrollImageModel.getNews();
        Double d = (Double) newsModel.getId();
        newsModel.setNewsId(d.intValue());
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("news", newsModel);
        intent.putExtras(bundle);
        intent.setClass(this.getContext(), NewsDetailActivity.class);
        startActivity(intent);
    }

    //预警
    private void startYujing() {
        setContentView(R.layout.fragment_tabmain_yujing);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.yujing_refresh);
        recyclerView = (RecyclerView) findViewById(R.id.yujing_recycler);
        banner = (Banner) findViewById(R.id.banner4);
    }

    //展望
    private void startZhanwang() {
        setContentView(R.layout.fragment_tabmain_zhanwang);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.zhanwang_refresh);
        recyclerView = (RecyclerView) findViewById(R.id.zhanwang_recycler);
        banner = (Banner) findViewById(R.id.banner3);
    }

    //检测
    private void startJiance() {
        setContentView(R.layout.fragment_tabmain_jiance);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.jiance_refresh);
        recyclerView = (RecyclerView) findViewById(R.id.jiance_recycler);
        banner = (Banner) findViewById(R.id.banner2);
    }

    //category   2:要闻 , 3:检测 ,4:展望 ,5:预警 ,6:广告
    //要闻
    private void startYaowen() {
        setContentView(R.layout.fragment_tabmain_yaowen);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout1);
        recyclerView = (RecyclerView) findViewById(R.id.YaowenList);
        banner = (Banner) findViewById(R.id.banner1);
    }

    //设置轮播图
    private void initBannerInfo() {
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        banner.setImageLoader(new BannerImageLoader());
        banner.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void OnBannerClick(int position) {
                clickBanner(position);
            }
        });
        ScrollImageRepository scrollImageRepository = ScrollImageRepository.getInstance(getContext());

        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, Object> filter = new HashMap<String, Object>();
        Map<String, Object> where = new HashMap<String, Object>();

        List<String> list = new ArrayList<>();
        list.add("image");
        list.add("news");

        where.put("enabled",true);
        where.put("categoryId", categoryId);

        filter.put("where",where);
        filter.put("order","order");
        filter.put("include",list);
        filter.put("limit", 4);

        Gson gson1 = new Gson();
        String filterStr =  gson1.toJson(filter);
        params.put("filter", filterStr);
        scrollImageRepository.invokeStaticMethod("all", params, new Adapter.JsonArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                if (response.length() > 0){
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
                    if(scrollImageList != null) {
                        scrollImageList.clear();
                    } else {
                        scrollImageList = new ArrayList<ScrollImageModel>(response.length());
                    }

                    bannerImgUrls.clear();
                    bannerTitles.clear();

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject jsonObj = response.optJSONObject(i);
                        ScrollImageModel newsModel = gson.fromJson(jsonObj.toString(),ScrollImageModel.class);
                        Double id = (Double) newsModel.getId();
                        newsModel.setScrollImageId(id.intValue());
                        scrollImageList.add(newsModel);
                        bannerImgUrls.add(newsModel.getImage().getUrl());
                        bannerTitles.add(newsModel.getTitle());
                    }
                    banner.setBannerTitles(bannerTitles);
                    banner.setImages(bannerImgUrls);
                    banner.start();
                }
            }

            @Override
            public void onError(Throwable t) {
//                recyclerView.getLayoutManager().fin
            }
        });
    }

    //查询要闻信息
    private void queryNews(int categoryId, final boolean isLoadMore) {
        if (!CommonUtil.checkNetState(getActivity())) { // 判断网络问题
            Toast.makeText(getContext(), "请检查网络连接", Toast.LENGTH_LONG).show();
            return;
        }
        NewsRepository newRepo = NewsRepository.getInstance(getContext(), null);
        Map<String, Object> params = new HashMap<String, Object>();
        // all 参数
        Map<String, Object> filter = new HashMap<String, Object>();
        Map<String, Object> where = new HashMap<>();

        where.put("categoryId", categoryId);
        filter.put("where", where);
        filter.put("include", "avatar");
        filter.put("order", "lastUpdated DESC"); //按priceDate排序
        filter.put("limit", LOAD_COUNT);
        if (!isLoadMore) {
            skip = 0;
        }
        filter.put("skip", skip);
        params.put("filter", filter);
        //方法二
        newRepo.invokeStaticMethod("all", params, new Adapter.JsonArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                Log.i("News All", response.toString());
                skip += response.length();
                Gson gson = CommonUtil.getGSON();
                List<NewsModel> objects = new ArrayList<>(response.length());
                for (int i = 0; i < response.length(); i++) {
                    JSONObject jsonObj = response.optJSONObject(i);
                    NewsModel newsModel = gson.fromJson(jsonObj.toString(), NewsModel.class);
                    Double id = (Double) newsModel.getId();
                    newsModel.setNewsId(id.intValue());
                    objects.add(newsModel);
                }

                if (response.length() > 0) {
                    if (isLoadMore) {
                        homeRecyclerAdapter.addItems(objects);
                        homeRecyclerAdapter.notifyItemRangeInserted(homeRecyclerAdapter.getItemCount() - 1 - objects.size() , objects.size());
                    } else {
                        homeRecyclerAdapter.updateItems(objects);
                        homeRecyclerAdapter.notifyDataSetChanged();
                    }
                }
                if (isLoadMore) {
                    homeRecyclerAdapter.loadMoreComplete(response.length() > 0); //还有数据
                    int firstItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int lastItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                    int loadMoreIndex = homeRecyclerAdapter.getItemCount() - 1;
                    if (loadMoreIndex <= lastItemPosition) {
                        homeRecyclerAdapter.notifyItemChanged(loadMoreIndex);
                    }
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onError(Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                homeRecyclerAdapter.loadMoreComplete(true);
                int lastItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                int loadMoreIndex = homeRecyclerAdapter.getItemCount() - 1;
                if (loadMoreIndex <= lastItemPosition) {
                    homeRecyclerAdapter.notifyItemChanged(loadMoreIndex);
                }

                Log.i("all", t.toString());
                Toast.makeText(getContext(), "请检查网络", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onRefresh() {
        queryNews(categoryId, false);
        initBannerInfo();
    }

    @Override
    public void onLoadMore(View view, int position) {
        Log.i("RecyclerView", "正在加载中...");
        queryNews(categoryId, true);
    }
}

