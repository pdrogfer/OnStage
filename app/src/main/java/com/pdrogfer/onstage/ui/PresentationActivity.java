package com.pdrogfer.onstage.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.pdrogfer.onstage.R;
import com.pdrogfer.onstage.Utils;
import com.pdrogfer.onstage.model.User;

public class PresentationActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressDialog authProgressDialog;
    private AutoCompleteTextView et_email;
    private EditText et_password;
    private RadioGroup userTypeRadioGroup;
    private RadioButton userFanRadioButton, userMusicianRadioButton, userVenueRadioButton;
    private EditText nameField;
    Button btnLogin, btnRegister;

    FirebaseAuth fbAuth;
    FirebaseDatabase fbDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        et_email = (AutoCompleteTextView) findViewById(R.id.field_email_login);
        et_password = (EditText) findViewById(R.id.field_password_login);
        userTypeRadioGroup = (RadioGroup) findViewById(R.id.radioGroupUserType);
        userFanRadioButton = (RadioButton) findViewById(R.id.radioButtonFan);
        userMusicianRadioButton = (RadioButton) findViewById(R.id.radioButtonMusician);
        userVenueRadioButton = (RadioButton) findViewById(R.id.radioButtonVenue);
        nameField = (EditText) findViewById(R.id.field_name);
        btnLogin = (Button) findViewById(R.id.btn_goto_login);
        btnRegister = (Button) findViewById(R.id.button_register);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

        setAutocomplete();

        fbAuth = FirebaseAuth.getInstance();
        fbDatabase = FirebaseDatabase.getInstance();
    }

    private void setAutocomplete() {
        String[] emailProviders = getResources().getStringArray(R.array.emails_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, emailProviders);
        et_email.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        // TODO: 04/12/2016 remove dummy login before publish
        // String email = Utils.TEST_EMAIL_FAN;
        // String password = Utils.TEST_PASSWORD_FAN;
//        String email = Utils.TEST_EMAIL_MUSICIAN;
//        String password = Utils.TEST_PASSWORD_MUSICIAN;
                String email = et_email.getText().toString();
                String password = et_password.getText().toString();
        if (!validateForm(email, password)) {
            return;
        }
        switch (view.getId()) {
            case R.id.btn_goto_login:
                logIn(email, password);
                break;
            case R.id.button_register:
                // register
                userTypeRadioGroup.setVisibility(View.VISIBLE);
                nameField.setVisibility(View.VISIBLE);
//                Intent intentRegister = new Intent(PresentationActivity.this, RegisterActivity.class);
//                intentRegister.putExtra("email", email);
//                intentRegister.putExtra("password", password);
//                startActivity(intentRegister);
//                finish();
                break;
        }
    }

    private void logIn(String email, String password) {
        showAuthProgressDialog();
        Log.d(Utils.FIREBASE_CLIENT, "Presentation Activity");

        fbAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            onLoginSuccessful(task.getResult().getUser());
                        } else {
                            Toast.makeText(PresentationActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                            hideAuthProgressDialog();
                        }
                    }
                });
    }

    private void onLoginSuccessful(FirebaseUser firebaseUser) {
        String userName = extractNameFromEmail(firebaseUser.getEmail());
        writeUserToDatabase(firebaseUser.getUid(), userName, firebaseUser.getEmail());

        Toast.makeText(this, "Logged in", Toast.LENGTH_LONG).show();
        hideAuthProgressDialog();
        startActivity(new Intent(PresentationActivity.this, GigsListActivity.class));
        finish();
    }

    private void writeUserToDatabase(String uid, String userName, String email) {
        fbDatabase.getReference().child("users").child(uid).setValue(new User(uid, userName, email, Utils.TEST_USER_TYPE_FAN));
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

}
