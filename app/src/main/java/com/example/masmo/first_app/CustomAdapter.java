package com.example.masmo.first_app;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by masmo on 29/03/2017.
 */

public class CustomAdapter extends BaseAdapter {
    Context mcontext;
    List<DataItem> data;



    public CustomAdapter(Context context, List<DataItem> objects) {


        this.mcontext=context;
        this.data=objects;
    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = View.inflate(mcontext,R.layout.itemrow,null);
        TextView timee = (TextView)v.findViewById(R.id.timee);
        TextView nomm = (TextView)v.findViewById(R.id.nomm);
        TextView numeroo = (TextView)v.findViewById(R.id.numeroo);
        TextView type = (TextView)v.findViewById(R.id.typ);

        timee.setText(data.get(position).time);
        nomm.setText(data.get(position).name);
        numeroo.setText(data.get(position).numeroo);
        type.setText(data.get(position).type);
        v.setTag(data.get(position).id);
        return v;
    }
}
