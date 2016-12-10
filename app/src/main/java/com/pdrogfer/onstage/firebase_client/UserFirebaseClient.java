package com.pdrogfer.onstage.firebase_client;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pdrogfer.onstage.Utils;
import com.pdrogfer.onstage.model.Gig;
import com.pdrogfer.onstage.model.User;

/**
 * This singleton class should hold a unique FirebaseAuth instance. to be called through the app
 *
 * It includes a OnFirebaseUserCompleted listener to update UI after tasks finished
 */
public class UserFirebaseClient implements UserOperationsSuperClient, OnDbRequestCompleted {

    private static UserFirebaseClient uniqueAuthInstance;

    private DatabaseFirebaseClient databaseClient;
    private FirebaseAuth mAuth;

    private final OnFirebaseUserCompleted firebaseListener;
    private final Context context;
//    private String artisticName;
//    private String userType;

    private UserFirebaseClient(Context context, OnFirebaseUserCompleted authListener) {
        databaseClient = DatabaseFirebaseClient.getInstance(context, this);
        mAuth = FirebaseAuth.getInstance();
        this.context = context;
        this.firebaseListener = authListener;

    }

    public static synchronized UserFirebaseClient getInstance(Context context, OnFirebaseUserCompleted listener) {
        if (uniqueAuthInstance == null) {
            uniqueAuthInstance = new UserFirebaseClient(context, listener);
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
                        Log.d(Utils.FIREBASE_CLIENT, "LogInActivity:onComplete: " + task.isSuccessful());

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            onAuthFailed("LogInActivity error");
                        }
                    }
                });
    }

    @Override
    public void signOut() {
        mAuth.signOut();
        firebaseListener.onSignOut();
    }

    @Override
    public void deleteUser() {
        FirebaseUser user = mAuth.getCurrentUser();
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    firebaseListener.onUserDeleted();
                }
            }
        });
    }

    @Override
    public void registerUser(String email, String password, final String userName, final String userType) {
//        this.artisticName = userName;
//        this.userType = userType;
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(Utils.FIREBASE_CLIENT, "createUser:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            onRegistrationSuccess(task.getResult().getUser(), userName, userType);
                        } else {
                            onRegistrationFailed("RegisterIn error");
                        }
                    }
                });
    }

    private void onAuthFailed(String errorMessage) {
        // return result to RegisterActivity
        firebaseListener.onLogInCompleted(false, null, null, null);
        Log.e(Utils.FIREBASE_CLIENT, "onComplete: " + errorMessage);
    }

    private void onAuthSuccess(FirebaseUser user) {
        // User already exists, so Retrieve User from Firebase Database
        // callback goes to onDbUserRetrievedCompleted()
        databaseClient.getUser(user.getEmail());
    }

    private void onRegistrationFailed(String errorMessage) {
        // return result to RegisterActivity
        firebaseListener.onRegistrationCompleted(false, null, null, null);
        Log.e(Utils.FIREBASE_CLIENT, "onRegistrationFailed: " + errorMessage);
    }

    private void onRegistrationSuccess(FirebaseUser user, String userName, String userType) {
        // Store user details in Firebase Database
        databaseClient.addUser(userName, user.getEmail(), userType);
    }

    @Override
    public void getUserFromFirebaseDb(String email) {
        databaseClient.getUser((email));
    }

    @Override
    public void onDbUserSavedCompleted(User user) {
        // return result to RegisterActivity. Firebase Auth only stores name and email
        firebaseListener.onRegistrationCompleted(
                true,
                user.getName(),
                user.getEmail(),
                user.getUserType());
    }

    @Override
    public void onDbUserRetrievedCompleted(User user) {
        // TODO: 10/12/2016 user from DB is received here
        firebaseListener.onLogInCompleted(
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
