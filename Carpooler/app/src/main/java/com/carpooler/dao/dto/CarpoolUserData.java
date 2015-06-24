package com.carpooler.dao.dto;

import com.carpooler.users.CarpoolUserStatus;

import java.util.Date;

/**
 * Created by raymond on 6/20/15.
 */
public class CarpoolUserData {
    protected static final String MAPPING=
            "{"
                + "\"type\":\"nested\","
                + "\"properties\":{"
                    + "\"userId\": {\"type\": \"string\", \"index\":\"not_analyzed\"},"
                    + "\"pickupLocation\":" + AddressData.MAPPING + ","
                    + "\"dropoffLocation\":" + AddressData.MAPPING + ","
                    + "\"status\":{\"type\":\"string\"},"
                    + "\"paymentAmount\":{\"type\":\"double\"},"
                    + "\"pickupDate\":{\"type\": \"date\", \"format\":\"date_time_no_millis\"},"
                    + "\"dropoffDate\":{\"type\": \"date\", \"format\":\"date_time_no_millis\"}"
                + "}"
            + "}";
    private String userId;
    private AddressData pickupLocation;
    private AddressData dropoffLocation;
    private CarpoolUserStatus status;
    private double paymentAmount;
    private Date pickupDate;
    private Date dropoffDate;

    public AddressData getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(AddressData pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public AddressData getDropoffLocation() {
        return dropoffLocation;
    }

    public void setDropoffLocation(AddressData dropoffLocation) {
        this.dropoffLocation = dropoffLocation;
    }

    public CarpoolUserStatus getStatus() {
        return status;
    }

    public void setStatus(CarpoolUserStatus status) {
        this.status = status;
    }

    public double getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(double paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public Date getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(Date pickupDate) {
        this.pickupDate = pickupDate;
    }

    public Date getDropoffDate() {
        return dropoffDate;
    }

    public void setDropoffDate(Date dropoffDate) {
        this.dropoffDate = dropoffDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
