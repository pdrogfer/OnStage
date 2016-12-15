package com.pdrogfer.onstage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.pdrogfer.onstage.model.Gig;
import com.pdrogfer.onstage.model.User;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static final String FIREBASE_GIGS = "gigs";
    public static final String FIREBASE_USERS = "users";
    public static final String TAG = "OnStage";
    public static final int NEW_GIG_REQUEST = 1;
    public static final int NEW_GIG_RESULT_OK = 200;
    public static final String FIREBASE_CLIENT = "UserFirebaseClient";
    public static final String DB_KEY_USER_NAME = "name";
    public static final String DB_KEY_USER_EMAIL = "email";
    public static final String DB_KEY_USER_PASSWORD = "password";
    public static final String DB_KEY_USER_TYPE = "user_type";
    public static final String USER_FAN = "fan";
    public static final String USER_MUSICIAN = "musician";
    public static final String USER_VENUE = "venue";

    // dummy user for testing
    public static final String TEST_EMAIL_MUSICIAN = "testmusician@hotmail.com";
    public static final String TEST_EMAIL_FAN = "testfan@hotmail.com";
    public static final String TEST_PASSWORD_MUSICIAN = "aaaaaa";
    public static final String TEST_PASSWORD_FAN = "aaaaaa";
    public static final String TEST_NAME = "testuser";
    public static final String TEST_USER_TYPE_MUSICIAN = USER_MUSICIAN;
    public static final String TEST_USER_TYPE_FAN = USER_FAN;

    // camera intents
    public static final int INTENT_REQUEST_CAMERA = 1;
    public static final int INTENT_SELECT_FILE = 2;
    public static final String UPDATED = "updated";
    public static final String UPDATE_ERROR = "error updating";

    public static List<Gig> widgetData = new ArrayList<>();

    public static void storeUser(String name, String email, String userType, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(DB_KEY_USER_NAME, name);
        editor.putString(DB_KEY_USER_EMAIL, email);
        editor.putString(DB_KEY_USER_TYPE, userType);
        editor.commit();
    }

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

    public static void storeUserType(String userType, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(DB_KEY_USER_TYPE, userType);
        editor.commit();
    }

    public static String getUserType(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(DB_KEY_USER_TYPE, null);
    }

    public static void updateWidgetData(Gig gig) {
        // limit this to the 10 most recent events
        if (widgetData.size() > 10) {
            widgetData.remove(0);
        }
        widgetData.add(gig);
    }

    public static boolean isUserEditor(Context context) {

        // TODO: 06/12/2016 get user type from user saved to preferences
        Log.i(TAG, "isUserEditor: " + getUserType(context) + " " + USER_MUSICIAN);
        return (getUserType(context) == USER_MUSICIAN);
    }
}
