package com.pdrogfer.onstage.firebase_client;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pdrogfer.onstage.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by pdro on 01/10/2016.
 *
 * This class perform authentication operations to comply with Rubric
 */

public class UserAuthServerClient implements UserAuthSuperClient {

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

        asyncHttpClient.get(baseUrl, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.i(TAG, "onSuccess: Loopj, JSONObject received");
                onAuthRequestOK(true, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Log.i(TAG, "onSuccess: Loopj, JSONArray received");

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

    private void onAuthFailed(boolean success, String errorMessage) {
        authServerListener.onAuthenticationCompleted(success, errorMessage);
    }

    private void onAuthRequestOK(boolean success, JSONArray responseArray) {
        if (responseArray.length() == 0) {
            Toast.makeText(context, "No user match our database. Please register", Toast.LENGTH_LONG).show();
            return;
        } else if (responseArray.length() > 1) {
            Toast.makeText(context, "Error, duplicated users. Please contact OnStage", Toast.LENGTH_LONG).show();
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

    @Override
    public void registerUser(String email, String password, String artisticName, String userType) {
        requestParams.put(Utils.DB_KEY_USER_EMAIL, email);
        requestParams.put(Utils.DB_KEY_USER_PASSWORD, password);
        requestParams.put(Utils.DB_KEY_USER_NAME, artisticName);
        requestParams.put(Utils.DB_KEY_USER_TYPE, userType);

        asyncHttpClient.get(urlRegister, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                onRegistrationSuccess(true, response);
                Log.i(TAG, "registerUser onSuccess: Loopj, JSONObject received");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                if (statusCode == 200) {
                    onRegistrationSuccess(true, response);
                    Log.i(TAG, "registerUser onSuccess: Loopj, JSONArray received");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                JSONArray userArray = new JSONArray();
                if (statusCode == 500) {
                    try {
                        userArray = errorResponse.getJSONArray("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (userArray.length() > 0) {
                        onRegistrationSuccess(true, userArray);
                        return;
                    }
                }
                Log.i(TAG, "registerUser onFailure: Loopj statusCode " + statusCode + errorResponse.toString());
                onRegistrationFailed(false, "Server error");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                onRegistrationFailed(false, responseString);
                Log.i(TAG, "registerUser onFailure: Loopj - " + statusCode + responseString);
            }
        });
    }

    private void onRegistrationSuccess(boolean success, JSONObject responseObject) {
        Log.i(TAG, "onRegistrationSuccess: responseObject");
    }

    private void onRegistrationSuccess(boolean success, JSONArray responseArray) {
        Log.i(TAG, "onRegistrationSuccess: responseArray");
        authServerListener.onAuthenticationCompleted(success, "User successfully registered");
    }

    private void onRegistrationFailed(boolean success, String errorMessage) {
        if (errorMessage == "") {
            errorMessage = "error in registration process";
        }
        authServerListener.onAuthenticationCompleted(success, errorMessage);
    }
}
