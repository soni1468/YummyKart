package com.example.pranshu.yummyrestaurant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.net.URLEncoder;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {
    EditText et;
    String number = null;
    SharedPreferences sf;
    SharedPreferences.Editor e;
    static LoginActivity instance;
    TextView textWarning;
    Context context;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_login);
        instance = this;
        context = this;

        relativeLayout = (RelativeLayout) findViewById(R.id.loginRelative);

        et = (EditText) findViewById(R.id.editText1);
        sf = getSharedPreferences("yummy", MODE_PRIVATE);
        textWarning = (TextView) findViewById(R.id.textLoginWarning);
        textWarning.setVisibility(View.GONE);

        if (sf.getString("Number", "").trim().length() > 0) {
            Intent i = new Intent(this, ContentActivity.class);
            i.putExtra("num", sf.getString("Number", ""));
            startActivity(i);
        }
    }

    public void LoginMe(View v) {
        number = et.getText().toString().trim();
        if(isValidNumber(number)){
            textWarning.setVisibility(View.GONE);
            new DoInBackgroundConfirmProfile().execute();
          /*  e = sf.edit();
            e.putString("Number", number);
            e.commit();
            Intent j = new Intent(this, ContentActivity.class);
            j.putExtra("num", number);
            startActivity(j);   */
        }else
            textWarning.setVisibility(View.VISIBLE);

    }

    private static boolean isValidNumber(String number){
        String exp = "^[0-9]{10}$";
        return number.matches(exp);
    }


    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    public class DoInBackgroundConfirmProfile extends AsyncTask<String,Void,String>{

        SpotsDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new SpotsDialog(context,"Logging in...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream = null;
            try {
                String json_url = "http://yummykart.pe.hu/restaurant_login_confirm.php";
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("Number", "UTF-8") + "=" + URLEncoder.encode(number, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                StringBuilder stringBuilder = new StringBuilder();
                String JSON_STRING;
                if (bufferedReader != null) {
                    while ((JSON_STRING = bufferedReader.readLine()) != null) {
                        stringBuilder.append(JSON_STRING + "\n");
                    }
                } else
                    return null;

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            if(s!=null){
                if(s.equals("1")){
                    e = sf.edit();
                    e.putString("Number", number);
                    e.commit();
                    Intent j = new Intent(context, ContentActivity.class);
                    j.putExtra("num", number);
                    startActivity(j);
                }else{
                    textWarning.setText("Number not registered!");
                    textWarning.setVisibility(View.VISIBLE);
                    Intent register = new Intent(LoginActivity.this,RegisterActivity.class);
                    startActivity(register);
                }
            }else{
                final Snackbar snackbar = Snackbar.make(relativeLayout, "check your internet connection!", Snackbar.LENGTH_INDEFINITE);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackbar.setActionTextColor(Color.parseColor("#0097A7"));
                snackbar.setAction("REFRESH", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DoInBackgroundConfirmProfile().execute();
                        snackbar.dismiss();
                    }
                });
                textView.setTextSize(13);
                snackbar.show();
            }
        }
    }


}
