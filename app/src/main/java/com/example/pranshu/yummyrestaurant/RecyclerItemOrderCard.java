package com.example.pranshu.yummyrestaurant;

/**
 * Created by PRanshu on 13-08-2017.
 */

class RecyclerItemOrderCard {
    private String ordercarditem;
    private String ordercardqty;
    private String ordercardprice;


    public RecyclerItemOrderCard(String ordercarditem, String ordercardqty,String ordercardprice) {
        this.ordercarditem = ordercarditem;
        this.ordercardqty = ordercardqty;
        this.ordercardprice = ordercardprice;
    }

    public String getOrdercarditem() {
        return ordercarditem;
    }

    public void setOrdercarditem(String ordercarditem) {
        this.ordercarditem = ordercarditem;
    }

    public String getOrdercardqty() {
        return ordercardqty;
    }

    public void setOrdercardqty(String ordercardqty) {
        this.ordercardqty = ordercardqty;
    }

    public String getOrdercardprice() {
        return ordercardprice;
    }

    public void setOrdercardprice(String ordercardprice) {
        this.ordercardprice = ordercardprice;
    }
}
