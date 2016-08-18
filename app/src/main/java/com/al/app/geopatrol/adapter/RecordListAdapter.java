package com.al.app.geopatrol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.al.app.geopatrol.R;
import com.al.app.geopatrol.model.XJRecord;
import com.esri.core.tasks.query.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LegendTian
 * Date: 2016/6/12.
 */
public class RecordListAdapter extends BaseAdapter{
    private Context context;
    private List<XJRecord> recordList;
    public static List<String> levelList= new ArrayList<String>();
    public static List<String> stateList= new ArrayList<String>(){};
    public RecordListAdapter(Context context, List<XJRecord> recordList) {
        this.context = context;
        this.recordList = recordList;
        levelList.add("正常");
        levelList.add("一级");
        levelList.add("二级");
        levelList.add("三级");
        stateList.add("已传");
        stateList.add("待传");
    }

    @Override
    public int getCount() {
        return recordList.size();
    }

    @Override
    public Object getItem(int position) {
        return recordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        XJRecord order = recordList.get(position);
        if (order == null){
            return null;
        }

        ViewHolder holder = null;
        if (view != null){
            holder = (ViewHolder) view.getTag();
        }else {
            view = LayoutInflater.from(context).inflate(R.layout.show_records_item, null);

            holder = new ViewHolder();
            holder.dateJPMTextView = (TextView) view.findViewById(R.id.dateJPMTextView);
            holder.dateLevelTextView = (TextView) view.findViewById(R.id.dateLevelTextView);
            holder.dateInfoTextView = (TextView) view.findViewById(R.id.dateInfoTextView);
            holder.dateTimeTextView = (TextView) view.findViewById(R.id.dateTimeTextView);
            holder.dateStateTextView = (TextView) view.findViewById(R.id.dateStateTextView);
            view.setTag(holder);
        }

        holder.dateJPMTextView.setText(order.getJPM());
        if(order.getLevel()!=null) {
            holder.dateLevelTextView.setText(levelList.get(Integer.parseInt(order.getLevel())));
        }
        holder.dateInfoTextView.setText(order.getException());
        holder.dateTimeTextView.setText(order.getRecordDate());
        holder.dateStateTextView.setText(stateList.get(Integer.parseInt(order.getPostState())));
        return view;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    public static class ViewHolder{
        public TextView dateJPMTextView;
        public TextView dateLevelTextView;
        public TextView dateInfoTextView;
        public TextView dateTimeTextView;
        public TextView dateStateTextView;
    }
}
