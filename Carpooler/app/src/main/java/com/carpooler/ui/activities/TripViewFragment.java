package com.carpooler.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.carpooler.R;
import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.dto.TripData;
import com.carpooler.trips.Trip;
import com.carpooler.trips.TripStatus;
import com.carpooler.ui.adapters.TripViewAdapter;

public class TripViewFragment extends Fragment {
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private TripViewAdapter tripViewAdapter;
    public static final String TRIP_ID_ARG = "tripId";
    private String tripId;
    private Trip trip;
    private TripDetailCallback callback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callback = (TripDetailCallback) activity;
    }
    protected void setupData() {
        tripViewAdapter = new TripViewAdapter(trip,callback);
        recyclerView.setAdapter(tripViewAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        View rootView = inflater.inflate(R.layout.fragment_trip_view, container, false);
        if (args!=null) {
            tripId = args.getString(TRIP_ID_ARG);
        }
        recyclerView = (RecyclerView) rootView.findViewById(R.id.trip_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.contentView);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadData();
    }
    private void loadData(){
        try {
            if (!refreshLayout.isRefreshing()){
                refreshLayout.setRefreshing(true);
            }
            callback.getTripDataService().getTripData(tripId,getCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private DatabaseService.GetCallback<TripData> getCallback = new DatabaseService.GetCallback<TripData>() {
        @Override
        public void doError(String message) {
            Toast.makeText(getActivity(),message,Toast.LENGTH_LONG).show();
        }

        @Override
        public void doException(Exception exception) {
            doError(exception.getMessage());
        }

        @Override
        public void doSuccess(TripData data) {
            trip = new Trip(data,callback);
            setupData();
        }
    };
    private void cancelTrip() {
        TripData tripData = new TripData();
        tripData.set_id(tripId);
        Trip trip = new Trip(tripData, callback);
        trip.setStatus(TripStatus.CANCELLED);
        trip.saveTrip();
    }
}
