package com.pdrogfer.onstage.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UsersDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "userstable.db";
    private static final int DATABASE_VERSION = 1;

    public UsersDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        UsersTable.onCreate(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersionNumber, int newVersionNumber) {
        UsersTable.onUpgrade(sqLiteDatabase, oldVersionNumber, newVersionNumber);
    }
}
