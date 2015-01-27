package com.gitmad.geophotos.Models;

import java.io.Serializable;

/**
 * Class to encapsulate user info.
 *
 * Created by Brian on 1/27/2015.
 */
public class User implements Serializable {

    private String userName;
    private String emailAddress;

    public User(String userName, String emailAddress) {
        this.userName = userName;
        this.emailAddress = emailAddress;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
}
