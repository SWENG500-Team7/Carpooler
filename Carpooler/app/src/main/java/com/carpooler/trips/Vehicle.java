package com.carpooler.trips;

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

    @Override
    public String toString() {
        return vehicleData.toString();
    }
    public VehicleData getData(){
        return vehicleData;
    }
}
