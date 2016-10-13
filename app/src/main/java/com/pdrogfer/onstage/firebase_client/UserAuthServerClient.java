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
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
        authServerListener.onAuthenticationCompleted(success, null, null, null, null);
    }

    // just in case I receive an array
    private void onAuthRequestOK(boolean success, JSONArray responseArray) {
        if (responseArray.length() == 0) {
            Toast.makeText(context, "No user match our database. Please register", Toast.LENGTH_LONG).show();
            authServerListener.onAuthenticationCompleted(success, null, null, null, null);
            return;
        } else if (responseArray.length() > 1) {
            Toast.makeText(context, "Error, duplicated users. Please contact OnStage", Toast.LENGTH_LONG).show();
            authServerListener.onAuthenticationCompleted(success, null, null, null, null);
            return;
        }

        // extract user fields from answer (should be an array with just on JSONObject)
        Log.i(TAG, "onAuthRequestOK: " + responseArray);
        try {
            JSONObject objectUser = new JSONObject(String.valueOf(responseArray.getJSONObject(0)));
            String userName = objectUser.getString(Utils.DB_KEY_USER_NAME);
            String userEmail = objectUser.getString(Utils.DB_KEY_USER_EMAIL);
            String userPassword = objectUser.getString(Utils.DB_KEY_USER_PASSWORD);
            String userType = objectUser.getString(Utils.DB_KEY_USER_TYPE);
            Log.i(TAG, "onAuthRequestOK: "
                    + userName + ", "
                    + userEmail + ", "
                    + userPassword + ", "
                    + userType);
            authServerListener.onAuthenticationCompleted(success, userName, userEmail, userPassword, userType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // but usually I just get a JSONObject
    private void onAuthRequestOK(boolean success, JSONObject responseObject) {
        //typical answer: {"message":[{"_id":"31","user_type":"MUSICIAN","password":"aaaaaa","email":"testuser@hotmail.com","name":"testuser"}],"status":true}
        String name, email, password, user_type;
        try {
            JSONArray arrayDetails = responseObject.getJSONArray("message");
            JSONObject user = arrayDetails.getJSONObject(0);
            name = user.getString(Contract.COLUMN_NAME);
            email = user.getString(Contract.COLUMN_EMAIL);
            password = user.getString(Contract.COLUMN_PASSWORD);
            user_type = user.getString(Contract.COLUMN_USER_TYPE);
            authServerListener.onAuthenticationCompleted(success, name, email, password, user_type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "onAuthRequestOK: could not read json values " + responseObject.toString());
    }
}
