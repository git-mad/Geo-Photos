package com.gitmad.geophotos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by andre on 2/22/15.
 */
public class LocationDataSource {

    private SQLiteDatabase database;
    private DatabaseHelper databaseHelper;
    private String[] columns = {
            DatabaseHelper.Location_ID,
            DatabaseHelper.Location_Name,
            DatabaseHelper.Location_Description,
            DatabaseHelper.Location_Longitude,
            DatabaseHelper.Location_Latitude,
    };

    private static LocationDataSource mLocationDataSource = null;

    public static LocationDataSource getInstance(Context context) {
        if (mLocationDataSource == null) {
            mLocationDataSource = new LocationDataSource(context);
        }
        return mLocationDataSource;
    }

    private LocationDataSource(Context context) {
        databaseHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = databaseHelper.getWritableDatabase();
    }

    public void close() {
        databaseHelper.close();
    }

    public LocationModel insertLocation(LocationModel locationModel)
    {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.Location_Name, locationModel.getName());
        cv.put(DatabaseHelper.Location_Description, locationModel.getDescription());
        cv.put(DatabaseHelper.Location_Latitude, locationModel.getLatitude());
        cv.put(DatabaseHelper.Location_Longitude, locationModel.getLongitude());

        long id = database.insert(DatabaseHelper.TABLE_LOCATION, null, cv);

        Cursor cursor = database.query(DatabaseHelper.TABLE_LOCATION,
                columns, DatabaseHelper.Location_ID + " = " + id, null,
                null, null, null);
        cursor.moveToFirst();
        LocationModel loc = cursorToLocationModel(cursor);
        Log.d("insertLocationModel", "id: " + loc.get_id());
        Log.d("insertLocationModel", "Long: " + loc.getLongitude());
        Log.d("insertLocationModel", "Lat: " + loc.getLatitude());

        cursor.close();
        return loc;
    }

    public ArrayList<LocationModel> getLocationList()
    {
        ArrayList<LocationModel> locations = new ArrayList<LocationModel>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_LOCATION,
                columns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            LocationModel loc = cursorToLocationModel(cursor);
            locations.add(loc);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return locations;
    }

    public LocationModel getLocation(long location_id) {

        Cursor cursor = database.query(DatabaseHelper.TABLE_LOCATION,
                new String[]{ DatabaseHelper.Location_ID, DatabaseHelper.Location_Longitude, DatabaseHelper.Location_Latitude },
                DatabaseHelper.Location_ID + " = " + location_id,
                null, null, null, null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            LocationModel location = new LocationModel();
            location.set_id(cursor.getLong(0));
            location.setLongitude(cursor.getDouble(1));
            location.setLatitude(cursor.getDouble(2));
            cursor.close();
            return location;
        }

        return null;
    }

    public LocationModel cursorToLocationModel(Cursor cursor)
    {
        LocationModel locationModel = new LocationModel();
        locationModel.set_id(cursor.getLong(0));
        locationModel.setName(cursor.getString(1));
        locationModel.setDescription(cursor.getString(2));
        locationModel.setLongitude(cursor.getDouble(3));
        locationModel.setLatitude(cursor.getDouble(4));
        return locationModel;
    }

    public void deleteLocation(LocationModel locationModel)
    {
        long id = locationModel.get_id();
        database.delete(DatabaseHelper.TABLE_LOCATION,DatabaseHelper.Location_ID + " = " + id,null);
    }
}
