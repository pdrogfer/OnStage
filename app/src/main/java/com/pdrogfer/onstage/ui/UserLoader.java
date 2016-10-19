package com.pdrogfer.onstage.ui;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.pdrogfer.onstage.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Loader for ActivityUser
 */

public class UserLoader extends AsyncTaskLoader<List<User>>{
    public UserLoader(Context context) {
        super(context);
    }

    @Override
    public List<User> loadInBackground() {
        List<User> usersList = new ArrayList<User>();


        return usersList;
    }
}
