package com.pdrogfer.onstage.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
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

public class CreateGig extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CreateGig";

    Button btnTime, btnDate, btnCreateGig, btnCancel;
    private static final String DATE_PICKER = "datePicker";
    private static final String TIME_PICKER = "timePicker";
    protected static TextView tvArtisticName, tvDate, tvTime;
    protected static EditText etName, etVenue, etAddress, etFee, etDescription;
    protected static int gYear, gMonth, gDay, gHour, gMinute;
    private Date gigDate;
    protected static String artisticName, venue, price, address, description, timeString, dateString;

    private DatabaseReference fbDatabaseGigs;
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
        tvArtisticName = (TextView) findViewById(R.id.tvCreateGigArtisticName);
        tvDate = (TextView) findViewById(R.id.tvCreateGigDate);
        tvTime = (TextView) findViewById(R.id.tvCreateGigTime);
        etName = (EditText) findViewById(R.id.etCreateGigName);
        etVenue = (EditText) findViewById(R.id.etCreateGigWhere);
        etAddress = (EditText) findViewById(R.id.etCreateGigAddress);
        etFee = (EditText) findViewById(R.id.etCreateGigPrice);
        etDescription = (EditText) findViewById(R.id.etCreateGigDescription);

        // Click listeners
        btnTime.setOnClickListener(this);
        btnDate.setOnClickListener(this);
        btnCreateGig.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        fbDatabaseGigs = FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_GIGS);
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
                artisticName = etName.getText().toString();
                venue = etVenue.getText().toString();
                price = etFee.getText().toString();
                address = etAddress.getText().toString();
                description = etDescription.getText().toString();
                if (!validateInputGig(artisticName, venue, address, price, description)) {
                    Toast.makeText(this, R.string.warning_fill_all_fields, Toast.LENGTH_LONG).show();
                    break;
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
                break;
            case R.id.btnCancelGigCreate:
                startActivity(new Intent(this, GigsListActivity.class));
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
