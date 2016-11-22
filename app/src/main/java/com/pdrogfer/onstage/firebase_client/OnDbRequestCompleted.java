package com.pdrogfer.onstage.firebase_client;

import com.pdrogfer.onstage.model.Gig;

/**
 * Interface to communicate Firebase Database events back to the UI
 */
public interface OnDbRequestCompleted {

    void onDbRequestCompleted(Gig gig);
}
