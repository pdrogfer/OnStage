package com.pdrogfer.onstage.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pdrogfer.onstage.R;
import com.pdrogfer.onstage.Utils;
import com.pdrogfer.onstage.firebase_client.OnFirebaseUserCompleted;
import com.pdrogfer.onstage.firebase_client.UserFirebaseClient;
import com.pdrogfer.onstage.firebase_client.UserOperationsSuperClient;
import com.pdrogfer.onstage.model.User;

public class LogInActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "LogInActivity";
    private EditText et_email, et_password;
    private Button btn_cancel, btn_login;

    ProgressDialog authProgressDialog;
    // private UserOperationsSuperClient userAuth;
    FirebaseAuth fbAuth;
    FirebaseDatabase fbDatabase;
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
//        userAuth = UserFirebaseClient.getInstance(this, this);
        fbAuth = FirebaseAuth.getInstance();
        fbDatabase = FirebaseDatabase.getInstance();
        context = this;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel_login:
                startActivity(new Intent(LogInActivity.this, PresentationActivity.class));
                finish();
                break;
            case R.id.btn_login_login:
                // TODO: 04/12/2016 remove dummy login before publish
                // String email = Utils.TEST_EMAIL_FAN;
                // String password = Utils.TEST_PASSWORD_FAN;
                String email = Utils.TEST_EMAIL_MUSICIAN;
                String password = Utils.TEST_PASSWORD_MUSICIAN;
//                String email = et_email.getText().toString();
//                String password = et_password.getText().toString();
                if (!validateForm(email, password)) {
                    return;
                }
                logIn(email, password);
                break;
        }
    }

    private void showAuthProgressDialog() {
        authProgressDialog = new ProgressDialog(this);
        authProgressDialog.setCancelable(false);
        authProgressDialog.setMessage(getString(R.string.dialog_wait));
        authProgressDialog.show();
    }

    private void hideAuthProgressDialog() {
        if (authProgressDialog != null) {
            authProgressDialog.dismiss();
        }
    }

    private void logIn(String email, String password) {
        showAuthProgressDialog();
        Log.d(Utils.FIREBASE_CLIENT, "LogInActivity");

        fbAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    onLoginSuccessful(task.getResult().getUser());
                } else {
                    Toast.makeText(LogInActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void onLoginSuccessful(FirebaseUser firebaseUser) {
        String userName = extractNameFromEmail(firebaseUser.getEmail());
        writeUserToDatabase(firebaseUser.getUid(), userName, firebaseUser.getEmail());

        Toast.makeText(this, "Logged in", Toast.LENGTH_LONG).show();
        startActivity(new Intent(LogInActivity.this, GigsListActivity.class));
        finish();
    }

    private void writeUserToDatabase(String uid, String userName, String email) {
        fbDatabase.getReference().child("users").child(uid).setValue(new User(uid, userName, email, Utils.TEST_USER_TYPE_FAN));
    }

    private void getUserfromDatabase(String uid) {
        fbDatabase.getReference().child(uid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(Utils.TAG, "getUser:onCancelled", databaseError.toException());
                    }
                }
        );
    }

    private String extractNameFromEmail(String email) {
        if (email.contains("@")) {
            String parts[] = email.split("@");
            return parts[0];
        } else {
            return email;
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
            et_password.setError(getString(R.string.warning_pwd_too_short));
            result = false;
        } else {
            et_password.setError(null);
        }
        return result;
    }
}
