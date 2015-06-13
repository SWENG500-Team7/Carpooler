package com.carpooler.trips;

import com.carpooler.users.CarpoolUser;
import com.carpooler.users.CarpoolUserStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jcsax on 6/2/15.
 */
public class Trip {

    private TripStatus status;
    private List<CarpoolUser> users = new ArrayList<CarpoolUser>();
    private double fuel_split = 0.00;

    /**
     * Adds and confirms a CarpoolUser to the list for this trip
     * @param user - a CarpoolUser
     */
    public void addCarpoolUser(CarpoolUser user) {
        users.add(user);
        user.changeStatus(CarpoolUserStatus.CONFIRMED_FOR_PICKUP);
    }

    /**
     * Removes a CarpoolUser for the list for this trip
     * @param user - a CarpoolUser
     */
    public void removeCarpoolUser(CarpoolUser user) {
        user.changeStatus(CarpoolUserStatus.CANCELLED);
        users.remove(user);
    }

    /**
     * Updates the status of a CarpoolUser to PICKED_UP for this trip
     * @param user - a CarpoolUser
     */
    public void pickupCarpoolUser(CarpoolUser user) {
        user.changeStatus(CarpoolUserStatus.PICKED_UP);
    }

    /**
     * Updates the status of a CarpoolUser to DROPPED_OFF for this trip
     * @param user - a CarpoolUser
     * @param cost - the total fuel cost for the current trip segment
     */
    public void dropoffCarpoolUser(CarpoolUser user, double cost) {
        double split = splitFuelCost(cost);
        for(CarpoolUser carpoolUser : users) {
            if(carpoolUser.getStatus() == CarpoolUserStatus.PICKED_UP)
                carpoolUser.setPaymentAmount(split);
        }
        user.changeStatus(CarpoolUserStatus.DROPPED_OFF);
    }

    /**
     * Updates the status of a CarpoolUser to NO_SHOW for this trip
     * @param user - a CarpoolUser
     */
    public void skipNoShow(CarpoolUser user) {
        user.changeStatus(CarpoolUserStatus.NO_SHOW);
    }

    /**
     * Allows a CarpoolUser to request a pickup on this trip;
     * Updates the status of a CarpoolUser to PENDING for this trip
     * @param user - a CarpoolUser
     */
    public void requestPickup(CarpoolUser user) {
        user.changeStatus(CarpoolUserStatus.PENDING);
    }

    /**
     * Splits the total fuel cost evenly among the list of CarpoolUsers
     * for each trip segment and adds to current fuel split
     * @param cost - the total fuel cost for the current trip segment
     * @return fuel_split - the fuel split for the current trip segment
     */
    public double splitFuelCost(double cost) {
        int userCount = 0;
        for(CarpoolUser user : users) {
            if(user.getStatus() == CarpoolUserStatus.PICKED_UP)
                userCount++;
        }
        if(userCount > 0)
            fuel_split = Math.round((fuel_split + cost/userCount)*100.0)/100.0;
        return fuel_split;
    }

    /**
     * Gets the list of CarpoolUsers
     * @return users - the list of CarpoolUsers
     */
    public List<CarpoolUser> getCarpoolUsers() {
        return users;
    }

    /**
     * Sets the current trip status
     * @param status - the trip status
     */
    public void setStatus(TripStatus status) {
        this.status = status;
    }

    /**
     * Gets the current trip status
     * @return status - the trip status
     */
    public TripStatus getStatus() {
        return status;
    }

}
