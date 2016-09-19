package com.pdrogfer.onstage.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pdrogfer.onstage.R;

public class GigDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String EXTRA_GIG_DETAILS_KEY = "gig_details_key";
    private String gigIntentKey;

    SupportMapFragment mapFragment;
    GoogleMap googleMap;
    boolean isMapReady;


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

        // TODO: 09/09/2016 Initialize views from details layout, override OnStart and assign values to UI fields


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
