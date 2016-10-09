package com.pdrogfer.onstage.firebase_client;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pdrogfer.onstage.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by pedrogonzalezferrandez on 09/10/16.
 */

public class UserRegServerClient implements UserOperationsSuperClient {

    private static final String TAG = "UserRegServerClient";
    private static UserRegServerClient uniqueRegServerInstance;

    private final OnAuthenticationCompleted regServerListener;
    private final Context context;

    AsyncHttpClient asyncHttpClient;
    RequestParams requestParams;

    String jsonResponse;

    // choose depending if in local network or not
    private final String baseUrl = "http://192.168.1.4/onstage/";
    //    private final String baseUrl = "http://kavy.servehttp.com/onstage/";
    private String urlLogin = baseUrl + "login.php";
    private String urlRegister = baseUrl + "insert_new_user.php";

    private UserRegServerClient(Context context, OnAuthenticationCompleted authServerListener) {
        this.context = context;
        this.regServerListener = authServerListener;

        asyncHttpClient = new AsyncHttpClient();
        requestParams = new RequestParams();
    }

    public static synchronized UserRegServerClient getInstance(Context context, OnAuthenticationCompleted listener) {
        if (uniqueRegServerInstance == null) {
            uniqueRegServerInstance = new UserRegServerClient(context, listener);
        }
        return uniqueRegServerInstance;
    }

    @Override
    public void checkAuth() {
    }

    @Override
    public void signIn(String email, String password) {
    }

    private void onAuthFailed(boolean success, String errorMessage) {
        regServerListener.onAuthenticationCompleted(success, errorMessage);
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
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                onRegistrationFailed(false, errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                JSONArray userArray = new JSONArray();
                // for some reason, server returns 500 in after successful registration
                // Log.i(TAG, "onFailure: statusCode " + statusCode + " - " + errorResponse.toString());
                if (statusCode == 500) {
                    try {
                        userArray = errorResponse.getJSONArray("message");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (userArray.length() > 0) {
                        onRegistrationSuccess(true, userArray);
                    } else {
                        Log.i(TAG, "registerUser onFailure: Loopj statusCode " + statusCode + errorResponse.toString());
                        onRegistrationFailed(false, "Server error");
                    }
                } else if (statusCode == 400) {
                    onRegistrationFailed(false, "Error. User already exists");
                } else {
                    onRegistrationFailed(false, "Some error in registration process");
                }
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
        regServerListener.onAuthenticationCompleted(success, "User successfully registered");
    }

    private void onRegistrationSuccess(boolean success, JSONArray responseArray) {
        Log.i(TAG, "onRegistrationSuccess: responseArray: " + responseArray.toString());
        regServerListener.onAuthenticationCompleted(success, "User successfully registered");
    }

    private void onRegistrationFailed(boolean success, String errorMessage) {
        if (errorMessage == "") {
            errorMessage = "error in registration process";
        }
        regServerListener.onAuthenticationCompleted(success, errorMessage);
    }
}
