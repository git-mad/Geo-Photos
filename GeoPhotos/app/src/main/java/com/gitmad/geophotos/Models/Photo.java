package com.gitmad.geophotos.Models;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import static com.gitmad.geophotos.MySQLiteHelper.*;

/**
 * Created by Brian on 1/27/2015.
 */
public class Photo implements Parcelable {

    private long id;

    private float longitude;
    private float latitude;
    private String notes;
    private long timeTaken;
    private Bitmap bitmap;
    private long albumId;

    public Photo(long id, float longitude, float latitude, String notes, long timeTaken,
                 Bitmap bitmap, long albumId) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.notes = notes;
        this.timeTaken = timeTaken;
        this.bitmap = bitmap;
        this.albumId = albumId;
    }

    public Photo(long id, float longitude, float latitude, String notes, long timeTaken) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.notes = notes;
        this.timeTaken = timeTaken;
    }

    public long getId() {
        return id;
    }

    public float getLongitude() {
        return longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    public long getAlbumId() {
        return albumId;
    }

    public Bitmap getBitmap(SQLiteDatabase db) {

        //read image data into memory only if it is requested//
        if (bitmap == null) {
            Cursor crsr = db.query(TABLE_PHOTOS, new String[]{COLUMN_IMAGE}, COLUMN_ID + " = " + id,
                    null, null, null, null);

            if (crsr.getCount() == 1) {
                byte[] bitmapBytes = crsr.getBlob(crsr.getColumnIndex(COLUMN_IMAGE));
                bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            } else {
                throw new InstantiationError("could not find image data for this record");
            }
        }

        return bitmap;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeFloat(longitude);
        dest.writeFloat(latitude);
        dest.writeString(notes);
        dest.writeLong(timeTaken);
        dest.writeParcelable(bitmap, flags);
        dest.writeLong(albumId);
    }

    /**
     * Creator for reading Object from Parcel
     */
    public static final Parcelable.Creator<Photo> CREATOR
            = new Parcelable.Creator<Photo>() {
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    /**
     * Constructor for Parcelable Creator
     * @param in parcel from which data is read
     */
    private Photo(Parcel in) {
        id = in.readLong();
        longitude = in.readFloat();
        latitude = in.readFloat();
        notes = in.readString();
        timeTaken = in.readLong();
        bitmap = (Bitmap) in.readParcelable(ClassLoader.getSystemClassLoader());
        albumId = in.readLong();
    }
}
