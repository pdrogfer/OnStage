package com.pdrogfer.onstage.database;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

public class InsertUserToLocalDbTask extends AsyncTask<String, Void, String> {

    Context context;
    OnAsyncTaskCompleted asyncTaskListener;

    public InsertUserToLocalDbTask(Context context, OnAsyncTaskCompleted asyncTaskListener) {
        this.context = context;
        this.asyncTaskListener = asyncTaskListener;
    }

    @Override
    protected String doInBackground(String... strings) {
        // user values are passed in a String[] 'strings'
        ContentValues values = new ContentValues();
        values.put(Contract.COLUMN_NAME, strings[0]);
        values.put(Contract.COLUMN_EMAIL, strings[1]);
        values.put(Contract.COLUMN_PASSWORD, strings[2]);
        values.put(Contract.COLUMN_USER_TYPE, strings[3]);
        values.put(Contract.COLUMN_USER_ACTIVE, strings[4]);
        Uri uri = context.getContentResolver().insert(UsersContentProvider.CONTENT_URI, values);
//        Toast.makeText(context, uri.toString(), Toast.LENGTH_LONG).show();
        return strings[0];
    }

    @Override
    protected void onPostExecute(String result) {
        // result is the name of the user if not empty
        asyncTaskListener.onTaskCompleted(result);
        super.onPostExecute(result);
    }
}
