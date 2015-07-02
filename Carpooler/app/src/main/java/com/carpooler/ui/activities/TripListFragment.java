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
import com.carpooler.dao.FindTripQuery;
import com.carpooler.dao.dto.GeoPointData;
import com.carpooler.dao.dto.TripData;
import com.carpooler.trips.TripStatus;
import com.carpooler.ui.adapters.TripRecyclerAdapter;

import java.util.Date;
import java.util.List;

public abstract class TripListFragment extends Fragment  implements DatabaseService.QueryCallback<TripData> {

    private RecyclerView recyclerView;
    protected SwipeRefreshLayout refreshLayout;
    private TripRecyclerAdapter adapter;
    protected TripDetailCallback callback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callback = (TripDetailCallback) activity;
    }

    protected void setupAdapter(List<TripData> trips) {
        adapter = new TripRecyclerAdapter(trips, callback);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trip_list, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.trip_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new TripRecyclerAdapter(null, callback);
        recyclerView.setAdapter(adapter);
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.contentView);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        setupArgs();
        return rootView;
    }
    @Override
    public void onStart() {
        super.onStart();
        loadData();
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
                    callback.getTripDataService().findTripsByHostIdAndStatus(callback.getUser().getGoogleId(), tripStatus, this);
                } else {
                    callback.getTripDataService().findTripsByUserIdAndStatus(callback.getUser().getGoogleId(), tripStatus, this);
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
        @Override
        protected void doLoadData() {
            try {
                callback.getTripDataService().findAvailableTrips(findTripQuery,this);
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
    }
}
