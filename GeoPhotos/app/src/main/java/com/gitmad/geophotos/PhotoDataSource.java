package com.gitmad.geophotos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by andre on 2/22/15.
 */
public class PhotoDataSource {
    private SQLiteDatabase database;
    private DatabaseHelper databaseHelper;
    private String[] columns = {
            DatabaseHelper.Photo_ID,
            DatabaseHelper.Photo_Filepath,
            DatabaseHelper.Photo_LocationId,
            DatabaseHelper.Photo_Data,
    };

    public PhotoDataSource(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = databaseHelper.getWritableDatabase();
    }

    public void close() {
        databaseHelper.close();
    }

    public PhotoModel insertPhoto(PhotoModel photoModel)
    {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.Photo_Filepath, photoModel.getFilepath());
        cv.put(DatabaseHelper.Photo_LocationId, photoModel.getLocation_ID());
        cv.put(DatabaseHelper.Photo_Data, photoModel.getData());

        long id = database.insert(DatabaseHelper.TABLE_PHOTO, null, cv);

        Cursor cursor = database.query(DatabaseHelper.TABLE_PHOTO,
                columns, DatabaseHelper.Photo_ID + " = " + id, null,
                null, null, null);
        cursor.moveToFirst();
        PhotoModel photo = cursorToPhotoModel(cursor);

        //Confirm that we were able to write to the database correctly.
        Log.d("insertPhoto", "Id: " + photo.get_id());
        Log.d("insertPhoto", "Path: " + photo.getFilepath());
        Log.d("insertPhoto", "LocationId: " + photo.getLocation_ID());

        cursor.close();
        return photo;
    }

    public ArrayList<PhotoModel> getPhotoList()
    {
        ArrayList<PhotoModel> pictures = new ArrayList<PhotoModel>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_LOCATION,
                columns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            PhotoModel loc = cursorToPhotoModel(cursor);
            pictures.add(loc);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return pictures;
    }

    public PhotoModel cursorToPhotoModel(Cursor cursor)
    {
        PhotoModel photoModel = new PhotoModel();
        photoModel.set_id(cursor.getLong(0));
        photoModel.setFilepath(cursor.getString(1));
        photoModel.setLocation_ID(cursor.getLong(2));
        photoModel.setData(cursor.getBlob(3));

        return photoModel;
    }

    public void deletePhoto(LocationModel photoModel)
    {
        long id = photoModel.get_id();
        database.delete(DatabaseHelper.TABLE_PHOTO,DatabaseHelper.Photo_ID + " = " + id,null);
    }
}
