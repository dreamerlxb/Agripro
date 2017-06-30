package com.idejie.android.aoc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.idejie.android.aoc.R;
import com.idejie.android.aoc.bean.TendList;
import com.idejie.android.aoc.model.PriceModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by sxb on 16/10/19.
 */

public class TendencyListAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private List<PriceModel> priceModelList;

    public TendencyListAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
        priceModelList = new ArrayList<>();
    }
    public TendencyListAdapter(Context context,List<PriceModel> models) {
        layoutInflater = LayoutInflater.from(context);
        priceModelList = models;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    @Override
    public int getCount() {
        return priceModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return priceModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PriceModel priceModel = priceModelList.get(position);

        ViewHolder viewHolder;
        if(convertView==null){
            convertView = layoutInflater.inflate(R.layout.item_tend_list,null);
            viewHolder=new ViewHolder();
            viewHolder.textArea=(TextView)convertView.findViewById(R.id.text_area);
            viewHolder.textSort=(TextView) convertView.findViewById(R.id.text_sort);
            viewHolder.textRank= (TextView) convertView.findViewById(R.id.text_rank);
            viewHolder.textPrice=(TextView)convertView.findViewById(R.id.text_price);
            viewHolder.textDate=(TextView)convertView.findViewById(R.id.text_date);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        viewHolder.textArea.setText(priceModel.getRegion().getProvince());
        viewHolder.textSort.setText(priceModel.getSort().getSubName());
        viewHolder.textRank.setText(priceModel.getGrade().getName());
        viewHolder.textPrice.setText(priceModel.getPrice()+"");
        viewHolder.textDate.setText(dateFormat.format(priceModel.getPriceDate()));

        return convertView;
    }

    class ViewHolder{
        TextView textArea,textSort,textRank,textPrice,textDate;
    }
}
