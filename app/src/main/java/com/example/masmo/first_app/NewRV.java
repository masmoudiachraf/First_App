package com.example.masmo.first_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by masmo on 08/04/2017.
 */

public class NewRV extends Activity  {

    private ProgressDialog pDialog;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    private static String url_login = "http://192.168.1.5/Coiffeur/GetCoiffureTypes.php";
    private static String url_login1 = "http://192.168.1.5/Coiffeur/GetClient.php";
    private static String url_login2 = "http://192.168.1.5/Coiffeur/insert_rdv.php";

    private String cids;
    private String id_coiff;
    private String date_rv;
    private String time_rv;
    private String type;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.new_rv);

        Intent int1=getIntent();
        cids=int1.getStringExtra("client_id");
        time_rv=int1.getStringExtra("time_rv");
        String date=int1.getStringExtra("date_rv");

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        Date testDate = null;
        try {
            testDate = sdf.parse(date);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        date_rv = formatter.format(testDate);

        new GetClient().execute(cids);

        final Spinner sp = (Spinner)findViewById(R.id.type_coiff);

        Button add = (Button)findViewById(R.id.add_rdv_btn);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type=sp.getSelectedItem().toString();
                new InsertClient().execute(cids,id_coiff,date_rv,type,time_rv);
            }
        });

    }



    private class GetTypeCoiffure extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(NewRV.this);
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
                        .appendQueryParameter("id_coiffeur", params[0]);
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
                List<String> data=new ArrayList<>();

                try {
                    JSONObject loggedin = new JSONObject(result);
                    JSONArray coi= loggedin.getJSONArray("type");
                    for(int i=0;i<coi.length();i++)
                    {
                        JSONObject js = coi.getJSONObject(i);
                        String type_name = js.getString("nom");
                        data.add(type_name);
                    }
                    Spinner c=(Spinner)findViewById(R.id.type_coiff);
                    ArrayAdapter<String> a = new ArrayAdapter<String>(NewRV.this,android.R.layout.simple_spinner_item,data);
                    c.setAdapter(a);

                }catch(JSONException e)
                {
                    Toast.makeText(NewRV.this, e.toString(), Toast.LENGTH_LONG).show();}

            }else if (result.isEmpty())
            {
                Toast.makeText(NewRV.this,"empty",Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(NewRV.this,result,Toast.LENGTH_LONG).show();

            }
        }

    }

    private class GetClient extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(NewRV.this);
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
                url = new URL(url_login1);

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
                try {
                    JSONObject loggedin = new JSONObject(result);
                    JSONArray cli= loggedin.getJSONArray("client");
                    JSONObject js = cli.getJSONObject(0);
                    id_coiff= Integer.toString(js.getInt("id_coiff"));

                    new GetTypeCoiffure().execute(id_coiff);

                }catch(JSONException e)
                {Toast.makeText(NewRV.this, e.toString(), Toast.LENGTH_LONG).show();}

            }else if (result.isEmpty())
            {

            }
            else
            {
                Toast.makeText(NewRV.this,result,Toast.LENGTH_LONG).show();

            }
        }

    }


    private class InsertClient extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(NewRV.this);
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
                url = new URL(url_login2);

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
                        .appendQueryParameter("cids", params[0])
                        .appendQueryParameter("id_coiff", params[1])
                        .appendQueryParameter("date_rv", params[2])
                        .appendQueryParameter("type", params[3])
                        .appendQueryParameter("time_rv", params[4]);
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

            if(result.equals("echec2"))
            {
                Toast.makeText(NewRV.this,"erreur lors de l'insertion",Toast.LENGTH_LONG).show();

            }else if (result.equals("echec1"))
            {
                Toast.makeText(NewRV.this,"404",Toast.LENGTH_LONG).show();
            }else if (result.equals("succ"))
            {
                NewRV.this.finish();
                Intent toacc = new Intent(NewRV.this,Acceuil.class);
                toacc.putExtra("clientid",cids);
                startActivity(toacc);
            }
            else if (result.isEmpty())
            {
                Toast.makeText(NewRV.this,"empty",Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(NewRV.this,result,Toast.LENGTH_LONG).show();

            }
        }

    }


}
