package com.idejie.android.aoc.fragment.tab;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.idejie.android.aoc.activity.NewsDetailActivity;
import com.idejie.android.aoc.model.NewsModel;
import com.idejie.android.aoc.R;
import com.idejie.android.aoc.repository.NewsRepository;
import com.idejie.android.library.fragment.LazyFragment;
import com.idejie.android.library.view.indicator.IndicatorViewPager;
import com.strongloop.android.loopback.RestAdapter;
import com.strongloop.android.loopback.callbacks.ListCallback;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerClickListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.idejie.android.aoc.activity.NewsDetailActivity.EXTRA;


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
    private SwipeRefreshLayout swipeRefreshLayout;
    public List<String> mTitle,mTag,mTime,mZan,mComment,mId;
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
    public interface OnListFragmentInteractionListener {
        void onYaowenListItemClick(NewsModel news);
    }
    private static final int REFRESH_COMPLETE = 0X110;

    private  Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            switch (msg.what)
            {
                case REFRESH_COMPLETE:
                    //mDatas.addAll(Arrays.asList("Lucene", "Canvas", "Bitmap"));
                    //mAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    break;

            }
        };
    };
    private void startYujing() {
        setContentView(R.layout.fragment_tabmain_yujing);
        String[] images= getResources().getStringArray(R.array.url4);
        String[] titles= getResources().getStringArray(R.array.title4);
        Banner banner = (Banner) findViewById(R.id.banner4);
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        //记得设置标题列表哦
        banner.setBannerTitle(titles);
        banner.setImages(images);
//        banner.setOnBannerClickListener(new Banner.OnBannerClickListener() {//设置点击事件
//            @Override
//            public void OnBannerClick(View view, int position) {
//                Toast.makeText(getApplicationContext(),"你点击了："+position,Toast.LENGTH_LONG).show();
//            }
//        });
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

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
        banner.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void OnBannerClick(int position) {
//
            }
        });
//        banner.setOnBannerClickListener(new Banner.OnBannerClickListener() {//设置点击事件
//            @Override
//            public void OnBannerClick(View view, int position) {
//                Toast.makeText(getApplicationContext(),"你点击了："+position,Toast.LENGTH_LONG).show();
//            }
//        });
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
//        banner.setOnBannerClickListener(new Banner.OnBannerClickListener() {//设置点击事件
//            @Override
//            public void OnBannerClick(View view, int position) {
//                Toast.makeText(getApplicationContext(),"你点击了："+position,Toast.LENGTH_LONG).show();
//            }
//        });
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
        queryYaowen(true);
//        banner.setOnBannerClickListener(new Banner.OnBannerClickListener() {//设置点击事件
//            @Override
//            public void OnBannerClick(View view, int position) {
//                Toast.makeText(getApplicationContext(),"你点击了："+position,Toast.LENGTH_LONG).show();
//                swipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout1);
////                queryYaowen(true);
//            }
//        });
        banner.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void OnBannerClick(int position) {
                Toast.makeText(getApplicationContext(),"你点击了："+position,Toast.LENGTH_LONG).show();
            }
        });

    }

    private void queryYaowen(boolean b) {
        if (!CommonUtil.checkNetState(getActivity())){
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(),"请检查网络连接",Toast.LENGTH_LONG).show();
        }
        RestAdapter adapter = new RestAdapter(getApplicationContext(), "http://211.87.227.214:3001/api");
        adapter.setAccessToken("4miVFTq2Yt3nDPPrTLLvJGSQNKH5k0x78fNyHENbwyICjii206NqmjL5ByChP6dO");
        NewsRepository newRepo=adapter.createRepository(NewsRepository.class);
        newRepo.findAll(new ListCallback<NewsModel>() {
            @Override
            public void onSuccess(List<NewsModel> objects) {
                initData(objects);
                RecyclerView recyclerView= (RecyclerView) findViewById(R.id.YaowenList);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setAdapter(new HomeAdapter());
            }

            @Override
            public void onError(Throwable t) {
                Toast.makeText(getContext(),"请检查网络",Toast.LENGTH_LONG).show();
            }
        });
    }
    public void onYaowenListItemClick(NewsModel news){
        Intent intent =new Intent(getContext(),NewsDetailActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("id",news.getId().toString());
        intent.putExtras(bundle);
        startActivityForResult(intent,1);
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


        @Override
        public int getCount() {
            return images.length;
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    protected void initData(List<NewsModel> obj)
    {
        mTitle = new ArrayList<String>();
        mTag = new ArrayList<String>();
        mZan = new ArrayList<String>();
        mComment = new ArrayList<String>();
        mTime= new ArrayList<String>();
        mId =new ArrayList<String>();
        for (int i=0;i<obj.size();i++){
            mTitle.add(obj.get(i).getTitle());
            mTag.add(obj.get(i).getTagId().toString());
//            mZan.add(obj.get(i).get);
//            mComment.add(obj.get(i).getTitle());

            mTime.add(obj.get(i).getLastUpdated());
            mId.add(obj.get(i).getId().toString());
        }

    }
    @Override
    public void onRefresh() {
        queryYaowen(true);
    }
    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder>
    {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.fragment_news_yaowen, parent,
                    false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position)
        {
            holder.tv_title.setText(mTitle.get(position));
            holder.newsID.setText(mId.get(position));
            switch (mTag.get(position)){
                case "1":
                    holder.tv_tag.setText("新闻");
                    break;
                case "2":
                    holder.tv_tag.setText("行情");
                    break;
                case "3":
                    holder.tv_tag.setText("报告");
                    break;
                case "4":
                    holder.tv_tag.setText("视频");
                    break;
            }
//            holder.tv_tag.setText(mTag.get(position));
//            holder.tv_comment.setText(mComment.get(position));
//            holder.tv_zan.setText(mZan.get(position));
            DateFormat format =new SimpleDateFormat("yyyy-MM-dd");
//            String date =mTime.get(position).replace("Z","UTC");
            try {
                Date timeNews =format.parse(mTime.get(position));
                holder.tv_time.setText(format.format(timeNews));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount()
        {
            return mTitle.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        {

            TextView tv_title,tv_tag,tv_zan,tv_comment,tv_time,newsID;
            private MyItemClickListener mListener;
            public MyViewHolder(View view)
            {
                super(view);
                tv_title = (TextView) view.findViewById(R.id.tv_item_title);
                tv_tag= (TextView) view.findViewById(R.id.tv_item_tag);
                tv_zan= (TextView) view.findViewById(R.id.tv_item_zan);
                tv_comment= (TextView) view.findViewById(R.id.tv_item_comment);
                tv_time= (TextView) view.findViewById(R.id.tv_item_time);
                newsID= (TextView) view.findViewById(R.id.newsID);
                view.setOnClickListener(this);

            }

            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"+"+((TextView)view.findViewById(R.id.newsID)).getText(),Toast.LENGTH_LONG).show();
                Intent intent =new Intent();
                intent.putExtra(EXTRA,""+((TextView)view.findViewById(R.id.newsID)).getText());
                intent.setClass(getContext(),NewsDetailActivity.class);
                startActivity(intent);
            }
        }

    }


}

