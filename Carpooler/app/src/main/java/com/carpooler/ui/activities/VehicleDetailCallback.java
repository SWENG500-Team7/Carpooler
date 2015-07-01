package com.carpooler.ui.activities;

/**
 * Created by Kevin on 6/28/2015.
 */
public interface VehicleDetailCallback extends ServiceActivityCallback {
    public void onVehicleSelected(String plateNumber);
    public void onAddVehicle();
}
