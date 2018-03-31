package com.example.pranshu.yummyrestaurant;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class OrderHistoryActivity extends AppCompatActivity {

    private List<RecyclerItemHistory> itemList;
    private RecyclerView recyclerviewhistory;
    private MyAdapterHistory adapterHistory;
    private String number, JSON_STRING;
    Typeface font, font1;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_history);

        relativeLayout = (RelativeLayout) findViewById(R.id.historyRelative);

        Toolbar toolBar = (Toolbar) findViewById(R.id.app_bar);
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        font = Typeface.createFromAsset(getAssets(), "fonts/commist.ttf");
        font1 = Typeface.createFromAsset(getAssets(), "fonts/pacifico.ttf");


        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        number = getIntent().getStringExtra("num");

        recyclerviewhistory = (RecyclerView) findViewById(R.id.recyclerViewHistory);
        recyclerviewhistory.setLayoutManager(new LinearLayoutManager(this));
        itemList = new ArrayList<>();

        pranHistory();
    }

    private void pranHistory() {
        new DoInBackgroundTaskHistory().execute();
    }

    public class DoInBackgroundTaskHistory extends AsyncTask<String, Void, String> {

        SpotsDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new SpotsDialog(OrderHistoryActivity.this);
            dialog.show();
        }

        @Override
        protected String doInBackground(String[] params) {
            InputStream inputStream = null;
            String history_url = "http://yummykart.pe.hu/restaurant_order_history.php";
            try {
                URL url = new URL(history_url);
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
                while ((JSON_STRING = bufferedReader.readLine()) != null) {
                    stringBuilder.append(JSON_STRING + "\n");
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
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.dismiss();
            // Toast.makeText(OrderHistoryActivity.this,result,Toast.LENGTH_SHORT).show();
            if (result != null) {
                if (result.trim().length() > 0) {
                    JSONObject jsonObject;
                    JSONArray jsonArray;
                    String sOrderNo, sNumber, sPin, sName, sAddress1, sAddress2, sLandmark, sOrder, sAmount, sDate, sTime, sStatus, sDiscount, sTax;
                    try {
                        jsonObject = new JSONObject(result);
                        jsonArray = jsonObject.getJSONArray("server_response");
                        int count = 0;
                        while (count < jsonArray.length()) {
                            JSONObject jo = jsonArray.getJSONObject(count);
                            sOrderNo = jo.getString("Order_No");
                            sNumber = jo.getString("Cust_Mobile");
                            sPin = jo.getString("Cust_Pin");
                            sName = jo.getString("Cust_Name");
                            sAddress1 = jo.getString("Add_Line_1");
                            sAddress2 = jo.getString("Add_Line_2");
                            sLandmark = jo.getString("Landmark");
                            sOrder = jo.getString("Order");
                            sAmount = jo.getString("Amount");
                            sDate = jo.getString("Date");
                            sTime = jo.getString("Time");
                            sDiscount = jo.getString("Discount");
                            sStatus = jo.getString("Status");
                            sTax = jo.getString("Tax");

                            itemList.add(new RecyclerItemHistory(sOrderNo, sNumber, sPin, sName, sAddress1, sAddress2, sLandmark, sOrder, sAmount, sDate, sTime, sDiscount, sStatus, sTax));
                            count++;
                        }
                        adapterHistory = new MyAdapterHistory(itemList, OrderHistoryActivity.this, font, font1);
                        recyclerviewhistory.setAdapter(adapterHistory);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(OrderHistoryActivity.this, "data not available!", Toast.LENGTH_LONG).show();
                }
            } else{
                final Snackbar snackbar = Snackbar.make(relativeLayout,"check your internet connection!",Snackbar.LENGTH_INDEFINITE);
               // Toast.makeText(getApplicationContext(),"check your internet connection!",Toast.LENGTH_SHORT).show();
                snackbar.setActionTextColor(Color.parseColor("#0097A7"));
                snackbar.setAction("REFRESH", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pranHistory();
                        snackbar.dismiss();
                    }
                });
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(15);
                snackbar.show();
            }
        }
    }
}
