package com.carpooler.trips;

import android.os.AsyncTask;

import com.carpooler.dao.VehicleRestService;
import com.carpooler.dao.dto.VehicleData;

/**
 * Created by Aidos on 07.06.2015.
 */
public class Vehicle {
    private final VehicleData vehicleData;

    public Vehicle(VehicleData vehicleData) {
        this.vehicleData = vehicleData;
    }

    public String getPlateNumber() {
        return vehicleData.getPlateNumber();
    }

    public void setPlateNumber(String plateNumber) {
        vehicleData.setPlateNumber(plateNumber);
    }

    public int getSeats() {
        return vehicleData.getSeats();
    }

    public void setSeats(int seats) {
        vehicleData.setSeats(seats);
    }

    public String getMake() {
        return vehicleData.getMake();
    }

    public void setMake(String make) {
        vehicleData.setMake(make);
    }

    public String getModel() {
        return vehicleData.getModel();
    }

    public void setModel(String model) {
        vehicleData.setModel(model);
    }

    public int getYear() {
        return vehicleData.getYear();
    }

    public void setYear(int year) {
        vehicleData.setYear(year);
    }

    public String getColor() {
        return vehicleData.getColor();
    }

    public void setColor(String color) {
        vehicleData.setColor(color);
    }

    private class VehicleMPGRequestor extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            vehicleData.setMPG(VehicleRestService.getMPG(getMake(), getModel(), String.valueOf(getYear())));
            return null;
        }
    }

    public int getMPG() {
        return vehicleData.getMPG();
    }

    public void setMPG() {
        new VehicleMPGRequestor().execute();
    }

    @Override
    public String toString() {
        return vehicleData.toString();
    }
    public VehicleData getData(){
        return vehicleData;
    }
}
