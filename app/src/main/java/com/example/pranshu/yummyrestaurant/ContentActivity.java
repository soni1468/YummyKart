package com.example.pranshu.yummyrestaurant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import java.util.Timer;
import java.util.TimerTask;

import dmax.dialog.SpotsDialog;

public class ContentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    String number;
    String JSON_STRING;
    private RecyclerView recyclerview;
    private MyAdapter adapter;
    private List<RecyclerItem> itemList;
    final long period = 60000;
    TextView tvNoOrder;
    ImageView ivNoOrder;
    DatabaseHelper myDB;
    boolean flag = false;
    static ContentActivity instance;
    TextView tvNavRestName, tvNavRestNum;
    String  s1 = "";
    SharedPreferences sf;
    SharedPreferences.Editor editor ;
    RelativeLayout relativeLayout;
    int flagForService,flagForOrderUpdate;
    Timer timer;
    Typeface font, font1;
    LoginActivity loginActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_main);

        Toolbar toolBar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolBar);
        relativeLayout = (RelativeLayout) findViewById(R.id.activity_Content);
        flagForService = 1;
        flagForOrderUpdate=1;

        loginActivity = LoginActivity.instance;

        instance = this;
        stopService(new Intent(this, MyService.class));

        number = getIntent().getStringExtra("num");

        sf = getSharedPreferences("yummyrestname", MODE_PRIVATE);

        ivNoOrder = (ImageView) findViewById(R.id.ivNoOrders);
        tvNoOrder = (TextView) findViewById(R.id.textNoOrders);
       // font = Typeface.createFromAsset(getAssets(), "fonts/pacifico.ttf");
        font1 = Typeface.createFromAsset(getAssets(), "fonts/timeburnernormal.ttf");
        tvNoOrder.setTypeface(font1);
        tvNoOrder.setVisibility(View.INVISIBLE);
        ivNoOrder.setVisibility(View.INVISIBLE);

        recyclerview = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        recyclerview.setHasFixedSize(true);

        itemList = new ArrayList<>();
        myDB = new DatabaseHelper(this);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolBar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);
            }

            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        tvNavRestNum = (TextView) header.findViewById(R.id.nav_text_num);
        tvNavRestName = (TextView) header.findViewById(R.id.nav_text_rest_name);
        tvNavRestNum.setText(number);
        if (sf.getString("Restaurantname", "").trim().length() > 0) {
            tvNavRestName.setText(sf.getString("Restaurantname", ""));

        } else {
            getRestName();
        }

        adapter = new MyAdapter(itemList, this, relativeLayout);
        recyclerview.setAdapter(adapter);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                pran();
            }

        }, 0, period);


    }

    private void getRestName() {
        new DoInBackgroundGetRestName().execute();
    }

    public void pran() {
        new DoInBackgroundGetOrders().execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        } else {
            if (flag)
                super.onBackPressed();
            flag = true;
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    flag = false;
                }
            }, 3000);
        }
    }

    public class DoInBackgroundGetOrders extends AsyncTask<String, Void, String> {
        //  SpotsDialog dialog;

        @Override
        protected void onPreExecute() {
            //   dialog = new SpotsDialog(ContentActivity.this,"Loading orders...");
            //   dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream = null;
            String json_url = "http://yummykart.pe.hu/restaurant_order_get.php";
            try {
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
            //dialog.dismiss();
            if (result != null) {
                if (result.trim().length() > 0) {
                    JSONObject jsonObject;
                    JSONArray jsonArray;
                    String sOrderNo, sNumber, sPin, sName, sAddress1, sAddress2, sLandmark, sOrder, sAmount, sDate, sTime, sDiscount, sTax;
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
                            sTax = jo.getString("Tax");
                            s1 = s1 + sOrderNo + "\n";
                            myDB.insertData(sOrderNo, sNumber, sPin, sName, sAddress1, sAddress2, sLandmark, sOrder, sAmount, sDate, sTime, sDiscount, sTax);
                            count++;
                        }
                        showNotification(count, s1);
                        showData(1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    //  Toast.makeText(getApplicationContext(), "No orders or bad internet connection!", Toast.LENGTH_LONG).show();
                    showData(0);
                }
            } else {
                final Snackbar snackbar = Snackbar.make(relativeLayout, "check your internet connection!", Snackbar.LENGTH_INDEFINITE);
                snackbar.setActionTextColor(Color.parseColor("#0097A7"));
                snackbar.setAction("REFRESH", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pran();
                        snackbar.dismiss();
                    }
                });
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(13);
                snackbar.show();
            }
            //  Toast.makeText(getApplicationContext(), "check your internet connection!", Toast.LENGTH_LONG).show();
        }
    }

    public void showNotification(int numOrder, String orderNos) {
       /* if (flagForBroadcast == 0) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Notification.Builder notification = new Notification.Builder(ContentActivity.this);
            Intent intent = new Intent(this, LoginActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
            notification.setDefaults(Notification.DEFAULT_ALL);
            notification.setAutoCancel(true);
            notification.setTicker("You have new orders !");
            notification.setSmallIcon(R.drawable.logo_logo);
            notification.setContentTitle("Order update.");
            notification.setContentText("You have " + numOrder + " new order" + "\n" + orderNos);
            notification.setContentIntent(pendingIntent);
            // notification.addAction(R.drawable.ic_send, "See Order", pendingIntent);
            notification.build().flags = Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(0, notification.build());
        } else {    */
        Snackbar snackbar = Snackbar.make(relativeLayout, "you have " + numOrder + " new order.", Snackbar.LENGTH_LONG);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.parseColor("#00B17A"));
        textView.setTextSize(16);
        // textView.setTypeface(font1);
        snackbar.show();

        //   }
    }

    private void showData(int x) {
        Cursor cData = myDB.getAllData();
        // Toast.makeText(this,cData.getCount(),Toast.LENGTH_LONG).show();
        if (cData.getCount() == 0) {
            tvNoOrder.setVisibility(View.VISIBLE);
            ivNoOrder.setVisibility(View.VISIBLE);
            relativeLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        } else {
            itemList.clear();
            while (cData.moveToNext()) {
                tvNoOrder.setVisibility(View.INVISIBLE);
                ivNoOrder.setVisibility(View.INVISIBLE);
                relativeLayout.setBackgroundColor(Color.parseColor("#BDBDBD"));
                itemList.add(new RecyclerItem(cData.getString(0), cData.getString(1), cData.getString(2), cData.getString(3), cData.getString(4), cData.getString(5), cData.getString(6), cData.getString(7), cData.getString(8), cData.getString(9), cData.getString(10), cData.getString(11), cData.getString(12), cData.getString(13)));
            }
            adapter.notifyDataSetChanged();
            if(flagForOrderUpdate==1) {
                Toast.makeText(getApplicationContext(), "list updated!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuLogout:
                Toast.makeText(this, "please uninstall the app and install again or clear data from settings!", Toast.LENGTH_LONG).show();
                break;
            case R.id.menuRefresh:
                Toast.makeText(this, "refreshing order list...", Toast.LENGTH_SHORT).show();
                pran();
                break;
        }

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_history) {
            Intent orderhistory = new Intent(this, OrderHistoryActivity.class);
            orderhistory.putExtra("num", number);
            startActivity(orderhistory);

        } else if (id == R.id.menu_profile) {
            Intent intentprofile = new Intent(this, ProfileActivity.class);
            intentprofile.putExtra("num", number);
            startActivity(intentprofile);

        } else if (id == R.id.menu_areas) {
            Intent intentpin = new Intent(this, PinActivity.class);
            intentpin.putExtra("num", number);
            startActivity(intentpin);

        } else if (id == R.id.menu_ordermenu) {
            Intent intentMenu = new Intent(this, MenuActivity.class);
            intentMenu.putExtra("num", number);
            startActivity(intentMenu);
        } else if (id == R.id.menu_helpline){
            Intent intentHelpline = new Intent(this,HelplineActivity.class);
            startActivity(intentHelpline);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Async task for getting RESTAURANT NAME

    public class DoInBackgroundGetRestName extends AsyncTask<String, Void, String> {

       // SpotsDialog alertDialog;

        @Override
        protected void onPreExecute() {
          //  alertDialog = new SpotsDialog(ContentActivity.this, "Logging you in...");
          //  alertDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream = null;
            try {
                String json_url = "http://yummykart.pe.hu/restaurant_name_for_navigationbar.php";
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
        protected void onPostExecute(String result) {
          //  alertDialog.dismiss();

            if (result != null) {
                if (result.trim().length() > 0) {
                    JSONObject jsonObject;
                    JSONArray jsonArray;
                    String restaurantName;
                    try {
                        jsonObject = new JSONObject(result);
                        jsonArray = jsonObject.getJSONArray("server_response");
                        JSONObject jo = jsonArray.getJSONObject(0);
                        restaurantName = jo.getString("Rest_Name");
                        tvNavRestName.setText(restaurantName);
                        editor = sf.edit();
                        editor.putString("Restaurantname", restaurantName);
                        editor.apply();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                final Snackbar snackbar = Snackbar.make(relativeLayout, "check your internet connection!", Snackbar.LENGTH_INDEFINITE);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                snackbar.setActionTextColor(Color.parseColor("#0097A7"));
                snackbar.setAction("REFRESH", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pran();
                        snackbar.dismiss();
                    }
                });
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(13);
                snackbar.show();
                //Toast.makeText(ContentActivity.this, "bad internet connection!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        flagForOrderUpdate=0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        flagForOrderUpdate=1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flagForService = 0;
        flagForOrderUpdate=0;
        startService(new Intent(this, MyService.class));
        timer.cancel();
    }
}
