package com.gitmad.geophotos;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CameraActivity extends ActionBarActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String mPhotoPath = "";
    private ImageButton mCaptureButton;
    private ImageButton mSaveButton;
    private EditText mNameBox;
    private TextView mLocationText;
    private ImageView mImageView;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private PhotoDataSource mPhotoDataSource;
    private LocationDataSource mLocationDataSource;
    private LocationManager locationManager;
    private LocationModel mLocationModel;
    private PhotoModel mPhotoModel;
    private String locationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        //Used to get the location that a picture was taken at.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();

        mImageView = (ImageView)findViewById(R.id.imageView);
        mCaptureButton = (ImageButton)findViewById(R.id.button);
        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        mSaveButton = (ImageButton)findViewById(R.id.saveButton);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        mNameBox = (EditText)findViewById(R.id.editText);
       // mLocationText = (TextView)findViewById(R.id.textView);

        //Retrieve the last known location.
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationProvider = LocationManager.NETWORK_PROVIDER;
        mLastLocation = locationManager.getLastKnownLocation(locationProvider);

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
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
                i = new Intent(CameraActivity.this, MapActivity.class);
                startActivity(i);
                break;
            case(R.id.menu_item_camera):
                i = new Intent(CameraActivity.this, CameraActivity.class);
                startActivity(i);
                break;
            case(R.id.menu_item_login):
                i = new Intent(CameraActivity.this, LoginActivity.class);
                startActivity(i);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager())!=null)
        {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            }
            catch(IOException ioex)
            {
                Log.e("GeoPhotos", "COULD NOT CREATE IMAGE FILE");
                Log.e("GeoPhotos", ioex.getMessage());
            }
            if(photoFile!=null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(mPhotoPath!= "")
        {
            Log.d("CameraActivity", mPhotoPath);
            Bitmap imageBitmap = BitmapFactory.decodeFile(mPhotoPath);
            //Scale the Bitmap so that it fits on canvas.
            if(imageBitmap != null)
            {
                Bitmap scaled = imageBitmap.createScaledBitmap(imageBitmap, (imageBitmap.getWidth()/4), (imageBitmap.getHeight()/4),false);
                mImageView.setImageBitmap(scaled);
                Bitmap thumbnail = imageBitmap.createScaledBitmap(imageBitmap, imageBitmap.getWidth()/10,imageBitmap.getHeight()/10, false);

                galleryAddPic(); //add the image to the gallery application!

                mLocationModel = new LocationModel();

                if(mLastLocation == null)
                {
                    locationProvider = LocationManager.GPS_PROVIDER;
                    mLastLocation = locationManager.getLastKnownLocation(locationProvider);
                }

                mLocationModel.setLatitude(mLastLocation.getLatitude());
                mLocationModel.setLongitude(mLastLocation.getLongitude());

                //TODO: Get description from a textbox.
                mLocationModel.setDescription(mNameBox.getText().toString());
                mLocationModel.setName(mPhotoPath);

                //LocationDataSource locationDataSource = LocationDataSource.getInstance(this);
                //PhotoDataSource photoDataSource = PhotoDataSource.getInstance(this);

                //locationDataSource.open();
                //locationDataSource.insertLocation(mLocationModel);
                //locationDataSource.close();

                //Store image thumbnail
                //Original image can be referenced from mPhotoPath.
                //Note: Original image too large to store in SQLite.
                int bytes = thumbnail.getByteCount();
                Log.d("CameraActivity", "Thumbnail Size: " + bytes + " bytes");
                ByteBuffer buffer = ByteBuffer.allocate(bytes);
                thumbnail.copyPixelsToBuffer(buffer);

                mPhotoModel = new PhotoModel();
                mPhotoModel.setData(buffer.array());
                mPhotoModel.setFilepath(mPhotoPath);
                mPhotoModel.setLocation_ID(mLocationModel.get_id());

                //photoDataSource.open();
                //photoDataSource.insertPhoto(mPhotoModel);
                //photoDataSource.close();

            }
        }
    }

    private void save()
    {
        LocationDataSource dataSource = LocationDataSource.getInstance(this);
        dataSource.open();
        mLocationModel = dataSource.insertLocation(mLocationModel);
        dataSource.close();

        PhotoDataSource photoDataSource = PhotoDataSource.getInstance(this);
        photoDataSource.open();
        photoDataSource.insertPhoto(mPhotoModel);
        photoDataSource.close();

        finish();
    }


    private File createImageFile() throws IOException
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()); //get current time for name
        String imageFileName = "GEO_"+timeStamp+"_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void galleryAddPic()
    {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    // Define a listener that responds to location updates
    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.
            Log.d("CameraActivity", "Location Changed to: " + location.toString());
            mLastLocation = location;
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };

}
