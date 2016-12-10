package com.pdrogfer.onstage.model;

public class User {

    private String uid;
    private String name;
    private String email;
    private String userType;

    public User() {
        // Default constructor required for calls to Firebase DataSnapshot.getValue(User.class)
    }

    public User(String uid, String name, String email, String userType) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.userType = userType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
