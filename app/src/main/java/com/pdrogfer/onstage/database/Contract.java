package com.pdrogfer.onstage.database;

/**
 * Created by pedrogonzalezferrandez on 10/10/16.
 */

public class Contract {

    // Database table
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_USER_TYPE = "user_type";

    public static final String DATABASE_NAME = "usersDb";
    public static final int DATABASE_VERSION = 1;
    public static final String USERS_TABLE_NAME = "usersTable";

    public static final String SCHEME = "content://";
    public static final String AUTHORITY = "com.pdrogfer.onstage.database";
    public static final String BASE_URI = SCHEME + AUTHORITY;
    public static final String PATH = "/" + USERS_TABLE_NAME;
}
