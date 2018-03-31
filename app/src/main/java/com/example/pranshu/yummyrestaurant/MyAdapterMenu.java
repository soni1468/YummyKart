package com.example.pranshu.yummyrestaurant;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.List;

import dmax.dialog.SpotsDialog;

/**
 * Created by PRanshu on 13-06-2017.
 */

public class MyAdapterMenu extends RecyclerView.Adapter<MyAdapterMenu.MyViewHolderMenu> {

    private List<RecyclerItemMenu> listItems;
    private Context mContext;
    private int itemPosition;
    CoordinatorLayout coordinatorLayout;

    public MyAdapterMenu(List<RecyclerItemMenu> listItems, Context mContext, CoordinatorLayout coordinatorLayout) {
        this.listItems = listItems;
        this.mContext = mContext;
        this.coordinatorLayout = coordinatorLayout;
    }

    @Override
    public MyAdapterMenu.MyViewHolderMenu onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_menu, parent, false);
        return new MyViewHolderMenu(v);
    }

    @Override
    public void onBindViewHolder(final MyAdapterMenu.MyViewHolderMenu holder, int position) {
        final RecyclerItemMenu itemList = listItems.get(position);

        if(itemList.getMenuBlock().equals("1")){
            holder.tvItem.setTextColor(Color.parseColor("#FF1744"));
            holder.tvPrice.setTextColor(Color.parseColor("#FF1744"));
            holder.tvType.setTextColor(Color.parseColor("#FF1744"));
        }else{
            holder.tvItem.setTextColor(Color.parseColor("#757575"));
            holder.tvPrice.setTextColor(Color.parseColor("#757575"));
            holder.tvType.setTextColor(Color.parseColor("#757575"));
        }
        holder.tvItem.setText(itemList.getMenuItem());
        holder.tvPrice.setText("₹" + itemList.getMenuPrice());
        holder.tvType.setText(itemList.getMenuType());
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class MyViewHolderMenu extends RecyclerView.ViewHolder {

        TextView tvItem;
        TextView tvPrice;
        TextView tvType;
     //   Button butMenuDel;

        public MyViewHolderMenu(View itemView) {
            super(itemView);
            tvItem = (TextView) itemView.findViewById(R.id.textMenuItem);
            tvPrice = (TextView) itemView.findViewById(R.id.textMenuPrice);
            tvType = (TextView) itemView.findViewById(R.id.textMenuType);
         //   butMenuDel = (Button) itemView.findViewById(R.id.buttonDeleteMenu);
        }
    }


}
