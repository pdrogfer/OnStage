package com.pdrogfer.onstage.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.pdrogfer.onstage.R;
import com.pdrogfer.onstage.database.Contract;
import com.pdrogfer.onstage.database.UsersContentProvider;

public class Presentation extends AppCompatActivity implements View.OnClickListener {

    Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presentation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnLogin = (Button) findViewById(R.id.button_log_in);
        btnRegister = (Button) findViewById(R.id.button_register);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);

        Stetho.initializeWithDefaults(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_log_in:
                // login
                startActivity(new Intent(Presentation.this, LogInActivity.class));
                finish();
                break;
            case R.id.button_register:
                // register
                startActivity(new Intent(Presentation.this, RegisterActivity.class));
                finish();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (checkAuthActiveUser()) {
            startActivity(new Intent(Presentation.this, GigsListActivity.class));
        }
    }

    private boolean checkAuthActiveUser() {


        Uri users = UsersContentProvider.CONTENT_URI;
        // TODO: 12/10/16 use column user_active to find if the user was logged in already
        // instead of tempName
        String tempName = null;
        Cursor cursor = getContentResolver().query(users, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                tempName = cursor.getString(cursor.getColumnIndex(Contract.COLUMN_NAME));
                Toast.makeText(this, tempName, Toast.LENGTH_LONG).show();
            } while (cursor.moveToNext());
        }
        
        return (tempName != null);
    }
}
