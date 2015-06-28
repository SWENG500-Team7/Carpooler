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

import com.carpooler.R;
import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.dto.TripData;
import com.carpooler.trips.TripStatus;
import com.carpooler.ui.adapters.TripRecyclerAdapter;

import java.util.List;

public class TripListFragment extends Fragment implements DatabaseService.QueryCallback<TripData>{

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private TripRecyclerAdapter adapter;
    private TripDetailCallback callback;
    public static final String STATUS_ARG = "status";
    public static final String HOSTED_ARG = "hosted";
    private TripStatus tripStatus;
    private boolean hosted = false; // true for "host"; false for "participant"

    protected void setupAdapter(List<TripData> trips){
        adapter = new TripRecyclerAdapter(trips,callback);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callback = (TripDetailCallback) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trip_list, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.trip_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new TripRecyclerAdapter(null,callback);
        recyclerView.setAdapter(adapter);
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.contentView);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        Bundle args = getArguments();
        if (args!=null) {
            String status = args.getString(STATUS_ARG, TripStatus.OPEN.name());
            tripStatus = TripStatus.valueOf(status);
            hosted = args.getBoolean(HOSTED_ARG);
        }else{
            tripStatus = TripStatus.OPEN;
        }

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
            if(hosted) {
                callback.getTripDataService().findTripsByHostIdAndStatus(callback.getUser().getGoogleId(), tripStatus, this);
            } else {
                callback.getTripDataService().findTripsByUserIdAndStatus(callback.getUser().getGoogleId(), tripStatus, this);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doError(String message) {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void doException(Exception exception) {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void doSuccess(List<TripData> data) {
        setupAdapter(data);
        refreshLayout.setRefreshing(false);

    }
}
