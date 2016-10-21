package com.pdrogfer.onstage.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import com.pdrogfer.onstage.R;
import com.pdrogfer.onstage.Utils;
import com.pdrogfer.onstage.firebase_client.OnDbRequestCompleted;
import com.pdrogfer.onstage.model.Gig;

import java.util.ArrayList;
import java.util.List;

public class WidgetGigFactory implements RemoteViewsService.RemoteViewsFactory, OnDbRequestCompleted {

    private static final String TAG = "WidgetGigFactory";

    List<Gig> dataCollection = new ArrayList<>();
    Context context;

    public WidgetGigFactory(Context context, Intent intent) {
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

        RemoteViews view = new RemoteViews(context.getPackageName(),
                android.R.layout.simple_list_item_1);

        view.setTextViewText(android.R.id.text1, dataCollection.get(position).getArtist()
                + " - " + dataCollection.get(position).getDate());
        view.setTextColor(android.R.id.text1, Color.BLACK);

        final Intent fillInIntent = new Intent();
        fillInIntent.setAction(WidgetProvider.ACTION_TOAST);
        final Bundle bundle = new Bundle();
        bundle.putString(WidgetProvider.EXTRA_STRING,
                mCollections.get(position));
        fillInIntent.putExtras(bundle);
        mView.setOnClickFillInIntent(android.R.id.text1, fillInIntent);

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
                dataCollection.add(Utils.widgetData.get(i));
        }
    }

    @Override
    public void onDbRequestCompleted(Gig gig) {

    }
}
