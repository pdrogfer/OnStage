package com.pdrogfer.onstage.firebase_client;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pdrogfer.onstage.Utils;
import com.pdrogfer.onstage.model.Gig;
import com.pdrogfer.onstage.model.User;
import com.pdrogfer.onstage.ui.GigsListActivity;

/**
 * This singleton class should hold a unique FirebaseAuth instance. to be called through the app
 *
 * It includes a OnAuthenticationCompleted listener to update UI after tasks finished
 */
public class UserAuthFirebaseClient implements UserOperationsSuperClient, OnDbRequestCompleted {

    private static UserAuthFirebaseClient uniqueAuthInstance;

    private DatabaseFirebaseClient databaseClient;
    private FirebaseAuth mAuth;

    private final OnAuthenticationCompleted authFirebaseListener;
    private final Context context;
    private String artisticName;
    private String userType;

    private UserAuthFirebaseClient(Context context,OnAuthenticationCompleted authListener) {
        databaseClient = DatabaseFirebaseClient.getInstance(context, this);
        mAuth = FirebaseAuth.getInstance();
        this.context = context;
        this.authFirebaseListener = authListener;

    }

    public static synchronized UserAuthFirebaseClient getInstance(Context context, OnAuthenticationCompleted listener) {
        if (uniqueAuthInstance == null) {
            uniqueAuthInstance = new UserAuthFirebaseClient(context, listener);
        }
        return uniqueAuthInstance;
    }

    @Override
    public void checkAuth() {
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    @Override
    public void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(Utils.LOG_IN, "LogInActivity:onComplete: " + task.isSuccessful());

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            onAuthFailed("LogInActivity error");
                        }
                    }
                });
    }

    @Override
    public void signOut(GigsListActivity gigsListActivity) {
        mAuth.signOut();
        authFirebaseListener.onSignOut();
    }

    @Override
    public void deleteUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    authFirebaseListener.onUserDeleted();
                }
            }
        });
    }

    @Override
    public void registerUser(String email, String password, final String artisticName, String userType) {

        this.artisticName = artisticName;
        this.userType = userType;
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(Utils.LOG_IN, "createUser:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            onAuthFailed("RegisterIn error");
                        }
                    }
                });
    }

    private void onAuthFailed(String errorMessage) {
        // return result to RegisterActivity
        authFirebaseListener.onAuthenticationCompleted(false, null, null, null);
        Log.e(Utils.LOG_IN, "onComplete: " + errorMessage);
    }

    private void onAuthSuccess(FirebaseUser user) {
        // TODO: 22/11/2016 maybe the null user field comes from here?
        // return result to RegisterActivity. Firebase Auth only stores name and email
        databaseClient.addUser(artisticName, user.getEmail(), userType);


    }


    @Override
    public void onDbUserRequestCompleted(User user) {
        authFirebaseListener.onAuthenticationCompleted(
                true,
                user.getName(),
                user.getEmail(),
                user.getUserType());
    }

    @Override
    public void onDbGigRequestCompleted(Gig gig) {
        // Do nothing here
    }
}
