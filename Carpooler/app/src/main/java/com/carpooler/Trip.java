package com.carpooler;

import java.util.List;

/**
 * Created by jcsax on 6/2/15.
 */
public class Trip {

    List<CarpoolUser> users;

    public void addCarpoolUser(CarpoolUser user) {
        users.add(user);
        user.setStatus(CarpoolUserStatus.CONFIRMED);
    }

    public void removeCarpoolUser(CarpoolUser user) {
        users.remove(user);
        user.setStatus(CarpoolUserStatus.UNLISTED);
    }

    public void pickupCarpoolUser(CarpoolUser user) {
        user.setStatus(CarpoolUserStatus.PICKED_UP);
    }

    public void dropoffCarpoolUser(CarpoolUser user) {
        user.setStatus(CarpoolUserStatus.DROPPED_OFF);
    }

    public void skipNoShow(CarpoolUser user) {
        user.setStatus(CarpoolUserStatus.NO_SHOW);
    }

    public void requestPickup(CarpoolUser user) {
        user.setStatus(CarpoolUserStatus.PENDING);
    }

    public double splitFuelCost(double cost) {
        double fuel_split = cost/users.size();
        for(CarpoolUser user : users) {
            user.setPaymentAmount(fuel_split);
        }
        // TODO: Need to round fuel_split to two decimal places
        return fuel_split;
    }

    public List<CarpoolUser> getCarpoolUsers() {
        return users;
    }

}
