package com.pdrogfer.onstage.database;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

public class CheckAuthActiveUserTask extends AsyncTask<String, Void, Boolean> {

    Context context;
    OnAsyncTaskCompleted asyncTaskListener;

    public CheckAuthActiveUserTask(Context context, OnAsyncTaskCompleted asyncTaskListener) {
        this.context = context;
        this.asyncTaskListener = asyncTaskListener;
    }


    @Override
    protected Boolean doInBackground(String... strings) {

        Uri users = UsersContentProvider.CONTENT_URI;
        String selectUserActive = "1";
        String tempName = null;
        Cursor cursor = context.getContentResolver().query(users, null, Contract.COLUMN_USER_ACTIVE + "=?", new String[]{selectUserActive}, null);
        if (cursor.moveToFirst()) {
            do {
                tempName = cursor.getString(cursor.getColumnIndex(Contract.COLUMN_NAME));
            } while (cursor.moveToNext());
        }
        return (tempName != null);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        asyncTaskListener.onTaskCompleted(String.valueOf(result));
        super.onPostExecute(result);
    }
}
