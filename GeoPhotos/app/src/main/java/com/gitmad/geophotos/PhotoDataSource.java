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

    private static PhotoDataSource mPhotoDataSource = null;

    public static PhotoDataSource getInstance(Context context) {
        if (mPhotoDataSource == null) {
            mPhotoDataSource = new PhotoDataSource(context);
        }
        return mPhotoDataSource;
    }

    private PhotoDataSource(Context context) {
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

        Cursor photoCursor = database.query(DatabaseHelper.TABLE_PHOTO,columns,DatabaseHelper.Photo_ID + " = " + id,null,null,null,null);

        Log.d("PhotoModel Cursor", "" + photoCursor.getColumnNames().length);
        Log.d("PhotoModel", "Inserted at ID: " + id);

        PhotoModel photoModel1 = new PhotoModel();

        if(photoCursor.moveToFirst())
        {
            photoModel1 = cursorToPhotoModel(photoCursor);
        }
        else
        {
            Log.d("PhotoDataSource", "cursor is NULL");
        }

        return photoModel1;
    }

    public ArrayList<PhotoModel> getPhotoList()
    {
        ArrayList<PhotoModel> pictures = new ArrayList<PhotoModel>();


        Cursor cursor = database.query(DatabaseHelper.TABLE_PHOTO,
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
