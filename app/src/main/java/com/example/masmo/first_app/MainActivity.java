package com.example.masmo.first_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.net.Uri;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.json.JSONArray;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class MainActivity extends Activity {
    String password;
    String numero;
    private ProgressDialog pDialog;
    EditText inputName;
    EditText inputPrice;
    EditText inputDesc;
    private static String url_login = "http://192.168.1.5/Coiffeur/login_client.php";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    private static int TAG_ID;
    private static  String NAME;
    private static int NUM ;
    private static String Statut;
    private static String Coiffeur;
    public static final int CONNECTION_TIMEOUT=10000;
    public static final int READ_TIMEOUT=15000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        final EditText numTel = (EditText)findViewById(R.id.mail);
        final EditText pass = (EditText)findViewById(R.id.pass);
        Button connect = (Button)findViewById(R.id.connect);
        Button inscrit = (Button)findViewById(R.id.inscrit);
        TextView coi = (TextView)findViewById(R.id.coiff);

        coi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent TologCoiff = new Intent(MainActivity.this,LoginCoiffeur.class);
                startActivity(TologCoiff);
            }
        });

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String numText = numTel.getText().toString();
                    final String passText = pass.getText().toString();
                    new GetClient().execute(numText,passText);
                }catch(Exception e)
                {
                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();

                }

            }
        });

        inscrit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent insc = new Intent(MainActivity.this,Inscription.class);
                startActivity(insc);
            }
        });


    }

    private class GetClient extends AsyncTask<String, String, String>
    {
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
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
                        .appendQueryParameter("numTel", params[0])
                        .appendQueryParameter("pass", params[1]);
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
                /* Here launching another activity when login successful. If you persist login state
                use sharedPreferences of Android. and logout button to clear sharedPreferences.
                 */

                int client_id =0;
                int client_statut=0;
                try {

                        JSONObject loggedin = new JSONObject(result);
                        JSONArray cli= loggedin.getJSONArray("client");
                        JSONObject js = cli.getJSONObject(0);
                        client_id= js.getInt("id");
                        client_statut=js.getInt("Statut");

                }catch(JSONException e)
                {Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();}
                if (client_statut==1) {
                    Intent intent = new Intent(MainActivity.this, Acceuil.class);
                    intent.putExtra("clientid", client_id);
                    startActivity(intent);
                    MainActivity.this.finish();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Veuillez pationtez pendant que votre coiffeur vérifie votre compte", Toast.LENGTH_LONG).show();

                }


            }else if (result.isEmpty()){

                // If username and password does not match display a error message
                Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_LONG).show();

            } else if (result.equalsIgnoreCase("exception") || result.equalsIgnoreCase("unsuccessful")) {

                Toast.makeText(MainActivity.this, "OOPs! Something went wrong. Connection Problem.", Toast.LENGTH_LONG).show();

            }
        }

    }
}



