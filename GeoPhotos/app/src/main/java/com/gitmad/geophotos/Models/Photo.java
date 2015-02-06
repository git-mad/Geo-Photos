package com.gitmad.geophotos.Models;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;

import com.gitmad.geophotos.R;

import java.io.Serializable;
import java.util.Calendar;

import static com.gitmad.geophotos.MySQLiteHelper.*;

/**
 * Created by Brian on 1/27/2015.
 */
public class Photo implements Parcelable {

    private long id;

    private double longitude;
    private double latitude;
    private String notes;
    private long timeTaken;
    private Bitmap bitmap;
    private long albumId;

    public Photo(long id, double longitude, double latitude, String notes, long timeTaken,
                 Bitmap bitmap, long albumId) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.notes = notes;
        this.timeTaken = timeTaken;
        this.bitmap = bitmap;
        this.albumId = albumId;
    }

    public Photo(long id, double longitude, double latitude, String notes, long timeTaken) {
        this(id, longitude, latitude, notes, timeTaken, null, -1);
    }

    public Photo(long id, double longitude, double latitude, String notes, long timeTaken,
                 Bitmap bmp) {
        this(id, longitude, latitude, notes, timeTaken, bmp, -1);
    }

    public Photo(long id, double longitude, double latitude, String notes, long timeTaken,
                 long albumId) {
        this(id, longitude, latitude, notes, timeTaken, null, albumId);
    }

    public long getId() {
        return id;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
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

    public String getFormattedTime() {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getTimeTaken());
        return String.format("%2d:%2d%s %s %2d, %4d",
                cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE), cal.get(Calendar.AM_PM),
                cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.YEAR));
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
                crsr.moveToFirst();
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
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
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
        bitmap = in.readParcelable(ClassLoader.getSystemClassLoader());
        albumId = in.readLong();
    }

    @Override
    public boolean equals(Object other) {
        if ((other == null) || !(other instanceof Photo)) {
            return false;
        }

        Photo otherPhoto = (Photo) other;

        return otherPhoto.getId() == getId()
                && otherPhoto.getNotes().equals(getNotes())
                && otherPhoto.getLatitude() == getLatitude()
                && otherPhoto.getAlbumId() == getAlbumId()
                && otherPhoto.getLongitude() == getLongitude()
                && otherPhoto.getTimeTaken() == getTimeTaken();
                // bitmaps not compared because this operation would be costly,
                // and likely unnecessary //
    }

    @Override
    public int hashCode() {
        return (int) id;
    }

    @Override
    public String toString() {
        return getFormattedTime();
    }

    public static Photo[] toPhotoArray(Parcelable[] parcelables) {
        Photo[] photos = new Photo[parcelables.length];

        for (int i = 0; i < parcelables.length; i++) {
            photos[i] = (Photo) parcelables[i];
        }

        return photos;
    }
}
