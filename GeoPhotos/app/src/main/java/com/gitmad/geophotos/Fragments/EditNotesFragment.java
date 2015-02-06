package com.gitmad.geophotos.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.gitmad.geophotos.Models.Photo;
import com.gitmad.geophotos.MySQLiteHelper;
import com.gitmad.geophotos.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditNotesFragment.PicEditListener} interface
 * to handle interaction events.
 * Use the {@link EditNotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditNotesFragment extends DialogFragment {

    private static final String KEY_PHOTO_TO_EDIT = "photoInfoKey";

    private PicEditListener mListener;

    private Photo photo;

    private long newAlbumId = -1;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param toEdit photo info to edit
     * @return A new instance of fragment EditNotesDialogFragment.
     */
    public static EditNotesFragment newInstance(Photo toEdit) {

        Log.d("mysteryException", "EditNotesDialogFragment.newInstance() start");
        EditNotesFragment fragment = new EditNotesFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_PHOTO_TO_EDIT, toEdit);
        fragment.setArguments(args);
        return fragment;
    }

    public EditNotesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            photo = getArguments().getParcelable(KEY_PHOTO_TO_EDIT);
        }


    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //inflate views//
        View rootView = View.inflate(getActivity(), R.layout.fragment_edit_photo, null);
        final EditText notesEditText = (EditText) rootView.findViewById(R.id.notesEditText);
        final Spinner albumSpinner = (Spinner) rootView.findViewById(R.id.albumSpinner);

        //populate adapter for album spinner//
        AsyncTask<Void, Void, Void> setupAdapterTask = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                MySQLiteHelper helper = new MySQLiteHelper(getActivity());
                final SQLiteDatabase db = helper.getReadableDatabase();
                Cursor crsr = db.query(MySQLiteHelper.TABLE_ALBUMS, MySQLiteHelper.COLUMNS_ALBUMS_TABLE,
                        null, null, null, null, null);

                final CursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
                        android.R.layout.simple_list_item_1, crsr,
                        new String[] {MySQLiteHelper.COLUMN_TITLE}, new int[]{android.R.id.text1}, 0);

                //set adapter//
                albumSpinner.setAdapter(adapter);

                //close database resources//
                db.close();
                helper.close();

                return null;
            }
        };
        setupAdapterTask.execute();

        //setup spinner onclick events//
        albumSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                newAlbumId = id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                newAlbumId = -1;
            }
        });

        //hint is different than text//
        //only use hint if no original text is available//
        if (photo.getNotes() == null || photo.getNotes().equals("")) {
            notesEditText.setHint("...");
        } else {
            notesEditText.setText(photo.getNotes());
        }

        //create Dialog using Builder//
        return new AlertDialog.Builder(getActivity())
                .setTitle("Notes")
                .setView(rootView)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Photo newPhoto = new Photo(photo.getId(), photo.getLongitude(),
                                photo.getLatitude(), notesEditText.getText().toString(),
                                photo.getTimeTaken(), photo.getBitmap(null),
                                newAlbumId != -1 ? newAlbumId : photo.getAlbumId());
                        mListener.onPicEdited(newPhoto);
                    }
                })
                .create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (PicEditListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface PicEditListener {
        public void onPicEdited(Photo newPhoto);
        public void onCancel(Photo oldPhoto);
    }

}
