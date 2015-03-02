package com.gitmad.geophotos;

import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.*;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapActivity extends ActionBarActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;

    private Location mLastLocation;

    private double mLatitude;

    private double mLongitude;

    private GoogleMap mMap;

    private ImageButton mCameraButton;

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        setContentView(R.layout.activity_map);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mCameraButton = (ImageButton)findViewById(R.id.add_button);
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapActivity.this, CameraActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mMap.setMyLocationEnabled(true);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitude = mLastLocation.getLatitude();
            mLongitude = mLastLocation.getLongitude();

            if (mLastLocation != null) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLatitude, mLongitude), 13));
            }
            if (mMap != null) {

                LocationDataSource locationDataSource = LocationDataSource.getInstance(this);
                locationDataSource.open();

                ArrayList<LocationModel> locations = locationDataSource.getLocationList();

                PhotoDataSource photoDataSource = PhotoDataSource.getInstance(this);
                photoDataSource.open();

                for (PhotoModel photo : photoDataSource.getPhotoList()) {

                    MarkerOptions marker = new MarkerOptions();
                    System.out.println(locationDataSource.getLocationList());
                    LocationModel location = new LocationModel();

                    for (LocationModel locationModel : locations) {
                        if (locationModel.get_id()-1 == photo.getLocation_ID()) {
                            location = locationModel;
                        }
                    }

                    Bitmap bmp = BitmapFactory.decodeFile(photo.getFilepath());
                    bmp = bmp.createScaledBitmap(bmp, 100, 100, false);
                    final BitmapDescriptor image = BitmapDescriptorFactory.fromBitmap(bmp);

                    marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin))
                            .title(location.getDescription())
                            .anchor(0.0f, 1.0f)
                            .position(new LatLng(location.getLatitude(), location.getLongitude()));

                    mMap.addMarker(marker);

                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            marker.setIcon(image);
                            return false;
                        }
                    });
                }

                locationDataSource.close();
                photoDataSource.close();

            }

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        updateUI();
    }

    private void updateUI() {
        mLatitude = mLastLocation.getLatitude();
        mLongitude = mLastLocation.getLongitude();
        if (mLastLocation != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLatitude, mLongitude), 13));
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            //showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }
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

        Intent i;
        switch(id){
            case(R.id.menu_item_map):
                i = new Intent(MapActivity.this, MapActivity.class);
                startActivity(i);
                break;
            case(R.id.menu_item_camera):
                i = new Intent(MapActivity.this, CameraActivity.class);
                startActivity(i);
                break;
            case(R.id.menu_item_login):
                i = new Intent(MapActivity.this, LoginActivity.class);
                startActivity(i);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}