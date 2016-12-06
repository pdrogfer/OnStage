package com.pdrogfer.onstage.firebase_client;

public interface OnAuthenticationCompleted {

    void onAuthenticationCompleted(Boolean success, String name, String email, String userType);

    void onSignOut();

    void onUserDeleted();
}
