package com.pdrogfer.onstage;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pdrogfer.onstage.model.Gig;

import java.util.Calendar;

public class CreateGig extends AppCompatActivity implements View.OnClickListener {

    // TODO: 26/06/16 THIS CLASS IS A UI CLASS, SO SHOULD NOT HANDLE LOGIC. DELEGATE GIG CREATION TO MYPRESENTER!!!

    Button btnTime, btnDate, btnCreateGig;
    protected static TextView tvDate, tvTime;
    protected static EditText etVenue, etPrice;
    protected static int gYear, gMonth, gDay, gHour, gMinute;
    protected static String venue, price;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mGigsRef = mRootRef.child(Utils.FIREBASE_GIGS);
    Gig tempGig; // to manipulate temporary gigs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_gig);

        btnTime = (Button) findViewById(R.id.btnCreateGigTime);
        btnDate = (Button) findViewById(R.id.btnCreateGigDate);
        btnCreateGig = (Button) findViewById(R.id.btnCreateGigCreate);
        tvDate = (TextView) findViewById(R.id.tvCreateGigDate);
        tvTime = (TextView) findViewById(R.id.tvCreateGigTime);
        etVenue = (EditText) findViewById(R.id.etCreateGigWhere);
        etPrice = (EditText) findViewById(R.id.etCreateGigPrice);
        btnTime.setOnClickListener(this);
        btnDate.setOnClickListener(this);
        btnCreateGig.setOnClickListener(this);
    }

    private void addGig() {
        venue = etVenue.getText().toString();
        price = etPrice.getText().toString();
        long timeNow = System.currentTimeMillis();

        // TODO: 06/06/16 use real user instead of demo 'El camion de la basura'
        tempGig = new Gig(timeNow, "El Camion de la Basura",
                gYear + "year " + gMonth + " " + gDay + ", " + gHour + "h " + gMinute + "min",
                venue, price);
        // TODO: 26/06/16 INSTEAD OF CREATING THE GIG HERE, PASS VALUES BACK TO MYPRESENTER AND CREATE THERE
        mGigsRef.child(String.valueOf(timeNow)).setValue(tempGig);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCreateGigDate:
                DialogFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getSupportFragmentManager(), "datePicker");
                break;
            case R.id.btnCreateGigTime:
                DialogFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.show(getSupportFragmentManager(), "timePicker");
                break;
            case R.id.btnCreateGigCreate:
                addGig();
        }
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
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            tvTime.setText(hourOfDay + "h " + minute + "min");
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

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            tvDate.setText(year + " " + month + " " + day);
            gYear = year;
            gMonth = month;
            gDay = day;
        }
    }
}
