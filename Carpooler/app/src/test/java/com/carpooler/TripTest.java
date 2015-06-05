package com.carpooler;

import junit.framework.TestCase;

/**
 * Created by jcsax on 6/2/15.
 */
public class TripTest extends TestCase {

    private Trip trip;
    private CarpoolUser user;

    public void setUp() throws Exception {
        super.setUp();
        trip = new Trip();
        user = new CarpoolUser();
    }

    public void tearDown() throws Exception {

    }

    public void testAddCarpoolUser() {
        trip.addCarpoolUser(user);
        assertEquals(1, trip.getCarpoolUsers().size());
        assertEquals(CarpoolUserStatus.CONFIRMED, user.getStatus());
    }

    public void testRemoveCarpoolUser() {
        trip.addCarpoolUser(user);
        assertEquals(1, trip.getCarpoolUsers().size());
        assertEquals(CarpoolUserStatus.CONFIRMED, user.getStatus());
        trip.removeCarpoolUser(user);
        assertEquals(0, trip.getCarpoolUsers().size());
        assertEquals(CarpoolUserStatus.UNLISTED, user.getStatus());
    }

    public void testPickupCarpoolUser() {
        trip.pickupCarpoolUser(user);
        assertEquals(CarpoolUserStatus.PICKED_UP, user.getStatus());
    }

    public void testDropoffCarpoolUser() {
        trip.dropoffCarpoolUser(user);
        assertEquals(CarpoolUserStatus.DROPPED_OFF, user.getStatus());
    }

    public void testSkipNoShow() {
        trip.skipNoShow(user);
        assertEquals(CarpoolUserStatus.NO_SHOW, user.getStatus());
    }

    public void testRequestPickup() {
        trip.requestPickup(user);
        assertEquals(CarpoolUserStatus.PENDING, user.getStatus());
    }

    public void testSplitFuelCost() {
        double fuel_cost = 12.00;
        trip.addCarpoolUser(new CarpoolUser());
        trip.addCarpoolUser(new CarpoolUser());
        trip.addCarpoolUser(new CarpoolUser());
        assertEquals(3, trip.getCarpoolUsers().size());
        double fuel_split = trip.splitFuelCost(fuel_cost);
        assertEquals(4.00, fuel_split);
    }

}