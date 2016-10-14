package com.pdrogfer.onstage.ui;


import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import static com.pdrogfer.onstage.ui.CreateGig.tvDate;
import static com.pdrogfer.onstage.ui.CreateGig.tvTime;

/**
 * A simple {@link Fragment} subclass.
 */
public class GigFragment extends Fragment {

    private DatabaseReference gigItem;
    private ValueEventListener valueEventListenerBackup;
    private Activity activity;

    private TextView tvVenue, tvTime, tvCity, tvDate;

    public GigFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this.getActivity();
        tvVenue = (TextView) activity.findViewById(R.id.tv_details_venue);
        tvTime = (TextView) activity.findViewById(R.id.tv_details_time);
        tvCity = (TextView) activity.findViewById(R.id.tv_details_city);
        tvDate = (TextView) activity.findViewById(R.id.tv_details_date);

        if (getArguments().containsKey(GigDetailsActivity.EXTRA_GIG_DETAILS_KEY)) {
            // all this according FirebaseSamples - database
            // TODO: 09/09/2016 Initialize Database, pass gigIntentKey to get the Gig we are interested in
            gigItem = FirebaseDatabase.getInstance().getReference()
                    .child(Utils.FIREBASE_GIGS).child(getArguments().getString(GigDetailsActivity.EXTRA_GIG_DETAILS_KEY));
            // TODO: 09/09/2016 Initialize views from details layout, override OnStart and assign values to UI fields
        } else {
        }

        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle("Details");
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

        ValueEventListener gigListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Gig gig = dataSnapshot.getValue(Gig.class);

                // collapsingToolbarLayout.setTitle(gig.getArtist());
                // TODO: 05/10/2016 set image here
                // ivCollapsingBackground.setBackground();
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
                Toast.makeText(activity, "Failed to load event details.", Toast.LENGTH_SHORT).show();
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
