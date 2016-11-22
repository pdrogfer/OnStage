package com.pdrogfer.onstage.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.pdrogfer.onstage.R;
import com.pdrogfer.onstage.Utils;
import com.pdrogfer.onstage.firebase_client.OnAuthenticationCompleted;
import com.pdrogfer.onstage.firebase_client.UserAuthFirebaseClient;
import com.pdrogfer.onstage.firebase_client.UserOperationsSuperClient;
import com.pdrogfer.onstage.model.UserType;

// Using an Interface to receive updates from UserAuthFirebaseClient-UserAuthServerClient
public class RegisterActivity extends BaseActivity implements View.OnClickListener, OnAuthenticationCompleted {

    private static final String TAG = "RegisterActivity";

    private EditText emailField, passwordField, nameField;
    private RadioGroup userTypeRadioGroup;
    private RadioButton userFanRadioButton, userMusicianRadioButton, userVenueRadioButton;
    private Button cancelButton, registerButton;

    private String emailValue, passwordValue, artisticNameValue, userTypeValue, isUserActiveValue;
    private ProgressDialog regProgressDialog;

    private UserOperationsSuperClient userRegistration;
    Context context;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // do authentication using Firebase
        userRegistration = UserAuthFirebaseClient.getInstance(this, this);
        context = this;

        // Views
        emailField = (EditText) findViewById(R.id.field_email);
        passwordField = (EditText) findViewById(R.id.field_password);
        nameField = (EditText) findViewById(R.id.field_name);
        userTypeRadioGroup = (RadioGroup) findViewById(R.id.radioGroupUserType);
        userFanRadioButton = (RadioButton) findViewById(R.id.radioButtonFan);
        userMusicianRadioButton = (RadioButton) findViewById(R.id.radioButtonMusician);
        userVenueRadioButton = (RadioButton) findViewById(R.id.radioButtonVenue);
        cancelButton = (Button) findViewById(R.id.btn_cancel_register);
        registerButton = (Button) findViewById(R.id.btn_register_register);

        // Click listeners
        cancelButton.setOnClickListener(this);
        registerButton.setOnClickListener(this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel_register:
                startActivity(new Intent(RegisterActivity.this, Presentation.class));
                finish();
                break;
            case R.id.btn_register_register:

                // for testing ONLY, remove in production
                emailField.setText(Utils.TEST_EMAIL);
                passwordField.setText(Utils.TEST_PASSWORD);
                nameField.setText(Utils.TEST_NAME);
                userTypeValue = String.valueOf(UserType.MUSICIAN);
                // ------- end testing block

                emailValue = emailField.getText().toString();
                passwordValue = passwordField.getText().toString();
                artisticNameValue = nameField.getText().toString();
                isUserActiveValue = "1";
                if (!validateForm(emailValue, passwordValue, artisticNameValue, userTypeValue)) {
                    return;
                }
                register();
                break;
        }
    }

    private void register() {
        showRegProgressDialog();
        Log.d(Utils.LOG_IN, "Register");
        userRegistration.registerUser(emailValue, passwordValue, artisticNameValue, userTypeValue);
    }

    private boolean validateForm(String email, String password, String artisticName, String userType) {
        boolean result = true;
        if (TextUtils.isEmpty(email)) {
            emailField.setError(getString(R.string.field_required_warning));
            result = false;
        } else {
            emailField.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            passwordField.setError(getString(R.string.field_required_warning));
            result = false;
        } else {
            passwordField.setError(null);
        }

        if (TextUtils.getTrimmedLength(password) < 6) {
            passwordField.setError("Too short, at least 6 characters");
            result = false;
        } else {
            passwordField.setError(null);
        }

        if (TextUtils.isEmpty(artisticName)) {
            nameField.setError(getString(R.string.field_required_warning));
            result = false;
        } else {
            nameField.setError(null);
        }
        return result;
    }

    // this method works ok for both auth and reg cases
    @Override
    public void onAuthenticationCompleted(Boolean success, String name, String email, String password, String user_type) {
        hideRegProgressDialog();
        if (!success) {
            Toast.makeText(this, "Error registering user", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSignOut() {
        // do nothing here
    }

    public void onRadioBtnClick(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.radioButtonFan:
                if (checked) {
                    userTypeValue = String.valueOf(UserType.FAN);
                    Log.i(Utils.TAG, "onRadioBtnClick: UserType: " + userTypeValue);
                }
                break;
            case R.id.radioButtonMusician:
                if (checked) {
                    userTypeValue = String.valueOf(UserType.MUSICIAN);
                    Log.i(Utils.TAG, "onRadioBtnClick: UserType: " + userTypeValue);
                }
                break;
            case R.id.radioButtonVenue:
                if (checked) {
                    userTypeValue = String.valueOf(UserType.VENUE);
                    Log.i(Utils.TAG, "onRadioBtnClick: UserType: " + userTypeValue);
                }
                break;
            default:
                userTypeValue = String.valueOf(UserType.FAN);
        }
    }

    // TODO: 22/11/2016 try to use BaseActivity methods instead
    private void showRegProgressDialog() {
        regProgressDialog = new ProgressDialog(this);
        regProgressDialog.setCancelable(false);
        regProgressDialog.setMessage("Please wait...");
        regProgressDialog.show();
    }

    private void hideRegProgressDialog() {
        if (regProgressDialog != null && regProgressDialog.isShowing()) {
            regProgressDialog.dismiss();
        }
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Register Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
