package com.pdrogfer.onstage.firebase_client;

public interface OnFirebaseUserCompleted {

    void onLogInCompleted(Boolean success, String name, String email, String userType);

    void onRegistrationCompleted(Boolean success, String name, String email, String userType);

    void onSignOut();

    void onUserDeleted();
}
