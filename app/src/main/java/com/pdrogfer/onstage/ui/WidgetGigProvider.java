package com.pdrogfer.onstage.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pdrogfer.onstage.R;
import com.pdrogfer.onstage.Utils;
import com.pdrogfer.onstage.firebase_client.DatabaseFirebaseClient;
import com.pdrogfer.onstage.firebase_client.OnDbRequestCompleted;
import com.pdrogfer.onstage.model.Gig;

import java.util.ArrayList;
import java.util.List;

public class WidgetGigProvider implements RemoteViewsService.RemoteViewsFactory, OnDbRequestCompleted {

    private static final String TAG = "WidgetGigProvider";

    List<String> dataCollection = new ArrayList<>();
    Context context;

    public WidgetGigProvider(Context context, Intent intent) {
        this.context = context;
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {
        initData();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return dataCollection.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        // TODO: 18/10/2016 use a custom layout for widget items
        RemoteViews view = new RemoteViews(context.getPackageName(),
                android.R.layout.simple_list_item_1);
        view.setTextViewText(android.R.id.text1, dataCollection.get(position));
        return view;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


    private void initData() {

        dataCollection.clear();
        for (int i = 0; i < Utils.widgetData.size(); i++) {
                dataCollection.add("item " + Utils.widgetData.get(i));
            Log.i(TAG, "initData: widgetData " + i + Utils.widgetData.get(i));
        }
    }

    @Override
    public void onDbRequestCompleted(Gig gig) {

    }
}
