package com.example.pranshu.yummyrestaurant;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

/**
 * Created by PRanshu on 20-05-2017.
 */

public class MyAdapterHistory extends RecyclerView.Adapter<MyAdapterHistory.MyViewHolder> {

    private List<RecyclerItemHistory> listItem;
    private Context mContext;
    private Typeface typeface, typeface1;

    public MyAdapterHistory(List<RecyclerItemHistory> listItem, Context mContext, Typeface typeface, Typeface typeface1) {
        this.listItem = listItem;
        this.mContext = mContext;
        this.typeface = typeface;
        this.typeface1 = typeface1;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_history, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        RecyclerItemHistory itemList = listItem.get(position);

        String sItem, sQuantity, sItemAmount;
        String sTotItem = "", sTotQuantity = "", sTotItemAmount = "";
        int sSubTotal=0;
        try {
            int count = 0, serial = 1;
            JSONArray ja = new JSONArray(itemList.getHisOrder());
            while (count < ja.length()) {
                JSONObject jsonObject = ja.getJSONObject(count);
                sItemAmount = jsonObject.getString("amount");
                sItem = jsonObject.getString("item");
                sQuantity = jsonObject.getString("quantity");
                sTotItem = sTotItem + "• " + sItem + "\n";
                sSubTotal = sSubTotal + Integer.parseInt(sItemAmount);
                //serial++;
                if (sItem.length() > 100) {
                    sTotQuantity = sTotQuantity + sQuantity + "\n" + "\n" + "\n" + "\n";
                    sTotItemAmount = sTotItemAmount + "₹" + sItemAmount  + "\n" + "\n" + "\n" + "\n";
                } else if (sItem.length() >= 66 && sItem.length() <= 100) {
                    sTotQuantity = sTotQuantity + sQuantity + "\n" + "\n" + "\n";
                    sTotItemAmount = sTotItemAmount + "₹" + sItemAmount  + "\n" + "\n" + "\n";
                } else if (sItem.length() >= 31 && sItem.length() <= 65) {
                    sTotQuantity = sTotQuantity + sQuantity + "\n" + "\n";
                    sTotItemAmount = sTotItemAmount + "₹" + sItemAmount  + "\n" + "\n";
                } else if (sItem.length() >= 1 && sItem.length() <= 30) {
                    sTotQuantity = sTotQuantity + sQuantity + "\n";
                    sTotItemAmount = sTotItemAmount + "₹" + sItemAmount  + "\n";
                }
                count++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        double tax_amount = sSubTotal * (Integer.parseInt(itemList.getHisTax()) * 0.01);
        double sub_total_amount = sSubTotal*1.0 + tax_amount;
        double discount_amount = sub_total_amount * (Integer.parseInt(itemList.getHisDiscount()) * 0.01);
        double grand_total = sub_total_amount - discount_amount;


        holder.tvOrderNo.setText("Order No. :  " + itemList.getHisOrderNo());
        holder.tvDateTime.setText(itemList.getHisDate() + "     " + itemList.getHisTime());
        holder.tvItem.setText(sTotItem);
        holder.tvQuantity.setText(sTotQuantity);
        holder.tvItemAmount.setText(sTotItemAmount);

        holder.tvSubTotal.setText("· Subtotal  :               ₹"+sSubTotal);

        if(itemList.getHisTax().equals("0")){
            holder.tvTax.setVisibility(View.GONE);
        }else {
            holder.tvTax.setVisibility(View.VISIBLE);
            holder.tvTax.setText("· GST (" + itemList.getHisTax() + "%)  :           + ₹" + String.format(Locale.ENGLISH, "%.2f", tax_amount));
        }
        if(itemList.getHisDiscount().equals("0")){
            holder.tvDisAmt.setVisibility(View.GONE);
        }else {
            holder.tvDisAmt.setVisibility(View.VISIBLE);
            holder.tvDisAmt.setText("· Discount  :            - ₹" + String.format(Locale.ENGLISH, "%.2f", discount_amount));
        }
        holder.tvTotalAmount.setText("· Grand Total  :    ₹" + grand_total);
        holder.butDiscount.setText(itemList.getHisDiscount() + "%");
        holder.tvCustDetails.setText(itemList.getHisName() + "\n" + itemList.getHisAdd1() + " , " + itemList.getHisAdd2() + "\n" + itemList.getHisLandmark() + "\n" + itemList.getHisNum());
        if (itemList.getHisStatus().equals("2")) {
            holder.tvStatus.setText("Delivered!");
            holder.tvStatus.setTypeface(typeface1);
            holder.tvStatus.setTextColor(Color.parseColor("#009688"));
        } else if (itemList.getHisStatus().equals("3")) {
            holder.tvStatus.setText("Canceled!");
            holder.tvStatus.setTypeface(typeface1);
            holder.tvStatus.setTextColor(Color.parseColor("#FF7043"));
        }
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView tvOrderNo, tvDateTime, tvItem, tvQuantity, tvItemAmount,tvSubTotal, tvTax, tvDisAmt, tvTotalAmount, tvCustDetails, tvStatus;
        public Button butDiscount;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvOrderNo = (TextView) itemView.findViewById(R.id.textHisOrderNo);
            tvDateTime = (TextView) itemView.findViewById(R.id.textHisDateTime);
            tvItem = (TextView) itemView.findViewById(R.id.textHisItem);
            tvQuantity = (TextView) itemView.findViewById(R.id.textHisQuantity);
            tvItemAmount = (TextView) itemView.findViewById(R.id.textHisItemAmount);
            tvSubTotal = (TextView) itemView.findViewById(R.id.textHisSubTotal);
            tvTax = (TextView) itemView.findViewById(R.id.textHisTax);
            tvDisAmt = (TextView) itemView.findViewById(R.id.textHisDiscountAmount);
            tvTotalAmount = (TextView) itemView.findViewById(R.id.textHisTotalAmt);
            butDiscount = (Button) itemView.findViewById(R.id.butHisDiscount);
            tvCustDetails = (TextView) itemView.findViewById(R.id.textHisCustDetails);
            tvStatus = (TextView) itemView.findViewById(R.id.textHisStatus);
        }
    }
}
