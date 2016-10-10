package com.pdrogfer.onstage.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UsersTable {

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + Contract.USERS_TABLE_NAME
            + "("
            + Contract.COLUMN_ID + " integer primary key autoincrement, "
            + Contract.COLUMN_NAME + " text not null, "
            + Contract.COLUMN_EMAIL + " text not null,"
            + Contract.COLUMN_PASSWORD + " text not null,"
            + Contract.COLUMN_USER_TYPE + " text not null"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(UsersTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + Contract.USERS_TABLE_NAME);
        onCreate(database);
    }
}
