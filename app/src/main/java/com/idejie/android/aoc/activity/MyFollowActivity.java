package com.idejie.android.aoc.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.idejie.android.aoc.R;
import com.idejie.android.aoc.application.UserApplication;
import com.idejie.android.aoc.dialog.FollowItemDialog;
import com.idejie.android.aoc.fragment.tab.CommonUtil;
import com.idejie.android.aoc.model.FavourModel;
import com.idejie.android.aoc.model.NewsModel;
import com.idejie.android.aoc.repository.FavourRepository;
import com.strongloop.android.remoting.adapters.Adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class MyFollowActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, FollowItemDialog.OnItemClickListener {
    private ListView followList;
    private SwipeRefreshLayout followRefresh;
    private FollowListAdapter followListAdapter;

    private ImageView backImg;
    private Button refreshBtn;

    private UserApplication userApplication;
    FollowItemDialog followItemDialog;
    int selectedPosition = 0;
    int skip = 0;

    private View listFooterView;
    private ProgressBar progressBar;
    private TextView progressTxt;
    boolean isFirstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guanzhu);

        userApplication = (UserApplication) getApplication();
        followList = (ListView) findViewById(R.id.follow_list);
        followRefresh  = (SwipeRefreshLayout) findViewById(R.id.follow_refresh);
        followRefresh.setOnRefreshListener(this);
        followListAdapter = new FollowListAdapter(this);

        followList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
                if (followItemDialog == null) {
                    followItemDialog = new FollowItemDialog(MyFollowActivity.this);
                    followItemDialog.setOnItemClickListener(MyFollowActivity.this);
                }
                followItemDialog.show();
                followItemDialog.setBtnDelText("取消关注");
            }
        });
        listFooterView = LayoutInflater.from(this).inflate(R.layout.loading_more, null);
        listFooterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                progressTxt.setText("加载中...");
                isFirstLoad = false;
                requestData(true);
            }
        });
        followList.addFooterView(listFooterView);
        progressBar = (ProgressBar) listFooterView.findViewById(R.id.loading_more_progress);
        progressTxt = (TextView) listFooterView.findViewById(R.id.loading_more_txt);
        followList.setAdapter(followListAdapter);
        refreshBtn = (Button) findViewById(R.id.refresh_btn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFirstLoad = true;
                followRefresh.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                progressTxt.setText("加载中...");
                requestData(false);
            }
        });
        backImg = (ImageView) findViewById(R.id.back_img);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        requestData(true);
    }

    private void requestData(final boolean isLoadMore){
        FavourRepository favourRepository = FavourRepository.getInstance(this,userApplication.getAccessToken());
        Map<String,Object> params = new HashMap<>();
        Map<String,Object> filter = new HashMap<>();
        Map<String,Object> where = new HashMap<>();
        Map<String,Object> include = new HashMap<>();
        Map<String,Object> scope = new HashMap<>();

        scope.put("include","avatar");
        include.put("relation","news");
        include.put("scope",scope);
        where.put("favorerId",userApplication.getUser().getUserId());
        filter.put("include",include);
        filter.put("order","created DESC"); //按priceDate排序
        filter.put("where",where);
        filter.put("limit", 10);
        filter.put("skip", skip);
        params.put("filter",filter);
        favourRepository.invokeStaticMethod("all", params, new Adapter.JsonArrayCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                int len = response.length();
                skip += len;
                if (isLoadMore || isFirstLoad) {
                    if (len > 0) {
                        progressTxt.setText("点我加载");
                    } else {
                        progressTxt.setText("没有了！！");
                    }
                    followRefresh.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                } else {
                    followRefresh.setRefreshing(false);
                }

                if (len > 0) {
                    Gson gson = CommonUtil.getGSON();
                    List<FavourModel> favourList = new ArrayList<FavourModel>();
                    for (int i = 0; i < len; i++) {
                        JSONObject jsonObject = response.optJSONObject(i);
                        FavourModel favourModel = gson.fromJson(jsonObject.toString(),FavourModel.class);
                        favourList.add(favourModel);
                    }
                    followRefresh.setVisibility(View.VISIBLE);
                    if (isLoadMore) {
                        followListAdapter.addItems(favourList);
                    } else {
                        followListAdapter.updateItems(favourList);
                    }
                    followListAdapter.notifyDataSetChanged();
                } else {
                    if (isFirstLoad) {
                        followRefresh.setVisibility(View.GONE);
                    } else {
                        followRefresh.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onError(Throwable t) {
                followRefresh.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                progressTxt.setText("点击加载");
                if (isFirstLoad) {
                    followRefresh.setVisibility(View.GONE);
                }
                Toast.makeText(MyFollowActivity.this, "网络出问题了，请重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh() {
        Log.i("MyFollowActivity","刷新数据");
        skip = 0;
        isFirstLoad = true;
        requestData(false);
    }

    @Override
    public void onItemClick(View v, int index) {
        followItemDialog.dismiss();
        FavourModel favourModel = (FavourModel) followListAdapter.getItem(selectedPosition);
        if (index == 0) {
            FavourRepository favourRepository = FavourRepository.getInstance(this,userApplication.getAccessToken());
            Double d = (Double) favourModel.getId();
            favourRepository.delFavour(d.intValue(), new Adapter.JsonObjectCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    if (response.optInt("count") == 1) {
                        followListAdapter.delItem(selectedPosition);
                        followListAdapter.notifyDataSetChanged();
                    }
                    Log.i("删除关注", response.toString());
                }

                @Override
                public void onError(Throwable t) {
                    Log.i("删除关注", t.toString());
                }
            });
        } else if (index == 1) {
            Intent intent =new Intent();
            Bundle bundle = new Bundle();
            NewsModel news = favourModel.getNews();
            Double d = (Double) news.getId();
            news.setNewsId(d.intValue());
            bundle.putSerializable("news",news);
            intent.putExtras(bundle);
            intent.setClass(MyFollowActivity.this,NewsDetailActivity.class);
            startActivity(intent);
        }
    }

    private class FollowListAdapter extends BaseAdapter {
        private List<FavourModel> favourList;
        private Context context;

        private SimpleDateFormat dateFormat ;

        public FollowListAdapter(Context context) {
            this.context = context;
            favourList = new ArrayList<>();
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        }

        public void addItems(List<FavourModel> list) {
            favourList.addAll(list);
        }
        public void updateItems(List<FavourModel> list) {
            favourList.clear();
            favourList.addAll(list);
        }

        public void delItem(int pos) {
            favourList.remove(pos);
        }

        @Override
        public int getCount() {
            return favourList.size();
        }

        @Override
        public Object getItem(int position) {
            return favourList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.follow_list_item,null);
                viewHolder = new ViewHolder();
                viewHolder.newsIcon = (ImageView) convertView.findViewById(R.id.news_icon);
                viewHolder.newsTitle = (TextView) convertView.findViewById(R.id.news_title);
                viewHolder.newsCategory = (TextView) convertView.findViewById(R.id.category_txt);
                viewHolder.newsTime = (TextView) convertView.findViewById(R.id.news_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            FavourModel newsModel = favourList.get(position);
            viewHolder.newsTime.setText(dateFormat.format(newsModel.getCreated()));
            if (newsModel.getNews() != null){
                viewHolder.newsTitle.setText(newsModel.getNews().getTitle());
            }

            if (newsModel.getNews().getAvatar() != null) {
                Glide
                    .with(context)
                    .load(newsModel.getNews().getAvatar().getUrl())
                    .centerCrop()
                    .crossFade()
                    .into(viewHolder.newsIcon);
            }

            switch (newsModel.getNews().getCategoryId()) {
                case 2:
                    viewHolder.newsCategory.setText("要闻");
                    break;
                case 3:
                    viewHolder.newsCategory.setText("监测");
                    break;
                case 4:
                    viewHolder.newsCategory.setText("展望");
                    break;
                case 5:
                    viewHolder.newsCategory.setText("预警");
                    break;
            }

            return convertView;
        }

        class ViewHolder {
            ImageView newsIcon;
            TextView newsTitle;
            TextView newsCategory;
            TextView newsTime;
        }
    }
}
