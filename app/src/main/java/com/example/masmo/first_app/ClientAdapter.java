package com.example.masmo.first_app;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by masmo on 01/05/2017.
 */

public class ClientAdapter extends BaseAdapter {
    Context mcontext;
    List<DataClient> Clients;

    public ClientAdapter(Context context, List<DataClient> objects) {


        this.mcontext=context;
        this.Clients=objects;
    }
    @Override
    public int getCount() {
        return Clients.size();
    }

    @Override
    public Object getItem(int position) {
        return Clients.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mcontext,R.layout.clients_rows_item,null);
        TextView nomCli = (TextView)v.findViewById(R.id.nomCli);
        TextView numCli = (TextView)v.findViewById(R.id.numCli);
        TextView StatCli = (TextView)v.findViewById(R.id.StatCli);

        nomCli.setText(Clients.get(position).name);
        numCli.setText(Clients.get(position).numTelClient);
        if(Clients.get(position).statut==0)
        {
            StatCli.setText("En attente");
        }else if(Clients.get(position).statut==1)
        {
            StatCli.setText("Vérifié");
        }
        v.setTag(Clients.get(position).id);
        return v;
    }
}
