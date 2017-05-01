package com.example.masmo.first_app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by masmo on 16/03/2017.
 */

public class Autre_jours extends Activity
{
    private ProgressDialog pDialog;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;
    private static String url_login = "http://192.168.1.7/Coiffeur/get_rdv_details.php";
    private static String url_login1 = "http://192.168.1.7/Coiffeur/GetClient.php";
    private static String url_login2 = "http://192.168.1.7/Coiffeur/insert_rdv.php";
    private static String url_login3 = "http://192.168.1.7/Coiffeur/GetCoiffureTypes.php";

    String dateselectionne;
    String otherday;
    String time_rv;
    String id_coiff;
    String cids;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.autre_jours);

        Intent fromacceuil = getIntent();
        final TextView auda = (TextView)findViewById(R.id.auda);
        dateselectionne=fromacceuil.getStringExtra("NouvelleDate").toString();
        auda.setText(dateselectionne);
         cids = fromacceuil.getStringExtra("clientid");
        new GetClient().execute(cids);

    }

    private class GetClient extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(Autre_jours.this);
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
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = df.format(c.getTime());
                try {
                    JSONObject loggedin = new JSONObject(result);
                    JSONArray cli= loggedin.getJSONArray("client");
                    JSONObject js = cli.getJSONObject(0);
                    id_coiff= Integer.toString(js.getInt("id_coiff"));

                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
                    Date testDate = null;
                    try {
                        testDate = sdf.parse(dateselectionne);
                    }catch(Exception ex){
                        ex.printStackTrace();
                    }
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    otherday = formatter.format(testDate);

                    new GetTodayRdv().execute(id_coiff,otherday);
                    TextView iden = (TextView)findViewById(R.id.idenau);
                    iden.setText(js.getString("name"));

                }catch(JSONException e)
                {
                    Toast.makeText(Autre_jours.this, e.toString(), Toast.LENGTH_LONG).show();}

            }else if (result.isEmpty())
            {

            }
            else
            {
                Toast.makeText(Autre_jours.this,result,Toast.LENGTH_LONG).show();

            }
        }

    }

    private class GetTodayRdv extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(Autre_jours.this);
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
                        .appendQueryParameter("id_Coiff", params[0])
                        .appendQueryParameter("date", params[1]);
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
            List<String> data=new ArrayList<>();
            pdLoading.dismiss();


                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */

            String propietere_rdv = null;
            String temps_rdv =null;
            String nume =null;
            String ty = null;
            int id = 0;
            int id_coifeur=0;
            int k=0;
            try {
                final String[]tabl= getResources().getStringArray(R.array.time);
                List<DataItem> fullday;
                fullday =new ArrayList<>();
                for (k = 0; k < tabl.length; k++) {
                    fullday.add(new DataItem(tabl[k], "", "","", 0));
                }
                if (!result.isEmpty()) {
                    JSONObject loggedin = new JSONObject(result);
                    JSONArray coi = loggedin.getJSONArray("rdv");

                    List<DataItem> lsData;
                    lsData = new ArrayList<>();
                    for (int i = 0; i < coi.length(); i++) {
                        JSONObject js = coi.getJSONObject(i);
                        propietere_rdv = js.getString("name");
                        temps_rdv = js.getString("temps");
                        nume = js.getString("num");
                        ty= js.getString("typeCoiff");
                        id = js.getInt("id");
                        lsData.add(new DataItem(temps_rdv, propietere_rdv, nume,ty, id));

                        for (k = 0; k < fullday.size(); k++) {
                            if (fullday.get(k).time.equals(temps_rdv)) {
                                fullday.get(k).id = id;
                                fullday.get(k).name = propietere_rdv;
                                fullday.get(k).numeroo = nume;
                                fullday.get(k).type=ty;
                            }
                        }
                    }
                }
                try {
                    final ListView lv = (ListView) findViewById(R.id.rvau);
                    CustomAdapter adapter = new CustomAdapter(Autre_jours.this, fullday);
                    lv.setAdapter(adapter);
                    for (int o=0;o<fullday.size();o++)
                    {
                        Calendar ti = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"));
                        Date currentLocalTime = ti.getTime();
                        DateFormat date = new SimpleDateFormat("HH:00");
                        String localTime = date.format(currentLocalTime);

                        if (fullday.get(o).time.equals(localTime))
                        {
                            lv.setSelectionFromTop(o, 0);

                        }
                    }
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if((Integer)view.getTag()==0)
                            {
                                Object a = (Object)lv.getItemIdAtPosition(position);
                                int pos = Integer.parseInt(a.toString());
                                time_rv = tabl[pos];

                                Calendar c = Calendar.getInstance();
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                                String formattedDate = df.format(c.getTime());
                                int co = formattedDate.compareTo(otherday);
                                new GetTypeCoiffure().execute(id_coiff);

                            }else
                            {
                                Toast.makeText(Autre_jours.this, "edit", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                } catch (Exception e) {
                    Toast.makeText(Autre_jours.this, e.toString(), Toast.LENGTH_LONG).show();

                }


            } catch (JSONException e) {
                Toast.makeText(Autre_jours.this, e.toString(), Toast.LENGTH_LONG).show();
            }


        }

    }

    private class GetTypeCoiffure extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(Autre_jours.this);
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
                url = new URL(url_login3);

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
                    AlertDialog.Builder mBuil= new AlertDialog.Builder(Autre_jours.this);
                    View mView = getLayoutInflater().inflate(R.layout.new_rv,null);

                    final Spinner c=(Spinner)mView.findViewById(R.id.type_coiff);
                    ArrayAdapter<String> a = new ArrayAdapter<String>(Autre_jours.this,android.R.layout.simple_spinner_item,data);
                    c.setAdapter(a);

                    Button add = (Button)mView.findViewById(R.id.add_rdv_btn);
                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String type=c.getSelectedItem().toString();
                            new InsertClient().execute(cids,id_coiff,otherday,type,time_rv);
                        }
                    });
                    mBuil.setView(mView);
                    AlertDialog dialog = mBuil.create();
                    dialog.show();

                }catch(JSONException e)
                {
                    Toast.makeText(Autre_jours.this, e.toString(), Toast.LENGTH_LONG).show();}

            }else if (result.isEmpty())
            {
                Toast.makeText(Autre_jours.this,"empty",Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(Autre_jours.this,result,Toast.LENGTH_LONG).show();

            }
        }

    }

    private class InsertClient extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(Autre_jours.this);
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
                Toast.makeText(Autre_jours.this,"erreur lors de l'insertion",Toast.LENGTH_LONG).show();

            }else if (result.equals("echec1"))
            {
                Toast.makeText(Autre_jours.this,"404",Toast.LENGTH_LONG).show();
            }else if (result.equals("succ"))
            {
                Toast.makeText(Autre_jours.this,"normalement t'ajouta",Toast.LENGTH_LONG).show();
            }
            else if (result.isEmpty())
            {
                Toast.makeText(Autre_jours.this,"empty",Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(Autre_jours.this,result,Toast.LENGTH_LONG).show();

            }
        }

    }


}
