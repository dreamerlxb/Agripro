package com.idejie.android.aoc.fragment.tab;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.idejie.android.aoc.R;
import com.idejie.android.aoc.callback.CustomCallback;
import com.idejie.android.aoc.model.NewsModel;
import com.idejie.android.aoc.repository.NewsRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.List;

/**
 * Created by sxb on 16/10/14.
 */

public class HomeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private static final int LOAD_MORE_ITEM = 100;
    private static final int NORMAL_ITEM = 101;

    private SimpleDateFormat dateFormat;

    private List<NewsModel> newsList = new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;
    private boolean hasMore = true;
    private boolean isLoadingMore = true;

    @Override
    public void onClick(View v) {
        int pos = (Integer) v.getTag();
        int type = getItemViewType(pos);
        if (type == LOAD_MORE_ITEM) {
            if (onLoadMoreListener != null){
                isLoadingMore = true;
                notifyItemChanged(newsList.size()); //改变LoadMore Item的状态
                onLoadMoreListener.onLoadMore(v, pos);
            }
        } else {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, pos);
            }
        }
    }

    private OnRecyclerViewItemClickListener onItemClickListener;
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }
    public void setOnItemClickListener(OnRecyclerViewItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    private OnRecyclerViewItemVisibleListener onItemVisibleListener;
    public static interface OnRecyclerViewItemVisibleListener {
        boolean onItemVisible(int pos);
    }
    public void setOnItemVisibleListener(OnRecyclerViewItemVisibleListener onItemVisibleListener) {
        this.onItemVisibleListener = onItemVisibleListener;
    }


    private OnRecyclerViewLoadMoreListener onLoadMoreListener;
    public static interface OnRecyclerViewLoadMoreListener {
        void onLoadMore(View view, int position);
    }

    public void setOnLoadMoreListener(OnRecyclerViewLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public HomeRecyclerAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        setHasStableIds(true);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    public void addItems(List<NewsModel> list) {
        newsList.addAll(list);
    }
    public void updateItems(List<NewsModel> list) {
//        isLoadingMore = false; //加载结束
        hasMore = true;
        newsList.clear();
        newsList.addAll(list);
    }

    /**
     * 加载完成
     * @param more 是否还有更多
     */
    public void loadMoreComplete(boolean more) {
        hasMore = more; // 是否还有更多数据
        isLoadingMore = false; //加载结束

        int loadMoreIndex = getItemCount() - 1;
        if (onItemVisibleListener.onItemVisible(loadMoreIndex)) {
            notifyItemChanged(loadMoreIndex);
        }
    }

    public Object getItemObject(int position) {
        if (position >= newsList.size()) {
            return null;
        }
        return newsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == LOAD_MORE_ITEM) {
            View v1 = inflater.inflate(R.layout.loading_more, parent, false);
            LoadMoreViewHolder holder1 = new LoadMoreViewHolder(v1);
            v1.setOnClickListener(this);
            return holder1;
        } else {
            View v = inflater.inflate(R.layout.fragment_news_yaowen, parent, false);
            MyViewHolder holder = new MyViewHolder(v);
            v.setOnClickListener(this);
            return holder;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == newsList.size()) {
            return LOAD_MORE_ITEM;
        }
        return NORMAL_ITEM;
    }

    @Override
    public int getItemCount() { // item的总数为列表的item + LoadMoreItem
        return newsList.size() + 1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = viewHolder.getItemViewType();
        viewHolder.itemView.setTag(position);
        if (viewType == LOAD_MORE_ITEM) {
            LoadMoreViewHolder holder = (LoadMoreViewHolder) viewHolder;
            if (isLoadingMore) { //正在加载中
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.tv_title.setText("正在加载中...");
            } else { // 加载完成
                holder.progressBar.setVisibility(View.GONE);
                if (hasMore) {
                    holder.itemView.setClickable(true);
                    holder.tv_title.setText("点我加载");
                } else {
                    holder.itemView.setClickable(false);
                    holder.tv_title.setText("已经没有数据了！！");
                }
            }

        } else {
            MyViewHolder holder = (MyViewHolder) viewHolder;
            NewsModel newsModel = newsList.get(position);
            holder.tv_title.setText(newsModel.getTitle());
            holder.newsID.setText(newsModel.getId().toString());

            switch (newsModel.getTagId()) {
                case 1:
                    holder.tv_tag.setText("新闻");
                    break;
                case 2:
                    holder.tv_tag.setText("行情");
                    break;
                case 3:
                    holder.tv_tag.setText("报告");
                    break;
                case 4:
                    holder.tv_tag.setText("视频");
                    break;
            }

            if (newsModel.isCommentsCountSync()) {
                holder.tv_comment.setText(newsModel.getCommentsCount() == 0 ? "评" : "评 " + newsModel.getCommentsCount());
            } else {
                NewsRepository newsRepository = NewsRepository.getInstance(context, null);
                newsRepository.newsCommentsCount(newsModel.getNewsId(), new CustomCallback(position) {
                    @Override
                    public void onSuccess(Object count) {
                        int position = (int) this.tag;
                        if (position < newsList.size()) {
                            NewsModel newsModel1 = newsList.get(position);
                            newsModel1.setCommentsCount((Integer) count);
                            newsModel1.setCommentsCountSync(true);
                            if(onItemVisibleListener.onItemVisible(position)) {
                                notifyItemChanged(position);
                            }
                        }
                    }
                    @Override
                    public void onError(Throwable t) {
                    }
                });
            }

            if (newsModel.isLikersCountSync()) {
                holder.tv_zan.setText(newsModel.getLikersCount() == 0 ? "赞" : "赞 " + newsModel.getLikersCount());
            } else {
                NewsRepository newsRepository = NewsRepository.getInstance(context, null);
                newsRepository.newsLikersCount(newsModel.getNewsId(), new CustomCallback(position) {
                    @Override
                    public void onSuccess(Object count) {
                        int position = (int) this.tag;
                        if (position < newsList.size()) {
                            NewsModel newsModel1 = newsList.get(position);
                            newsModel1.setLikersCount((Integer) count);
                            newsModel1.setLikersCountSync(true);
                            if(onItemVisibleListener.onItemVisible(position)) {
                                notifyItemChanged(position);
                            }
                        }
                    }
                    @Override
                    public void onError(Throwable t) {

                    }
                });
            }

            String str = dateFormat.format(newsModel.getCreated());
            holder.tv_time.setText(str);
            if (newsModel.getAvatar() != null) {
                Glide.with(context).load(newsModel.getAvatar().getUrl()).placeholder(R.mipmap.loading).into(holder.iv_icon);
            } else {
                holder.iv_icon.setImageResource(R.mipmap.loading);
            }
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_tag, tv_zan, tv_comment, tv_time, newsID;
        ImageView iv_icon;

        public MyViewHolder(View view) {
            super(view);
            tv_title = (TextView) view.findViewById(R.id.tv_item_title);
            tv_tag = (TextView) view.findViewById(R.id.tv_item_tag);
            tv_zan = (TextView) view.findViewById(R.id.tv_item_zan);
            tv_comment = (TextView) view.findViewById(R.id.tv_item_comment);
            tv_time = (TextView) view.findViewById(R.id.tv_item_time);
            newsID = (TextView) view.findViewById(R.id.newsID);
            iv_icon = (ImageView) view.findViewById(R.id.newsImage);
        }
    }

    class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        ProgressBar progressBar;

        public LoadMoreViewHolder(View view) {
            super(view);
            tv_title = (TextView) view.findViewById(R.id.loading_more_txt);
            progressBar = (ProgressBar) view.findViewById(R.id.loading_more_progress);
        }
    }
}
