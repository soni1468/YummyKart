package com.example.pranshu.yummyrestaurant;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

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
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by PRanshu on 19-06-2017.
 */

public class MyService extends Service {
    public static final int TIME_INTERVAL = 30000;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    ContentActivity contentActivity;
    String s1 = "", num;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        contentActivity = ContentActivity.instance;

        if (mTimer != null) {
            mTimer.cancel();
        } else {
            mTimer = new Timer();
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mTimer.scheduleAtFixedRate(new displayTimerTask(), 0, TIME_INTERVAL);

        return START_STICKY;
    }

    public class displayTimerTask extends TimerTask {
        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    if (contentActivity != null) {
                        new DoInServiceGetOrders().execute();
                    }
                }
            });
        }
    }

    public class DoInServiceGetOrders extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream = null;
            String json_url = "http://yummykart.pe.hu/restaurant_order_get_service.php";
            try {
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("Number", "UTF-8") + "=" + URLEncoder.encode(contentActivity.number, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));

                StringBuilder stringBuilder = new StringBuilder();
                String JSON_STRING;
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
            //  Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
            if (result != null) {
                if (result.trim().length() > 0) {
                    JSONObject jsonObject;
                    JSONArray jsonArray;
                    String s2 = "", sOrderNo, sNumber, sPin, sName, sAddress1, sAddress2, sLandmark, sOrder, sAmount, sDate, sTime, sDiscount;
                    try {
                        jsonObject = new JSONObject(result);
                        jsonArray = jsonObject.getJSONArray("server_response");
                        int count = 0;
                        s1 = "";
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
                            s1 = s1 + sOrderNo + "\n";
                            count++;
                        }
                        showNotiSnack(count);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                if (contentActivity.flagForService == 1) {
                    Snackbar snackbar = Snackbar.make(contentActivity.relativeLayout, "check your internet connection!", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.RED);
                    textView.setTextSize(16);
                    snackbar.show();
                }
            }
        }
    }

    private void showNotiSnack(int count) {
      /*  if (contentActivity.flagForBroadcast == 1) {
            Snackbar snackbar = Snackbar.make(contentActivity.relativeLayout, "You have " + count + " new order.", Snackbar.LENGTH_LONG);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.GREEN);
            textView.setTextSize(18);
            snackbar.show();
        } else {    */
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder notification = new Notification.Builder(getApplicationContext());
        Intent intent = new Intent(this, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        notification.setDefaults(Notification.DEFAULT_ALL);
        notification.setAutoCancel(true);
        notification.setTicker("You have new orders!");
        notification.setSmallIcon(R.drawable.logo_logo);
        notification.setContentTitle("Order update.");
        notification.setContentText("You have " + count + " new order.");
        notification.setContentIntent(pendingIntent);
        // notification.addAction(R.drawable.ic_send, "See Order", pendingIntent);
        notification.build().flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification.build());
        //  }
    }


}
