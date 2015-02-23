package com.gitmad.geophotos;

/**
 * Created by andre on 2/22/15.
 */
public class LocationModel {

    /*
    //Location Table Columns
    public static final String TABLE_LOCATION = "location";
    public static final String Location_ID = "_id";
    public static final String Location_Latitude = "latitude";
    public static final String Location_Longitude = "longitude";
    public static final String Location_Name = "name";
    public static final String Location_Description = "description";
     */

    private long _id;
    private double latitude;
    private double longitude;
    private String name;
    private String description;

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
