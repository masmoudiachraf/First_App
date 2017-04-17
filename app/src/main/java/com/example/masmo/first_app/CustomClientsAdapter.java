package com.example.masmo.first_app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by masmo on 09/04/2017.
 */

public class CustomClientsAdapter  extends BaseAdapter {

    Context mcontext;
    List<ClientsData> data;



    public CustomClientsAdapter(Context context, List<ClientsData> objects) {


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
        View v = View.inflate(mcontext,R.layout.clients_rows_item,null);
        TextView Stat = (TextView)v.findViewById(R.id.StatCli);
        TextView nomCli = (TextView)v.findViewById(R.id.nomCli);


        nomCli.setText(data.get(position).name_cli);
        Stat.setText(data.get(position).statut);
        v.setTag(data.get(position).id_cli);
        return v;
    }
}