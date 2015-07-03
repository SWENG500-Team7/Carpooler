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
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.carpooler.R;
import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.dto.TripData;
import com.carpooler.trips.Trip;
import com.carpooler.trips.TripStatus;

import java.util.Date;
import java.util.List;

public class TripDetailFragment extends Fragment implements MenuItem.OnMenuItemClickListener, DatePicker.OnDateChangedListener, TimePicker.OnTimeChangedListener {

    private SwipeRefreshLayout refreshLayout;
    private View rootView;
    private MenuItem miEdit;
    private MenuItem miDelete;
    private MenuItem miSave;
    private DatePicker tripDatePicker;
    private TimePicker tripStartTimePicker;
    private TimePicker tripEndTimePicker;
    private int year;
    private int month;
    private int day;
    private int start_hour;
    private int start_minute;
    private int end_hour;
    private int end_minute;
    private ServiceActivityCallback callback;
    public static final String TRIP_ID_ARG = "tripId";
    private String tripId;
    public static final String CREATE_TRIP_ARG = "createTrip";
    private boolean createTrip = false;
    public static final String STATUS_ARG = "status";
    private TripStatus tripStatus;
    private TextView textView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callback = (ServiceActivityCallback) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args!=null) {
            tripId = args.getString(TRIP_ID_ARG, null);
            String status = args.getString(STATUS_ARG, TripStatus.IN_ROUTE.name());
            tripStatus = TripStatus.valueOf(status);

            createTrip = args.getBoolean(CREATE_TRIP_ARG, false);
        }else{
            tripStatus = TripStatus.IN_ROUTE;
        }
        boolean hasMenu = true;
        // Inflate the layout for this fragment
        if (tripId != null) {
            rootView = inflater.inflate(R.layout.fragment_trip_detail, container, false);
            textView = (TextView) rootView.findViewById(R.id.hello);
        } else if (createTrip) {
            rootView = inflater.inflate(R.layout.fragment_add_trip, container, false);
            tripDatePicker = (DatePicker) rootView.findViewById(R.id.tripDatePicker);
            tripStartTimePicker = (TimePicker) rootView.findViewById(R.id.tripStartTimePicker);
            tripEndTimePicker = (TimePicker) rootView.findViewById(R.id.tripEndTimePicker);
        } else {
            rootView = inflater.inflate(R.layout.fragment_trip_inprogress, container, false);
            hasMenu = false;
        }

        //Signal to system that this fragment has it's own actionbar items
        setHasOptionsMenu(hasMenu);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_trip_detail, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        miEdit = (MenuItem) menu.findItem(R.id.mi_edit_trip);
        miDelete = (MenuItem) menu.findItem(R.id.mi_cancel_trip);
        miSave = (MenuItem) menu.findItem(R.id.mi_save_trip);
        miEdit.setOnMenuItemClickListener(this);
        miDelete.setOnMenuItemClickListener(this);
        miSave.setOnMenuItemClickListener(this);

        //Start in mode depending if user is creating a new trip
        setFormEnabled(createTrip);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (tripId !=null){
            try {
                callback.getTripDataService().getTripData(tripId, getCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (createTrip) {
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_edit_trip:
                setFormEnabled(true);
                break;
            case R.id.mi_cancel_trip:
                cancelTrip();
                goBack();
                break;
            case R.id.mi_save_trip:
                saveTrip();
                setFormEnabled(false);
                goBack();
                break;
            default:
                break;
        }
        return true;
    }

    public void goBack()
    {
        CarpoolerActivity activity = (CarpoolerActivity) getActivity();
        activity.goBack(getString(R.string.nav_item_hosted_trips));
    }

    private void setFormEnabled(boolean enabled) {
        miSave.setVisible(enabled);
        miEdit.setVisible(!enabled);
        miDelete.setVisible(!enabled);
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

    private void saveTrip() {
        TripData tripData = new TripData();
        tripData.setHostId(callback.getUser().getGoogleId());
        tripData.setStatus(TripStatus.OPEN);
        tripData.setStartTime(new Date(year, month, day, start_hour, start_minute));
        tripData.setEndTime(new Date(year, month, day, end_hour, end_minute));
        Trip trip = new Trip(tripData, callback.getTripDataService());
        trip.saveTrip();
    }

    private void cancelTrip() {
        TripData tripData = new TripData();
        tripData.set_id(tripId);
        tripData.setStatus(TripStatus.CANCELLED);
        Trip trip = new Trip(tripData, callback.getTripDataService());
        trip.saveTrip();
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        if (view == tripDatePicker) {
            this.year = year;
            month = monthOfYear;
            day = dayOfMonth;
        }
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        if (view == tripStartTimePicker) {
            start_hour = hourOfDay;
            start_minute = minute;
        } else if (view == tripEndTimePicker) {
            end_hour = hourOfDay;
            end_minute = minute;
        }
    }
}