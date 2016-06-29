package com.pdrogfer.onstage;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pdrogfer.onstage.model.User;
import com.pdrogfer.onstage.ui.GigsListActivity;
import com.pdrogfer.onstage.ui.SignInActivity;

/**
 * Created by pedrogonzalezferrandez on 29/06/16.
 *
 * This singleton class should hold a unique FirebaseAuth instance. to be called through the app
 */
public class UserAuthFirebaseClient {

    private static UserAuthFirebaseClient uniqueInstance;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private UserAuthFirebaseClient() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    public static synchronized UserAuthFirebaseClient getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new UserAuthFirebaseClient();
        }
        return uniqueInstance;
    }

    public void checkAuth() {
        if (mAuth.getCurrentUser() != null) {
            onAuthSuccess(mAuth.getCurrentUser());
        }
    }

    public void signIn(final Context context, String email, String password, final String artisticName) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(Utils.LOG_IN, "LogIn:onComplete:" + task.isSuccessful());
                        
                        if (task.isSuccessful()) {
                            onAuthSuccess(context, task.getResult().getUser(), artisticName);
                        } else {
                            Toast.makeText(context, "Log In Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void onAuthSuccess(Context context, FirebaseUser user, String artisticName) {
        // String username = usernameFromEmail(user.getEmail());
        String artisticUserName = artisticName;

        // Write new user
        writeNewUser(user.getUid(), artisticUserName, user.getEmail());


        // TODO: 29/06/16
        // Go to GigsListActivity
        startActivity(new Intent(context, GigsListActivity.class));
        finish();

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
