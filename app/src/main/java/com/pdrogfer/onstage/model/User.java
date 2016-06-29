package com.pdrogfer.onstage.model;

/**
 * Created by pdro on 11/06/2016.
 */
public class User {

    public String name;
    public String email;

    public User() {
        // Default constructor required for calls to Firebase DataSnapshot.getValue(User.class)
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
