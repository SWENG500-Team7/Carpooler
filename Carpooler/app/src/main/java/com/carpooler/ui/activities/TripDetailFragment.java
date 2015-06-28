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
import android.widget.TextView;

import com.carpooler.R;
import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.dto.TripData;
import com.carpooler.trips.TripStatus;

import java.util.List;

public class TripDetailFragment extends Fragment {

    private SwipeRefreshLayout refreshLayout;
    private View rootView;
    private ServiceActivityCallback callback;
    public static final String TRIP_ID_ARG = "tripId";
    private String searchId;
    public static final String STATUS_ARG = "status";
    private TripStatus tripStatus;
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
        rootView = inflater.inflate(R.layout.fragment_trip_detail, container, false);
        Bundle args = getArguments();
        if (args!=null) {
            searchId = args.getString(TRIP_ID_ARG, null);
            String status = args.getString(STATUS_ARG, TripStatus.IN_ROUTE.name());
            tripStatus = TripStatus.valueOf(status);
        }else{
            tripStatus = TripStatus.IN_ROUTE;
        }
        textView = (TextView) rootView.findViewById(R.id.hello);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (searchId!=null){
            try {
                callback.getTripDataService().getTripData(searchId, getCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.trip_detail_recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.contentView);
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    loadData();
                }
            });
            loadData();
        }
    }

    private void loadData(){
        try {
            if (!refreshLayout.isRefreshing()){
                refreshLayout.setRefreshing(true);
            }
            callback.getTripDataService().findTripsByUserIdAndStatus(callback.getUser().getGoogleId(), tripStatus, queryCallback);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private DatabaseService.GetCallback<TripData> getCallback = new DatabaseService.GetCallback<TripData>() {
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
    };

    private DatabaseService.QueryCallback<TripData> queryCallback = new DatabaseService.QueryCallback<TripData>() {
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
            refreshLayout.setRefreshing(false);
        }
    };

}
