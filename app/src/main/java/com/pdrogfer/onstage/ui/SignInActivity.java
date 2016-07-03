package com.pdrogfer.onstage.ui;

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

import com.pdrogfer.onstage.firebase_client.OnAuthenticationCompleted;
import com.pdrogfer.onstage.R;
import com.pdrogfer.onstage.firebase_client.UserAuthFirebaseClient;
import com.pdrogfer.onstage.Utils;
import com.pdrogfer.onstage.model.UserType;

// Using an Interface to receive updates from UserAuthFirebaseClient
public class SignInActivity extends BaseActivity implements View.OnClickListener, OnAuthenticationCompleted {

    private EditText mEmailField, mPasswordField, mNameField;
    private RadioGroup mUserTypeRadioGroup;
    private RadioButton mUserFanRadioButton, mUserMusicianRadioButton, mUserVenueRadioButton;
    private String email, password, artisticName, userType;
    private Button mLogInButton, mRegisterButton;

    private UserAuthFirebaseClient userAuth;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        userAuth = UserAuthFirebaseClient.getInstance(this, this);
        context = this;

        // Views
        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);
        mNameField = (EditText) findViewById(R.id.field_name);
        mUserTypeRadioGroup = (RadioGroup) findViewById(R.id.radioGroupUserType);
        mUserFanRadioButton = (RadioButton) findViewById(R.id.radioButtonFan);
        mUserMusicianRadioButton = (RadioButton) findViewById(R.id.radioButtonMusician);
        mUserVenueRadioButton = (RadioButton) findViewById(R.id.radioButtonVenue);
        mLogInButton = (Button) findViewById(R.id.button_log_in);
        mRegisterButton = (Button) findViewById(R.id.button_register);

        // Click listeners
        mLogInButton.setOnClickListener(this);
        mRegisterButton.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check auth on Activity start
        userAuth.checkAuth();

        // set default userType ?
        userType = String.valueOf(UserType.FAN);
    }

    private void logIn() {
        Log.d(Utils.LOG_IN, "LogIn");

        userAuth.signIn(email, password, artisticName, userType);
    }

    private void register() {
        Log.d(Utils.LOG_IN, "Register");

        userAuth.registerUser(email, password, artisticName, userType);
    }

    @Override
    public void onClick(View v) {
        email = mEmailField.getText().toString();
        password = mPasswordField.getText().toString();
        artisticName = mNameField.getText().toString();
        if (!validateForm(email, password, artisticName, userType)) {
            return;
        }
        showProgressDialog();
        switch (v.getId()) {
            case R.id.button_log_in:
                logIn();
                break;
            case R.id.button_register:
                register();
                break;
        }
    }

    private boolean validateForm(String email, String password, String artisticName, String userType) {
        boolean result = true;
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError(getString(R.string.field_required_warning));
            result = false;
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError(getString(R.string.field_required_warning));
            result = false;
        } else {
            mPasswordField.setError(null);
        }

        if (TextUtils.isEmpty(artisticName)) {
            mNameField.setError(getString(R.string.field_required_warning));
            result = false;
        } else {
            mNameField.setError(null);
        }
        // userType is set to FAN by default
        return result;
    }



    @Override
    public void onAuthenticationCompleted(Boolean success, String message) {
        hideProgressDialog();
        if (success) {
            startActivity(new Intent(SignInActivity.this, GigsListActivity.class));
            finish();
        } else {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    public void onRadioBtnClick(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.radioButtonFan:
                if (checked) {
                    userType = String.valueOf(UserType.FAN);
                    Log.i(Utils.TAG, "onRadioBtnClick: UserType: " + userType);
                }
                    break;
            case R.id.radioButtonMusician:
                if (checked) {
                    userType = String.valueOf(UserType.MUSICIAN);
                    Log.i(Utils.TAG, "onRadioBtnClick: UserType: " + userType);
                }
                    break;
            case R.id.radioButtonVenue:
                if (checked) {
                    userType = String.valueOf(UserType.VENUE);
                    Log.i(Utils.TAG, "onRadioBtnClick: UserType: " + userType);
                }
                    break;
        }
    }
}
