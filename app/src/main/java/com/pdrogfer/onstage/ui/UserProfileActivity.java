package com.pdrogfer.onstage.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.pdrogfer.onstage.R;
import com.pdrogfer.onstage.firebase_client.OnFirebaseUserCompleted;
import com.pdrogfer.onstage.firebase_client.UserFirebaseClient;
import com.pdrogfer.onstage.firebase_client.UserOperationsSuperClient;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener, OnFirebaseUserCompleted {

    Context context;
    Button btnProfileDelete;
    private UserOperationsSuperClient userAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        btnProfileDelete = (Button) findViewById(R.id.btn_profile_delete);
        btnProfileDelete.setOnClickListener(this);

        // do authentication using Firebase
        userAuth = UserFirebaseClient.getInstance(this, this);
        context = this;
    }

    @Override
    public void onClick(View view) {
        userAuth.deleteUser();
    }

    @Override
    public void onLogInCompleted(Boolean success, String name, String email, String user_type) {
        // Don't implement
    }

    @Override
    public void onRegistrationCompleted(Boolean success, String name, String email, String userType) {

    }

    @Override
    public void onSignOut() {
        // TODO: 29/11/2016 implement menu to sign out, like in list activity
    }

    @Override
    public void onUserDeleted() {
        Toast.makeText(this, "Account successfully deleted", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, PresentationActivity.class));
        finish();
    }
}
