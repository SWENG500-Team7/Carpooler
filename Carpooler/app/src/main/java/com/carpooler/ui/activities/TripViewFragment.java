package com.carpooler.ui.activities;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carpooler.R;
import com.carpooler.ui.adapters.CarpoolUserAdapter;
import com.carpooler.ui.adapters.TripRowHolder;

public class TripViewFragment extends AbstractTripView {
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private CarpoolUserAdapter carpoolUserAdapter;
    private TripRowHolder tripRowHolder;

    @Override
    protected void setupData() {
        tripRowHolder.loadData(trip);
        carpoolUserAdapter = new CarpoolUserAdapter(trip, callback);
        recyclerView.setAdapter(carpoolUserAdapter);
    }

    @Override
    protected void preLoadData() {
        if (!refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(true);
        }
    }

    @Override
    protected void postLoadData() {
        if (refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trip_view, container, false);
        setupArgs();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.trip_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.contentView);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        tripRowHolder = new TripRowHolder(rootView, callback, false);
        return rootView;
    }
}
