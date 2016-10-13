package com.pdrogfer.onstage.firebase_client;

public interface OnAuthenticationCompleted {

    void onAuthenticationCompleted(Boolean success, String name, String email, String password, String user_type);

    void onSignOut();
}
