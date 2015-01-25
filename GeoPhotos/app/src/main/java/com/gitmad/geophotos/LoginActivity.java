package com.gitmad.geophotos;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class LoginActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        //TODO: Initialize Email, Username, and EditText here.
        //exe. EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
        Button loginButton = (Button) findViewById(R.id.loginButton);

        //NOTE: We need to manage the Activity Lifecycle properly.
        //See http://developer.android.com/training/basics/activity-lifecycle/index.html
        //TODO: Keep the state of the textboxes when the user exits and re-enters the application.
        //Override appropriate methods in the Activity Lifecycle callbacks and use the Bundle savedInstanceState to store
        //and retrieve information

        //Make a click listener for the buttons.
        //TODO: Send the username to the HomeScreen Activity
        //Hint: Use Extras with the intent to get send the username over.
        Intent i = new Intent(this,HomeScreen.class);

        loginButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        login();
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    public void login()
    {
        //Authentication logic will be added here in the future.
        Intent i = new Intent(this,HomeScreen.class);
        startActivity(i);
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