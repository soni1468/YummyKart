package com.example.pranshu.yummyrestaurant;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by PRanshu on 15-05-2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static String Db_Name = "OrdersDB.db";
    public static String Table_Name = "orders_table";
    public static String Col_Order_No = "Order_No";
    public static String Col_Cust_Mobile = "Customer_Mobile";
    public static String Col_Cust_Pin = "Customer_Pin";
    public static String Col_Cust_Name = "Customer_Name";
    public static String Col_Add_1 = "Address_1";
    public static String Col_Add_2 = "Address_2";
    public static String Col_Landmark = "Landmark";
    public static String Col_Order = "Orders";
    public static String Col_Amount = "Amount";
    public static String Col_Date = "Date";
    public static String Col_Time = "Time";
   // public static String Col_Confirm = "Confirm_Flag";
    public static String Col_OFD = "OFD_Flag";
    public static String Col_Discount = "Discount";
    public static String Col_Tax = "GST";

    public DatabaseHelper(Context context) {
        super(context, Db_Name, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + Table_Name + " (" + Col_Order_No + " VARCHAR(100)," + Col_Cust_Mobile + " VARCHAR(20)," + Col_Cust_Pin + " VARCHAR(30)," + Col_Cust_Name + " VARCHAR(60)," + Col_Add_1 + " VARCHAR(70)," + Col_Add_2 + " VARCHAR(70)," + Col_Landmark + " VARCHAR(60)," + Col_Order + " VARCHAR(300)," + Col_Amount + " VARCHAR(10)," + Col_Date + " VARCHAR(20)," + Col_Time + " VARCHAR(20)," + Col_Discount + " VARCHAR(10)," + Col_OFD + " VARCHAR(2) DEFAULT 0," + Col_Tax + " VARCHAR(10))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Table_Name);
        onCreate(db);
    }

    public void insertData(String order_no, String cust_mobile, String cust_pin, String cust_name, String add_1, String add_2, String landmark, String order, String amount, String date, String time, String discount, String tax) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_Order_No, order_no);
        contentValues.put(Col_Cust_Mobile, cust_mobile);
        contentValues.put(Col_Cust_Pin, cust_pin);
        contentValues.put(Col_Cust_Name, cust_name);
        contentValues.put(Col_Add_1, add_1);
        contentValues.put(Col_Add_2, add_2);
        contentValues.put(Col_Landmark, landmark);
        contentValues.put(Col_Order, order);
        contentValues.put(Col_Amount, amount);
        contentValues.put(Col_Date, date);
        contentValues.put(Col_Time, time);
        contentValues.put(Col_Discount, discount);
        contentValues.put(Col_Tax,tax);


        db.insert(Table_Name, null, contentValues);
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cResult = db.rawQuery("select * FROM " + Table_Name + " order by " + Col_Order_No + " desc", null);
        return cResult;
    }

    public void updateOFDFlag(String order_no, String flag) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Col_OFD, flag);
        db.update(Table_Name, contentValues, Col_Order_No + " = " + order_no, null);
    }


    public void deleteData(String order_no) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Table_Name, Col_Order_No + "= " + order_no, null);
    }
}

