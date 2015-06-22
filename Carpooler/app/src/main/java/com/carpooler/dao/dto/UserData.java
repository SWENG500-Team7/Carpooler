package com.carpooler.dao.dto;

import com.carpooler.dao.annotations.ElasticData;

import java.util.ArrayList;
import java.util.List;

import io.searchbox.annotations.JestId;

/**
 * Created by raymond on 6/13/15.
 */
@ElasticData(
        index = "user",
        type = "user",
        mapping = "{\""
                    + "user\":{" +
                        "\"properties\":{"
                            + "\"userId\": {\"type\": \"string\", \"indexed\":\"not_analyzed\", \"copy_to\":\"_id\"},"
                            + "\"vehicle\": " + VehicleData.MAPPING
                        + "}"
                    + "}"
                + "}"
)

public class UserData implements DatabaseObject {
    @JestId
    private String userId;
    private List<VehicleData> vehicle = new ArrayList<>();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<VehicleData> getVehicle() {
        return vehicle;
    }

    public void setVehicle(List<VehicleData> vehicle) {
        this.vehicle = vehicle;
    }

}
