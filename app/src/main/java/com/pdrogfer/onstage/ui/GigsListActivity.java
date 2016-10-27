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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pdrogfer.onstage.R;
import com.pdrogfer.onstage.Utils;
import com.pdrogfer.onstage.firebase_client.OnAuthenticationCompleted;
import com.pdrogfer.onstage.firebase_client.UserAuthServerClient;
import com.pdrogfer.onstage.firebase_client.UserOperationsSuperClient;
import com.pdrogfer.onstage.model.Gig;
import com.pdrogfer.onstage.model.UserType;

public class GigsListActivity extends AppCompatActivity implements OnAuthenticationCompleted{

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<Gig, GigViewHolder> mAdapter;
    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(Utils.FIREBASE_GIGS);
    private UserOperationsSuperClient userOperationsSuperClient;
    private AdView bannerAdView;

    private boolean usingMasterDetailFlow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gigs_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setBannerAdView();
        setRecyclerView();
        setFabGigList();

        // this selects Firebase or Server (with ContentProvider) for user authentication
//        userOperationsSuperClient = UserAuthFirebaseClient.getInstance(this, this);
        userOperationsSuperClient = UserAuthServerClient.getInstance(this, this);

        if (findViewById(R.id.gig_detail_container) != null) {
            usingMasterDetailFlow = true;
        }
    }

    private void setBannerAdView() {
        bannerAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest
                .Builder()
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

                        if (usingMasterDetailFlow) {
                            Bundle arguments = new Bundle();
                            arguments.putString(GigDetailsActivity.EXTRA_GIG_DETAILS_KEY, gigKey);
                            GigFragment gigFragment = new GigFragment();
                            gigFragment.setArguments(arguments);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.gig_detail_container, gigFragment)
                                    .commit();
                        } else {
                            Intent intentDetails = new Intent(getApplicationContext(), GigDetailsActivity.class);
                            intentDetails.putExtra(GigDetailsActivity.EXTRA_GIG_DETAILS_KEY, gigKey);
                            startActivity(intentDetails);
                        }
                    }
                });
            }
        };
        recyclerView.setAdapter(mAdapter);
    }

    private void setFabGigList() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_new_gig);
        Log.i(Utils.TAG, "setFabGigList: USER TYPE " + Utils.getUserType(Utils.DB_KEY_USER_TYPE, this));
        // If the user is a fan, hyde the fab so it can not create gigs
        if (Utils.getUserType(Utils.DB_KEY_USER_TYPE, this) == String.valueOf(UserType.FAN)) {
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
                Toast.makeText(this, R.string.confirmation_gig_created, Toast.LENGTH_LONG).show();
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
                userOperationsSuperClient.signOut(this);
                return true;
            case R.id.action_profile:
                startActivity(new Intent(this, ActivityUser.class));
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

    @Override
    public void onAuthenticationCompleted(Boolean success, String name, String email, String password, String user_type) {
        // do nothing here
    }

    @Override
    public void onSignOut() {
        Toast.makeText(this, R.string.confirmation_log_out, Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, Presentation.class));
    }
}
