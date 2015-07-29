package com.carpooler.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.carpooler.R;
import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.dto.TripData;
import com.carpooler.trips.TripStatus;

import java.util.List;

/**
 */
public class TripInProgressFragment extends Fragment {

    private TripDetailCallback callback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callback = (TripDetailCallback) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trip_inprogress, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadData();
    }

    private void loadData(){
        try {
            callback.getTripDataService().findTripsByHostOrUserIdAndStatus(callback.getUser().getGoogleId(), TripStatus.IN_ROUTE, queryCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private DatabaseService.QueryCallback<TripData> queryCallback = new DatabaseService.QueryCallback<TripData>() {
        @Override
        public void doError(String message) {
            Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
        }

        @Override
        public void doException(Exception exception) {
            doError(exception.getMessage());
        }

        @Override
        public void doSuccess(List<TripData> data) {
            if (!data.isEmpty()){
                TripData foundTrip = data.get(0);
                callback.onTripSelected(foundTrip.get_id());
            }
        }
    };
}
