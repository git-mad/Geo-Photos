package com.gitmad.geophotos.Fragments;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.gitmad.geophotos.Models.Photo;
import com.gitmad.geophotos.R;

import com.gitmad.geophotos.Fragments.dummy.DummyContent;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.model.LatLng;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link com.gitmad.geophotos.Fragments.PhotoListFragment.OnPhotoChoiceListener}
 * interface.
 */
public class PhotoListFragment extends Fragment implements AbsListView.OnItemClickListener {

    private static final String ARG_PHOTOS = "phtots key";
    private static final String ARG_LOCATION = "locationkey";

    private OnPhotoChoiceListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    private Photo[] photos;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param photos photos to be placed in list.
     * @return A new instance of fragment PhotoInfoFragment.
     */
    public static PhotoListFragment newInstance(Photo[] photos) {
        PhotoListFragment fragment = new PhotoListFragment();
        Bundle args = new Bundle();
        args.putParcelableArray(ARG_PHOTOS, photos);
        fragment.setArguments(args);
        return fragment;
    }

    public static PhotoListFragment newInstance(Photo[] photos, LatLng location) {
        PhotoListFragment fragment = new PhotoListFragment();
        Bundle args = new Bundle();
        args.putParcelableArray(ARG_PHOTOS, photos);
        args.putParcelable(ARG_LOCATION, location);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PhotoListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get photos from arguments//
        photos = Photo.toPhotoArray(getArguments().getParcelableArray(ARG_PHOTOS));


        //get location from args//
        final LatLng location = (LatLng) getArguments().getParcelable(ARG_LOCATION);

        //append how far away the photo was taken if location provided//
        if (location != null) {
            mAdapter = new ArrayAdapter<Photo>(getActivity(),
                    android.R.layout.simple_list_item_1, android.R.id.text1, photos) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView tv = (TextView) view.findViewById(android.R.id.text1);

                    //append distance text//
                    Photo curr = photos[position];
                    float[] res = new float[1];
                    Location.distanceBetween(curr.getLatitude(),
                            curr.getLongitude(), location.latitude, location.longitude, res);
                    tv.setText(tv.getText() + String.format("%1d", res[0]));
                    return view;
                }
            };
        } else {
            mAdapter = new ArrayAdapter<Photo>(getActivity(),
                    android.R.layout.simple_list_item_1, android.R.id.text1, photos);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_list, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        //set text if there aren't any elements to display//
        if (photos.length == 0) {
            Log.d("lack of empty text", "setEmptyText() calling");
            setEmptyText("Take some Photos!");
        }

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnPhotoChoiceListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPhotoChoiceListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onPhotoChosen(photos[position]);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        Log.d("lack of empty text", "called SetEmptyText()");
        if (emptyView instanceof TextView) {
            Log.d("lack of empty text", "SetEmptyText() instanceof true");
            ((TextView) emptyView).setText(emptyText);
        } else {
            Log.d("lack of empty text", "calledSetEmptyText() instanceof false");
            TextView tv = new TextView(getActivity());
            tv.setText(emptyText);
            mListView.setEmptyView(tv);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnPhotoChoiceListener {
        public void onPhotoChosen(Photo photo);
    }

}
