package com.example.stmak.wuzzupweather;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.lang.reflect.Type;

public class MyListAdapter extends BaseAdapter{
    private Context context;
    private String[] arr;
    private Typeface weatherFont;

    public MyListAdapter(Context context, String[] arr) {
        this.context = context;
        this.arr = arr;
        weatherFont = Typeface.createFromAsset(context.getAssets(), "fonts/weather.ttf");
    }

    @Override
    public int getCount() {
        return arr.length;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View customView = convertView;

        if (convertView == null) {
            LayoutInflater li = LayoutInflater.from(context);
            customView = li.inflate(R.layout.list_item, null);
        }

        TextView tv = customView.findViewById(R.id.Tvcheck);
        TextView icon = customView.findViewById(R.id.icon_list);
        icon.setTypeface(weatherFont);
        TextView temperature = customView.findViewById(R.id.temp_list);
        String[] tempArr = arr[position].split("[;]");

        tv.setText(tempArr[0]);
        icon.setText(tempArr[1]);
        temperature.setText(tempArr[2]);

        return customView;
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public boolean isEnabled(int pos){
        return false;
    }
}