package com.pdrogfer.onstage.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.pdrogfer.onstage.R;
import com.pdrogfer.onstage.Utils;
import com.pdrogfer.onstage.firebase_client.OnAuthenticationCompleted;
import com.pdrogfer.onstage.firebase_client.UserAuthFirebaseClient;
import com.pdrogfer.onstage.firebase_client.UserOperationsSuperClient;

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

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel_register:
                startActivity(new Intent(RegisterActivity.this, PresentationActivity.class));
                finish();
                break;
            case R.id.btn_register_register:
                emailValue = emailField.getText().toString();
                passwordValue = passwordField.getText().toString();
                artisticNameValue = nameField.getText().toString();
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
            passwordField.setError(getString(R.string.warning_pwd_too_short));
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
    public void onAuthenticationCompleted(Boolean success, String name, String email, String userType) {
        hideRegProgressDialog();
        if (!success) {
            Toast.makeText(this, "Error registering user", Toast.LENGTH_LONG).show();
        } else {
            String message = "Registration successful: " + name + " " + email + " " + userType;
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, GigsListActivity.class));
        }
    }

    @Override
    public void onSignOut() {
        // do nothing here
    }

    @Override
    public void onUserDeleted() {

    }

    public void onRadioBtnClick(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.radioButtonFan:
                if (checked) {
                    userTypeValue = Utils.USER_FAN;
                    Log.i(Utils.TAG, "onRadioBtnClick: UserType: " + userTypeValue);
                }
                break;
            case R.id.radioButtonMusician:
                if (checked) {
                    userTypeValue = Utils.USER_MUSICIAN;
                    Log.i(Utils.TAG, "onRadioBtnClick: UserType: " + userTypeValue);
                }
                break;
            case R.id.radioButtonVenue:
                if (checked) {
                    userTypeValue = Utils.USER_MUSICIAN;
                    Log.i(Utils.TAG, "onRadioBtnClick: UserType: " + userTypeValue);
                }
                break;
            default:
                userTypeValue = Utils.USER_FAN;
        }
    }

    // TODO: 22/11/2016 try to use BaseActivity methods instead
    private void showRegProgressDialog() {
        regProgressDialog = new ProgressDialog(this);
        regProgressDialog.setCancelable(false);
        regProgressDialog.setMessage(getString(R.string.dialog_wait));
        regProgressDialog.show();
    }

    private void hideRegProgressDialog() {
        if (regProgressDialog != null && regProgressDialog.isShowing()) {
            regProgressDialog.dismiss();
        }
    }


    @Override
    public void onStop() {
        super.onStop();

    }
}
