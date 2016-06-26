package com.pdrogfer.onstage;

/**
 * Created by pdro on 11/06/2016.
 */
public class User {

    public String username;
    public String email;

    public User() {
        // Default constructor required for calls to Firebase DataSnapshot.getValue(User.class)
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
}
