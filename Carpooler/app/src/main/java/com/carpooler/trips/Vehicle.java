package com.carpooler.trips;

import com.carpooler.dao.dto.VehicleData;

/**
 * Created by Aidos on 07.06.2015.
 */
public class Vehicle {
    private int seats;
    private String make;
    private String model;
    private int year;
    private String color;
    private String plateNumber;

    public Vehicle(int seats, String pN) {
        this.seats = seats;
        this.plateNumber = pN;
    }

    public String getPlateNumber() {
        return this.plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public VehicleData toVehicleDTO() {
        VehicleData vehicleDTO = new VehicleData();
        vehicleDTO.setColor(color);
        vehicleDTO.setPlateNumber(plateNumber);
        vehicleDTO.setSeats(seats);
        vehicleDTO.setMake(make);
        vehicleDTO.setModel(model);
        vehicleDTO.setYear(year);

        return vehicleDTO;
    }

    public static Vehicle fromVehicleDTO(VehicleData data) {
        Vehicle vehicle = new Vehicle(data.getSeats(), data.getPlateNumber());
        vehicle.setMake(data.getMake());
        vehicle.setModel(data.getModel());
        vehicle.setYear(data.getYear());
        vehicle.setColor(data.getColor());

        return vehicle;
    }

    @Override
    public String toString() {
        return make + " " + model + " (" + plateNumber + ")";
    }
}
