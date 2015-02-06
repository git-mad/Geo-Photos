package com.gitmad.geophotos.Activities;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.gitmad.geophotos.Fragments.PhotoInfoFragment;
import com.gitmad.geophotos.Models.Photo;
import com.gitmad.geophotos.R;

public class PhotoInfoActivity extends ActionBarActivity {

    private PhotoInfoFragment infoFragment;
    private Photo photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_info);

        if (savedInstanceState == null) {
            //get photo from intent, and create fragment, passing photo//
            photo = getIntent().getExtras().getParcelable(ViewPhotoActivity.KEY_PHOTO);
            infoFragment = PhotoInfoFragment.newInstance(photo);

            //replace is safer in general, as it will remove any fragments already attached
            //to the view//
            getFragmentManager().beginTransaction()
                    .replace(R.id.photoInfoFrameLayout, infoFragment, "infofrag")
                    .commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo_info, menu);
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
}
