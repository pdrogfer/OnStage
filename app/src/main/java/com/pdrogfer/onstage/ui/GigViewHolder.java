package com.pdrogfer.onstage.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pdrogfer.onstage.R;

/**
 * Created by pedrogonzalezferrandez on 26/06/16.
 */
public class GigViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener, View.OnLongClickListener {

    View mView;
    ImageView ivIconArtist;
    TextView tvArtist;
    TextView tvVenue;
    TextView tvDate;

    private OnClickRecyclerViewItemListener clickRecyclerViewItemListener;

    public GigViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        ivIconArtist = (ImageView) mView.findViewById(R.id.iv_list_item_icon_artist);
        tvArtist = (TextView) mView.findViewById(R.id.tv_list_item_artist);
        tvVenue = (TextView) mView.findViewById(R.id.tv_list_item_venue);
        tvDate = (TextView) mView.findViewById(R.id.tv_list_item_date);

        mView.setOnClickListener(this);
        mView.setOnLongClickListener(this);
    }

    public void setClickRecyclerViewItemListener(OnClickRecyclerViewItemListener listener) {
        this.clickRecyclerViewItemListener = listener;
    }

    public void setArtist(String artist) {
        tvArtist.setText(artist);
    }

    public void setVenue(String venue) {
        tvVenue.setText(venue);
    }

    public void setDate(String date) {
        tvDate.setText(date);
    }

    @Override
    public void onClick(View v) {
        clickRecyclerViewItemListener.onClickRecyclerViewItem(v, getAdapterPosition(), false);
    }

    @Override
    public boolean onLongClick(View v) {
        clickRecyclerViewItemListener.onClickRecyclerViewItem(v, getAdapterPosition(), true);
        return true;
    }
}
