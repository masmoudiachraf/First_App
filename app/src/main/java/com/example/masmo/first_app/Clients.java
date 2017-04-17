package com.example.masmo.first_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by masmo on 16/03/2017.
 */

public class Clients extends Activity
{
    private ProgressDialog pDialog;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    private static String url_login = "http://192.168.1.4/Coiffeur/GetCoiffeurAllClients.php";

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.clients_interface);

        Intent int1=getIntent();
        String cid=int1.getStringExtra("coiffeurid");
        new GetCoiffeursClients().execute(cid);
    }

    private class GetCoiffeursClients extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(Clients.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading");
            pdLoading.setCancelable(false);
            pdLoading.show();

        }
        @Override
        protected String doInBackground(String... params) {
            try {

                // Enter URL address where your php file resides
                url = new URL(url_login);

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return e.toString();
            }
            try {
                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");

                // setDoInput and setDoOutput method depict handling of both send and receive
                conn.setDoInput(true);
                conn.setDoOutput(true);

                // Append parameters to URL
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("id", params[0]);
                String query = builder.build().getEncodedQuery();

                // Open connection for sending data
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                return "exception";
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    // Pass data to onPostExecute method
                    return(result.toString());

                }else{

                    return("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";
            } finally {
                conn.disconnect();
            }


        }

        @Override
        protected void onPostExecute(String result) {

            //this method will be running on UI thread


            pdLoading.dismiss();
            if(!result.isEmpty())
            {

                String name=null;
                try {
                    JSONObject loggedin = new JSONObject(result);
                    JSONArray cli= loggedin.getJSONArray("client");
                    List<ClientsData>clientss;
                    clientss=new ArrayList<>();
                    for (int i = 0; i < cli.length(); i++) {
                        JSONObject js = cli.getJSONObject(i);
                        name= js.getString("name");
                        clientss.add(new ClientsData(js.getInt("id"),js.getString("name"),js.getInt("NumTel"),js.getInt("Statut"),js.getInt("id_coiff")));
                    }
                    ListView lv = (ListView)findViewById(R.id.clientsList);
                    try {
                        CustomClientsAdapter adapterClient = new CustomClientsAdapter(Clients.this, clientss);
                        lv.setAdapter(adapterClient);

                    }catch (Exception e)
                    {
                        Toast.makeText(Clients.this, e.toString(), Toast.LENGTH_LONG).show();}


                   // ArrayAdapter a = new ArrayAdapter<>(Clients.this,android.R.layout.simple_list_item_1,clientss);



                }catch(JSONException e)
                {
                    Toast.makeText(Clients.this, e.toString(), Toast.LENGTH_LONG).show();}

            }else if (result.isEmpty())
            {

            }
            else
            {
                Toast.makeText(Clients.this,result,Toast.LENGTH_LONG).show();

            }
        }

    }
}
