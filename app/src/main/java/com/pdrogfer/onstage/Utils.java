package com.pdrogfer.onstage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utils {
    public static final String FIREBASE_GIGS = "gigs";
    public static final String TAG = "OnStage";
    public static final int NEW_GIG_REQUEST = 1;
    public static final int NEW_GIG_RESULT_OK = 200;
    public static final String LOG_IN = "RegisterActivity";
    public static final String ARTISTIC_NAME = "ARTISTIC_NAME";
    public static final String USER_TYPE = "USER_TYPE";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String EMAIL_FOR_TESTING = "testuser@hotmail.com";
    public static final String PASSWORD_FOR_TESTING = "aaaaaa";


    // Helper functions to get artisticName and userType anywhere
    public static void storeArtisticName(String keyName, String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(keyName, value);
        editor.commit();
    }

    public static String getArtisticName(String keyName, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(keyName, null);
    }

    public static void storeUserType(String keyUserType, String userType, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(keyUserType, userType);
        editor.commit();
    }

    public static String getUserType(String keyUserType, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(keyUserType, null);
    }
}
