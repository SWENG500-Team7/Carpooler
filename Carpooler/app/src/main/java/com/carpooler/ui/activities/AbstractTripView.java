package com.carpooler.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.dto.TripData;
import com.carpooler.trips.Trip;

/**
 * Abstract class supporting view a single trip
 */
public abstract class AbstractTripView extends Fragment {
    protected Trip trip;
    protected TripDetailCallback callback;
    protected String tripId;
    public static final String TRIP_ID_ARG = "tripId";

    protected abstract void setupData();
    protected abstract void preLoadData();
    protected abstract void postLoadData();

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callback = (TripDetailCallback) activity;
    }

    /**
     * Call this from on create view
     */
    protected void setupArgs(){
        Bundle args = getArguments();
        if (args!=null) {
            tripId = args.getString(TRIP_ID_ARG);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        loadData();
    }

    protected void loadData(){
        try {
            preLoadData();
            callback.getTripDataService().getTripData(tripId,getCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private DatabaseService.GetCallback<TripData> getCallback = new DatabaseService.GetCallback<TripData>() {
        @Override
        public void doError(String message) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            postLoadData();
        }

        @Override
        public void doException(Exception exception) {
            doError(exception.getMessage());
        }

        @Override
        public void doSuccess(TripData data) {
            trip = new Trip(data,callback);
            setupData();
            postLoadData();
        }
    };
}
