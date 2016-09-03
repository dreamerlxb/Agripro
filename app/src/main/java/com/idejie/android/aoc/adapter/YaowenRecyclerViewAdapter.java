package com.idejie.android.aoc.adapter;

import android.content.Context;
import android.media.Image;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.idejie.android.aoc.R;
import com.idejie.android.aoc.fragment.tab.SecondLayerFragment;
import com.idejie.android.aoc.model.NewsModel;

import java.util.List;

/**
 * Created by shandongdaxue on 16/9/2.
 */
public class YaowenRecyclerViewAdapter extends RecyclerView.Adapter<YaowenRecyclerViewAdapter.ViewHolder> {
    private final List<NewsModel> newsList;
    private final SecondLayerFragment.OnListFragmentInteractionListener mListener;
    private Context context;
    public YaowenRecyclerViewAdapter(Context context, List<NewsModel> newsList, SecondLayerFragment.OnListFragmentInteractionListener mListener) {
        this.context=context;
        this.newsList=newsList;
        this.mListener=mListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_news_yaowen,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final NewsModel news =newsList.get(position);
        Glide.with(context).load(news.getImage().getFileUrl(context)).placeholder(R.mipmap.loading).into(holder.iv_avatar);
        holder.tv_title.setText(news.getTitle());
        holder.tv_tag.setText(news.getTag().getName());
        //赞的数量
//        holder.tv_zan.setText(context.getString(R.string.zan),Integer.parseInt());
        //评论的数量
//        holder.tv_comment.setText(context.getString(R.string.comment),Integer.parseInt());
        holder.tv_time.setText(news.getLastUpdated());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null !=mListener){
                    news.setId(news.getId());
                    mListener.onYaowenListItemClick(news);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder{
        public final View mView;
        public ImageView iv_avatar;
        public TextView tv_title,tv_tag,tv_zan,tv_comment,tv_time;
        public ViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            iv_avatar= (ImageView) itemView.findViewById(R.id.newsImage);
            tv_title= (TextView) itemView.findViewById(R.id.tv_item_title);
            tv_tag= (TextView) itemView.findViewById(R.id.tv_item_tag);
            tv_zan= (TextView) itemView.findViewById(R.id.tv_item_zan);
            tv_comment= (TextView) itemView.findViewById(R.id.tv_item_comment);
            tv_time= (TextView) itemView.findViewById(R.id.tv_item_time);
        }
    }
}
