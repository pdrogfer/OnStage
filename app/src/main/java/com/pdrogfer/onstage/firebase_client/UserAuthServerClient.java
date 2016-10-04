package com.pdrogfer.onstage.firebase_client;

import android.content.Context;

/**
 * Created by pdro on 01/10/2016.
 *
 * This class perform authentication operations to comply with Rubric
 */

public class UserAuthServerClient implements UserAuthSuperClient {

    private static UserAuthServerClient uniqueAuthServerInstance;

    private final OnAuthenticationCompleted authServerListener;
    private final Context context;

    private UserAuthServerClient(Context context, OnAuthenticationCompleted authServerListener) {
        this.context = context;
        this.authServerListener = authServerListener;
    }

    public static synchronized UserAuthServerClient getInstance(Context context, OnAuthenticationCompleted listener) {
        if (uniqueAuthServerInstance == null) {
            uniqueAuthServerInstance = new UserAuthServerClient(context, listener);
        }
        return uniqueAuthServerInstance;
    }

    @Override
    public void checkAuth() {

    }

    @Override
    public void signIn(String email, String password, String artisticName, String userType) {

    }

    @Override
    public void registerUser(String email, String password, String artisticName, String userType) {

    }

    private void onAuthFailed(String errorMessage) {

    }

    private void onAuthSuccess() {

    }
}
