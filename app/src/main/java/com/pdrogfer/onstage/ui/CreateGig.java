package com.pdrogfer.onstage.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pdrogfer.onstage.R;
import com.pdrogfer.onstage.Utils;
import com.pdrogfer.onstage.firebase_client.DatabaseFirebaseClient;
import com.pdrogfer.onstage.firebase_client.OnDbRequestCompleted;
import com.pdrogfer.onstage.model.Gig;
import com.pdrogfer.onstage.model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateGig extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "CreateGig";

    private GoogleApiClient googleApiClient;

    Button btnTime, btnDate, btnCreateGig, btnCancel, btnAddress;
    private static final String DATE_PICKER = "datePicker";
    private static final String TIME_PICKER = "timePicker";
    private static final int PLACE_PICKER_REQUEST = 1;
    protected static TextView tvArtisticName, tvDate, tvTime, tvAddress;
    protected static EditText etName, etVenue, etFee, etDescription;
    protected static int gYear, gMonth, gDay, gHour, gMinute;
    private Date gigDate;
    private Place place;
    protected static String artisticName, venue, price, address, description, timeString, dateString;

    private DatabaseReference fbDatabaseGigs;
    private DatabaseReference fbDatabaseGeoFire;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_gig);

        gigDate = new Date();

        // Views
        btnTime = (Button) findViewById(R.id.btnCreateGigTime);
        btnDate = (Button) findViewById(R.id.btnCreateGigDate);
        btnCreateGig = (Button) findViewById(R.id.btnCreateGigCreate);
        btnCancel = (Button) findViewById(R.id.btnCancelGigCreate);
        btnAddress = (Button) findViewById(R.id.btnAddress);
        tvArtisticName = (TextView) findViewById(R.id.tvCreateGigArtisticName);
        tvDate = (TextView) findViewById(R.id.tvCreateGigDate);
        tvTime = (TextView) findViewById(R.id.tvCreateGigTime);
        tvAddress = (TextView) findViewById(R.id.tvCreateGigAddress);
        etName = (EditText) findViewById(R.id.etCreateGigName);
        etVenue = (EditText) findViewById(R.id.etCreateGigWhere);
        etFee = (EditText) findViewById(R.id.etCreateGigPrice);
        etDescription = (EditText) findViewById(R.id.etCreateGigDescription);

        // Click listeners
        btnTime.setOnClickListener(this);
        btnDate.setOnClickListener(this);
        btnCreateGig.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnAddress.setOnClickListener(this);

        fbDatabaseGigs = FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_GIGS);
        fbDatabaseGeoFire = FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_GEOFIRE);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCreateGigDate:
                DialogFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getSupportFragmentManager(), DATE_PICKER);
                break;
            case R.id.btnCreateGigTime:
                DialogFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getSupportFragmentManager(), TIME_PICKER);
                break;
            case R.id.btnCreateGigCreate:
                createGig();
                break;
            case R.id.btnCancelGigCreate:
                startActivity(new Intent(this, GigsListActivity.class));
                finish();
                break;
            case R.id.btnAddress:
                getPlace();
        }
    }

    private void createGig() {
        artisticName = etName.getText().toString();
        venue = etVenue.getText().toString();
        price = etFee.getText().toString();
        address = tvAddress.getText().toString();
        description = etDescription.getText().toString();
        if (!validateInputGig(artisticName, venue, address, price, description)) {
            Toast.makeText(this, R.string.warning_fill_all_fields, Toast.LENGTH_LONG).show();
            return;
        }
        long timestamp = System.currentTimeMillis();

        fbDatabaseGigs.child(String.valueOf(timestamp)).setValue(new Gig(timestamp,
                artisticName,
                venue,
                address,
                dateString,
                timeString,
                price,
                description)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    returnToListActivity();
                } else {
                    Toast.makeText(context, "Error creating event", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // TODO: 26/12/2016 store location in GeoFire
        GeoFire geoFire = new GeoFire(fbDatabaseGeoFire);
        if (place != null) {
            geoFire.setLocation(String.valueOf(timestamp),
                    new GeoLocation(place.getLatLng().latitude, place.getLatLng().longitude),
                    new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            if (error != null) {
                                System.err.println("There was an error saving the location to GeoFire: " + error);
                            } else {
                                System.out.println("Location saved on server successfully!");
                            }
                        }
                    });
        }
    }

    private void getPlace() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(data, this);
                String resultAddress = String.format("Place: %s, %s", place.getName(), place.getAddress());
                Toast.makeText(this, resultAddress, Toast.LENGTH_LONG).show();
                tvAddress.setText(resultAddress);

                // TODO: 26/12/2016 set place Latitude and Longitude

            }
        }
    }

    private boolean validateInputGig(String artisticName, String venue, String address, String fee, String description) {
        boolean validation = true;
        if (TextUtils.isEmpty(artisticName)) {
            etName.setError(getString(R.string.field_required_warning));
            validation = false;
        } else {
            etName.setError(null);
        }
        if (TextUtils.isEmpty(venue)) {
            etVenue.setError(getString(R.string.field_required_warning));
            validation = false;
        } else {
            etVenue.setError(null);
        }
        if (TextUtils.isEmpty(address)) {
            etVenue.setError(getString(R.string.field_required_warning));
            validation = false;
        } else {
            etVenue.setError(null);
        }
        if (TextUtils.isEmpty(fee)) {
            etFee.setError(getString(R.string.field_required_warning));
            validation = false;
        } else {
            etFee.setError(null);
        }
        if (TextUtils.isEmpty(description)) {
            etDescription.setError(getString(R.string.field_required_warning));
            validation = false;
        } else {
            etDescription.setError(null);
        }
        return validation;
    }

    private void returnToListActivity() {
        startActivity(new Intent(this, GigsListActivity.class));
        finish();
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(),
                    this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            if (minute < 10) {
                timeString = hourOfDay + ":0" + minute;
            } else {
                timeString = hourOfDay + ":" + minute;
            }
            tvTime.setText(timeString);
            gHour = hourOfDay;
            gMinute = minute;
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            gYear = year;
            gMonth = month;
            gDay = day;

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            // not including year, seems redundant
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM");
            dateString = simpleDateFormat.format(calendar.getTime());
            tvDate.setText(dateString);
        }
    }
}
