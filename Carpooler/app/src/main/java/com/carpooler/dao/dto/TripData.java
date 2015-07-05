package com.carpooler.dao.dto;

import com.carpooler.dao.annotations.ElasticData;
import com.carpooler.trips.TripStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
                            + "\"hostVehicle\": {\"type\": \"string\", \"index\":\"not_analyzed\"},"
                            + "\"status\": {\"type\": \"string\", \"index\":\"not_analyzed\"},"
                            + "\"fuelSplit\": {\"type\": \"double\"},"
                            + "\"openSeats\":{\"type\":\"integer\"},"
                            + "\"startTime\": {\"type\": \"date\", \"format\":\"date_time_no_millis\"},"
                            + "\"endTime\": {\"type\": \"date\", \"format\":\"date_time_no_millis\"},"
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
    private String hostVehicle;
    private TripStatus status = TripStatus.OPEN;
    private AddressData startLocation;
    private AddressData endLocation;
    private Date startTime;
    private Date endTime;
    private List<CarpoolUserData> users = new ArrayList<>();
    private double fuelSplit = 0.00;
    private int openSeats;

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

    public double getFuelSplit() {

        return fuelSplit;
    }

    public String getHostId() {
        return hostId;
    }

    public void setHostId(String hostId) {
        this.hostId = hostId;
    }

    public void setFuelSplit(double fuelSplit) {
        this.fuelSplit = fuelSplit;
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

    public String getHostVehicle() {
        return hostVehicle;
    }

    public void setHostVehicle(String hostVehicle) {
        this.hostVehicle = hostVehicle;
    }

    public int getOpenSeats() {
        return openSeats;
    }

    public void setOpenSeats(int openSeats) {
        this.openSeats = openSeats;
    }
}
