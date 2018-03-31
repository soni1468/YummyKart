package com.example.pranshu.yummyrestaurant;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by PRanshu on 13-06-2017.
 */

public class MyAdapterOrderListView extends RecyclerView.Adapter<MyAdapterOrderListView.MyViewHolderCard> {
 /*   private List<RecyclerItemOrderCard> itemlist;
    private Context mContext;
    private int resourceid;
    private LayoutInflater inflater;

    public MyAdapterOrderListView(Context mContext, int resourceid, List<RecyclerItemOrderCard> itemlist) {
        super(mContext, resourceid, itemlist);
        this.itemlist = itemlist;
        this.resourceid = resourceid;
        this.mContext = mContext;
        inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        RecyclerItemOrderCard item = itemlist.get(position);
        convertView = inflater.inflate(resourceid, null);

        if (item != null) {
            TextView tvItem, tvQty, tvPrice;
            tvItem = (TextView) convertView.findViewById(R.id.textCardItem);
            tvQty = (TextView) convertView.findViewById(R.id.textCardQuantity);
            tvPrice = (TextView) convertView.findViewById(R.id.textCardPrice);

            if (tvItem != null) {
                tvItem.setText(item.getOrdercarditem());
            }
            if (tvQty != null) {
                tvQty.setText(item.getOrdercardqty());
            }
            if (tvPrice != null) {
                tvPrice.setText(item.getOrdercardprice());
            }
        }

        return convertView;

    }   */

    private List<RecyclerItemOrderCard> listItems;
    private Context mContext;

    public MyAdapterOrderListView(List<RecyclerItemOrderCard> listItems, Context mContext) {
        this.listItems = listItems;
        this.mContext = mContext;
       // Toast.makeText(mContext,"gandmarao",Toast.LENGTH_LONG).show();
    }

    @Override
    public MyAdapterOrderListView.MyViewHolderCard onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.menuitemcard_listviewitem, parent, false);
        return new MyViewHolderCard(v);
    }

    @Override
    public void onBindViewHolder(final MyAdapterOrderListView.MyViewHolderCard holder, int position) {
        final RecyclerItemOrderCard itemList = listItems.get(position);

        holder.tvItem.setText(itemList.getOrdercarditem());
        holder.tvQty.setText(itemList.getOrdercardqty());
        holder.tvPrice.setText("₹" + itemList.getOrdercardprice());
      //  holder.tvItem.setText("Chicken");
       // holder.tvQty.setText("2");
       // holder.tvPrice.setText("₹200");
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class MyViewHolderCard extends RecyclerView.ViewHolder {

        TextView tvItem;
        TextView tvPrice;
        TextView tvQty;

        public MyViewHolderCard(View itemView) {
            super(itemView);
            tvItem = (TextView) itemView.findViewById(R.id.textCardItem);
            tvPrice = (TextView) itemView.findViewById(R.id.textCardPrice);
            tvQty = (TextView) itemView.findViewById(R.id.textCardQuantity);
        }
    }


}
