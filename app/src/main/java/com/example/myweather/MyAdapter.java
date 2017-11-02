package com.example.myweather;

import android.content.SharedPreferences;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Xing on 11/1/2017.
 */

public class MyAdapter extends BaseAdapter {

    private ArrayList<String> list;

    private static HashMap<Integer, Boolean> isSelected;

    private Context context;

    private LayoutInflater inflater = null;


    public MyAdapter(ArrayList<String> list, Context context) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        isSelected = new HashMap<Integer, Boolean>();
        // Get the city list from the  SharedPreferences
        initData();
    }

    // initialize isSelected
    private void initData() {
        for (int i = 0; i < list.size(); i++) {
            getIsSelected().put(i, false);
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {

            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.list_view_item, null);
            holder.tv = (TextView) convertView.findViewById(R.id.city);
            holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv.setText(list.get(position));

        holder.cb.setChecked(getIsSelected().get(position));
        return convertView;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        MyAdapter.isSelected = isSelected;
    }

    public static class ViewHolder {
        TextView tv;
        CheckBox cb;
    }

}
