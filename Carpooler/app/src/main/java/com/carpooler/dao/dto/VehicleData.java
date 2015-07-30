package com.carpooler.dao.dto;

/**
 * Created by raymond on 6/13/15.
 */
public class VehicleData {
    protected static final String MAPPING =
            "{"
                + "\"properties\":{"
                    + "\"seats\":{\"type\":\"integer\"},"
                    + "\"make\":{\"type\":\"string\"},"
                    + "\"model\":{\"type\":\"string\"},"
                    + "\"color\":{\"type\":\"string\"},"
                    + "\"plateNumber\":{\"type\":\"string\"},"
                    + "\"year\":{\"type\":\"integer\"},"
                    + "\"mpg\":{\"type\":\"integer\"}"
                + "}"
            + "}";
    private int seats;
    private String make;
    private String model;
    private int year;
    private String color;
    private String plateNumber;
    private int mpg;

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

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public int getMPG() {
        return mpg;
    }

    public void setMPG(int mpg) {
        this.mpg = mpg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VehicleData that = (VehicleData) o;

        return !(plateNumber != null ? !plateNumber.equals(that.plateNumber) : that.plateNumber != null);

    }

    @Override
    public int hashCode() {
        return plateNumber != null ? plateNumber.hashCode() : 0;
    }
}
