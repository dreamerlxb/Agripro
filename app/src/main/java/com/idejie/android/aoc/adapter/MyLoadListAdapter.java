package com.idejie.android.aoc.adapter;

/**
 * Created by slf on 16/8/31.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.bean.MyUploadList;
import com.idejie.android.aoc.bean.SearchList;

import java.util.List;


public class MyLoadListAdapter extends ArrayAdapter<MyUploadList> {

    private Context context;
    private int resourceId;

    public MyLoadListAdapter(Context context, int textViewResourceId, List<MyUploadList> objects){
        super(context,textViewResourceId,objects);
        this.context=context;
        resourceId=textViewResourceId;
    }

    @Override
    public View getView( int position,View convertView,ViewGroup parent){
        MyUploadList myUploadList=getItem(position);
        View view;
        final ViewHolder viewHolder;
        if(convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resourceId,null);
            viewHolder=new ViewHolder();
            viewHolder.textSort=(TextView) view.findViewById(R.id.text_sort_grade);
            viewHolder.textPrice=(TextView)view.findViewById(R.id.text_price);
            viewHolder.textArea=(TextView)view.findViewById(R.id.text_area);
            view.setTag(viewHolder);
        }else{
            view=convertView;
            viewHolder=(ViewHolder) view.getTag();
        }
        viewHolder.textSort.setText(myUploadList.getSort());
        viewHolder.textPrice.setText(myUploadList.getPrice()+"");
        viewHolder.textArea.setText(myUploadList.getArea());

        return view;
    }


    class ViewHolder{
        TextView textSort,textPrice,textAmount,textUpDate,textMarketName,textArea;
    }

}
