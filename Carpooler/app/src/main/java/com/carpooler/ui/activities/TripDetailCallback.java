package com.carpooler.ui.activities;

import com.carpooler.users.Address;

/**
 * Created by raymond on 6/24/15.
 */
public interface TripDetailCallback extends ServiceActivityCallback {
    public void onTripSelected(String tripId);
    public void onAddTrip();
    public void onHostCompleteTrip(String tripId);
    public void onUserCompleteTrip(String tripId);
    public void onUserPaid(String tripId);
    public void onJoinTripSelected(String tripId);
    public void navigate(Address address);
}
