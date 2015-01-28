package com.gitmad.geophotos.Models;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import static com.gitmad.geophotos.MySQLiteHelper.*;

/**
 * Created by Brian on 1/27/2015.
 */
public class Album {

    private long id;
    private String title;
    private String notes;
    private Photo[] photos;

    private Bitmap coverPhoto; //maybe?

    public Album(long id, String title, String notes) {
        this.id = id;
        this.title = title;
        this.notes = notes;
    }


    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getNotes() {
        return notes;
    }

    public Photo[] getPhotos(SQLiteDatabase db) {
        if (photos == null) {
            final String[] photoColumns = {COLUMN_ID, COLUMN_NOTES, COLUMN_TIME_TAKEN, COLUMN_TITLE,
                    COLUMN_LONGITUDE, COLUMN_LATITUDE};
            Cursor crsr = db.query(TABLE_PHOTOS, photoColumns, COLUMN_ALBUM_ID + " = " + id,
                    null, null, null, null);

            photos = new Photo[crsr.getCount()];

            crsr.moveToFirst();
            for (int i = 0; !crsr.isAfterLast(); i++) {
                photos[i] = new Photo(crsr.getInt(crsr.getColumnIndex(COLUMN_ID)),
                        crsr.getFloat(crsr.getColumnIndex(COLUMN_LONGITUDE)),
                        crsr.getFloat(crsr.getColumnIndex(COLUMN_LATITUDE)),
                        crsr.getString(crsr.getColumnIndex(COLUMN_NOTES)),
                        crsr.getLong(crsr.getColumnIndex(COLUMN_TIME_TAKEN)));
            }
        }

        return photos;
    }
}
