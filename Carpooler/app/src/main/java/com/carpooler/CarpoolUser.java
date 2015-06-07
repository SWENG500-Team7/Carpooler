package com.carpooler;

import java.util.Date;

/**
 * Created by raymond on 6/6/15.
 */
public class CarpoolUser {
    private Address pickupLocation;
    private Address dropoffLocation;
    private CarpoolUserStatus status = CarpoolUserStatus.PENDING;
    private double paymentAmount;
    private Date pickupDate;
    private Date dropoffDate;

    public Address getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(Address pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public Address getDropoffLocation() {
        return dropoffLocation;
    }

    public void setDropoffLocation(Address dropoffLocation) {
        this.dropoffLocation = dropoffLocation;
    }

    public CarpoolUserStatus getStatus() {
        return status;
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

    public void confirmPickup(){
        changeStatus(CarpoolUserStatus.CONFIRMED_PICK_UP);
    }

    public void changeStatus(CarpoolUserStatus nextStatus){
        if (status.isValidateNextState(nextStatus)){
            status = nextStatus;
        }else{
            throw new IllegalArgumentException("Invalid to move from " + status + " to " + nextStatus);
        }

    }
}
