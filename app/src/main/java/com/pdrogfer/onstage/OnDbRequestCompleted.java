package com.pdrogfer.onstage;

import com.pdrogfer.onstage.model.Gig;

/**
 * Created by pedrogonzalezferrandez on 29/06/16.
 *
 * Interface to communicate Firebase Database events back to the UI
 */
public interface OnDbRequestCompleted {

    void onDbRequestCompleted(Gig gig);
}
