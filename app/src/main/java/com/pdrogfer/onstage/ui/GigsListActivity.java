package com.pdrogfer.onstage.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pdrogfer.onstage.R;
import com.pdrogfer.onstage.Utils;
import com.pdrogfer.onstage.model.Gig;
import com.pdrogfer.onstage.model.UserType;

public class GigsListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<Gig, GigViewHolder> mAdapter;
    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("gigs");
    private AdView bannerAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gigs_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setBannerAdView();
        setRecyclerView();
        setFab();
    }

    private void setBannerAdView() {
        bannerAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest
                .Builder()
                // TODO: 07/07/2016 REMOVE 'addTestDevice' IN PRODUCTION
                .addTestDevice("7C67DD5800874F3698615ADD51366D95")
                .build();
        bannerAdView.loadAd(adRequest);
    }

    private void setRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_gigs);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new FirebaseRecyclerAdapter<Gig, GigViewHolder>(Gig.class,
                R.layout.list_item_card,
                GigViewHolder.class,
                reference) {
            @Override
            protected void populateViewHolder(final GigViewHolder viewGig, Gig model, final int position) {
                final DatabaseReference gigRef = getRef(position);

                final String gigKey = gigRef.getKey();
                viewGig.bindToGig(model, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(Utils.TAG, "onItemClickTitle: position " + position);
                        // TODO: 08/07/2016  see sample 'database' to pass the Gig info to Details Activity
                        Intent intentDetails = new Intent(getApplicationContext(), GigDetailsActivity.class);
                        intentDetails.putExtra(GigDetailsActivity.EXTRA_GIG_DETAILS_KEY, gigKey);
                        startActivity(intentDetails);
                    }
                });
            }
        };
        recyclerView.setAdapter(mAdapter);
    }

    private void setFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Log.i(Utils.TAG, "setFab: USER TYPE " + Utils.getUserType(Utils.USER_TYPE, this));
        // If the user is a fan, hyde the fab so it can not create gigs
        if (Utils.getUserType(Utils.USER_TYPE, this) == String.valueOf(UserType.FAN)) {
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            p.setAnchorId(View.NO_ID);
            fab.setLayoutParams(p);
            fab.setVisibility(View.GONE);
        } else {
            if (fab != null) {
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        createNewGig(view);
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.NEW_GIG_REQUEST) {
            if (resultCode == Utils.NEW_GIG_RESULT_OK) {
                Gig tempGig = new Gig();
                reference.push().setValue(tempGig);
                Toast.makeText(this, "Gig created", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void createNewGig(View view) {
        Intent intNewGig = new Intent(this, CreateGig.class);
        startActivityForResult(intNewGig, Utils.NEW_GIG_REQUEST);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gigs_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, Presentation.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }
}
