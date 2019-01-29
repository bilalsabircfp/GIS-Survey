package com.example.dzone.gis.Database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.dzone.gis.Navgation.Home;

/**
 * Created by Belal on 1/27/2017.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    //Constants for Database name, table name, and column names

    public static final String DB_NAME = "NamesDB";
    public static final String TABLE_NAME = "survey";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "s_name";
    public static final String COLUMN_FOOD = "t_food";
    public static final String COLUMN_AREA = "area";
    public static final String COLUMN_SHOP = "shop";
    public static final String COLUMN_OWNER = "owner";
    public static final String COLUMN_NIC = "nic";
    public static final String COLUMN_NUM = "num";
    public static final String COLUMN_NUM_WORKER = "num_worker";
    public static final String COLUMN_ALL = "all_food";
    public static final String COLUMN_REMARKS = "remarks";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LON = "lon";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_UID = "uid";
    public static final String COLUMN_IMAGE_PATH = "img";

    public static final String[] COL_NAME = {"s_name","t_food","area","shop","owner","nic","num","num_worker"
    ,"all_food","remarks","lat","lon","status","date","img"};

    //database version
    private static final int DB_VERSION = 1;

    //Constructor
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //creating the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME
                + "(" + COLUMN_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME +
                " VARCHAR, " + COLUMN_FOOD +
        " VARCHAR, " + COLUMN_AREA + " VARCHAR, " + COLUMN_SHOP+ " VARCHAR, "
                + COLUMN_OWNER + " VARCHAR, " + COLUMN_NIC + " VARCHAR, " + COLUMN_NUM + " VARCHAR, "
                + COLUMN_NUM_WORKER + " VARCHAR, " + COLUMN_ALL + " VARCHAR, "
                + COLUMN_REMARKS + " VARCHAR, " + COLUMN_LAT + " VARCHAR, "
                + COLUMN_LON + " VARCHAR, " + COLUMN_STATUS + " INTEGER," + COLUMN_IMAGE_PATH + " VARCHAR,"
                + COLUMN_DATE + " VARCHAR, " + COLUMN_UID + " INTEGER);";
        db.execSQL(sql);
    }

    //upgrading the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        String sql = "DROP TABLE IF EXISTS Persons";
//        db.execSQL(sql);
//        onCreate(db);
    }

    /*
    * This method is taking two arguments
    * first one is the name that is to be saved
    * second one is the status
    * 0 means the name is synced with the server
    * 1 means the name is not synced with the server
    * */
    public boolean addName(String area, String t_food, String name, String shop,
                           String owner, String nic, String num, String lat,
                           String lon, String num_workers, String all, String remarks,
                           int status, String img_path, String date, int uid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_AREA, area);
        contentValues.put(COLUMN_FOOD, t_food);
        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_SHOP, shop);
        contentValues.put(COLUMN_OWNER, owner);
        contentValues.put(COLUMN_NIC, nic);
        contentValues.put(COLUMN_NUM, num);
        contentValues.put(COLUMN_LAT, lat);
        contentValues.put(COLUMN_LON, lon);
        contentValues.put(COLUMN_NUM_WORKER, num_workers);
        contentValues.put(COLUMN_ALL, all);
        contentValues.put(COLUMN_REMARKS, remarks);
        contentValues.put(COLUMN_STATUS, status);
        contentValues.put(COLUMN_IMAGE_PATH, img_path);
        contentValues.put(COLUMN_DATE, date);
        contentValues.put(COLUMN_UID, uid);



        db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    /*
    * This method taking two arguments
    * first one is the id of the name for which
    * we have to update the sync status
    * and the second one is the status that will be changed
    * */
    public boolean updateNameStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_STATUS, status);
        db.update(TABLE_NAME, contentValues, COLUMN_ID + "=" + id, null);
        db.close();
        return true;
    }

    /*
    * this method will give us all the name stored in sqlite
    * */
    public Cursor getNames() {

        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME +" ;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }


    public Cursor getNamesbyStatus(int id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM names where id = " + id, null);

        return c;
    }

    /*
    * this method is for getting all the unsynced name
    * so that we can sync it with database
    * */


//    String[] name;
    public Cursor getUnsyncedNames(int uid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_UID + " = "+uid+" AND "+COLUMN_STATUS+" = 0;";
//        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME + " = ?;";


        Cursor c = db.rawQuery(sql, null);
        return c;
    }
}
