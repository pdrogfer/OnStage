package com.pdrogfer.onstage.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.pdrogfer.onstage.R;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnProfileDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        btnProfileDelete = (Button) findViewById(R.id.btn_profile_delete);

        btnProfileDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // TODO: 28/11/2016 delete user from Firebase Auth
    }
}
