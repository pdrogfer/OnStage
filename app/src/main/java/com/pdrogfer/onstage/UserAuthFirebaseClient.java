package com.pdrogfer.onstage;

/**
 * Created by pedrogonzalezferrandez on 29/06/16.
 *
 * This singleton class should hold a unique FirebaseAuth instance. to be called through the app
 */
public class UserAuthFirebaseClient {

    private static UserAuthFirebaseClient uniqueInstance;


    private UserAuthFirebaseClient() {
    }

    public static synchronized UserAuthFirebaseClient getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new UserAuthFirebaseClient();
        }
        return uniqueInstance;
    }

}
