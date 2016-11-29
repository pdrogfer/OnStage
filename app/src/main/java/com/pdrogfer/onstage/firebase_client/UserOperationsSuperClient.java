package com.pdrogfer.onstage.firebase_client;

import com.pdrogfer.onstage.ui.GigsListActivity;

public interface UserOperationsSuperClient {

    void checkAuth();

    void signIn(String email, String password);

    void registerUser(String email,
                      String password,
                      final String artisticName,
                      String userType);

    void signOut(GigsListActivity gigsListActivity);

    void deleteUser();
}
