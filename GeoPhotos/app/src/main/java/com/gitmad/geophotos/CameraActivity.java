package com.gitmad.geophotos;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
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
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CameraActivity extends ActionBarActivity implements GooglePlayServicesClient.ConnectionCallbacks {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private String mPhotoPath = "";
    private Button mCaptureButton;
    private ImageView mImageView;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private PhotoDataSource mPhotoDataSource;
    private LocationDataSource mLocationDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        //Used to get the location that a picture was taken at.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();

        mImageView = (ImageView)findViewById(R.id.imageView);
        mCaptureButton = (Button)findViewById(R.id.button);
        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }

    //TODO: Make the location from MapActivitiy accessible here.
    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    public void onLocationChanged(Location location) {
        mLastLocation = location;
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
    }

    @Override
    public void onDisconnected(){}

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
                Bitmap thumbnail = imageBitmap.createScaledBitmap(imageBitmap, 100,100, false);

                galleryAddPic(); //add the image to the gallery application!

                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                LocationModel locationModel = new LocationModel();
                if(mLastLocation != null)
                {
                    locationModel.setLatitude(mLastLocation.getLatitude());
                    locationModel.setLongitude(mLastLocation.getLongitude());
                }
                else
                {
                    locationModel.setLatitude(-1);
                    locationModel.setLongitude(-1);
                }


                //TODO: Implement way for users to add description to their locations.
                locationModel.setDescription("Description goes here");
                locationModel.setName(mPhotoPath);

                LocationDataSource dataSource = new LocationDataSource(this);
                dataSource.open();
                locationModel = dataSource.insertLocation(locationModel);
                dataSource.close();

                //Convert the Bitmap to an array of bytes
                int bytes = scaled.getByteCount();
                ByteBuffer buffer = ByteBuffer.allocate(bytes);
                scaled.copyPixelsToBuffer(buffer);

                PhotoModel photoModel = new PhotoModel();
                photoModel.setData(buffer.array());
                photoModel.setFilepath(mPhotoPath);
                photoModel.setLocation_ID(locationModel.get_id());

                PhotoDataSource photoDataSource = new PhotoDataSource(this);
                photoDataSource.open();
                photoDataSource.insertPhoto(photoModel);
                photoDataSource.close();
            }
        }
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
}
