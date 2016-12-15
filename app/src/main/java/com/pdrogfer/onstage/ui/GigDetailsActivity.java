package com.pdrogfer.onstage.ui;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pdrogfer.onstage.R;
import com.pdrogfer.onstage.Utils;
import com.pdrogfer.onstage.model.Gig;

public class GigDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_GIG_DETAILS_KEY = "gig_details_key";
    private String gigIntentKey;

    private DatabaseReference gigReference;
    private ValueEventListener valueEventListenerBackup;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView tvVenue, tvTime, tvAddress, tvDate, tvFee, tvDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gig_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_new_gig);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO: 27/10/2016 replace this when implementing SHARE action
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get gig key from intent
        gigIntentKey = getIntent().getStringExtra(EXTRA_GIG_DETAILS_KEY);
        if (gigIntentKey == null) {
            throw new IllegalArgumentException(getString(R.string.warning_no_key));
        }

        // all this according FirebaseSamples - database
        gigReference = FirebaseDatabase.getInstance().getReference()
                .child(Utils.FIREBASE_GIGS).child(gigIntentKey);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        tvVenue = (TextView) findViewById(R.id.tv_details_venue);
        tvTime = (TextView) findViewById(R.id.tv_details_time);
        tvAddress = (TextView) findViewById(R.id.tv_details_address);
        tvDate = (TextView) findViewById(R.id.tv_details_date);
        tvFee = (TextView) findViewById(R.id.tv_details_fee);
        tvDescription = (TextView) findViewById(R.id.tv_details_description);

    }

    @Override
    protected void onStart() {
        super.onStart();

        ValueEventListener gigListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Gig gig = dataSnapshot.getValue(Gig.class);

                collapsingToolbarLayout.setTitle(gig.getArtist());
                tvVenue.setText(gig.getVenue());
                tvTime.setText(gig.getStartTime() + " h");
                tvAddress.setText(gig.getAddress());
                tvDate.setText(gig.getDate());
                tvFee.setText(gig.getPrice());
                tvDescription.setText(gig.getDescription());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(Utils.TAG, "GigDetailsActivity onCancelled: ", databaseError.toException());
                Toast.makeText(GigDetailsActivity.this, R.string.warning_load_error, Toast.LENGTH_SHORT).show();
            }
        };
        gigReference.addValueEventListener(gigListener);

        // Keep copy of post listener so we can remove it when app stops
        valueEventListenerBackup = gigListener;
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Remove post value event listener
        if (valueEventListenerBackup != null) {
            gigReference.removeEventListener(valueEventListenerBackup);
        }
    }
}
