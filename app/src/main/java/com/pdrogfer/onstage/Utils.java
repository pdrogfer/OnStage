package com.pdrogfer.onstage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by pedrogonzalezferrandez on 06/06/16.
 */
public class Utils {
    public static final String FIREBASE_GIGS = "gigs";
    public static final String TAG = "OnStage";
    public static final int NEW_GIG_REQUEST = 1;
    public static final int NEW_GIG_RESULT_OK = 200;
    public static final String LOG_IN = "SignInActivity";
    public static final String ARTISTIC_NAME = "ARTISTIC_NAME";



    // Helper functions to get artisticName anywhere
    public static void storeArtisticName(String key, String value, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getArtisticName(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }
}
