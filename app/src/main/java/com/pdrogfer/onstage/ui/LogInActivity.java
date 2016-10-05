package com.pdrogfer.onstage.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pdrogfer.onstage.R;
import com.pdrogfer.onstage.Utils;
import com.pdrogfer.onstage.firebase_client.OnAuthenticationCompleted;
import com.pdrogfer.onstage.firebase_client.UserAuthServerClient;
import com.pdrogfer.onstage.firebase_client.UserAuthSuperClient;

public class LogInActivity extends BaseActivity implements View.OnClickListener, OnAuthenticationCompleted {

    private EditText et_email, et_password;
    private Button btn_cancel, btn_login;

    private UserAuthSuperClient userAuth;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        et_email = (EditText) findViewById(R.id.field_email_login);
        et_password = (EditText) findViewById(R.id.field_password_login);
        btn_cancel = (Button) findViewById(R.id.btn_cancel_login);
        btn_login = (Button) findViewById(R.id.btn_login_login);
        btn_cancel.setOnClickListener(this);
        btn_login.setOnClickListener(this);

        // do authentication using Firebase
//        userAuth = UserAuthFirebaseClient.getInstance(this, this);
//        context = this;

        // do authentication using Server
        userAuth = UserAuthServerClient.getInstance(this, this);
        context = this;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel_login:
                startActivity(new Intent(LogInActivity.this, Presentation.class));
                finish();
                break;
            case R.id.btn_login_login:
//                String email = et_email.getText().toString();
//                String password = et_password.getText().toString();
//                if (!validateForm(email, password)) {
//                    return;
//                }
//                showProgressDialog();
//                logIn(email, password);

                // TESTING ONLY
                showProgressDialog();
                logIn(Utils.EMAIL_FOR_TESTING, Utils.PASSWORD_FOR_TESTING);
                break;
        }
    }

    private void logIn(String email, String password) {
        Log.d(Utils.LOG_IN, "LogInActivity");
        userAuth.signIn(email, password);
    }

    @Override
    public void onAuthenticationCompleted(Boolean success, String message) {
        hideProgressDialog();
        if (success) {
            startActivity(new Intent(LogInActivity.this, GigsListActivity.class));
            finish();
        } else {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateForm(String email, String password) {
        boolean result = true;
        if (TextUtils.isEmpty(email)) {
            et_email.setError(getString(R.string.field_required_warning));
            result = false;
        } else {
            et_email.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            et_password.setError(getString(R.string.field_required_warning));
            result = false;
        } else {
            et_password.setError(null);
        }

        if (TextUtils.getTrimmedLength(password) < 6) {
            et_password.setError("Too short, at least 6 characters");
            result = false;
        } else {
            et_password.setError(null);
        }
        return result;
    }
}
