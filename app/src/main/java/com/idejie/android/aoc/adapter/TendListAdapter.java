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
import android.widget.TextView;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.bean.SearchList;
import com.idejie.android.aoc.bean.TendList;

import java.util.List;

/**
 * Created by draft on 2015/7/28.
 */
public class TendListAdapter extends ArrayAdapter<TendList> {

    private Context context;
    private int resourceId;
    public TendListAdapter(Context context, int textViewResourceId, List<TendList> objects){
        super(context,textViewResourceId,objects);
        this.context=context;
        resourceId=textViewResourceId;
    }

    @Override
    public View getView( int position,View convertView,ViewGroup parent){
        TendList textTendList=getItem(position);
        View view;
        final ViewHolder viewHolder;
        if(convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder=new ViewHolder();
            viewHolder.textArea=(TextView)view.findViewById(R.id.text_area);
            viewHolder.textSort=(TextView) view.findViewById(R.id.text_sort);
            viewHolder.textRank= (TextView) view.findViewById(R.id.text_rank);
            viewHolder.textPrice=(TextView)view.findViewById(R.id.text_price);
            viewHolder.textDate=(TextView)view.findViewById(R.id.text_date);
            view.setTag(viewHolder);
        }else{
            view=convertView;
            viewHolder=(ViewHolder) view.getTag();
        }
        viewHolder.textArea.setText(textTendList.getArea());
        viewHolder.textSort.setText(textTendList.getSort());
        viewHolder.textRank.setText(textTendList.getRank());
        viewHolder.textPrice.setText(textTendList.getPrice());
        viewHolder.textDate.setText(textTendList.getDate());


        return view;
    }


    class ViewHolder{
        TextView textArea,textSort,textRank,textPrice,textDate;
    }

}
