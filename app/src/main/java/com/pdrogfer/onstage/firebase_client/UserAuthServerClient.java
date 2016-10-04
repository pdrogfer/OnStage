package com.pdrogfer.onstage.firebase_client;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pdrogfer.onstage.Utils;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by pdro on 01/10/2016.
 *
 * This class perform authentication operations to comply with Rubric
 */

public class UserAuthServerClient implements UserAuthSuperClient {

    private static UserAuthServerClient uniqueAuthServerInstance;

    private final OnAuthenticationCompleted authServerListener;
    private final Context context;

    AsyncHttpClient asyncHttpClient;
    RequestParams requestParams;

    String jsonResponse;

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
    public void signIn(String email, String password, String artisticName, String userType) {

    }

    @Override
    public void signIn(String email, String password) {
        requestParams.put(Utils.EMAIL, email);
        requestParams.put(Utils.PASSWORD, password);

        asyncHttpClient.post("http://kavy.servehttp.com/login_json.php", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                jsonResponse = response.toString();
                onAuthSuccess(true, jsonResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    @Override
    public void registerUser(String email, String password, String artisticName, String userType) {

    }

    private void onAuthFailed(String errorMessage) {

    }

    private void onAuthSuccess(boolean success, String jsonResponse) {
        authServerListener.onAuthenticationCompleted(success, jsonResponse);
    }
}
