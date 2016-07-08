package com.pdrogfer.onstage.ui;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pdrogfer.onstage.R;
import com.pdrogfer.onstage.Utils;
import com.pdrogfer.onstage.model.Gig;

/**
 * Created by pedrogonzalezferrandez on 26/06/16.
 */
public class GigViewHolder extends RecyclerView.ViewHolder {

    ImageView ivIconArtist;
    TextView tvArtist;
    TextView tvVenue;
    TextView tvDate;

    public GigViewHolder(View view) {
        super(view);
        ivIconArtist = (ImageView) view.findViewById(R.id.iv_list_item_icon_artist);
        tvArtist = (TextView) view.findViewById(R.id.tv_list_item_artist);
        tvVenue = (TextView) view.findViewById(R.id.tv_list_item_venue);
        tvDate = (TextView) view.findViewById(R.id.tv_list_item_date);

    }

    public void bindToGig(Gig gig, View.OnClickListener clickListener) {
        tvArtist.setText(gig.getArtist());
        tvVenue.setText(gig.getVenue());
        tvDate.setText(gig.getDate());


        // TODO: 08/07/2016 SOLUTION FOR NOW: CREATE AN 'INFO' FIELD AND SET CLICK LISTENER TO IT
        tvArtist.setOnClickListener(clickListener);
    }

}
