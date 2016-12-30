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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pdrogfer.onstage.R;
import com.pdrogfer.onstage.Utils;
import com.pdrogfer.onstage.model.User;

public class PresentationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PresentationActivity";

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

    @Override
    protected void onStart() {
        super.onStart();
        if (fbAuth.getCurrentUser() != null) {
            goToListActivity();
        }
    }

    private void setAutocomplete() {
        String[] emailProviders = getResources().getStringArray(R.array.emails_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, emailProviders);
        et_email.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();
        if (!validateFormLogin(email, password)) {
            return;
        }
        switch (view.getId()) {
            case R.id.btn_goto_login:
                logIn(email, password);
                break;
            case R.id.button_register:
                if (userTypeRadioGroup.getVisibility() == View.VISIBLE) {
                    registerUser();

                } else {
                    // open register fields
                    userTypeRadioGroup.setVisibility(View.VISIBLE);
                    userFanRadioButton.setVisibility(View.VISIBLE);
                    userMusicianRadioButton.setVisibility(View.VISIBLE);
                    userVenueRadioButton.setVisibility(View.VISIBLE);
                    nameField.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Please fill in the details", Toast.LENGTH_SHORT).show();
                }
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
        getUserFromDatabase(firebaseUser.getUid()); // callback received in onUserReceived()
    }


    private void registerUser() {
        showAuthProgressDialog();
        String emailValue = et_email.getText().toString();
        String passwordValue = et_password.getText().toString();
        String artisticNameValue = nameField.getText().toString();
        String userTypeValue = getUserType();
        if (validateFormRegister(emailValue, passwordValue, artisticNameValue, userTypeValue)) {
            fbAuth.createUserWithEmailAndPassword(emailValue, passwordValue)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                onRegistrationSuccessful(task.getResult().getUser());
                            } else {
                                Toast.makeText(PresentationActivity.this, "Registration Error", Toast.LENGTH_SHORT).show();
                                hideAuthProgressDialog();
                            }
                        }
                    });
        } else {
            hideAuthProgressDialog();
            return;
        }

    }

    private void onRegistrationSuccessful(FirebaseUser user) {
        // Write user to Firebase Database
        String userEmail = et_email.getText().toString();
        String userName = nameField.getText().toString();
        String userType = getUserType();
        fbDatabase.getReference().child("users").child(user.getUid()).setValue(
                new User(user.getUid(), userName, userEmail, userType));
        getUserFromDatabase(user.getUid());
    }

    private boolean validateFormLogin(String email, String password) {
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

    private boolean validateFormRegister(String email, String password, String artisticName, String userTypeValue) {
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

        if (TextUtils.isEmpty(artisticName)) {
            nameField.setError(getString(R.string.field_required_warning));
            result = false;
        } else {
            nameField.setError(null);
        }

        if (userTypeValue == null) {
            Toast.makeText(this, "Please select a user type", Toast.LENGTH_SHORT).show();
            result = false;
        }
        return result;
    }

    private String getUserType() {
        if (userFanRadioButton.isChecked()) {
            // Toast.makeText(this, "user type = fan", Toast.LENGTH_SHORT).show();
            return Utils.USER_FAN;
        } else if (userMusicianRadioButton.isChecked()) {
            // Toast.makeText(this, "user type = musician", Toast.LENGTH_SHORT).show();
            return Utils.USER_MUSICIAN;
        } else if (userVenueRadioButton.isChecked()) {
            // Toast.makeText(this, "user type = venue", Toast.LENGTH_SHORT).show();
            return Utils.USER_VENUE;
        } else {
            return null;
        }

    }

    private void getUserFromDatabase(String Uid) {
        fbDatabase.getReference().child("users").child(Uid).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        onUserReceived(user);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                }
        );
    }

    private void onUserReceived(User user) {

        // Toast.makeText(this, "user logged and retrieved: " + user.toString(), Toast.LENGTH_SHORT).show();
        Log.i(TAG, "onUserReceived: " + user.toString());
        Utils.storeUserToSharedPrefs(user.getUid(), user.getName(), user.getEmail(), user.getUserType(), this);
        goToListActivity();
    }

    private void goToListActivity() {
        hideAuthProgressDialog();
        startActivity(new Intent(PresentationActivity.this, GigsListActivity.class));
        finish();
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
