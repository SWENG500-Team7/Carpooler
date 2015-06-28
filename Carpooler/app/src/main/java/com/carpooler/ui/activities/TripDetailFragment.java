package com.carpooler.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carpooler.R;
import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.dto.TripData;

public class TripDetailFragment extends Fragment implements DatabaseService.GetCallback<TripData> {
    private ServiceActivityCallback callback;
    public static final String TRIP_ID_ARG = "tripId";
    private String searchId;
    TextView textView;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callback = (ServiceActivityCallback) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_trip_detail, container, false);
        Bundle args = getArguments();
        if (args!=null) {
            searchId = args.getString(TRIP_ID_ARG);
        }
        textView = (TextView) rootView.findViewById(R.id.hello);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (searchId!=null){
            try {
                callback.getTripDataService().getTripData(searchId,this);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void doError(String message) {
        textView.setText(message);
    }

    @Override
    public void doException(Exception exception) {
        textView.setText(exception.getMessage());
    }

    @Override
    public void doSuccess(TripData data) {
        textView.setText(data.getStartTime().toString());
    }
}
