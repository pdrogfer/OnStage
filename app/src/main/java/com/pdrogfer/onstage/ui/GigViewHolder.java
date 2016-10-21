package com.pdrogfer.onstage.ui;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pdrogfer.onstage.R;
import com.pdrogfer.onstage.Utils;
import com.pdrogfer.onstage.model.Gig;

/**
 * This is shown in items in Gig List Activity
 */
public class GigViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "GigViewHolder";
    RelativeLayout relativeLayout; // used only to capture click events
    ImageView ivIconArtist;
    TextView tvArtist, tvVenue, tvDate;

    public GigViewHolder(View view) {
        super(view);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.card_relative_layout);
        ivIconArtist = (ImageView) view.findViewById(R.id.iv_list_item_icon_artist);
        tvArtist = (TextView) view.findViewById(R.id.tv_list_item_artist);
        tvVenue = (TextView) view.findViewById(R.id.tv_list_item_venue);
        tvDate = (TextView) view.findViewById(R.id.tv_list_item_date);
    }

    public void bindToGig(Gig gig, View.OnClickListener clickListener) {
        tvArtist.setText(gig.getArtist());
        tvVenue.setText(gig.getVenue());
        tvDate.setText(gig.getDate());

        // setting up the click listener to the relative layout we capture clicks
        // it the whole card surface
        relativeLayout.setOnClickListener(clickListener);

        // Widgets doesn't work with Firebase, so this
        Utils.updateWidgetData(gig.getArtist());
    }

}
