package com.pdrogfer.onstage.ui;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pdrogfer.onstage.R;
import com.pdrogfer.onstage.UserAuthFirebaseClient;
import com.pdrogfer.onstage.Utils;

public class SignInActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mArtisticNameField;
    private Button mLogInButton;
    private Button mRegisterButton;

    private UserAuthFirebaseClient userAuth;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        userAuth = UserAuthFirebaseClient.getInstance();
        context = this;

        // Views
        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);
        mArtisticNameField = (EditText) findViewById(R.id.field_name);
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
    }

    private void logIn() {
        Log.d(Utils.LOG_IN, "LogIn");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();


    }

    private void register() {
        Log.d(Utils.LOG_IN, "Register");
        if (!validateForm()) {
            return;
        }

        showProgressDialog();
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        String artisticName = mArtisticNameField.getText().toString();

        userAuth.signIn(context, email, password, artisticName);
    }



    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(mEmailField.getText().toString())) {
            mEmailField.setError(getString(R.string.field_required_warning));
            result = false;
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(mPasswordField.getText().toString())) {
            mPasswordField.setError(getString(R.string.field_required_warning));
            result = false;
        } else {
            mPasswordField.setError(null);
        }

        if (TextUtils.isEmpty(mArtisticNameField.getText().toString())) {
            mArtisticNameField.setError(getString(R.string.field_required_warning));
            result = false;
        } else {
            mArtisticNameField.setError(null);
        }

        return result;
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_log_in:
                logIn();
                break;
            case R.id.button_register:
                register();
                break;
        }
    }
}
