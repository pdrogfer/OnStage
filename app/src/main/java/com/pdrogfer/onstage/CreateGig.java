package com.pdrogfer.onstage;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.pdrogfer.onstage.firebase_client.DatabaseFirebaseClient;
import com.pdrogfer.onstage.firebase_client.OnDbRequestCompleted;
import com.pdrogfer.onstage.model.Gig;
import com.pdrogfer.onstage.ui.GigsListActivity;

import java.util.Calendar;

public class CreateGig extends AppCompatActivity implements View.OnClickListener, OnDbRequestCompleted {

    Button btnTime, btnDate, btnCreateGig;
    protected static TextView tvDate, tvTime;
    protected static EditText etVenue, etPrice;
    protected static int gYear, gMonth, gDay, gHour, gMinute;
    protected static String artisticName, venue, price;

    private DatabaseFirebaseClient databaseClient;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_gig);

        databaseClient = DatabaseFirebaseClient.getInstance(this, this);
        context = this;

        // Views
        btnTime = (Button) findViewById(R.id.btnCreateGigTime);
        btnDate = (Button) findViewById(R.id.btnCreateGigDate);
        btnCreateGig = (Button) findViewById(R.id.btnCreateGigCreate);
        tvDate = (TextView) findViewById(R.id.tvCreateGigDate);
        tvTime = (TextView) findViewById(R.id.tvCreateGigTime);
        etVenue = (EditText) findViewById(R.id.etCreateGigWhere);
        etPrice = (EditText) findViewById(R.id.etCreateGigPrice);

        // Click listeners
        btnTime.setOnClickListener(this);
        btnDate.setOnClickListener(this);
        btnCreateGig.setOnClickListener(this);

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
                artisticName = Utils.getArtisticName(Utils.ARTISTIC_NAME, context);
                venue = etVenue.getText().toString();
                price = etPrice.getText().toString();
                long timestamp = System.currentTimeMillis();
                databaseClient.addGig(timestamp,
                        artisticName,
                        venue,
                        gYear + "year " + gMonth + " " + gDay,
                        gHour + "h " + gMinute + "min",
                        price);
        }
    }

    @Override
    public void onDbRequestCompleted(Gig gig) {
        Toast.makeText(this, gig.getArtist() + " you have a new Gig at " + gig.getVenue(),
                Toast.LENGTH_LONG).show();
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
