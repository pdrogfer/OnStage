package com.pdrogfer.onstage.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.pdrogfer.onstage.R;

public class GigDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_GIG_DETAILS_KEY = "gig_details_key";
    private String gigIntentKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gig_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get gig key from intent
        gigIntentKey = getIntent().getStringExtra(EXTRA_GIG_DETAILS_KEY);
        if (gigIntentKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // all this according FirebaseSamples - database

        // TODO: 09/09/2016 Initialize Database, pass gigIntentKey to get the Gig we are interested in

        // TODO: 09/09/2016 Initialize views from details layout, override OnStart and assign values to UI fields


    }
}
