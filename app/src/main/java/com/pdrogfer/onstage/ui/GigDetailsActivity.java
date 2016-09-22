package com.pdrogfer.onstage.ui;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pdrogfer.onstage.R;
import com.pdrogfer.onstage.Utils;
import com.pdrogfer.onstage.model.Gig;

public class GigDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String EXTRA_GIG_DETAILS_KEY = "gig_details_key";
    private String gigIntentKey;

    private DatabaseReference gigReference;
    private ValueEventListener valueEventListenerBackup;

    SupportMapFragment mapFragment;
    GoogleMap googleMap;
    boolean isMapReady;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView ivCollapsingBackground;
    private TextView tvVenue, tvTime, tvCity, tvDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gig_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initMapView();

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
        gigReference = FirebaseDatabase.getInstance().getReference()
                .child(Utils.FIREBASE_GIGS).child(gigIntentKey);
        // TODO: 09/09/2016 Initialize views from details layout, override OnStart and assign values to UI fields

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        ivCollapsingBackground = (ImageView) findViewById(R.id.ivBigImage);
        tvVenue = (TextView) findViewById(R.id.tv_details_venue);
        tvTime = (TextView) findViewById(R.id.tv_details_time);
        tvCity = (TextView) findViewById(R.id.tv_details_city);
        tvDate = (TextView) findViewById(R.id.tv_details_date);

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
                tvTime.setText(gig.getStartTime());
                tvCity.setText(gig.getCity());
                tvDate.setText(gig.getDate());
                // TODO: 22/09/2016 add image field
                // TODO: 22/09/2016 add description field 

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(Utils.TAG, "GigDetailsActivity onCancelled: ", databaseError.toException());
                Toast.makeText(GigDetailsActivity.this, "Failed to load event details.", Toast.LENGTH_SHORT).show();
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

    private void initMapView() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentMap);
        mapFragment.getMapAsync(this);
        isMapReady = false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        isMapReady = true;
        // TODO: 19/09/2016 get location from gig
        double latitude = 38.3;
        double longitude = -0.4;
        String locationName = "this is the place";
        moveMapToGigLocation(latitude, longitude, locationName);
    }

    private void moveMapToGigLocation(double latitude, double longitude, String locationName) {
        // move map to location
        LatLng location = new LatLng(latitude, longitude);
        googleMap.addMarker(new MarkerOptions().position(location).title(locationName));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
    }
}
