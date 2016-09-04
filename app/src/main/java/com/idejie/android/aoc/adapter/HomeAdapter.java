package com.idejie.android.aoc.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.model.NewsModel;

import java.util.List;
import java.util.logging.Logger;

import static com.idejie.android.aoc.R.id.list_item;
import static com.idejie.android.aoc.R.id.viewGroup;
import static com.loc.e.i;

/**
 * Created by shandongdaxue on 16/9/4.
 */
public class HomeAdapter extends RecyclerView.Adapter {
    public static interface OnRecyclerViewListener {
        void onItemClick(int position);
        boolean onItemLongClick(int position);
    }
    private OnRecyclerViewListener onRecyclerViewListener;
    private List<NewsModel> list;
    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }
    public HomeAdapter(List<NewsModel> list) {
        this.list = list;
    }
    private static final String TAG = HomeAdapter.class.getSimpleName();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        Logger.d(TAG, "onCreateViewHolder, i: " + viewType);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_news_yaowen, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
        MyViewHolder holders = (MyViewHolder) holder;
        holders.position = i;
        NewsModel news = list.get(i);
        holder.title.setText(news.getTitle());
        holder.a.setText(person.getAge() + "岁");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(View view) {
            super(view);
        }
    }
}
