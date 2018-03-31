package com.example.pranshu.yummyrestaurant;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

/**
 * Created by PRanshu on 12-05-2017.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<RecyclerItem> listItems;
    private Context mContext;
    private DatabaseHelper myDB;
    RecyclerItem itemList1;
    RelativeLayout relativeLayout;
    String sorderno;
    //private List<RecyclerItemOrderCard> ordercardlistview;
    //ListView listView;
    //RecyclerView cardRecycler;
    //private int flagforstatus;
    //MyAdapterOrderListView adapterOrderCard;
    // int tax;

    public MyAdapter(List<RecyclerItem> listItems, Context mContext, RelativeLayout relativeLayout) {
        this.listItems = listItems;
        this.mContext = mContext;
        this.relativeLayout = relativeLayout;
        // this.tax = Integer.parseInt(tax);
        //   ordercardlistview = new ArrayList<>();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_order, parent, false);
        myDB = new DatabaseHelper(mContext);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final RecyclerItem itemList = listItems.get(position);
        itemList1 = listItems.get(position);
        String sAmount, sCategory, sItem, sPricePerItem, sQuantity, sType;
        String sTotalItem = "", sTotalQuantity = "", sItemAmount = "";
        int count = 0, serial = 1, totamt = 0;
        try {


            JSONArray ja = new JSONArray(itemList.getOrders());
            while (count < ja.length()) {
                JSONObject jsonObject = ja.getJSONObject(count);
                sAmount = jsonObject.getString("amount");
                totamt = totamt + Integer.parseInt(sAmount);
                sItem = jsonObject.getString("item");
                sQuantity = jsonObject.getString("quantity");
                sTotalItem = sTotalItem + "• " + sItem + "\n";
                serial++;
                if (sItem.length() > 102) {
                    sTotalQuantity = sTotalQuantity + sQuantity + "\n" + "\n" + "\n" + "\n";
                    sItemAmount = sItemAmount + "₹" + sAmount + "\n" + "\n" + "\n" + "\n";
                } else if (sItem.length() >= 69 && sItem.length() <= 102) {
                    sTotalQuantity = sTotalQuantity + sQuantity + "\n" + "\n" + "\n";
                    sItemAmount = sItemAmount + "₹" + sAmount + "\n" + "\n" + "\n";
                } else if (sItem.length() >= 35 && sItem.length() <= 68) {
                    sTotalQuantity = sTotalQuantity + sQuantity + "\n" + "\n";
                    sItemAmount = sItemAmount + "₹" + sAmount + "\n" + "\n";
                } else if (sItem.length() >= 1 && sItem.length() <= 34) {
                    sTotalQuantity = sTotalQuantity + sQuantity + "\n";
                    sItemAmount = sItemAmount + "₹" + sAmount + "\n";
                }
                count++;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        double tax_amount = totamt * (Integer.parseInt(itemList.getTax()) * 0.01);
        double sub_total_amount = totamt*1.0 + tax_amount;
        double discount_amount = sub_total_amount * (Integer.parseInt(itemList.getDiscount()) * 0.01);
        double grand_total = sub_total_amount - discount_amount;

        holder.textOrderNo.setText("Order No. : " + itemList.getOrder_no());
        holder.textDateTime.setText(itemList.getDate() + "     " + itemList.getTime());
        holder.textDetails.setText(itemList.getName() + "\n" + itemList.getAddress1() + " , " + itemList.getAddress2() + "\n" + "Landmark - " + itemList.getLandmark());
        holder.buttonNumber.setText(itemList.getNum());
        holder.textItem.setText(sTotalItem);
        holder.textQuantity.setText(sTotalQuantity);
        holder.textAmount.setText(sItemAmount);
        holder.buttonDis.setText(itemList.getDiscount() + "%");
        holder.textSubTot.setText("· Subtotal  :               ₹" + totamt);

        if(itemList.getDiscount().equals("0")) {
            holder.textDisAmt.setVisibility(View.GONE);
        }else{
            holder.textDisAmt.setVisibility(View.VISIBLE);
            holder.textDisAmt.setText("· Discount  :              -  ₹" +String.format(Locale.ENGLISH,"%.2f",discount_amount));
        }

        if(itemList.getTax().equals("0")){
            holder.textGst.setVisibility(View.GONE);
        }else{
            holder.textGst.setVisibility(View.VISIBLE);
            holder.textGst.setText("· GST " + " (" + itemList.getTax() + "%)  :          + ₹" + String.format(Locale.ENGLISH,"%.2f",tax_amount));
        }

        holder.textTotalAmount.setText("· Grand Total  :    ₹" +String.format(Locale.ENGLISH,"%.2f",grand_total) );

        if (itemList.getOfd_flag().equals("1")) {
            holder.buttonOFD.setTextColor(Color.parseColor("#D50000"));
        } else {
            holder.buttonOFD.setTextColor(Color.parseColor("#000000"));
        }

      /*  holder.buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemList.getConfirm_flag().equals("0")) {
                    new DeliveryUpdation().execute(itemList.getOrder_no(), "1");
                  //  Toast.makeText(mContext, "Order No. " + itemList.getOrder_no() + " is Confirmed.", Toast.LENGTH_SHORT).show();
                    holder.buttonConfirm.setTextColor(Color.parseColor("#009688"));
                    itemList.setConfirm_flag("1");
                    itemList1.setConfirm_flag("1");
                    sorderno = itemList.getOrder_no();
                    // myDB.updateCofirmFlag(itemList.getOrder_no(), "1");
                }
            }
        });   */
        holder.buttonOFD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemList.getOfd_flag().equals("0")) {
                    new DeliveryUpdation().execute(itemList.getOrder_no(), "1");
                    holder.buttonOFD.setTextColor(Color.parseColor("#D50000"));
                    itemList.setOfd_flag("1");
                    itemList1.setOfd_flag("1");
                    sorderno = itemList.getOrder_no();
                }
            }
        });
        holder.buttonDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DeliveryUpdation().execute(itemList.getOrder_no(), "2");
                sorderno = itemList.getOrder_no();
                listItems.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());


            }
        });
        holder.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemList.getOfd_flag().equals("0")) {
                    final Dialog dialog = new Dialog(mContext);
                    dialog.setContentView(R.layout.dialog_confirm_layout);
                    Button butYES = (Button) dialog.findViewById(R.id.butConfirmYES);
                    Button butNO = (Button) dialog.findViewById(R.id.butConfirmNO);

                    butYES.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new DeliveryUpdation().execute(itemList.getOrder_no(), "3");
                            sorderno = itemList.getOrder_no();
                            listItems.remove(holder.getAdapterPosition());
                            notifyItemRemoved(holder.getAdapterPosition());
                            dialog.dismiss();
                        }
                    });
                    butNO.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();

                } else if (itemList.getOfd_flag().equals("1")) {
                    final Dialog dialog = new Dialog(mContext);
                    dialog.setContentView(R.layout.dialog_confirm_warning);
                    Button butOK = (Button) dialog.findViewById(R.id.butWarningOK);

                    butOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }

            }
        });
        holder.buttonNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + itemList.getNum()));
                mContext.startActivity(i);
            }
        });
      /*  holder.textMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, holder.textMenu);
                popupMenu.inflate(R.menu.menu_card);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menuCall:
                                Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + itemList.getNum()));
                                mContext.startActivity(i);
                                break;
                            case R.id.menuCancelOrder:
                                if (itemList.getConfirm_flag().equals("0")) {
                                    final Dialog dialog = new Dialog(mContext);
                                    dialog.setContentView(R.layout.dialog_confirm_layout);
                                    Button butYES = (Button) dialog.findViewById(R.id.butConfirmYES);
                                    Button butNO = (Button) dialog.findViewById(R.id.butConfirmNO);

                                    butYES.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            new DeliveryUpdation().execute(itemList.getOrder_no(), "4");
                                            sorderno = itemList.getOrder_no();
                                            //  myDB.deleteData(itemList.getOrder_no());
                                            listItems.remove(holder.getAdapterPosition());
                                            notifyItemRemoved(holder.getAdapterPosition());
                                            dialog.dismiss();
                                        }
                                    });
                                    butNO.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.show();

                                } else if (itemList.getOfd_flag().equals("1")) {
                                    final Dialog dialog = new Dialog(mContext);
                                    dialog.setContentView(R.layout.dialog_confirm_warning);
                                    Button butOK = (Button) dialog.findViewById(R.id.butWarningOK);

                                    butOK.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.show();
                                } else if (itemList.getConfirm_flag().equals("1")) {
                                    final Dialog dialog = new Dialog(mContext);
                                    dialog.setContentView(R.layout.dialog_confirm_ofd_layout);
                                    Button butOFDYES = (Button) dialog.findViewById(R.id.butConfirmOFDYES);
                                    Button butOFDNO = (Button) dialog.findViewById(R.id.butConfirmOFDNO);

                                    butOFDYES.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            new DeliveryUpdation().execute(itemList.getOrder_no(), "4");
                                            sorderno = itemList.getOrder_no();
                                            //   myDB.deleteData(itemList.getOrder_no());
                                            listItems.remove(holder.getAdapterPosition());
                                            notifyItemRemoved(holder.getAdapterPosition());
                                            dialog.dismiss();
                                        }
                                    });
                                    butOFDNO.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dialog.dismiss();
                                        }
                                    });
                                    dialog.show();
                                }
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });   */


    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView textOrderNo;
        public TextView textDateTime;
        public TextView textDetails;
        public TextView textItem;
        public TextView textQuantity;
        public TextView textAmount;
        public TextView textSubTot;
        public TextView textDisAmt;
        public TextView textGst;
        public TextView textTotalAmount;
        public Button buttonOFD;
        public Button buttonDelivered;
        public Button buttonCancel;
        public Button buttonNumber;
        public Button buttonDis;


        public MyViewHolder(View itemView) {
            super(itemView);
            textOrderNo = (TextView) itemView.findViewById(R.id.textOrderNo);
            textDateTime = (TextView) itemView.findViewById(R.id.textDateTime);
            textDetails = (TextView) itemView.findViewById(R.id.textCustDetails);
            textItem = (TextView) itemView.findViewById(R.id.textItem);
            textQuantity = (TextView) itemView.findViewById(R.id.textQuantity);
            textAmount = (TextView) itemView.findViewById(R.id.textAmount);
            textSubTot = (TextView) itemView.findViewById(R.id.textSubTotal);
            textDisAmt = (TextView) itemView.findViewById(R.id.textDiscountAmt);
            textGst = (TextView) itemView.findViewById(R.id.textGst);
            textTotalAmount = (TextView) itemView.findViewById(R.id.textTotalAmount);
            buttonOFD = (Button) itemView.findViewById(R.id.buttonOFD);
            buttonDelivered = (Button) itemView.findViewById(R.id.buttonDelivered);
            buttonCancel = (Button) itemView.findViewById(R.id.buttonCancel);
            buttonNumber = (Button) itemView.findViewById(R.id.buttonNumber);
            buttonDis = (Button) itemView.findViewById(R.id.butDiscount);

        }
    }


    public class DeliveryUpdation extends AsyncTask<String, Void, String> {
        String flag;
        SpotsDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new SpotsDialog(mContext, "updating order status...");
        }

        @Override
        protected String doInBackground(String[] params) {
            String numBER = params[0];
            flag = params[1];
            String json_URL = "http://yummykart.pe.hu/restaurant_status_update.php";
            InputStream inputStream = null;

            try {
                URL url = new URL(json_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("Order_Number", "UTF-8") + "=" + URLEncoder.encode(numBER, "UTF-8") + "&" + URLEncoder.encode("Flag", "UTF-8") + "=" + URLEncoder.encode(flag, "UTF-8");
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


            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            } catch (ProtocolException e1) {
                e1.printStackTrace();
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
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
            if (result != null) {
                if (result.equals("updated")) {
                    switch (flag) {
                        case "1":
                            //  Toast.makeText(mContext, "Order no. " + itemList1.getOrder_no() + " is out for delivery", Toast.LENGTH_SHORT).show();
                            myDB.updateOFDFlag(sorderno, "1");
                            Snackbar.make(relativeLayout, "order out for delivery.", Snackbar.LENGTH_SHORT).show();
                            break;
                        case "2":
                            myDB.deleteData(sorderno);
                            Snackbar.make(relativeLayout, "order delivered.", Snackbar.LENGTH_SHORT).show();
                            break;
                        case "3":
                            myDB.deleteData(sorderno);
                            Snackbar.make(relativeLayout, "order canceled.", Snackbar.LENGTH_SHORT).show();
                            break;
                    }
                } else
                    Snackbar.make(relativeLayout, "order status not updated! please try again.", Snackbar.LENGTH_SHORT).show();
                // Toast.makeText(mContext, "Status not updated !", Toast.LENGTH_SHORT).show();

            } else {
                final Snackbar snackbar = Snackbar.make(relativeLayout, "check your internet connection!", Snackbar.LENGTH_INDEFINITE);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                textView.setTextSize(13);
                snackbar.show();
            }
        }
    }

}
