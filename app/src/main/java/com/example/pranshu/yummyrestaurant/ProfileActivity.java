package com.example.pranshu.yummyrestaurant;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
import java.net.URLEncoder;

import dmax.dialog.SpotsDialog;

public class ProfileActivity extends AppCompatActivity {
    EditText etRestName, etMinOrder, etOpentime, etClosetime, etDiscount, etTax;
    Button buttonSave, buttonCancel;
    ImageButton imgbutpic;
    String number, JSON_STRING = null;
    TextView etPhone,etEP;
    LinearLayout linearLayout;
    Typeface font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_profile);


        etRestName = (EditText) findViewById(R.id.input_res_name);
        etPhone = (TextView) findViewById(R.id.input_res_mobile_no);
        etMinOrder = (EditText) findViewById(R.id.input_res_min_order);
        etOpentime = (EditText) findViewById(R.id.input_res_open_time);
        etClosetime = (EditText) findViewById(R.id.input_res_close_time);
        etDiscount = (EditText) findViewById(R.id.input_res_discount);
        etTax = (EditText) findViewById(R.id.input_res_tax);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        buttonSave = (Button) findViewById(R.id.buttonSave);
        imgbutpic = (ImageButton) findViewById(R.id.buttonPic);
        linearLayout  = (LinearLayout) findViewById(R.id.profileLinear);
        etEP = (TextView) findViewById(R.id.textProfileRestName);

        font = Typeface.createFromAsset(getAssets(),"fonts/timeburnernormal.ttf");

        etEP.setTypeface(font);

        Toolbar toolBar = (Toolbar) findViewById(R.id.app_bar);
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        number = getIntent().getStringExtra("num");

        new DoInBackgroundProfile(this).execute(number);

    }

    public void CancelButton(View v) {
        onBackPressed();
    }

    public void SaveProfile(View v) {
        String name, phn, min, open, close, discount, tax;
        if (etRestName.getText().toString().trim().length() > 0 && etPhone.getText().toString().trim().length() > 0 && etMinOrder.getText().toString().trim().length() > 0
                && etOpentime.getText().toString().trim().length() > 0 && etClosetime.getText().toString().trim().length() > 0 && etDiscount.getText().toString().trim().length() > 0 && etTax.getText().toString().trim().length() > 0) {
            name = etRestName.getText().toString().trim();
            phn = etPhone.getText().toString().trim();
            min = etMinOrder.getText().toString().trim();
            open = etOpentime.getText().toString().trim();
            close = etClosetime.getText().toString().trim();
            discount = etDiscount.getText().toString().trim();
            tax = etTax.getText().toString().trim();
            new DoInBackgroundProfileUpdate().execute(number, name, phn, min, open, close, discount, tax);
        } else
            Toast.makeText(getApplicationContext(), "fill all details!", Toast.LENGTH_SHORT).show();

    }

    //Async Task for getting PROFILE DETAILS

    public class DoInBackgroundProfile extends AsyncTask<String, Void, String> {

        SpotsDialog alertDialog;
        Context context;

        public DoInBackgroundProfile(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            alertDialog = new SpotsDialog(ProfileActivity.this,"Loding Profile...");
            alertDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream = null;
            try {
                String json_url = "http://yummykart.pe.hu/restaurant_profile_getdata.php";
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
        protected void onPostExecute(String result) {
            //  Toast.makeText(ProfileActivity.this, result, Toast.LENGTH_LONG).show();
            alertDialog.dismiss();
            if (result != null) {
                if(result.trim().length()>0) {
                    JSONObject jsonObject;
                    JSONArray jsonArray;
                    String sRestName, sNumber, sMinOrder, sOpenTime, sCloseTime, sImgUrl, sDiscount, sTax;
                    try {
                        jsonObject = new JSONObject(result);
                        jsonArray = jsonObject.getJSONArray("server_response");
                        JSONObject jo = jsonArray.getJSONObject(0);
                        sRestName = jo.getString("Rest_Name");
                        sNumber = jo.getString("Phone");
                        sMinOrder = jo.getString("Min_Order");
                        sOpenTime = jo.getString("Open_Time");
                        sCloseTime = jo.getString("Close_Time");
                        sImgUrl = jo.getString("Img_URL");
                        sDiscount = jo.getString("Discount");
                        sTax = jo.getString("GST");

                        etRestName.setText(sRestName);
                        etPhone.setText(sNumber);
                        etMinOrder.setText(sMinOrder);
                        etOpentime.setText(sOpenTime);
                        etClosetime.setText(sCloseTime);
                        etDiscount.setText(sDiscount);
                        etTax.setText(sTax);
                        Picasso.with(ProfileActivity.this).load(sImgUrl).resize(400, 400).transform(new RoundTransformation(300, 10)).into(imgbutpic);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    etPhone.setText(number);
                    Toast.makeText(context, "error fetching data or data not available!", Toast.LENGTH_SHORT).show();
                }

            } else {
                final Snackbar snackbar = Snackbar.make(linearLayout,"check your internet connection!",Snackbar.LENGTH_INDEFINITE);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackbar.setActionTextColor(Color.parseColor("#0097A7"));
                snackbar.setAction("REFRESH", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DoInBackgroundProfile(ProfileActivity.this).execute(number);
                        snackbar.dismiss();
                    }
                });
                textView.setTextSize(13);
                snackbar.show();
            }

        }
    }

    public class DoInBackgroundProfileUpdate extends AsyncTask<String, Void, String> {
        SpotsDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new SpotsDialog(ProfileActivity.this, "Updating profile...");
            dialog.show();
        }


        @Override
        protected String doInBackground(String... params) {
            String name, phn, min, open, close, discount, tax;
            name = params[1];
            phn = params[2];
            min = params[3];
            open = params[4];
            close = params[5];
            discount = params[6];
            tax = params[7];
            InputStream inputStream = null;

            try {
                String json_url = "http://yummykart.pe.hu/restaurant_profile_setdata.php";
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("POST");
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("Login_Number", "UTF-8") + "=" + URLEncoder.encode(number, "UTF-8")
                        + "&" + URLEncoder.encode("Restaurant_Name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8")
                        + "&" + URLEncoder.encode("Number", "UTF-8") + "=" + URLEncoder.encode(phn, "UTF-8")
                        + "&" + URLEncoder.encode("Minimum_Order", "UTF-8") + "=" + URLEncoder.encode(min, "UTF-8")
                        + "&" + URLEncoder.encode("Open_Time", "UTF-8") + "=" + URLEncoder.encode(open, "UTF-8")
                        + "&" + URLEncoder.encode("Close_Time", "UTF-8") + "=" + URLEncoder.encode(close, "UTF-8")
                        + "&" + URLEncoder.encode("Discount", "UTF-8") + "=" + URLEncoder.encode(discount, "UTF-8")
                        + "&" + URLEncoder.encode("GST", "UTF-8") + "=" + URLEncoder.encode(tax, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"ISO-8859-1"));

                StringBuilder stringBuilder = new StringBuilder();
                String Json_String;
                while ((Json_String = bufferedReader.readLine()) != null) {
                    stringBuilder.append(Json_String + "\n");
                }

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
            {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            if(result!=null) {
                Toast.makeText(ProfileActivity.this, result, Toast.LENGTH_LONG).show();
            } else {
                final Snackbar snackbar = Snackbar.make(linearLayout, "check your internet connection!", Snackbar.LENGTH_INDEFINITE);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                snackbar.setActionTextColor(Color.parseColor("#0097A7"));
                snackbar.setAction("REFRESH", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DoInBackgroundProfile(ProfileActivity.this).execute(number);
                        snackbar.dismiss();
                    }
                });
                textView.setTextSize(13);
                snackbar.show();
            }
        }
    }
}
