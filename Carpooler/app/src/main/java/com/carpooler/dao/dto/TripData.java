package com.carpooler.dao.dto;

import android.util.Log;

import com.carpooler.dao.annotations.ElasticData;
import com.carpooler.trips.Trip;
import com.carpooler.trips.TripStatus;
import com.carpooler.trips.Vehicle;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Comparator;

import io.searchbox.annotations.JestId;

/**
 * Created by raymond on 6/20/15.
 */
@ElasticData(
        index = "trips",
        type = "trip",
        mapping =
                "{\""
                    + "trip\":{" +
                        "\"properties\":{"
                            + "\"hostId\": {\"type\": \"string\", \"index\":\"not_analyzed\"},"
                            + "\"hostVehicle\": " + VehicleData.MAPPING + ","
                            + "\"status\": {\"type\": \"string\", \"index\":\"not_analyzed\"},"
                            + "\"fuelPrice\": {\"type\": \"double\"},"
                            + "\"fuelTotal\": {\"type\": \"double\"},"
                            + "\"tolls\": {\"type\": \"double\"},"
                            + "\"openSeats\":{\"type\":\"integer\"},"
                            + "\"startTime\": {\"type\": \"date\", \"format\":\"date_time_no_millis\"},"
                            + "\"endTime\": {\"type\": \"date\", \"format\":\"date_time_no_millis\"},"
                            + "\"totalDistance\": {\"type\": \"int\"},"
                            + "\"startLocation\": " + AddressData.MAPPING + ","
                            + "\"endLocation\": " + AddressData.MAPPING + ","
                            + "\"users\": " + CarpoolUserData.MAPPING
                        + "}"
                    + "}"
                + "}"
)
public class TripData implements DatabaseObject{
    @JestId
    private transient String _id;
    private String hostId;
    private Vehicle hostVehicle;
    private TripStatus status = TripStatus.OPEN;
    private AddressData startLocation;
    private AddressData endLocation;
    private Date startTime;
    private Date endTime;
    private List<CarpoolUserData> users = new ArrayList<>();
    private double fuelPrice = 0.00;
    private double fuelTotal = 0.00;
    private double tolls = 0.00;
    private int openSeats;
    private int totalDistance = 0;

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus status) {
        this.status = status;
    }

    public List<CarpoolUserData> getUsers() {
        return users;
    }

    public void setUsers(List<CarpoolUserData> users) {
        this.users = users;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public double getFuelPrice() {
        return fuelPrice;
    }

    public void setFuelPrice(double fuelPrice) {
        this.fuelPrice = fuelPrice;
    }

    public double getFuelTotal() {
        return fuelTotal;
    }

    public double getTolls() {
        return tolls;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public void setFuelTotal(double fuelTotal) {
        this.fuelTotal = fuelTotal;
    }

    public void setTolls(double tolls) {
        this.tolls = tolls;
    }

    public AddressData getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(AddressData startLocation) {
        this.startLocation = startLocation;
    }

    public AddressData getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(AddressData endLocation) {
        this.endLocation = endLocation;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Vehicle getHostVehicle() {
        return hostVehicle;
    }

    public void setHostVehicle(Vehicle hostVehicle) {
        this.hostVehicle = hostVehicle;
    }

    public int getOpenSeats() {
        return openSeats;
    }

    public void setOpenSeats(int openSeats) {
        this.openSeats = openSeats;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
    }

}
