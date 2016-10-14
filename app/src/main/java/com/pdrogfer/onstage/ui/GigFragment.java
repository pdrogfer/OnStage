package com.pdrogfer.onstage.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pdrogfer.onstage.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GigFragment extends Fragment {


    public GigFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gig, container, false);
    }

}
