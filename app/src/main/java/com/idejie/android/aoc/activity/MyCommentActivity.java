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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.idejie.android.aoc.R;
import com.idejie.android.aoc.application.UserApplication;
import com.idejie.android.aoc.dialog.FollowItemDialog;
import com.idejie.android.aoc.fragment.tab.CommonUtil;
import com.idejie.android.aoc.model.CommentModel;
import com.idejie.android.aoc.model.FavourModel;
import com.idejie.android.aoc.model.NewsModel;
import com.idejie.android.aoc.repository.CommentRepository;
import com.idejie.android.aoc.repository.FavourRepository;
import com.strongloop.android.remoting.adapters.Adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * 用户的评论
 */
public class MyCommentActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, FollowItemDialog.OnItemClickListener{
    private ListView commentList;
    private SwipeRefreshLayout commentRefresh;
    private MyCommentListAdapter commentListAdapter;
    private Button refreshBtn;

    private ImageView backImg;

    private UserApplication userApplication;
    FollowItemDialog commentItemDialog;
    int selectedPosition = 0;

    int skip = 0;

    private View listFooterView;
    private ProgressBar progressBar;
    private TextView progressTxt;
    boolean isFirstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_comment);

        userApplication = (UserApplication) getApplication();

        commentList = (ListView) findViewById(R.id.comment_list);
        commentRefresh  = (SwipeRefreshLayout) findViewById(R.id.comment_refresh);
        commentRefresh.setOnRefreshListener(this);

        refreshBtn = (Button) findViewById(R.id.refresh_btn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFirstLoad = true;
                commentRefresh.setVisibility(View.VISIBLE);
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
        commentList.addFooterView(listFooterView);
        progressBar = (ProgressBar) listFooterView.findViewById(R.id.loading_more_progress);
        progressTxt = (TextView) listFooterView.findViewById(R.id.loading_more_txt);

        commentListAdapter = new MyCommentListAdapter(this);
        commentList.setAdapter(commentListAdapter);
        commentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPosition = position;
                if (commentItemDialog == null) {
                    commentItemDialog = new FollowItemDialog(MyCommentActivity.this);
                    commentItemDialog.setOnItemClickListener(MyCommentActivity.this);
                }
                commentItemDialog.show();
                commentItemDialog.setBtnDelText("删除评论");
            }
        });

        requestData(true);
    }

    @Override
    public void onRefresh() {
        Log.i("MyCommentActivity","刷新数据");
        skip = 0;
        isFirstLoad = true;
        requestData(false);
    }

    private void requestData(final boolean isLoadMore){
        CommentRepository commentRepository = CommentRepository.getInstance(this,userApplication.getAccessToken());
        Map<String,Object> params = new HashMap<>();
        Map<String,Object> filter = new HashMap<>();
        Map<String,Object> where = new HashMap<>();

        where.put("authorId",userApplication.getUser().getUserId());
        filter.put("include","news");
        filter.put("order","created DESC"); //按priceDate排序
        filter.put("where",where);
        filter.put("limit", 10);
        filter.put("skip", skip);
        params.put("filter",filter);
        commentRepository.invokeStaticMethod("all", params, new Adapter.JsonArrayCallback() {
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
                    progressBar.setVisibility(View.GONE);
                    commentRefresh.setRefreshing(false);
                } else {
                    commentRefresh.setRefreshing(false);
                }
                if (len > 0) {
                    Gson gson = CommonUtil.getGSON();
                    List<CommentModel> favourList = new ArrayList<CommentModel>();
                    for (int i = 0; i < len; i++) {
                        JSONObject jsonObject = response.optJSONObject(i);
                        CommentModel favourModel = gson.fromJson(jsonObject.toString(),CommentModel.class);
                        favourList.add(favourModel);
                    }

                    if (isLoadMore) {
                        commentListAdapter.addItems(favourList);
                    } else {
                        commentListAdapter.updateItems(favourList);
                    }
                    commentListAdapter.notifyDataSetChanged();
                } else {
                    if (isFirstLoad) {
                        commentRefresh.setVisibility(View.GONE);
                    } else {
                        commentRefresh.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onError(Throwable t) {
                commentRefresh.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                progressTxt.setText("点击加载");
                if (isFirstLoad) {
                    commentRefresh.setVisibility(View.GONE);
                }
                Toast.makeText(MyCommentActivity.this, "网络出问题了，请重试", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemClick(View v, int index) {
        commentItemDialog.dismiss();
        CommentModel commentModel = (CommentModel) commentListAdapter.getItem(selectedPosition);
        if (index == 0) {
            CommentRepository commentRepository = CommentRepository.getInstance(this,userApplication.getAccessToken());
            Double d = (Double) commentModel.getId();
            Map<String,Object> params = new HashMap<>();
            params.put("id", d.intValue());
            commentRepository.invokeStaticMethod("deleteById", params, new Adapter.JsonObjectCallback() {
                @Override
                public void onSuccess(JSONObject response) {
                    if (response.optInt("count") == 1) {
                        commentListAdapter.delItem(selectedPosition);
                        commentListAdapter.notifyDataSetChanged();
                    }
                    Log.i("删除评论", response.toString());
                }

                @Override
                public void onError(Throwable t) {
                    Log.i("删除评论", t.toString());
                }
            });
        } else if (index == 1) {
            Intent intent =new Intent();
            Bundle bundle = new Bundle();
            NewsModel news = commentModel.getNews();
            Double d = (Double) news.getId();
            news.setNewsId(d.intValue());
            bundle.putSerializable("news",news);
            intent.putExtras(bundle);
            intent.setClass(MyCommentActivity.this, NewsDetailActivity.class);
            startActivity(intent);
        }
    }

    private class MyCommentListAdapter extends BaseAdapter {
        private List<CommentModel> commentsList;
        private Context context;
        private SimpleDateFormat dateFormat;

        public MyCommentListAdapter(Context context) {
            this.context = context;
            commentsList = new ArrayList<>();

            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        }

        public void addItems(List<CommentModel> list) {
            commentsList.addAll(list);
        }

        @Override
        public int getCount() {
            return commentsList.size();
        }

        @Override
        public Object getItem(int position) {
            return commentsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.my_comment_list_item,null);
                viewHolder = new ViewHolder();

                viewHolder.newsIcon = (ImageView) convertView.findViewById(R.id.news_icon);
                viewHolder.newsTitle = (TextView) convertView.findViewById(R.id.news_title);
//                viewHolder.newsDetail = (TextView) convertView.findViewById(R.id.news_detail);
                viewHolder.newsTime = (TextView) convertView.findViewById(R.id.news_time);
                viewHolder.myComment = (TextView) convertView.findViewById(R.id.my_comment_txt);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            CommentModel newsModel = commentsList.get(position);
            viewHolder.newsTime.setText(dateFormat.format(newsModel.getCreated()));
//            viewHolder.newsDetail.setText(newsModel.getNews().getContent());
            viewHolder.newsTitle.setText(newsModel.getNews().getTitle());

            viewHolder.myComment.setText(newsModel.getContent());

            return convertView;
        }

        public void delItem(int selectedPosition) {
            commentsList.remove(selectedPosition);
        }

        public void updateItems(List<CommentModel> favourList) {
            commentsList.clear();
            commentsList.addAll(favourList);
        }

        class ViewHolder {
            ImageView newsIcon;
            TextView newsTitle;
//            TextView newsDetail;
            TextView newsTime;
            TextView myComment;
        }
    }
}
