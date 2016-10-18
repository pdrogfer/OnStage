package com.pdrogfer.onstage.ui;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.List;

public class WidgetGigProvider implements RemoteViewsService.RemoteViewsFactory {

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
        for (int i = 1; i < 11; i++) {
            dataCollection.add("List view item at " + i);
        }
    }
}
