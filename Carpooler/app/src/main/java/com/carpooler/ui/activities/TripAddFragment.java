package com.carpooler.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.carpooler.R;
import com.carpooler.dao.dto.TripData;
import com.carpooler.trips.AddressErrorCallback;
import com.carpooler.trips.Trip;
import com.carpooler.trips.Vehicle;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class TripAddFragment extends Fragment implements MenuItem.OnMenuItemClickListener, DatePicker.OnDateChangedListener, TimePicker.OnTimeChangedListener, AdapterView.OnItemSelectedListener {

    private View rootView;
    private MenuItem miEdit;
    private MenuItem miDelete;
    private MenuItem miSave;
    private DatePicker tripDatePicker;
    private TimePicker tripStartTimePicker;
    private TimePicker tripEndTimePicker;
    private Spinner vehicleSpinner;
    private HashMap<String, Vehicle> vehicleMap = new HashMap<String, Vehicle>();
    private int year;
    private int month;
    private int day;
    private int start_hour;
    private int start_minute;
    private int end_hour;
    private int end_minute;
    private ServiceActivityCallback callback;
    private Trip trip;
    private AddressFieldsManager addressFieldsManager;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callback = (ServiceActivityCallback) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_trip_add, container, false);
        vehicleSpinner = (Spinner) rootView.findViewById(R.id.tripVehicleDropdown);
        vehicleSpinner.setOnItemSelectedListener(this);
        tripDatePicker = (DatePicker) rootView.findViewById(R.id.tripDatePicker);
        tripStartTimePicker = (TimePicker) rootView.findViewById(R.id.tripStartTimePicker);
        tripEndTimePicker = (TimePicker) rootView.findViewById(R.id.tripEndTimePicker);
        initDateAndTimeOnView();
        addressFieldsManager = new AddressManager(rootView,R.id.start_address,R.id.end_address);
        trip = new Trip(new TripData(),callback);

        //Signal to system that this fragment has it's own actionbar items
        setHasOptionsMenu(true);
        return rootView;
    }

    private void initDateAndTimeOnView() {
        final Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        tripDatePicker.init(year, month, day, this);
        start_hour = calendar.get(Calendar.HOUR_OF_DAY);
        start_minute = calendar.get(Calendar.MINUTE);
        tripStartTimePicker.setCurrentHour(start_hour);
        tripStartTimePicker.setCurrentMinute(start_minute);
        tripStartTimePicker.setOnTimeChangedListener(this);
        end_hour = calendar.get(Calendar.HOUR_OF_DAY);
        end_minute = calendar.get(Calendar.MINUTE);
        tripEndTimePicker.setCurrentHour(end_hour);
        tripEndTimePicker.setCurrentMinute(end_minute);
        tripEndTimePicker.setOnTimeChangedListener(this);
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
        miEdit = menu.findItem(R.id.mi_edit_trip);
        miDelete = menu.findItem(R.id.mi_cancel_trip);
        miSave = menu.findItem(R.id.mi_save_trip);
        miEdit.setOnMenuItemClickListener(this);
        miDelete.setOnMenuItemClickListener(this);
        miSave.setOnMenuItemClickListener(this);

        //Start in mode depending if user is creating a new trip
        setFormEnabled(true);
        checkSave();
    }

    @Override
    public void onStart() {
        super.onStart();
        initVehicleSpinner();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_edit_trip:
                setFormEnabled(true);
                break;
            case R.id.mi_cancel_trip:
//TODO                cancelTrip();
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


    private void initVehicleSpinner() {
        List<Vehicle> vehicleList = callback.getUser().getVehicles();
        String[] vehicles = new String[vehicleList.size()];
        int i = 0;
        for (Vehicle vehicle : vehicleList) {
            vehicleMap.put(vehicle.getYear() + " " + vehicle.getMake() + " " + vehicle.getModel(), vehicle);
            vehicles[i] = vehicle.getYear() + " " + vehicle.getMake() + " " + vehicle.getModel();
            i++;
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, vehicles);
        vehicleSpinner.setAdapter(spinnerAdapter);
    }

    private void saveTrip() {
        Calendar startCal = Calendar.getInstance();
        startCal.set(year,month,day,start_hour,start_minute);
        trip.setStartTime(startCal.getTime());
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

    private void checkSave(){
        if (miSave!=null && trip!=null) {
            if (trip.getStartLocation() != null && trip.getEndLocation() != null) {
                miSave.setEnabled(true);
            } else {
                miSave.setEnabled(false);
            }
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == vehicleSpinner) {
            Vehicle vehicle = vehicleMap.get(vehicleSpinner.getItemAtPosition(position).toString());
            trip.setHostVehicle(vehicle);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing
    }

    private class AddressManager extends AddressFieldsManager{

        public AddressManager(View view, int startAddressId, int endAddressId) {
            super(view, startAddressId, endAddressId);
        }

        @Override
        protected Activity getActivity() {
            return TripAddFragment.this.getActivity();
        }

        @Override
        protected void checkSave() {
            TripAddFragment.this.checkSave();
        }

        protected void setStartLocation(String address, AddressErrorCallback callback) throws RemoteException {
            trip.setStartLocation(address,callback);
        }

        @Override
        protected void setEndLocation(String address, AddressErrorCallback callback) throws RemoteException {
            trip.setEndLocation(address, callback);
            trip.setFuelPrice();
        }
    }
}
