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

    private Button loginButton;
    private EditText emailEditText;
    private EditText userNameEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        //TODO: Initialize Email, Username, and Password EditText here.
        //exe. EditText emailEditText = (EditText) findViewById(R.id.emailEditText);
        loginButton = (Button) findViewById(R.id.loginButton);

        loginButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        login();
                    }
                }
        );
    }

    //NOTE: We need to manage the Activity Lifecycle properly.
    //See http://developer.android.com/training/basics/activity-lifecycle/index.html
    //TODO: Keep the state of the textboxes for when Activity gets destroyed
    //This happens when user presses the back button, rotates screen, etc.
    //http://developer.android.com/training/basics/activity-lifecycle/recreating.html
    //TODO: Override onSaveInstanceState and onRestoreInstanceState

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Always call the superclass so it can save the view hierarchy state
        //Notice how we do this last
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        //Notice how we do this first
        super.onRestoreInstanceState(savedInstanceState);
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
        //TODO: Send the username to the HomeScreen Activity
        //Hint: Use Extras with the intent to get send the username over.
        //Pro Tip: Get the String from the EditText with userName.getText().toString();

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