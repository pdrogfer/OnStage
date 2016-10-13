package com.pdrogfer.onstage.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

public class UpdateActiveUserLocalTask extends AsyncTask<String, Void, String> {

    Context context;
    OnAsyncTaskCompleted asyncTaskListener;

    Uri users = UsersContentProvider.CONTENT_URI;

    public UpdateActiveUserLocalTask(Context context, OnAsyncTaskCompleted asyncTaskListener) {
        this.context = context;
        this.asyncTaskListener = asyncTaskListener;
    }

    @Override
    protected String doInBackground(String... strings) {
        // user values are passed in a String[] 'strings'

        /*
        * Case 1: user exists in remote but not in local --> create local user
        *
        * Case 2: user exists in remote and in local --> proceed with the update
        */
        if (checkUserLocalExists(strings)) {
            // user exists, proceed with the update
            // first, disable all active users
            ContentValues disableUser = new ContentValues();
            disableUser.put(Contract.COLUMN_USER_ACTIVE, "0");
            int previous = context.getContentResolver().update(
                    UsersContentProvider.CONTENT_URI,
                    disableUser,
                    Contract.COLUMN_USER_ACTIVE + "=" + "1", null);

            // second, enable the selected user
            int result = 0;
            if (previous > 0) {
                ContentValues enableUser = new ContentValues();
                enableUser.put(Contract.COLUMN_USER_ACTIVE, "1");
                result = context.getContentResolver().update(
                        UsersContentProvider.CONTENT_URI,
                        enableUser,
                        Contract.COLUMN_EMAIL + "=" + strings[1], null);
            }

            return (result > 0) ? "updated" : "error updating";
        } else {
            // create local user
            ContentValues values = new ContentValues();
            values.put(Contract.COLUMN_NAME, strings[0]);
            values.put(Contract.COLUMN_EMAIL, strings[1]);
            values.put(Contract.COLUMN_PASSWORD, strings[2]);
            values.put(Contract.COLUMN_USER_TYPE, strings[3]);
            values.put(Contract.COLUMN_USER_ACTIVE, strings[4]);
            Uri uri = context.getContentResolver().insert(users, values);
            return "new local user at " + uri;
        }



    }

    private boolean checkUserLocalExists(String[] strings) {
        Cursor cursor = context.getContentResolver().query(users, null, Contract.COLUMN_EMAIL + "=?", new String[]{strings[1]}, null);
        if (cursor.moveToFirst()) {
            return true;
        }
        return false;
    }

    @Override
    protected void onPostExecute(String result) {
        // result is the name of the user if not empty
        asyncTaskListener.onTaskCompleted(result);
        super.onPostExecute(result);
    }
}
