package com.pdrogfer.onstage.model;

public class User {

    public String name;
    public String email;
    public String userType;

    public User() {
        // Default constructor required for calls to Firebase DataSnapshot.getValue(User.class)
    }

    public User(String name, String email, String userType) {
        this.name = name;
        this.email = email;
        this.userType = userType;
    }
}
