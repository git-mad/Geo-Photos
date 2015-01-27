package com.gitmad.geophotos.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.gitmad.geophotos.Activities.HomeScreen;
import com.gitmad.geophotos.Models.User;
import com.gitmad.geophotos.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.gitmad.geophotos.Fragments.LoginFragment.LoginListener} interface
 * to handle interaction events.
 */
public class LoginFragment extends Fragment {

    private LoginListener listener;

    //Text field references//
    private EditText emailEditText;
    private EditText userNameEditText;
    private EditText passwordEditText;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Tell Fragment to save it's instance state//
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        //hold references for when user presses loginButton later//
        emailEditText = (EditText) rootView.findViewById(R.id.emailEditText);
        userNameEditText = (EditText) rootView.findViewById(R.id.usernameEditText);
        passwordEditText = (EditText) rootView.findViewById(R.id.passwordEditText);

        //set up onClickListener//
        Button loginButton = (Button) rootView.findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (LoginListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if(emailEditText.getText() != null){
            savedInstanceState.putString("emailAddress", emailEditText.getText().toString());
        }
        if(userNameEditText.getText()!= null){
            savedInstanceState.putString("userName", userNameEditText.getText().toString());
        }
        if(passwordEditText.getText() != null){
            savedInstanceState.putString("password", passwordEditText.getText().toString());
        }

        // Always call the superclass so it can save the view hierarchy state
        //Notice how we do this last
        super.onSaveInstanceState(savedInstanceState);
    }

//    @Override
//    public void onViewStateRestored(Bundle savedInstanceState) {
//        // Always call the superclass so it can restore the view hierarchy
//        //Notice how we do this first
//        super.onViewStateRestored(savedInstanceState);
//
//        if (savedInstanceState != null) {
//            if (savedInstanceState.containsKey("emailAddress")) {
//                emailEditText.setText(savedInstanceState.getString("emailAddress"));
//            }
//            if (savedInstanceState.containsKey("userName")) {
//                userNameEditText.setText(savedInstanceState.getString("emailAddress"));
//            }
//            if (savedInstanceState.containsKey("password")) {
//                passwordEditText.setText(savedInstanceState.getString("password"));
//            }
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    /*
        Helper Methods
     */

    private void login() {
        //TODO Authentication logic will be added here in the future.
        //Hint: Use Extras with the intent to get send the username over.
        //Pro Tip: Get the String from the EditText with userName.getText().toString();
        
        if ((userNameEditText.getText() != null) && (passwordEditText.getText() != null)
                    && (emailEditText.getText() != null)) {
            //Create object to encapsulate user data//
            User userInfo = new User(userNameEditText.getText().toString(),
                    emailEditText.getText().toString());

            //pass user data back to listener//
            listener.loginSuccessful(userInfo);
        }
    }


    /*
        Inner types
     */

    public interface LoginListener {
        /**
         * Called when the user was successfully authenticated
         * @param user the non sensitive user info of the authenticated user
         */
        public void loginSuccessful(User user);

        /**
         * Called when the called when the user tries to log in, but cannot
         * @param reason the reason that authentication failed, reptesented by the
         *               {@link com.gitmad.geophotos.Fragments.LoginFragment.LoginFailureReason}
         *               enum.
         */
        public void loginFailed(LoginFailureReason reason);
    }

    /**
     * enum that represents the reason that a login attempt failed.
     */
    public enum LoginFailureReason {
        USERNAME_NOT_FOUND,
        PASSWORD_INCORRECT,
        OTHER
    }
}
