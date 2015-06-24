package com.carpooler.ui.activities;

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
import com.carpooler.dao.TripDataService;
import com.carpooler.dao.dto.TripData;
import com.carpooler.trips.TripStatus;
import com.carpooler.ui.adapters.TripRecyclerAdapter;

import java.util.List;

public class TripListFragment extends Fragment implements MessageFragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private TripRecyclerAdapter adapter;
    private TripDataService tripDataService;
    private static final int QUERY_ID=100;
    protected void setupAdapter(List<TripData> trips){
        adapter = new TripRecyclerAdapter(trips);
        recyclerView.setAdapter(adapter);
    }

    public void setTripDataService(TripDataService tripDataService) {
        this.tripDataService = tripDataService;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trip_list, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new TripRecyclerAdapter(null);
        recyclerView.setAdapter(adapter);
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

    @Override
    public void onStop() {
        super.onStop();
        tripDataService = null;
    }

    private void loadData(){
        try {
            if (!refreshLayout.isRefreshing()){
                refreshLayout.setRefreshing(true);
            }
            if (tripDataService!=null) {
                tripDataService.findTripsByHostIdAndStatus("testuser", TripStatus.OPEN, QUERY_ID);
            }
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
}
