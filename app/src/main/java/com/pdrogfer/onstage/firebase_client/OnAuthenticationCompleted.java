package com.pdrogfer.onstage.firebase_client;

public interface OnAuthenticationCompleted {

    void onAuthenticationCompleted(Boolean success, String message);

    void onSignOut();
}
