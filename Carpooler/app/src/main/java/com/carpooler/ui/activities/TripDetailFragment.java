package com.carpooler.ui.activities;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carpooler.R;

public class TripDetailFragment extends Fragment implements MessageFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trip_detail, container, false);
    }

    @Override
    public void handleMessage(Message msg) {

    }

    @Override
    public void handleError(Message msg) {

    }
}
