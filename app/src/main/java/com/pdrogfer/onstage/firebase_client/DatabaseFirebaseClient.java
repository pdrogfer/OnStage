package com.pdrogfer.onstage.firebase_client;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pdrogfer.onstage.Utils;
import com.pdrogfer.onstage.model.Gig;

/**
 * Created by pedrogonzalezferrandez on 26/06/16.
 *
 * This Singleton class holds a unique instance of Firebase Database reference, to be used through the app
 */
public class DatabaseFirebaseClient {

    private static DatabaseFirebaseClient uniqueDatabaseInstance;

    DatabaseReference mRootRef;
    DatabaseReference mGigsRef;
    Gig tempGig; // to manipulate temporary gigs

    private final OnDbRequestCompleted databaseListener;
    private final Context context;

    private DatabaseFirebaseClient(Context context, OnDbRequestCompleted databaseListener) {
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mGigsRef = mRootRef.child(Utils.FIREBASE_GIGS);
        this.context = context;
        this.databaseListener = databaseListener;
    }

    public static synchronized DatabaseFirebaseClient getInstance(Context context, OnDbRequestCompleted databaseListener) {
        if (uniqueDatabaseInstance == null) {
            uniqueDatabaseInstance = new DatabaseFirebaseClient(context, databaseListener);
        }
        return uniqueDatabaseInstance;
    }

    public void addGig(long timestamp,
                        String artisticName,
                        String venue,
                        String date,
                        String startTime,
                        String price,
                        String description) {

        tempGig = new Gig(timestamp,
                artisticName,
                venue,
                date,
                startTime,
                price,
                description);
        mGigsRef.child(String.valueOf(timestamp)).setValue(tempGig);
        // notify back the UI of operation completed
        databaseListener.onDbRequestCompleted(tempGig);
    }

    public DatabaseReference getGigsRef() {
        return mGigsRef;
    }

    public void setGigsRef(DatabaseReference mGigsRef) {
        this.mGigsRef = mGigsRef;
    }
}
