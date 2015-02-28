package com.gitmad.geophotos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    //Photo Table Columns
    public static final String TABLE_PHOTO = "photo";
    public static final String Photo_ID = "_id";
    public static final String Photo_Filepath = "path";
    public static final String Photo_LocationId = "locationID";
    public static final String Photo_Data = "data";

    //Location Table Columns
    public static final String TABLE_LOCATION = "location";
    public static final String Location_ID = "_id";
    public static final String Location_Latitude = "latitude";
    public static final String Location_Longitude = "longitude";
    public static final String Location_Name = "name";
    public static final String Location_Description = "description";

    private static final String DATABASE_NAME = "geo-photos.db";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase database;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table if not exists "
            + TABLE_PHOTO + " ("
            + Photo_ID + " integer primary key autoincrement, "
            + Photo_Filepath + " text not null, "
            + Photo_LocationId + " text not null, "
            + Photo_Data + " blob not null"
            + ");";

    private static final String LOCATION_CREATE = "create table if not exists "
            + TABLE_LOCATION + " ("
            + Location_ID + " integer primary key autoincrement, "
            + Location_Latitude + " real, "
            + Location_Longitude + " real, "
            + Location_Name + " text, "
            + Location_Description + " text"
            + ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.d("DatabaseHelper", "onCreate");
        Log.d("DatabaseHelper", "DatabaseCreate query: " + DATABASE_CREATE);
        Log.d("DatabaseHelper", "LocationCreate query: " + LOCATION_CREATE);

        database.execSQL(DATABASE_CREATE);
        database.execSQL(LOCATION_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        onCreate(db);
    }

}