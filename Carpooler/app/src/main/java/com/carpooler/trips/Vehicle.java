package com.carpooler.trips;

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
}
