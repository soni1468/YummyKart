package com.example.pranshu.yummyrestaurant;

/**
 * Created by PRanshu on 11-05-2017.
 */

public class RecyclerItem {
    private String order_no;
    private String num;
    private String pin;
    private String name;
    private String address1;
    private String address2;
    private String landmark;
    private String orders;
    private String amount;
    private String date;
    private String time;
    private String discount;
    private String ofd_flag;
    private String tax;


    public RecyclerItem(String order_no,String num,String pin,String name,String address1,String address2,String landmark, String orders, String amount,  String date, String time, String discount, String ofd_flag, String tax) {
        this.order_no = order_no;
        this.num = num;
        this.pin = pin;
        this.name = name;
        this.address1 = address1;
        this.address2 = address2;
        this.landmark = landmark;
        this.orders = orders;
        this.amount = amount;
        this.date = date;
        this.time = time;
        this.discount = discount;
        this.ofd_flag = ofd_flag;
        this.tax = tax;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getOrders() {
        return orders;
    }

    public void setOrders(String orders) {
        this.orders = orders;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getOfd_flag() {
        return ofd_flag;
    }

    public void setOfd_flag(String ofd_flag) {
        this.ofd_flag = ofd_flag;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }
}
