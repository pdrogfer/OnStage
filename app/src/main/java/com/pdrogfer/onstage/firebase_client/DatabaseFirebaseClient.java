package com.pdrogfer.onstage.firebase_client;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pdrogfer.onstage.Utils;
import com.pdrogfer.onstage.model.Gig;
import com.pdrogfer.onstage.model.User;

/**
 * Created by pedrogonzalezferrandez on 26/06/16.
 *
 * This Singleton class holds a unique instance of Firebase Database reference, to be used through the app
 */
public class DatabaseFirebaseClient {

    private static DatabaseFirebaseClient uniqueDatabaseInstance;

    DatabaseReference mRootRef;
    DatabaseReference mGigsRef;
    DatabaseReference mUsersRef;
    Gig tempGig; // to manipulate temporary gigs
    User tempUser;

    private final OnDbRequestCompleted databaseListener;
    private final Context context;

    private DatabaseFirebaseClient(Context context, OnDbRequestCompleted databaseListener) {
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mGigsRef = mRootRef.child(Utils.FIREBASE_GIGS);
        mUsersRef = mRootRef.child(Utils.FIREBASE_USERS);
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
        mGigsRef.child(String.valueOf(timestamp)).setValue(new Gig(timestamp,
                artisticName,
                venue,
                date,
                startTime,
                price,
                description));
        // notify back the UI of operation completed
        databaseListener.onDbGigRequestCompleted(tempGig);
    }

    public void addUser(String name, String email, String userType) {
        mUsersRef.child(name).setValue(new User(name, email, userType));
        // notify back the UI of operation completed
        databaseListener.onDbUserSavedCompleted(tempUser);
    }

    public void getUser(String email) {
        mUsersRef.child(email).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        databaseListener.onDbUserRetrievedCompleted(user);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(Utils.TAG, "getUser:onCancelled", databaseError.toException());
                    }
                }
        );
    }


    public DatabaseReference getGigsRef() {
        return mGigsRef;
    }

    public void setGigsRef(DatabaseReference mGigsRef) {
        this.mGigsRef = mGigsRef;
    }

    public DatabaseReference getUsersRef() {
        return mUsersRef;
    }

    public void setUsersRef(DatabaseReference mUsersRef) {
        this.mUsersRef = mUsersRef;
    }
}
