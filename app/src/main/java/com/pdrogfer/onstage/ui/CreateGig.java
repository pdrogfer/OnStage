package com.pdrogfer.onstage.ui;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.pdrogfer.onstage.R;
import com.pdrogfer.onstage.firebase_client.DatabaseFirebaseClient;
import com.pdrogfer.onstage.firebase_client.OnDbRequestCompleted;
import com.pdrogfer.onstage.model.Gig;
import com.pdrogfer.onstage.model.User;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateGig extends AppCompatActivity implements View.OnClickListener, OnDbRequestCompleted {

    private static final String TAG = "CreateGig";

    Button btnTime, btnDate, btnCreateGig, btnCancel;
    private static final String DATE_PICKER = "datePicker";
    private static final String TIME_PICKER = "timePicker";
    protected static TextView tvArtisticName, tvDate, tvTime;
    protected static EditText etName, etVenue, etFee, etDescription;
    protected static int gYear, gMonth, gDay, gHour, gMinute;
    private Date gigDate;
    protected static String artisticName, venue, price, description, timeString, dateString;

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
        btnCancel = (Button) findViewById(R.id.btnCancelGigCreate);
        tvArtisticName = (TextView) findViewById(R.id.tvCreateGigArtisticName);
        tvDate = (TextView) findViewById(R.id.tvCreateGigDate);
        tvTime = (TextView) findViewById(R.id.tvCreateGigTime);
        etName = (EditText) findViewById(R.id.etCreateGigName);
        etVenue = (EditText) findViewById(R.id.etCreateGigWhere);
        etFee = (EditText) findViewById(R.id.etCreateGigPrice);
        etDescription = (EditText) findViewById(R.id.etCreateGigDescription);

        // Click listeners
        btnTime.setOnClickListener(this);
        btnDate.setOnClickListener(this);
        btnCreateGig.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
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
                description = etDescription.getText().toString();
                if (!validateInputGig(artisticName, venue, price, description)) {
                    Toast.makeText(this, R.string.warning_fill_all_fields, Toast.LENGTH_LONG).show();
                    break;
                }
                long timestamp = System.currentTimeMillis();

                databaseClient.addGig(timestamp,
                        artisticName,
                        venue,
                        dateString,
                        timeString,
                        price,
                        description);
                break;
            case R.id.btnCancelGigCreate:
                startActivity(new Intent(this, GigsListActivity.class));
        }
    }

    private boolean validateInputGig(String artisticName, String venue, String fee, String description) {
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


    @Override
    public void onDbGigRequestCompleted(Gig gig) {
        Toast.makeText(this, gig.getArtist() + getString(R.string.new_gig_confirmation) + gig.getVenue(),
                Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, GigsListActivity.class));
        finish();
    }

    @Override
    public void onDbUserRequestCompleted(User user) {
        // Do nothing here, this callback is for RegisterActivity
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
