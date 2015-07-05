package com.carpooler.dao.dto;

import com.carpooler.dao.annotations.ElasticData;
import com.carpooler.users.Rating;

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
                            + "\"vehicle\": " + VehicleData.MAPPING + ","
                            + "\"averageRating\": {\"type\": \"string\", \"indexed\":\"not_analyzed\"},"
                            + "\"ratings\": {\"type\": \"integer\"},"
                            + "\"reviews\": " + UserReviewData.MAPPING
                        + "}"
                    + "}"
                + "}"
)

public class UserData implements DatabaseObject {
    @JestId
    private String userId;
    private List<VehicleData> vehicle = new ArrayList<>();
    private Rating averageRating;
    private int ratings;
    private List<UserReviewData> reviews = new ArrayList<>();

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

    public Rating getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Rating averageRating) {
        this.averageRating = averageRating;
    }
    public List<UserReviewData> getReviews() {
        return reviews;
    }

    public void setReviews(List<UserReviewData> reviews) {
        this.reviews = reviews;
    }

    public int getRatings() {
        return ratings;
    }

    public void setRatings(int ratings) {
        this.ratings = ratings;
    }
}
