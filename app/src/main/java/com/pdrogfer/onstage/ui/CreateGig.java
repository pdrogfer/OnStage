package com.pdrogfer.onstage.ui;

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

import com.pdrogfer.onstage.R;
import com.pdrogfer.onstage.Utils;
import com.pdrogfer.onstage.firebase_client.DatabaseFirebaseClient;
import com.pdrogfer.onstage.firebase_client.OnDbRequestCompleted;
import com.pdrogfer.onstage.model.Gig;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateGig extends AppCompatActivity implements View.OnClickListener, OnDbRequestCompleted {

    Button btnTime, btnDate, btnCreateGig;
    protected static TextView tvDate, tvTime;
    protected static EditText etVenue, etPrice;
    protected static int gYear, gMonth, gDay, gHour, gMinute;
    private Date gigDate;
    protected static String artisticName, venue, price;

    private DatabaseFirebaseClient databaseClient;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_gig);

        databaseClient = DatabaseFirebaseClient.getInstance(this, this);
        context = this;

        gigDate = new Date();

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
                artisticName = Utils.getArtisticName(Utils.DB_KEY_USER_NAME, context);
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
            return new TimePickerDialog(getActivity(),
                    this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            String timeString;
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
            tvDate.setText(year + " " + month + " " + day);
            gYear = year;
            gMonth = month;
            gDay = day;

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy");
            String dateString = simpleDateFormat.format(calendar.getTime());
            tvDate.setText(dateString);
        }
    }
}
