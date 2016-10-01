package com.pdrogfer.onstage.firebase_client;

import android.content.Context;

/**
 * Created by pdro on 01/10/2016.
 *
 */

public interface UserAuthSuperClient {

    void checkAuth();

    void signIn(String email,
                String password,
                final String artisticName,
                String userType);

    void registerUser(String email,
                      String password,
                      final String artisticName,
                      String userType);

}
