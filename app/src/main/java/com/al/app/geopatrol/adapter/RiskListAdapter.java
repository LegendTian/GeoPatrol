package com.al.app.geopatrol.adapter;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.al.app.geopatrol.R;
import com.al.app.geopatrol.activitys.CountrycodeActivity;
import com.al.app.geopatrol.model.RiskTrack;
import com.al.app.geopatrol.model.XJTrouble;

import java.util.List;

public class RiskListAdapter extends ArrayAdapter<XJTrouble> {

    private final List<XJTrouble> list;
    private final Activity context;

    static class ViewHolder {
        protected TextView name;
        protected ImageView flag;
    }

    public RiskListAdapter(Activity context, List<XJTrouble> list) {
        super(context, R.layout.activity_countrycode_row, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.activity_countrycode_row , null);
            final ViewHolder viewHolder = new ViewHolder();

            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.flag = (ImageView) view.findViewById(R.id.flag);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.name.setText(list.get(position).getException());
        holder.flag.setImageDrawable(new BitmapDrawable(list.get(position).getImage()));
        return view;
    }
}