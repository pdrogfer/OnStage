package com.pdrogfer.onstage.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

public class UsersContentProvider extends ContentProvider {

    private static final String TAG = "UsersContentProvider";
    private SQLiteDatabase databaseUsers;
    public static final Uri CONTENT_URI = Uri.parse(Contract.SCHEME + Contract.AUTHORITY + Contract.PATH);

    private static HashMap<String, String> USERS_PROJECTION_MAP;

    static final int USERS = 1;
    static final int USERS_ID = 2;

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(Contract.AUTHORITY, Contract.USERS_TABLE_NAME, USERS);
        uriMatcher.addURI(Contract.AUTHORITY, Contract.USERS_TABLE_NAME + "/#", USERS_ID);
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        UsersDatabaseHelper dbHelper = new UsersDatabaseHelper(context);
        databaseUsers = dbHelper.getWritableDatabase();
        Log.i(TAG, "db == null: " + (databaseUsers == null));
        return (databaseUsers == null)? false:true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(Contract.USERS_TABLE_NAME);
        switch (uriMatcher.match(uri)) {
            case USERS:
                queryBuilder.setProjectionMap(USERS_PROJECTION_MAP);
                break;
            case USERS_ID:
                queryBuilder.appendWhere(Contract.COLUMN_ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Cursor cursor = queryBuilder.query(databaseUsers, projection, selection, selectionArgs, null, null, sortOrder);
        // Register to watch a content URI for changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case USERS:
                return "vnd.android.cursor.dir/vnd." + Contract.AUTHORITY + "." + Contract.DATABASE_NAME + "." + Contract.USERS_TABLE_NAME;
            case USERS_ID:
                return "vnd.android.cursor.item/vnd." + Contract.AUTHORITY + "." + Contract.DATABASE_NAME + "." + Contract.USERS_TABLE_NAME;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int uriType = uriMatcher.match(uri);
        // add a new user profile
        long rowID = databaseUsers.insert(Contract.USERS_TABLE_NAME, null, contentValues);

        // if record added successfully
        if (rowID > 0) {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        // If not
        try {
            throw new SQLException("Failed to add a record into " + uri);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case USERS:
                count = databaseUsers.delete(Contract.USERS_TABLE_NAME, selection, selectionArgs);
                break;
            case USERS_ID:
                String id = uri.getPathSegments().get(1);
                if (TextUtils.isEmpty(selection)) {
                    count = databaseUsers.delete(
                            Contract.USERS_TABLE_NAME,
                            Contract.COLUMN_ID + "=" + id,
                            null);
                } else {
                    count = databaseUsers.delete(
                            Contract.USERS_TABLE_NAME,
                            Contract.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case USERS:
                count = databaseUsers.update(
                        Contract.USERS_TABLE_NAME, contentValues, selection, selectionArgs);
                break;

            case USERS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    count = databaseUsers.update(Contract.USERS_TABLE_NAME,
                            contentValues,
                            Contract.COLUMN_ID + "=" + id,
                            null);
                } else {
                    count = databaseUsers.update(Contract.USERS_TABLE_NAME,
                            contentValues,
                            Contract.COLUMN_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
