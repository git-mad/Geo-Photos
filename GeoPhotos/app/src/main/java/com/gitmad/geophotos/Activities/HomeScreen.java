package com.gitmad.geophotos.Activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gitmad.geophotos.Fragments.EditNotesDialogFragment;
import com.gitmad.geophotos.Models.Photo;
import com.gitmad.geophotos.Models.User;
import com.gitmad.geophotos.MySQLiteHelper;
import com.gitmad.geophotos.R;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import static com.gitmad.geophotos.MySQLiteHelper.*;

public class HomeScreen extends ActionBarActivity
        implements EditNotesDialogFragment.PicEditListener {

    public static final String KEY_USER_DATA = "userDataKey";
    public static final int REQUEST_CODE_IMAGE_CAPTURE = 11234;

    private String userName;
    private String emailAddress;
    private String password;
    private TextView welcomeTextView;

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

                //TODO make sure we record the location

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

            //TODO add long and lat

            Photo photo = new Photo(-1, -1, -1, null, Calendar.getInstance().getTimeInMillis());

            EditNotesDialogFragment.newInstance(photo)
                    .show(getFragmentManager(), "notesDialogFrag");
        }
    }

    @Override
    public void onPicEdited(Photo newPhoto) {

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
}
