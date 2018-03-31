package com.example.pranshu.yummyrestaurant;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import fr.ganfra.materialspinner.MaterialSpinner;

public class MenuActivity extends AppCompatActivity {
    String number;
    RecyclerView recyclerView;
    MyAdapterMenu adapterMenu;
    List<RecyclerItemMenu> itemlist;
    String category[] = {"Recommended", "Chinese", "Curry", "Rolls", "Dry", "Bread", "Biryani & Rice", "Combos"};
    String type[] = {"VEG", "NONVEG"};
    Dialog dialog_add, dialog_update_delete, dialog_update;
    ArrayAdapter<String> categoryAdapter, typeAdapter;
    CoordinatorLayout coordinatorLayout;
    int itemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_menu);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.menuCoordinate);

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
        dialog_add = new Dialog(MenuActivity.this);
        dialog_update_delete = new Dialog(MenuActivity.this);
        dialog_update = new Dialog(MenuActivity.this);

        categoryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, category);
        typeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, type);

        itemlist = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewMenu);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
            }

            @Override
            public void onLongClick(final View view, final int position) {
                itemPosition = position;
                final RecyclerItemMenu itemList = itemlist.get(position);

                dialog_update_delete.setContentView(R.layout.dialog_menu_update_delete);
                Button butUpdate = (Button) dialog_update_delete.findViewById(R.id.buttonUpadteMenu);
                Button butDelete = (Button) dialog_update_delete.findViewById(R.id.buttonDeleteMenu);
                final Button butBlock = (Button) dialog_update_delete.findViewById(R.id.buttonBlockMenu);

                if(itemList.getMenuBlock().equals("1")){
                    butBlock.setText("UNBLOCK");
                   // view.setBackgroundColor(Color.parseColor("#FF5722"));
                    butBlock.setTextColor(Color.parseColor("#DD2C00"));
                }else if(itemList.getMenuBlock().equals("0")){
                    butBlock.setText("BLOCK");
                  //  view.setBackgroundColor(Color.parseColor("#9E9E9E"));
                    butBlock.setTextColor(Color.parseColor("#212121"));
                }

                butDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_update_delete.dismiss();
                        final Dialog dialogdelete = new Dialog(MenuActivity.this);
                        dialogdelete.setContentView(R.layout.dialog_delete_layout);
                        Button buttonYes = (Button) dialogdelete.findViewById(R.id.buttonConfirmYES);
                        Button buttonNo = (Button) dialogdelete.findViewById(R.id.buttonConfirmNO);
                        buttonYes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogdelete.dismiss();
                                FunDeleteMenu(itemList.getMenuItem());
                                itemlist.remove(position);
                                adapterMenu.notifyDataSetChanged();
                                dialog_update_delete.dismiss();

                            }
                        });
                        buttonNo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogdelete.dismiss();
                            }
                        });
                        dialogdelete.show();

                    }
                });
                butUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_update_delete.dismiss();
                        final RecyclerItemMenu itemList = itemlist.get(position);
                        final String sCategory, sItem, sPrice, sPiece, sType;
                        sItem = itemList.getMenuItem();
                        sPrice = itemList.getMenuPrice();
                        sPiece = itemList.getMenuPiece();
                        sCategory = itemList.getMenuCategory();
                        sType = itemList.getMenuType();
                        dialog_update.setContentView(R.layout.fabmenu_dialog_layout);

                        final EditText etMenuItem = (EditText) dialog_update.findViewById(R.id.editTextMenuItem);
                        final EditText etMenuPrice = (EditText) dialog_update.findViewById(R.id.editTextMenuPrice);
                        final EditText etMenuPiece = (EditText) dialog_update.findViewById(R.id.editTextMenuPiece);
                        final MaterialSpinner spinnerCat = (MaterialSpinner) dialog_update.findViewById(R.id.spinnerCategory);
                        final MaterialSpinner spinnerType = (MaterialSpinner) dialog_update.findViewById(R.id.spinnerType);
                        Button butAdd = (Button) dialog_update.findViewById(R.id.buttonMenuAdd);
                        Button butCancel = (Button) dialog_update.findViewById(R.id.buttonMenuCancel);
                        spinnerCat.setAdapter(categoryAdapter);
                        spinnerCat.setPaddingSafe(0, 0, 0, 0);
                        spinnerType.setAdapter(typeAdapter);
                        spinnerType.setPaddingSafe(0, 0, 0, 0);

                        etMenuItem.setText(sItem.trim());
                        etMenuPrice.setText(sPrice);
                        etMenuPiece.setText((sPiece));
                        butAdd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String sITEM, sPRICE, sPIECE, sCATEGORY, sTYPE;

                                if (etMenuItem.getText().toString().trim().length() > 0 && etMenuPrice.getText().toString().trim().length() > 0) {
                                    sITEM = etMenuItem.getText().toString().trim();
                                    sPRICE = etMenuPrice.getText().toString().trim();
                                    if (etMenuPiece.getText().toString().trim().length() > 0) {
                                        sPIECE = etMenuPiece.getText().toString().trim();
                                    } else {
                                        sPIECE = "0";
                                    }
                                    sCATEGORY = spinnerCat.getSelectedItem().toString();
                                    sTYPE = spinnerType.getSelectedItem().toString();
                                    if (sCATEGORY.equals("Category")) {
                                        Toast.makeText(getApplicationContext(), "please choose Category!", Toast.LENGTH_SHORT).show();
                                    } else if (sTYPE.equals("Type")) {
                                        Toast.makeText(getApplicationContext(), "please choose Type!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        itemlist.remove(position);
                                        itemlist.add(position,new RecyclerItemMenu(number, sITEM, sPRICE, sPIECE, sCATEGORY, sTYPE,itemList.getMenuBlock()));
                                        adapterMenu.notifyDataSetChanged();
                                        new DoInBackgroundMenuUpdate().execute(sItem, sITEM, sPRICE, sPIECE, sCATEGORY, sTYPE);
                                        dialog_update.dismiss();
                                    }
                                } else
                                    Toast.makeText(getApplicationContext(), "fill all deatails!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        butCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog_update.dismiss();
                            }
                        });
                        dialog_update.show();
                    }
                });

                butBlock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (itemList.getMenuBlock().equals("0")) {
                            new DoInBackgroundMenuBlock().execute(number,"1",itemList.getMenuItem(),itemList.getMenuPrice(),itemList.getMenuType(),position+" ",itemList.getMenuPiece(),itemList.getMenuCategory());
                            dialog_update_delete.dismiss();
                        } else if (itemList.getMenuBlock().equals("1")) {
                            new DoInBackgroundMenuBlock().execute(number,"0",itemList.getMenuItem(),itemList.getMenuPrice(),itemList.getMenuType(),position+" ",itemList.getMenuPiece(),itemList.getMenuCategory());
                            dialog_update_delete.dismiss();
                        }
                    }
                });

                dialog_update_delete.show();
            }
        }));

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabbuttonMenu);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShowMenuAddDialog();

            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && fab.isShown()) {
                    fab.hide();
                } else if (dy < 0)
                    fab.show();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            }
        });


        new DoInBackgroundMenuGet().execute();
    }

    public void FunDeleteMenu(String MenuItem) {
        new DoInBackgroundMenuDelete().execute(number, MenuItem);
    }

    public void ShowMenuAddDialog() {
        dialog_add.setContentView(R.layout.fabmenu_dialog_layout);
        final EditText etMenuItem = (EditText) dialog_add.findViewById(R.id.editTextMenuItem);
        final EditText etMenuPrice = (EditText) dialog_add.findViewById(R.id.editTextMenuPrice);
        final EditText etMenuPiece = (EditText) dialog_add.findViewById(R.id.editTextMenuPiece);
        final MaterialSpinner spinnerCat = (MaterialSpinner) dialog_add.findViewById(R.id.spinnerCategory);
        final MaterialSpinner spinnerType = (MaterialSpinner) dialog_add.findViewById(R.id.spinnerType);
        Button butAdd = (Button) dialog_add.findViewById(R.id.buttonMenuAdd);
        Button butCancel = (Button) dialog_add.findViewById(R.id.buttonMenuCancel);
        spinnerCat.setAdapter(categoryAdapter);
        spinnerCat.setPaddingSafe(0, 0, 0, 0);
        spinnerType.setAdapter(typeAdapter);
        spinnerType.setPaddingSafe(0, 0, 0, 0);


        butAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sITEM, sPRICE, sPIECE, sCATEGORY, sTYPE;

                if (etMenuItem.getText().toString().trim().length() > 0 && etMenuPrice.getText().toString().trim().length() > 0) {
                    sITEM = etMenuItem.getText().toString().trim();
                    sPRICE = etMenuPrice.getText().toString().trim();
                    if (etMenuPiece.getText().toString().trim().length() > 0) {
                        sPIECE = etMenuPiece.getText().toString().trim();
                    } else {
                        sPIECE = "0";
                    }
                    sCATEGORY = spinnerCat.getSelectedItem().toString();
                    sTYPE = spinnerType.getSelectedItem().toString();
                    if (sCATEGORY.equals("Category")) {
                        Toast.makeText(getApplicationContext(), "please choose Category!", Toast.LENGTH_SHORT).show();
                    } else if (sTYPE.equals("Type")) {
                        Toast.makeText(getApplicationContext(), "please choose Type!", Toast.LENGTH_SHORT).show();
                    } else {
                        itemlist.add(new RecyclerItemMenu(number, sITEM, sPRICE, sPIECE, sCATEGORY, sTYPE,"0"));
                        adapterMenu.notifyDataSetChanged();
                        new DoInBackgroundMenuAdd().execute(sITEM, sPRICE, sPIECE, sCATEGORY, sTYPE);
                        dialog_add.dismiss();
                    }
                } else
                    Toast.makeText(getApplicationContext(), "fill all deatails!", Toast.LENGTH_SHORT).show();
            }
        });

        butCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_add.dismiss();
            }
        });
        dialog_add.show();
    }

    //Getting MENU DETAILS OF RESTAURANT
    public class DoInBackgroundMenuGet extends AsyncTask<String, Void, String> {
        SpotsDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new SpotsDialog(MenuActivity.this, "Loading Menu...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            InputStream inputStream = null;
            try {
                String json_url = "http://yummykart.pe.hu/restaurant_menu_get.php";
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
            super.onPostExecute(result);
            dialog.dismiss();
            if (result != null) {
                if (result.equals("Unsuccessful")) {
                    Toast.makeText(MenuActivity.this, "error fetching data! please try again.", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        JSONArray jsonArray = jsonObject.getJSONArray("server response");

                        String sItem, sPrice, sPiece, sCategory, sType, sNumber, sBlock;
                        int count = 0;

                        itemlist.clear();
                        while (count < jsonArray.length()) {
                            JSONObject jo = jsonArray.getJSONObject(count);
                            sNumber = jo.getString("Number");
                            sItem = jo.getString("Item");
                            sPrice = jo.getString("Price");
                            sPiece = jo.getString("Piece");
                            sCategory = jo.getString("Category");
                            sType = jo.getString("Type");
                            sBlock = jo.getString("Block");

                            itemlist.add(new RecyclerItemMenu(number, sItem, sPrice, sPiece, sCategory, sType,sBlock));
                            count++;
                        }
                        adapterMenu = new MyAdapterMenu(itemlist, MenuActivity.this, coordinatorLayout);
                        recyclerView.setAdapter(adapterMenu);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            } else {
                final Snackbar snackbar = Snackbar.make(coordinatorLayout, "check your internet connection!", Snackbar.LENGTH_INDEFINITE);
                snackbar.setActionTextColor(Color.parseColor("#0097A7"));
                snackbar.setAction("REFRESH", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DoInBackgroundMenuGet().execute();
                        snackbar.dismiss();
                    }
                });
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(14);
                snackbar.show();
            }
        }
    }

    //ADDING MENU DETAILS TO DATABASE
    public class DoInBackgroundMenuAdd extends AsyncTask<String, Void, String> {
        SpotsDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new SpotsDialog(MenuActivity.this, "Adding Menu...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String sItem, sCategory, sPrice, sType, sPiece;
            sItem = params[0];
            sPrice = params[1];
            sPiece = params[2];
            sCategory = params[3];
            sType = params[4];
            InputStream inputStream = null;
            try {
                String json_url = "http://yummykart.pe.hu/restaurant_menu_add.php";
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("Number", "UTF-8") + "=" + URLEncoder.encode(number, "UTF-8")
                        + "&" + URLEncoder.encode("Item", "UTF-8") + "=" + URLEncoder.encode(sItem, "UTF-8")
                        + "&" + URLEncoder.encode("Price", "UTF-8") + "=" + URLEncoder.encode(sPrice, "UTF-8")
                        + "&" + URLEncoder.encode("Category", "UTF-8") + "=" + URLEncoder.encode(sCategory, "UTF-8")
                        + "&" + URLEncoder.encode("Type", "UTF-8") + "=" + URLEncoder.encode(sType, "UTF-8")
                        + "&" + URLEncoder.encode("Piece", "UTF-8") + "=" + URLEncoder.encode(sPiece, "UTF-8");
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
            dialog.dismiss();
            if (result != null) {
                if (result.equals("1")) {
                    Toast.makeText(MenuActivity.this, "menu added sucessfully!", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(MenuActivity.this, "error adding menu! please try again.", Toast.LENGTH_LONG).show();
            } else {
                final Snackbar snackbar = Snackbar.make(coordinatorLayout, "check your internet connection!", Snackbar.LENGTH_INDEFINITE);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                snackbar.setActionTextColor(Color.parseColor("#0097A7"));
                snackbar.setAction("REFRESH", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DoInBackgroundMenuGet().execute();
                        snackbar.dismiss();
                    }
                });
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(14);
                snackbar.show();
            }
        }
    }


    public class DoInBackgroundMenuUpdate extends AsyncTask<String, Void, String> {
        SpotsDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new SpotsDialog(MenuActivity.this, "Updating Menu...");
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String sItemPrev, sItem, sCategory, sPrice, sType, sPiece;
            sItemPrev = params[0];
            sItem = params[1];
            sPrice = params[2];
            sPiece = params[3];
            sCategory = params[4];
            sType = params[5];
            InputStream inputStream = null;
            try {
                String json_url = "http://yummykart.pe.hu/restaurant_menu_update.php";
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("PrevItem", "UTF-8") + "=" + URLEncoder.encode(sItemPrev, "UTF-8")
                        + "&" + URLEncoder.encode("Number", "UTF-8") + "=" + URLEncoder.encode(number, "UTF-8")
                        + "&" + URLEncoder.encode("Item", "UTF-8") + "=" + URLEncoder.encode(sItem, "UTF-8")
                        + "&" + URLEncoder.encode("Price", "UTF-8") + "=" + URLEncoder.encode(sPrice, "UTF-8")
                        + "&" + URLEncoder.encode("Category", "UTF-8") + "=" + URLEncoder.encode(sCategory, "UTF-8")
                        + "&" + URLEncoder.encode("Type", "UTF-8") + "=" + URLEncoder.encode(sType, "UTF-8")
                        + "&" + URLEncoder.encode("Piece", "UTF-8") + "=" + URLEncoder.encode(sPiece, "UTF-8");
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
            // dialog.dismiss();
            if (result != null) {
                if (result.equals("1")) {
                    Toast.makeText(MenuActivity.this, "menu updated sucessfully!", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(MenuActivity.this, "error updating menu! please try again.", Toast.LENGTH_LONG).show();
            } else {
                final Snackbar snackbar = Snackbar.make(coordinatorLayout, "check your internet connection!", Snackbar.LENGTH_INDEFINITE);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                snackbar.setActionTextColor(Color.parseColor("#0097A7"));
                snackbar.setAction("REFRESH", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DoInBackgroundMenuGet().execute();
                        snackbar.dismiss();
                    }
                });
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(14);
                snackbar.show();
            }
        }
    }


    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private ClickListener clickListener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    //DELETING MENU FROM DATABASE
    private class DoInBackgroundMenuDelete extends AsyncTask<String, Void, String> {
        // SpotsDialog dialog;

        @Override
        protected void onPreExecute() {
            // dialog = new SpotsDialog(getApplicationContext(), "Deleting menu...");
            // dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String sNumber, sItem;
            sNumber = params[0];
            sItem = params[1];
            InputStream inputStream = null;
            try {
                String json_url = "http://yummykart.pe.hu/restaurant_menu_delete.php";
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("Number", "UTF-8") + "=" + URLEncoder.encode(sNumber, "UTF-8")
                        + "&" + URLEncoder.encode("Item", "UTF-8") + "=" + URLEncoder.encode(sItem, "UTF-8");
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
        protected void onPostExecute(String s) {
            //  dialog.dismiss();
            if (s != null) {
                if (s.equals("item deleted successfully!")) {
                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            } else {
                final Snackbar snackbar = Snackbar.make(coordinatorLayout, "check your internet connection!", Snackbar.LENGTH_INDEFINITE);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                snackbar.setActionTextColor(Color.parseColor("#0097A7"));
                snackbar.setAction("REFRESH", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DoInBackgroundMenuGet().execute();
                        snackbar.dismiss();
                    }
                });
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(14);
                snackbar.show();
            }
        }

    }


    private class DoInBackgroundMenuBlock extends AsyncTask<String, Void, String> {
        // SpotsDialog dialog;
        String sNumber, sItem, sBlock,sPrice,sType,sPiece,sCategory;
        int sPosition;

        @Override
        protected void onPreExecute() {
            // dialog = new SpotsDialog(getApplicationContext(), "Deleting menu...");
            // dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            sNumber = params[0];
            sBlock = params[1];
            sItem = params[2];
            sPrice = params[3];
            sType = params[4];
            sPosition = Integer.parseInt(params[5].trim());
            sPiece = params[6];
            sCategory = params[7];
            InputStream inputStream = null;
            try {
                String json_url = "http://yummykart.pe.hu/restaurant_menu_block.php";
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("Number", "UTF-8") + "=" + URLEncoder.encode(sNumber, "UTF-8")
                        + "&" + URLEncoder.encode("Item", "UTF-8") + "=" + URLEncoder.encode(sItem, "UTF-8")
                        + "&" + URLEncoder.encode("Block", "UTF-8") + "=" + URLEncoder.encode(sBlock, "UTF-8");
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
        protected void onPostExecute(String s) {
            //  dialog.dismiss();
            if (s != null) {
                if (s.equals("1")) {
                    if(sBlock.equals("1")) {
                        Toast.makeText(getApplicationContext(), "item blocked successfully!", Toast.LENGTH_SHORT).show();
                       // new DoInBackgroundMenuGet().execute();
                        itemlist.set(sPosition,new RecyclerItemMenu(number,sItem,sPrice,sPiece,sCategory,sType,sBlock));
                        adapterMenu.notifyDataSetChanged();
                    }else{
                        Toast.makeText(getApplicationContext(), "item unblocked successfully!", Toast.LENGTH_SHORT).show();
                       // new DoInBackgroundMenuGet().execute();
                        itemlist.set(sPosition,new RecyclerItemMenu(number,sItem,sPrice,sPiece,sCategory,sType,sBlock));
                        adapterMenu.notifyDataSetChanged();
                    }
                } else {
                    if(sBlock.equals("1")) {
                        Toast.makeText(getApplicationContext(), "item not blocked! please try again", Toast.LENGTH_SHORT).show();
                       // new DoInBackgroundMenuGet().execute();
                        itemlist.set(sPosition,new RecyclerItemMenu(number,sItem,sPrice,sPiece,sCategory,sType,sBlock));
                        adapterMenu.notifyDataSetChanged();
                    }else{
                        Toast.makeText(getApplicationContext(), "item not unblocked! please try again", Toast.LENGTH_SHORT).show();
                      //  new DoInBackgroundMenuGet().execute();
                        itemlist.set(sPosition,new RecyclerItemMenu(number,sItem,sPrice,sPiece,sCategory,sType,sBlock));
                        adapterMenu.notifyDataSetChanged();
                    }
                }
            } else {
                final Snackbar snackbar = Snackbar.make(coordinatorLayout, "check your internet connection!", Snackbar.LENGTH_INDEFINITE);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                snackbar.setActionTextColor(Color.parseColor("#0097A7"));
                snackbar.setAction("REFRESH", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DoInBackgroundMenuGet().execute();
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
