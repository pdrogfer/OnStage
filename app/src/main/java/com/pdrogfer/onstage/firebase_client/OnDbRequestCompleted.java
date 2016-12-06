package com.pdrogfer.onstage.firebase_client;

import com.pdrogfer.onstage.model.Gig;
import com.pdrogfer.onstage.model.User;

/**
 * Interface to communicate Firebase Database events back to the UI
 */
public interface OnDbRequestCompleted {

    void onDbGigRequestCompleted(Gig gig);
    void onDbUserRequestCompleted(User user);
}
