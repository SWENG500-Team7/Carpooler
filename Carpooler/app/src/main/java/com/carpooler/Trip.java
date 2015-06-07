package com.carpooler;

import java.util.List;

/**
 * Created by jcsax on 6/2/15.
 */
public class Trip {

    private TripStatus status;
    private List<CarpoolUser> users;
    private double fuel_split = 0.00;

    /**
     * Adds and confirms a CarpoolUser to the list for this trip
     * @param user - a CarpoolUser
     */
    public void addCarpoolUser(CarpoolUser user) {

    }

    /**
     * Removes a CarpoolUser for the list for this trip
     * @param user - a CarpoolUser
     */
    public void removeCarpoolUser(CarpoolUser user) {

    }

    /**
     * Updates the status of a CarpoolUser to PICKED_UP for this trip
     * @param user - a CarpoolUser
     */
    public void pickupCarpoolUser(CarpoolUser user) {

    }

    /**
     * Updates the status of a CarpoolUser to DROPPED_OFF for this trip
     * @param user - a CarpoolUser
     */
    public void dropoffCarpoolUser(CarpoolUser user) {

    }

    /**
     * Updates the status of a CarpoolUser to NO_SHOW for this trip
     * @param user - a CarpoolUser
     */
    public void skipNoShow(CarpoolUser user) {

    }

    /**
     * Allows a CarpoolUser to request a pickup on this trip;
     * Updates the status of a CarpoolUser to PICKED_UP for this trip
     * @param user - a CarpoolUser
     */
    public void requestPickup(CarpoolUser user) {

    }

    /**
     * Splits the total fuel cost evenly among the list of CarpoolUsers
     * for each trip segment
     * @param cost - the total fuel cost for the current trip segment
     * @return fuel_split - the fuel split for the current trip segment
     */
    public double splitFuelCost(double cost) {
        // TODO: Need to round fuel_split to two decimal places
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
