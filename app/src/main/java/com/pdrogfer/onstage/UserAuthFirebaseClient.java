package com.pdrogfer.onstage;

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
import com.pdrogfer.onstage.model.User;

/**
 * Created by pedrogonzalezferrandez on 29/06/16.
 *
 * This singleton class should hold a unique FirebaseAuth instance. to be called through the app
 */
public class UserAuthFirebaseClient {

    private static UserAuthFirebaseClient uniqueInstance;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private final OnAuthenticationCompleted listener;
    private final Context context;
    private String artisticName;

    private UserAuthFirebaseClient(Context context,OnAuthenticationCompleted listener) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        this.context = context;
        this.listener = listener;

    }

    public static synchronized UserAuthFirebaseClient getInstance(Context context, OnAuthenticationCompleted listener) {
        if (uniqueInstance == null) {
            uniqueInstance = new UserAuthFirebaseClient(context, listener);
        }
        return uniqueInstance;
    }

    public void checkAuth() {
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    public void signIn(String email, String password, final String artisticName) {

        this.artisticName = artisticName;
        Boolean success;
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(Utils.LOG_IN, "LogIn:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                        } else {
                            // return result to SignInActivity
                            listener.onAuthenticationCompleted(false);
                        }
                    }
                });
    }

    private void onAuthSuccess(FirebaseUser user) {
        // String username = usernameFromEmail(user.getEmail());  // using artisticName instead

        // Write new user
        writeNewUser(user.getUid(), artisticName, user.getEmail());

        // return result to SignInActivity
        listener.onAuthenticationCompleted(true);
    }

    // [START basic_write]
    private void writeNewUser(String userId, String artisticName, String email) {
        User user = new User(artisticName, email);
        mDatabase.child("users").child(userId).setValue(user);
    }
    // [END basic_write]


    // not used in my implementation, storing artisticName instead
    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

}
