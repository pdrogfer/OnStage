package com.pdrogfer.onstage.ui;

import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.pdrogfer.onstage.R;
import com.pdrogfer.onstage.database.Contract;
import com.pdrogfer.onstage.model.User;

public class ActivityUser extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int URL_LOADER = 0;

    ListView lvUserProfiles;
    SimpleCursorAdapter simpleCursorAdapter;

    public String[] mFromColumns = {Contract.COLUMN_NAME, Contract.COLUMN_EMAIL, Contract.COLUMN_USER_TYPE};
    public int[] mToFields = {R.id.tv_user_name, R.id.tv_user_email, R.id.tv_user_usertype};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        getSupportLoaderManager().initLoader(0, null, this);

        lvUserProfiles = (ListView) findViewById(R.id.lv_user_list);
        simpleCursorAdapter = new SimpleCursorAdapter(
                this,
                R.layout.user_list_item,
                null,
                mFromColumns,
                mToFields,
                0);

        lvUserProfiles.setAdapter(simpleCursorAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loader_id, Bundle args) {
        /*
     * Takes action based on the ID of the Loader that's being created
     */
        switch (loader_id) {
            case URL_LOADER:
                // Returns a new CursorLoader
                Uri uri = Uri.parse(Contract.BASE_URI + Contract.PATH);
                return new CursorLoader(
                        this,   // Parent activity context
                        uri,        // Table to query
                        null,     // Projection to return
                        null,            // No selection clause
                        null,            // No selection arguments
                        null             // Default sort order
                );
            default:
                // An invalid id was passed in
                return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        simpleCursorAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        simpleCursorAdapter.changeCursor(null);
    }
}
