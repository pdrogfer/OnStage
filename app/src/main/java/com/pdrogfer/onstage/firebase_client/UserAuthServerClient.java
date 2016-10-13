package com.pdrogfer.onstage.firebase_client;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pdrogfer.onstage.Utils;
import com.pdrogfer.onstage.database.Contract;
import com.pdrogfer.onstage.database.UsersContentProvider;
import com.pdrogfer.onstage.ui.GigsListActivity;
import com.pdrogfer.onstage.ui.Presentation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * This class perform authentication operations to comply with Rubric
 */

public class UserAuthServerClient implements UserOperationsSuperClient {

    private static final String TAG = "UserAuthServerClient";
    private static UserAuthServerClient uniqueAuthServerInstance;

    private final OnAuthenticationCompleted authServerListener;
    private final Context context;

    AsyncHttpClient asyncHttpClient;
    RequestParams requestParams;

    String jsonResponse;

    // choose depending if in local network or not
    private final String baseUrl = "http://192.168.1.4/onstage/";
//    private final String baseUrl = "http://kavy.servehttp.com/onstage/";
    private String urlLogin = baseUrl + "login.php";
    private String urlRegister = baseUrl + "insert_new_user.php";

    private UserAuthServerClient(Context context, OnAuthenticationCompleted authServerListener) {
        this.context = context;
        this.authServerListener = authServerListener;

        asyncHttpClient = new AsyncHttpClient();
        requestParams = new RequestParams();
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
    public void signIn(String email, String password) {
        requestParams.put(Utils.DB_KEY_USER_EMAIL, email);
        requestParams.put(Utils.DB_KEY_USER_PASSWORD, password);

        asyncHttpClient.get(urlLogin, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i(TAG, "onSuccess: Loopj, JSONObject received");
                onAuthRequestOK(true, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Log.i(TAG, "onSuccess: Loopj, JSONArray received: " + response.toString());

                onAuthRequestOK(true, response);
                Log.i(TAG, "signIn onSuccess: Loopj, JSONArray received");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.i(TAG, "signIn onFailure: Loopj - statusCode " + statusCode + ", JSONObject " + errorResponse.toString());
                onAuthFailed(false, "User doesn' exist. Please register");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.i(TAG, "signIn onFailure: Loopj - statusCode " + statusCode + responseString);
                onAuthFailed(false, "User doesn' exist. Please register");
            }
        });
    }

    @Override
    public void signOut(GigsListActivity gigsListActivity) {
        Uri users = UsersContentProvider.CONTENT_URI;
        String selectUserActive = "1";
        ContentValues values = new ContentValues();
        values.put(Contract.COLUMN_USER_ACTIVE, 0);
        int result = gigsListActivity.getContentResolver().update(users, values, Contract.COLUMN_USER_ACTIVE + "=?", new String[]{selectUserActive});
        if (result > 0) {
            Toast.makeText(gigsListActivity, "You are logged out", Toast.LENGTH_LONG).show();
            authServerListener.onSignOut();
        } else {
            Toast.makeText(gigsListActivity, "Error logging out", Toast.LENGTH_LONG).show();
        }
        gigsListActivity.startActivity(new Intent(gigsListActivity, Presentation.class));
    }

    @Override
    public void registerUser(String email, String password, String artisticName, String userType) {
        // not implemented here, belongs to UserRegServerClient
    }

    private void onAuthFailed(boolean success, String errorMessage) {
        if (errorMessage == "") {
            errorMessage = "error in registration process";
        }
        authServerListener.onAuthenticationCompleted(success, errorMessage);
    }

    private void onAuthRequestOK(boolean success, JSONArray responseArray) {
        if (responseArray.length() == 0) {
            Toast.makeText(context, "No user match our database. Please register", Toast.LENGTH_LONG).show();
            authServerListener.onAuthenticationCompleted(success, "No user match our database. Please register");
            return;
        } else if (responseArray.length() > 1) {
            Toast.makeText(context, "Error, duplicated users. Please contact OnStage", Toast.LENGTH_LONG).show();
            authServerListener.onAuthenticationCompleted(success, "Error, duplicated users. Please contact OnStage");
            return;
        }

        // extract user fields from answer (should be an array with just on JSONObject)
        String userName = "";
        String userEmail = "";
        String userPassword = "";
        String userType = "";
        // etc...
        Log.i(TAG, "onAuthRequestOK: " + responseArray);
        try {
            JSONObject objectUser = new JSONObject(String.valueOf(responseArray.getJSONObject(0)));
            userName = objectUser.getString(Utils.DB_KEY_USER_NAME);
            userEmail = objectUser.getString(Utils.DB_KEY_USER_EMAIL);
            userPassword = objectUser.getString(Utils.DB_KEY_USER_PASSWORD);
            userType = objectUser.getString(Utils.DB_KEY_USER_TYPE);
            Log.i(TAG, "onAuthRequestOK: "
                    + userName + ", "
                    + userEmail + ", "
                    + userPassword + ", "
                    + userType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // TODO: 06/10/16 store user in a SQLite database, using content providers, with a field 'active' so we can know which user is logged on start
        authServerListener.onAuthenticationCompleted(success, userName);
    }

    private void onAuthRequestOK(boolean success, JSONObject responseObject) {
        // TODO: 09/10/16 I am getting the user's details here
        Log.i(TAG, "onAuthSuccess: responseObject: " + responseObject.toString());
        authServerListener.onAuthenticationCompleted(success, "YES!");
    }
}
