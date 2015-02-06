package com.gitmad.geophotos;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.gitmad.geophotos.Models.Photo;

import java.util.ArrayList;

import static com.gitmad.geophotos.MySQLiteHelper.*;

/**
 * Created by Brian on 1/31/2015.
 */
public class ReadPhotosFromDatabaseTask extends AsyncTask<Long, Photo[], Photo[]> {

    private Context context;
    private PhotoReadListener listener;


    private MySQLiteHelper helper;
    private SQLiteDatabase db;

    public ReadPhotosFromDatabaseTask(Context context, PhotoReadListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected Photo[] doInBackground(Long... params) {
        helper = new MySQLiteHelper(context);
        db = helper.getReadableDatabase();
        //these are the columns that we will want from the database//
        String[] columns = {COLUMN_ID, COLUMN_ALBUM_ID, COLUMN_NOTES, COLUMN_LONGITUDE,
                COLUMN_LATITUDE, COLUMN_TIME_TAKEN};

        //declare array to hold cursors for each album//
        Cursor[] cursors = new Cursor[params.length];

        //If no album ids were provided, get all Photos//
        if (params.length == 0) {
            cursors = new Cursor[1];
            cursors[0] = db.query(TABLE_PHOTOS, columns, null, null, null, null,
                    COLUMN_TIME_TAKEN + " DESC");
        } else {
            //read photos from different albums into their own cursors//
            for (int idIndex = 0; idIndex < params.length; idIndex++) {
                cursors[idIndex] =  db.query(TABLE_PHOTOS, columns,
                        COLUMN_ALBUM_ID + " = " + params[idIndex], null, null, null,
                        COLUMN_TIME_TAKEN + " DESC");
            }
        }


        //arrayList that holds all photos//
        ArrayList<Photo> allPhotos = new ArrayList<>();

        for (int cursorIndex = 0; cursorIndex < cursors.length; cursorIndex++) {
            Cursor crsr = cursors[cursorIndex];

            //array to hold this album's photos//
            Photo[] photos = new Photo[crsr.getCount()];

            crsr.moveToFirst();

            for (int i = 0; !crsr.isAfterLast(); i++) {
                photos[i] = new Photo(crsr.getLong(crsr.getColumnIndex(COLUMN_ID)),
                        crsr.getDouble(crsr.getColumnIndex(COLUMN_LONGITUDE)),
                        crsr.getDouble(crsr.getColumnIndex(COLUMN_LATITUDE)),
                        crsr.getString(crsr.getColumnIndex(COLUMN_NOTES)),
                        crsr.getLong(crsr.getColumnIndex(COLUMN_TIME_TAKEN)),
                        crsr.getLong(crsr.getColumnIndex(COLUMN_ALBUM_ID)));

                //also add to arrayList//
                allPhotos.add(photos[i]);

                crsr.moveToNext();
            }

            crsr.close();
            //publish progress produces album//
            publishProgress(photos);
        }

        //return as array//
        Photo[] allPhotosArray = new Photo[allPhotos.size()];
        return allPhotos.toArray(allPhotosArray);
    }

    @Override
    protected void onProgressUpdate(Photo[]... values) {
        super.onProgressUpdate(values);
        //pass back to listener//
        listener.readAlbum(values[0]);
    }

    @Override
    protected void onPostExecute(Photo[] photos) {
        super.onPostExecute(photos);

        //close out resources//
        db.close();
        helper.close();

        //pass back to listener//
        listener.readAll(photos);
    }

    public interface PhotoReadListener {
        public void readAlbum(Photo[] album);
        public void readAll(Photo[] photos);
    }
}
