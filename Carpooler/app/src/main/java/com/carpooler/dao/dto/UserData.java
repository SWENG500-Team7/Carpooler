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
                            + "\"vehicle\": {"
                                + "\"properties\":{"
                                    + "\"seats\":{\"type\":\"integer\"},"
                                    + "\"make\":{\"type\":\"string\"},"
                                    + "\"model\":{\"type\":\"string\"},"
                                    + "\"color\":{\"type\":\"string\"},"
                                    + "\"plateNumber\":{\"type\":\"string\"},"
                                    + "\"year\":{\"type\":\"integer\"}"
                                + "}"
                            + "}"
                        + "}"
                    + "}"
                + "}"
)

public class UserData implements DatabaseObject {
    @JestId
    private String userId;
    private List<Vehicle> vehicle = new ArrayList<>();

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Vehicle> getVehicle() {
        return vehicle;
    }

    public void setVehicle(List<Vehicle> vehicle) {
        this.vehicle = vehicle;
    }

}
