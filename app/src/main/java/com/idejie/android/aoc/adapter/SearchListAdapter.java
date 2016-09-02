package com.idejie.android.aoc.adapter;

/**
 * Created by slf on 16/8/31.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.bean.SearchList;

import java.util.List;

/**
 * Created by draft on 2015/7/28.
 */
public class SearchListAdapter  extends ArrayAdapter<SearchList> {

    private Context context;
    private int resourceId;

    public SearchListAdapter(Context context,int textViewResourceId,List<SearchList> objects){
        super(context,textViewResourceId,objects);
        this.context=context;
        resourceId=textViewResourceId;
    }

    @Override
    public View getView( int position,View convertView,ViewGroup parent){
        SearchList searchList=getItem(position);
        View view;
        final ViewHolder viewHolder;
        if(convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder=new ViewHolder();
            viewHolder.textSort=(TextView) view.findViewById(R.id.text_sort);
            viewHolder.textPrice=(TextView)view.findViewById(R.id.text_price);
            viewHolder.textTend=(TextView)view.findViewById(R.id.text_tend);
            viewHolder.textArea=(TextView)view.findViewById(R.id.text_area);
            view.setTag(viewHolder);
        }else{
            view=convertView;
            viewHolder=(ViewHolder) view.getTag();
        }
        viewHolder.textSort.setText(searchList.getSort());
        viewHolder.textPrice.setText(searchList.getPrice()+"");
        viewHolder.textTend.setText(searchList.getTend());
        viewHolder.textArea.setText(searchList.getArea());

        return view;
    }


    class ViewHolder{
        TextView textSort,textPrice,textTend,textArea;
    }

}
