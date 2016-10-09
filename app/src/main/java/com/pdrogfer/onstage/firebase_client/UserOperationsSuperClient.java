package com.pdrogfer.onstage.firebase_client;

/**
 * Created by pdro on 01/10/2016.
 *
 */

public interface UserOperationsSuperClient {

    void checkAuth();

    void signIn(String email, String password);

    void registerUser(String email,
                      String password,
                      final String artisticName,
                      String userType);

}
