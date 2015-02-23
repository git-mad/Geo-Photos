package com.gitmad.geophotos;

/**
 * Created by andre on 2/22/15.
 */
public class PhotoModel {
    //Photo Table Columns
    private long _id;
    private String filepath;
    private long location_ID;
    private byte[] data;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public long getLocation_ID() {
        return location_ID;
    }

    public void setLocation_ID(long location_ID) {
        this.location_ID = location_ID;
    }

    public String toString()
    {
        return "ID: " + _id + " Filepath: " + filepath;
    }

}
