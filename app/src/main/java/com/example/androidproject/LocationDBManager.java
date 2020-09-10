package com.example.androidproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class LocationDBManager extends SQLiteOpenHelper {
    static final String LOCATION_DB = "Location.db";
    static final String LOCATION_TABLE = "location";
    Context context = null;
    private static LocationDBManager dbManager = null;

    static final String CREATE_DB = " CREATE TABLE " + LOCATION_TABLE +
            " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "date TEXT NOT NULL," +
            " time TEXT NOT NULL, " +
            "latitude DOUBLE, " +
            "longitude DOUBLE); ";

    public LocationDBManager(Context context, String dbName, SQLiteDatabase.CursorFactory factory, int version){
        super(context, dbName, factory, version);
        this.context = context;
    }

    public static  LocationDBManager getInstance(Context context){
        if(dbManager == null){
            dbManager = new LocationDBManager(context, LOCATION_DB, null, 1);
        }
        return  dbManager;
    }

    @Override
    public void onOpen(SQLiteDatabase db){
        super.onOpen(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV){

    }

    public long insert(ContentValues addValue){
        return getWritableDatabase().insert(LOCATION_TABLE, null, addValue);
    }

    public Cursor query(String [] columns, String selection, String[] selectionArgs,
                        String groupBy, String having, String orderBy){
        return getReadableDatabase().query(LOCATION_TABLE, columns, selection,
                selectionArgs, groupBy, having, orderBy);
    }

    public int delete(String whereClause, String[] whereArgs){
        return getWritableDatabase().delete(LOCATION_TABLE, whereClause, whereArgs);
    }
}
