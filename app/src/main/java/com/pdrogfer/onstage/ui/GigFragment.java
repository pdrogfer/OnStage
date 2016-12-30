package com.pdrogfer.onstage.ui;


import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class GigFragment extends Fragment {

    private final String TAG = "GigFragment";

    private DatabaseReference gigItem;
    private ValueEventListener valueEventListenerBackup;
    private Activity activity;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView tvVenue, tvTime, tvAddress, tvDate, tvFee, tvDescription;
    // private ImageView ivBanner; // not implemented yet

    public GigFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this.getActivity();

        if (getArguments().containsKey(GigDetailsActivity.EXTRA_GIG_DETAILS_KEY)) {
            // all this according FirebaseSamples - database
            // see GigDetailsActivity for implementation
            gigItem = FirebaseDatabase.getInstance().getReference()
                    .child(Utils.FIREBASE_GIGS).child(getArguments().getString(GigDetailsActivity.EXTRA_GIG_DETAILS_KEY));
        } else {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gig, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        collapsingToolbarLayout = (CollapsingToolbarLayout) getView().findViewById(R.id.toolbar_layout);
        tvVenue = (TextView) getView().findViewById(R.id.tv_details_venue);
        tvTime = (TextView) getView().findViewById(R.id.tv_details_time);
        tvAddress = (TextView) getView().findViewById(R.id.tv_details_address);
        tvFee = (TextView) getView().findViewById(R.id.tv_details_fee);
        tvDate = (TextView) getView().findViewById(R.id.tv_details_date);
        tvDescription = (TextView) getView().findViewById(R.id.tv_details_description);

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
                Toast.makeText(activity, R.string.warning_load_error, Toast.LENGTH_SHORT).show();
            }
        };
        gigItem.addValueEventListener(gigListener);

        // Keep copy of post listener so we can remove it when app stops
        valueEventListenerBackup = gigListener;
    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (valueEventListenerBackup != null) {
            gigItem.removeEventListener(valueEventListenerBackup);
        }
    }

}
