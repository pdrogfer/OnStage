package com.pdrogfer.onstage.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.pdrogfer.onstage.R;

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

        // TODO: 04/10/2016 check auth to skip all the process for normal use
    }
}
