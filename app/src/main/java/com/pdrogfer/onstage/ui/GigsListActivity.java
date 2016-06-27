package com.pdrogfer.onstage.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pdrogfer.onstage.CreateGig;
import com.pdrogfer.onstage.Gig;
import com.pdrogfer.onstage.MyPresenter;
import com.pdrogfer.onstage.R;
import com.pdrogfer.onstage.Utils;

public class GigsListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseRecyclerAdapter<Gig, GigViewHolder> mAdapter;
    MyPresenter myPresenter;
    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("gigs");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gigs_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_gigs);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new FirebaseRecyclerAdapter<Gig, GigViewHolder>(Gig.class,
                R.layout.list_item,
                GigViewHolder.class,
                reference) {
            @Override
            protected void populateViewHolder(GigViewHolder viewHolder, Gig model, int position) {
                viewHolder.setArtist(model.getArtist());
                viewHolder.setVenue(model.getLocal());
                viewHolder.setDate(model.getDate());
            }
        };
        recyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Utils.NEW_GIG_REQUEST) {
            if (resultCode == Utils.NEW_GIG_RESULT_OK) {
                // TODO: 26/06/16 THIS IS A UI CLASS MOVE ALL GIG CREATION LOGIC TO PRESENTER!!
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
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.cleanup();
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
                startActivity(new Intent(this, SignInActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
