package com.gitmad.geophotos.Activities;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentSender;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gitmad.geophotos.Fragments.EditNotesDialogFragment;
import com.gitmad.geophotos.Models.Photo;
import com.gitmad.geophotos.Models.User;
import com.gitmad.geophotos.MySQLiteHelper;
import com.gitmad.geophotos.R;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;


import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import static com.gitmad.geophotos.MySQLiteHelper.*;

public class HomeScreen extends ActionBarActivity
        implements EditNotesDialogFragment.PicEditListener,
        GooglePlayServicesClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String KEY_USER_DATA = "userDataKey";

    //Request Codes//
    public static final int REQUEST_CODE_IMAGE_CAPTURE = 11234;
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final int SHOW_ERROR_REQUEST = 8000;

    private String userName;
    private String emailAddress;
    private String password;
    private TextView welcomeTextView;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homescreen_layout);

        Bundle bundle = getIntent().getExtras();
        //TODO: Grab the username from the extras, set the username string appropriately.
        //Stuck? See here: http://stackoverflow.com/questions/4233873/how-to-get-extra-data-from-intent-in-android

        if (bundle != null && bundle.containsKey(KEY_USER_DATA)) {
            User userData = (User) bundle.getSerializable(KEY_USER_DATA);
            userName = userData.getUserName();
            emailAddress = userData.getEmailAddress();
        }

        welcomeTextView = (TextView) findViewById(R.id.welcomeTextView);
        welcomeTextView.setText("Welcome, "+ userName);

        //Set up buttons click listeners//
        ((Button) findViewById(R.id.takePhotoButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_CODE_IMAGE_CAPTURE);
                }
            }
        });

        ((Button) findViewById(R.id.viewPhotosButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreen.this, ViewPhotosActivity.class));
            }
        });

        //check if Google Play Services is available//
        isGooglePlayServicesAvailable();

        //set up client//
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    public boolean isGooglePlayServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode == ConnectionResult.SUCCESS) {
            return true;
        } else {

            //try to resolve error//
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);
            if (errorDialog != null) {
                ErrorDialogFragment errorFrag = new ErrorDialogFragment();
                errorFrag.setDialog(errorDialog);
                errorFrag.show(getFragmentManager(), "Location Services");
            }
        }
        return false;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
         /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            GooglePlayServicesUtil.showErrorDialogFragment(connectionResult.getErrorCode(), this,
                    SHOW_ERROR_REQUEST);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation == null) {
            Toast.makeText(this, "Location Not Available!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bitmap bmp = (Bitmap) data.getExtras().get("data");

            //try again. Location sometimes takes time to load and taking the picture might//
            //have given it the time necessary//
            if (mLastLocation == null) {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            }

            Photo photo;

            //If location is still unavailable, set a value that is impossible//
            if (mLastLocation == null) {
                photo = new Photo(-1, Double.MIN_VALUE, Double.MIN_VALUE,
                        null, Calendar.getInstance().getTimeInMillis());
            } else {
                photo = new Photo(-1, mLastLocation.getLongitude(), mLastLocation.getLatitude(),
                        null, Calendar.getInstance().getTimeInMillis());
            }

            //Let user edit the notes for the photo before saving//
            EditNotesDialogFragment.newInstance(photo)
                    .show(getFragmentManager(), "notesDialogFrag");
        } else if (requestCode == CONNECTION_FAILURE_RESOLUTION_REQUEST) {
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
                if (resultCode == RESULT_OK) {
                    if (isGooglePlayServicesAvailable()) {
                        mGoogleApiClient.connect();
                    }
                }
        }
    }

    @Override
    public void onPicEdited(Photo newPhoto) {

        //user is done editing notes, now save photo//

        AsyncTask<Photo, Void, Void> writePhotoTask = new AsyncTask<Photo, Void, Void>() {
            @Override
            protected Void doInBackground(Photo... params) {

                //readability//
                Photo picToWrite = params[0];

                //get DB refs//
                MySQLiteHelper helper = new MySQLiteHelper(HomeScreen.this);
                SQLiteDatabase db = helper.getWritableDatabase();

                //Create ContentValues and add info//
                ContentValues vals = new ContentValues(1);
                vals.put(COLUMN_LATITUDE, picToWrite.getLatitude());
                vals.put(COLUMN_LONGITUDE, picToWrite.getLongitude());
                //compressing Bitmap to png//
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                vals.put(COLUMN_IMAGE,
                        picToWrite.getBitmap(db).compress(Bitmap.CompressFormat.PNG, 9, outStream));
                vals.put(COLUMN_NOTES, picToWrite.getNotes());
                vals.put(COLUMN_ALBUM_ID, picToWrite.getAlbumId());
                vals.put(COLUMN_TIME_TAKEN, picToWrite.getTimeTaken());

                //write to DB//
                db.insert(TABLE_PHOTOS, null, vals);

                //Close Out Resources//
                db.close();
                helper.close();

                return null;
            }
        };
        writePhotoTask.execute(newPhoto);
    }

    @Override
    public void onCancel(Photo oldPhoto) {
        //In this case we need to write it either way//
        onPicEdited(oldPhoto);
    }

    /*
     * Handle results returned to the FragmentActivity
     * by Google Play services
     */

    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;
        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }
}
