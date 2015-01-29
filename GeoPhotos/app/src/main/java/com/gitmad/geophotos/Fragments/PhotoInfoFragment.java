package com.gitmad.geophotos.Fragments;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gitmad.geophotos.Models.Photo;
import com.gitmad.geophotos.MySQLiteHelper;
import com.gitmad.geophotos.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PhotoInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotoInfoFragment extends Fragment {

    private static final String ARG_PHOTO = "photokey";
    private static final String ARG_ALBUM_NAME = "albumKey";

    private Photo photo;
    private String albumName;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param photo photo whose data will be displayed
     * @param albumName name of the album of this photo.
     * @return A new instance of fragment PhotoInfoFragment.
     */
    public static PhotoInfoFragment newInstance(Photo photo, String albumName) {
        PhotoInfoFragment fragment = new PhotoInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PHOTO, photo);
        args.putString(ARG_ALBUM_NAME, albumName);
        fragment.setArguments(args);
        return fragment;
    }

    public static PhotoInfoFragment newInstance(Photo photo) {
        return newInstance(photo, null);
    }

    public PhotoInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            photo = getArguments().getParcelable(ARG_PHOTO);
            albumName = getArguments().getString(ARG_ALBUM_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo_info, container, false);

        //format time//
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(photo.getTimeTaken());
        String timeString = String.format("%2d:%2d%s %s %2d, %4d",
                cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE), cal.get(Calendar.AM_PM),
                cal.get(Calendar.MONTH), cal.get(Calendar.DATE), cal.get(Calendar.YEAR));

        ((TextView) rootView.findViewById(R.id.timeTextView)).setText("Taken " + timeString);


        //read time if necessary//
        MySQLiteHelper helper = new MySQLiteHelper(getActivity());
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor crsr = db.query(MySQLiteHelper.TABLE_ALBUMS, new String[]{MySQLiteHelper.COLUMN_TITLE},
                MySQLiteHelper.COLUMN_ID + " = " + photo.getAlbumId(), null, null, null, null);
        crsr.moveToFirst();
        if (!crsr.isAfterLast()) {
            albumName = crsr.getString(crsr.getColumnIndex(MySQLiteHelper.COLUMN_TITLE));
        }

        //could still be null, so check with ternary operation//
        ((TextView) rootView.findViewById(R.id.albumTextView)).setText("Album:"
                + albumName == null ? "" : albumName);

        //set notes//
        ((TextView) rootView.findViewById(R.id.notesTextView)).setText(photo.getNotes());


        /*
            MAP STUPH
         */

        //use FragmentManager to find fragment, rather than view hierarchy//
        MapFragment mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);


        //setup map how we choose//
        GoogleMap map = mapFrag.getMap();

        //use MarkerOptions to add Marker to map//
        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(photo.getLatitude(), photo.getLongitude()))
                .title("Photo taken here!");
        map.addMarker(markerOptions);

        return rootView;
    }
}
