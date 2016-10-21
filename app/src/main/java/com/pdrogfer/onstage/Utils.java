package com.pdrogfer.onstage;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static final String FIREBASE_GIGS = "gigs";
    public static final String TAG = "OnStage";
    public static final int NEW_GIG_REQUEST = 1;
    public static final int NEW_GIG_RESULT_OK = 200;
    public static final String LOG_IN = "RegisterActivity";
    public static final String DB_KEY_USER_NAME = "name";
    public static final String DB_KEY_USER_EMAIL = "email";
    public static final String DB_KEY_USER_PASSWORD = "password";
    public static final String DB_KEY_USER_TYPE = "user_type";

    // dummy user for testing
    public static final String TEST_EMAIL = "testuser@hotmail.com";
    public static final String TEST_PASSWORD = "aaaaaa";
    public static final String TEST_NAME = "testuser";
    public static final String TEST_USER_TYPE = "MUSICIAN";

    // camera intents
    public static final int INTENT_REQUEST_CAMERA = 1;
    public static final int INTENT_SELECT_FILE = 2;

    public static List<String> widgetData = new ArrayList<>();

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

    public static void updateWidgetData(String artist) {
        // limit this to the 10 most recent events
        if (widgetData.size() > 10) {
            widgetData.remove(0);
        }
        widgetData.add(artist);
    }
}
