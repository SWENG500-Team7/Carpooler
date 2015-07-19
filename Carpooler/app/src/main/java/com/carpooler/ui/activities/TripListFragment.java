package com.carpooler.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.carpooler.R;
import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.FindTripQuery;
import com.carpooler.dao.dto.GeoPointData;
import com.carpooler.dao.dto.TripData;
import com.carpooler.trips.TripSearchResults;
import com.carpooler.trips.TripStatus;
import com.carpooler.ui.adapters.TripRecyclerAdapter;

import java.util.Date;
import java.util.List;

import io.searchbox.core.SearchResult;

public abstract class TripListFragment extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    protected SwipeRefreshLayout refreshLayout;
    private TripRecyclerAdapter adapter;
    protected TripDetailCallback callback;
    private ImageButton mAddButton;
    protected TripSearchResults mResults;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callback = (TripDetailCallback) activity;
    }

    protected void setupAdapter(TripSearchResults tripSearchResults) {
        adapter = new TripRecyclerAdapter(tripSearchResults, callback);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trip_list, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.trip_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.contentView);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        mAddButton = (ImageButton) rootView.findViewById(R.id.btn_add_trip);
        mAddButton.setOnClickListener(this);
        setupArgs();
        return rootView;
    }
    @Override
    public void onStart() {
        super.onStart();
        loadData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_trip:
                callback.onAddTrip();
                break;
        }
    }

    private abstract class ErrorCallback {
        public void doError(String message) {
            refreshLayout.setRefreshing(false);
        }

        public void doException(Exception exception) {
            refreshLayout.setRefreshing(false);
        }

    }
    private class TripQueryCallback extends ErrorCallback implements DatabaseService.QueryCallback<TripData> {

        public void doSuccess(List<TripData> data) {
            TripSearchResults tripSearchResults = new TripSearchResults(data,callback);
            setupAdapter(tripSearchResults);
            refreshLayout.setRefreshing(false);
        }

    }
    private class TripHitsQueryCallback extends ErrorCallback implements DatabaseService.QueryHitsCallback<TripData> {
        @Override
        public void doSuccess(List<SearchResult.Hit<TripData, Void>> data) {
            //TripSearchResults tripSearchResults = new TripSearchResults(data,callback,true);
            mResults = new TripSearchResults(data,callback,true);
            setupAdapter(/*tripSearchResults*/mResults);
            refreshLayout.setRefreshing(false);
        }


    }
    protected void loadData(){
        if (!refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(true);
        }
        doLoadData();
    }
    protected abstract void doLoadData();
    protected abstract void setupArgs();

    public static class Status extends TripListFragment{
        public static final String STATUS_ARG = "status";
        public static final String HOSTED_ARG = "hosted";
        private TripStatus tripStatus = TripStatus.OPEN;
        private boolean hosted = false; // true for "host"; false for "participant"

        @Override
        protected void doLoadData() {
            try {
                if (hosted) {
                    callback.getUser().findHostedTrips(tripStatus, new TripQueryCallback());
                } else {
                    callback.getUser().findParticipatingTrips(tripStatus, new TripQueryCallback());
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void setupArgs() {
            Bundle args = getArguments();
            if (args != null) {
                String status = args.getString(STATUS_ARG, TripStatus.OPEN.name());
                tripStatus = TripStatus.valueOf(status);
                hosted = args.getBoolean(HOSTED_ARG);
            }

        }
    }

    public static class Search extends TripListFragment{
        private FindTripQuery findTripQuery;
        public static final String START_LON = "startLon";
        public static final String START_LAT = "startLat";
        public static final String END_LON = "endLon";
        public static final String END_LAT = "endLat";
        public static final String START_DATE = "startDate";
        public static final String SEARCH_DISTANCE = "searchDistance";
        public static final String TIME_RANGE = "timeRange";

        private MenuItem mi_SortByStartTime;
        private MenuItem mi_SortBySeats;
        private MenuItem mi_SortByPickupDistance;

        @Override
        protected void doLoadData() {
            try {
                callback.getTripDataService().findAvailableTrips(findTripQuery,new TripHitsQueryCallback());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void setupArgs() {
            Bundle args = getArguments();
            double startLon = args.getDouble(START_LON);
            double startLat = args.getDouble(START_LAT);
            GeoPointData startPoint = new GeoPointData();
            startPoint.setLat(startLat);
            startPoint.setLon(startLon);
            double endLon = args.getDouble(END_LON);
            double endtLat = args.getDouble(END_LAT);
            GeoPointData endPoint = new GeoPointData();
            endPoint.setLat(endtLat);
            endPoint.setLon(endLon);
            Date startDate = new Date(args.getLong(START_DATE));
            int searchDistance = args.getInt(SEARCH_DISTANCE);
            int timeRange = args.getInt(TIME_RANGE);
            findTripQuery = new FindTripQuery();
            findTripQuery.setDistance(searchDistance);
            findTripQuery.setTimeRange(timeRange);
            findTripQuery.setStartTime(startDate);
            findTripQuery.setStartPoint(startPoint);
            findTripQuery.setEndPoint(endPoint);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            super.setHasOptionsMenu(true);
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Inflate the menu; this adds items to the action bar if it is present.
            super.onCreateOptionsMenu(menu, inflater);
            menu.clear();
            inflater.inflate(R.menu.menu_sort_triplist, menu);
        }

        public void onPrepareOptionsMenu(Menu menu) {
            mi_SortByStartTime = menu.findItem(R.id.mi_sort_time);
            mi_SortBySeats = menu.findItem(R.id.mi_sort_seats);
            mi_SortByPickupDistance = menu.findItem(R.id.mi_sort_pickupRadius);
            mi_SortByStartTime.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return false;
                }
            });

            mi_SortBySeats.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return false;
                }
            });

            mi_SortByPickupDistance.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    mResults.sortByStartDistance();
                    return true;
                }
            });
            mi_SortBySeats.setVisible(true);
            mi_SortByStartTime.setVisible(true);
            mi_SortByPickupDistance.setVisible(true);

        }
    }
}
