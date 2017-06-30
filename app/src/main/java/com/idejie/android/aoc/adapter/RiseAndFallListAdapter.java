package com.idejie.android.aoc.adapter;

/**
 * Created by slf on 16/8/31.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.bean.RiseAndFallBean;
import com.idejie.android.aoc.bean.SearchList;
import com.idejie.android.aoc.bean.TendList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by draft on 2015/7/28.
 */
public class RiseAndFallListAdapter extends BaseAdapter {

    private Context context;
    private List<RiseAndFallBean> riseAndFallBeanList;

    public RiseAndFallListAdapter(Context context, List<RiseAndFallBean> objects){
        this.context=context;
        riseAndFallBeanList = objects;
    }
    public RiseAndFallListAdapter(Context context){
        this(context,new ArrayList<RiseAndFallBean>());
    }

    public void addItems(List<RiseAndFallBean> objects){
        riseAndFallBeanList.addAll(objects);
    }

    public void updateItems(List<RiseAndFallBean> objects){
        riseAndFallBeanList.clear();
        riseAndFallBeanList.addAll(objects);
    }

    @Override
    public int getCount() {
        return riseAndFallBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return riseAndFallBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView( int position,View convertView,ViewGroup parent){
        RiseAndFallBean textTendList = riseAndFallBeanList.get(position);
        ViewHolder viewHolder;
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.rise_fall_list_item,null);
            viewHolder=new ViewHolder();
            viewHolder.textArea=(TextView)convertView.findViewById(R.id.text_area);
            viewHolder.textSort=(TextView) convertView.findViewById(R.id.text_sort);
            viewHolder.textRank= (TextView) convertView.findViewById(R.id.text_rank);
            viewHolder.textPrice=(TextView)convertView.findViewById(R.id.text_price);
//            viewHolder.textDate=(TextView)convertView.findViewById(R.id.text_date);
            convertView.setTag(viewHolder);
        }else{
            viewHolder=(ViewHolder) convertView.getTag();
        }
        viewHolder.textArea.setText(textTendList.getRegion());
        viewHolder.textSort.setText(textTendList.getSort());
//        String.
        viewHolder.textPrice.setText(String.format("%.2f", textTendList.getPrice()));//
        viewHolder.textRank.setText(textTendList.getRiseAndFall());
//        viewHolder.textDate.setText(textTendList.getDateStr());

        return convertView;
    }


    class ViewHolder{
        TextView textArea,textSort,textRank,textPrice;//textDate;
    }
}
