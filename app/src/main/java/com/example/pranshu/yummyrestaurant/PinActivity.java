package com.example.pranshu.yummyrestaurant;

import android.app.Dialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.net.URLEncoder;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class PinActivity extends AppCompatActivity {

    ArrayList<String> pinlist;
    ListView lv;
    ArrayAdapter<String> adapter;
    String number;
    String json_url1 = "http://yummykart.pe.hu/restaurant_pin_get.php";
    String json_url2 = "http://yummykart.pe.hu/restaurant_pin_delete.php";
    String json_url3 = "http://yummykart.pe.hu/restaurant_pin_add.php";
    String sPin;
    int index;
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_pin);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.pinCoordinate);

        number = getIntent().getStringExtra("num");

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

        lv = (ListView) findViewById(R.id.listviewPin);

        pinlist = new ArrayList<String>();

        new DoInBackgroundPinGet().execute();

        registerForContextMenu(lv);


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabbuttonPin);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(PinActivity.this);
                dialog.setContentView(R.layout.fabpin_dialog_layout);
                Button butSave = (Button) dialog.findViewById(R.id.customDialogSave);
                Button butCancel = (Button) dialog.findViewById(R.id.customDialogCancel);
                final EditText etPin = (EditText) dialog.findViewById(R.id.customEditTextDialog);

                butSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int flag = 0;
                        sPin = etPin.getText().toString().trim();
                        if (sPin.length() == 6) {
                            for (int j = 0; j < pinlist.size(); j++) {
                                if (sPin.equals(pinlist.get(j))) {
                                    flag = 1;
                                    break;
                                }
                            }
                            if (flag == 1) {
                                Toast.makeText(PinActivity.this, "pin already exists!", Toast.LENGTH_SHORT).show();
                            } else {
                                new DoInBackgroundPinAdd().execute(sPin);
                                dialog.dismiss();
                            }
                        } else
                            Toast.makeText(PinActivity.this, "invalid pin!", Toast.LENGTH_SHORT).show();


                    }
                });
                butCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();

            }
        });

    }


    //Async task FOR GETTING PIN

    public class DoInBackgroundPinGet extends AsyncTask<String, Void, String> {
        SpotsDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new SpotsDialog(PinActivity.this,"Loading Pin...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream=null;
            try {
                URL url = new URL(json_url1);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("Number", "UTF-8") + "=" + URLEncoder.encode(number, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"ISO-8859-1"));

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
            dialog.dismiss();
            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("server_response");
                    int count = 0;
                    String sPin;
                    while (count < jsonArray.length()) {
                        JSONObject jo = jsonArray.getJSONObject(count);
                        sPin = jo.getString("Pin");
                        pinlist.add(sPin);
                        count++;
                    }
                    adapter = new ArrayAdapter<String>(PinActivity.this, R.layout.list_item_pin, pinlist);
                    lv.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                final Snackbar snackbar = Snackbar.make(coordinatorLayout, "check your internet connection!", Snackbar.LENGTH_INDEFINITE);
                snackbar.setActionTextColor(Color.parseColor("#0097A7"));
                snackbar.setAction("REFRESH", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DoInBackgroundPinGet().execute();
                        snackbar.dismiss();
                    }
                });
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(14);
                snackbar.show();
                // Toast.makeText(PinActivity.this,"check your internet connection!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, "Delete Pin");
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        if (item.getTitle() == "Delete Pin") {

            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_delete_layout);
            Button butYes = (Button) dialog.findViewById(R.id.buttonConfirmYES);
            Button butNo = (Button) dialog.findViewById(R.id.buttonConfirmNO);
            TextView tvText = (TextView) dialog.findViewById(R.id.customDialogText);
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            index = info.position;
            tvText.setText("Do you really want to delete pin : "+pinlist.get(index)+" ?");
            butYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DoInBackgroundPinDelete().execute(pinlist.get(index));
                    dialog.dismiss();
                }
            });
            butNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
        return true;

    }

    //Async Task FOR DELETING PIN

    public class DoInBackgroundPinDelete extends AsyncTask<String, Void, String> {

        SpotsDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new SpotsDialog(PinActivity.this,"Deleting Pin...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String pin = params[0];
            InputStream inputStream=null;
            try {
                URL url = new URL(json_url2);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("Number", "UTF-8") + "=" + URLEncoder.encode(number, "UTF-8")
                        + "&" + URLEncoder.encode("Pin", "UTF-8") + "=" + URLEncoder.encode(pin, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));

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
            dialog.dismiss();
            if (result != null) {
                if (result.equals("pin deleted!")) {
                    pinlist.remove(index);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(PinActivity.this, result, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "error deleting pin! please try again.", Toast.LENGTH_SHORT).show();
            }else {
                final Snackbar snackbar = Snackbar.make(coordinatorLayout, "check your internet connection!", Snackbar.LENGTH_INDEFINITE);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                snackbar.setActionTextColor(Color.parseColor("#0097A7"));
                snackbar.setAction("REFRESH", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DoInBackgroundPinGet().execute();
                        snackbar.dismiss();
                    }
                });
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(14);
                snackbar.show();
            }
        }
    }

    //Async Task FOR ADDING PIN

    public class DoInBackgroundPinAdd extends AsyncTask<String, Void, String> {
        SpotsDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new SpotsDialog(PinActivity.this,"Adding Pin...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String pin = params[0];
            InputStream inputStream=null;
            try {
                URL url = new URL(json_url3);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("Number", "UTF-8") + "=" + URLEncoder.encode(number, "UTF-8")
                        + "&" + URLEncoder.encode("Pin", "UTF-8") + "=" + URLEncoder.encode(pin, "UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

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
            dialog.dismiss();
            if (result != null) {
                if (result.equals("pin added successfully!")) {
                    pinlist.add(sPin);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(PinActivity.this, result, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "error adding pin! please try again.", Toast.LENGTH_SHORT).show();
            }else {
                final Snackbar snackbar = Snackbar.make(coordinatorLayout, "check your internet connection!", Snackbar.LENGTH_INDEFINITE);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                snackbar.setActionTextColor(Color.parseColor("#0097A7"));
                snackbar.setAction("REFRESH", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DoInBackgroundPinGet().execute();
                        snackbar.dismiss();
                    }
                });
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(14);
                snackbar.show();
            }
        }
    }
}
