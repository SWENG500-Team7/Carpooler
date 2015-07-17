package com.carpooler.ui.activities;


import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carpooler.R;
import com.carpooler.trips.AddressErrorCallback;
import com.carpooler.users.CarpoolUser;

/**
 */
public class JoinTripFragment extends AbstractTripView implements MenuItem.OnMenuItemClickListener {
    private MenuItem miEdit;
    private MenuItem miDelete;
    private MenuItem miSave;
    private CarpoolUser carpoolUser;
    private TextView startTime;
    private AddressFieldsManager addressFieldsManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_join_trip, container, false);
        startTime = (TextView) rootView.findViewById(R.id.startTime);
        addressFieldsManager = new AddressManager(rootView,R.id.start_address,R.id.end_address);
        setupArgs();
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_trip_detail, menu);
    }
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        miSave = menu.findItem(R.id.mi_save_trip);
        miSave.setOnMenuItemClickListener(this);
        miEdit = menu.findItem(R.id.mi_edit_trip);
        miDelete = menu.findItem(R.id.mi_cancel_trip);
        miSave.setVisible(true);
        miEdit.setVisible(false);
        miDelete.setVisible(false);
        checkSave();
    }

    @Override
    protected void setupData() {
        startTime.setText(trip.getStartTime().toString());
        carpoolUser = trip.requestJoinTrip();
        checkSave();
    }

    @Override
    protected void preLoadData() {

    }

    @Override
    protected void postLoadData() {

    }
    private void checkSave(){
        if (miSave!=null && carpoolUser!=null) {
            if (carpoolUser.getPickupLocation() != null && carpoolUser.getDropoffLocation() != null) {
                miSave.setVisible(true);
            } else {
                miSave.setVisible(false);
            }
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        trip.addUser(carpoolUser);
        callback.onTripSelected(tripId);
        return true;
    }
    private class AddressManager extends AddressFieldsManager{

        public AddressManager(View view, int startAddressId, int endAddressId) {
            super(view, startAddressId, endAddressId);
        }

        @Override
        protected Activity getActivity() {
            return JoinTripFragment.this.getActivity();
        }

        @Override
        protected void checkSave() {
            JoinTripFragment.this.checkSave();
        }

        protected void setStartLocation(String address, AddressErrorCallback callback) throws RemoteException {
            carpoolUser.setPickupLocation(address, callback);
        }

        @Override
        protected void setEndLocation(String address, AddressErrorCallback callback) throws RemoteException {
            carpoolUser.setDropoffLocation(address, callback);
        }
    }
}
