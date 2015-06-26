package com.carpooler.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carpooler.R;
import com.carpooler.dao.dto.TripData;
import com.carpooler.trips.TripStatus;
import com.carpooler.ui.adapters.TripRecyclerAdapter;

import java.util.List;

public class TripListFragment extends Fragment implements MessageFragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private TripRecyclerAdapter adapter;
    private static final int QUERY_ID=100;
    private TripDetailCallback callback;
    public static final String STATUS_ARG = "status";
    private TripStatus tripStatus;

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
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
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
                callback.getTripDataService().findTripsByHostIdAndStatus("testuser", tripStatus, QUERY_ID);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what){
            case QUERY_ID:
                setupAdapter((List<TripData>) msg.obj);
                refreshLayout.setRefreshing(false);
                break;
        }
    }

    @Override
    public void handleError(Message msg) {
        refreshLayout.setRefreshing(false);
    }
}
