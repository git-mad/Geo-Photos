package com.gitmad.geophotos;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Brian on 1/27/2015.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "GeoPhotoDatabase";
    private static final int DB_VERSION = 1;

    /*
        SQL strings
     */

    //Table Names//
    public static final String TABLE_PHOTOS = "photosTable";
    public static final String TABLE_ALBUMS = "albumsTable";


    //** Column Names **//
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NOTES = "notes";
    public static final String COLUMN_TIME_TAKEN = "timeTaken";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_ALBUM_ID = "albumid";

    public static final String[] COLUMNS_PHOTOS_TABLE = {
            COLUMN_ID, COLUMN_ALBUM_ID, COLUMN_IMAGE, COLUMN_LATITUDE, COLUMN_LONGITUDE,
            COLUMN_NOTES, COLUMN_TIME_TAKEN
    };

    public static final String[] COLUMNS_ALBUMS_TABLE = {
            COLUMN_ID, COLUMN_TITLE, COLUMN_NOTES
    };

    private static final String CREATE_PHOTOS_TABLE_SQL = "CREATE TABLE IF NOT EXISTS "+TABLE_PHOTOS+"( "+
            COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            COLUMN_LONGITUDE+" REAL NOT NULL, "+
            COLUMN_LATITUDE+" REAL NOT NULL, "+
            COLUMN_NOTES+" TEXT NOT NULL, "+
            COLUMN_TIME_TAKEN+" INTEGER NOT NULL, "+
            COLUMN_IMAGE+" BLOB NOT NULL, "+
            COLUMN_ALBUM_ID+" INTEGER NOT NULL, "+
            "FOREIGN KEY("+COLUMN_ALBUM_ID+") REFERENCES "+TABLE_ALBUMS+"("+COLUMN_ID+") );";

    private static final String CREATE_ALBUMS_TABLE_SQL = "CREATE TABLE IF NOT EXISTS "+TABLE_ALBUMS+"( "+
            COLUMN_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            COLUMN_NOTES+" TEXT NOT NULL, "+
            COLUMN_TITLE+" TEXT NOT NULL, "+
            COLUMN_ALBUM_ID+" INTEGER NOT NULL);";

    public MySQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ALBUMS_TABLE_SQL);
        db.execSQL(CREATE_PHOTOS_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
