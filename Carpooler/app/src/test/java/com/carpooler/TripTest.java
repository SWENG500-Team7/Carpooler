package com.carpooler;

import junit.framework.TestCase;

/**
 * Created by jcsax on 6/2/15.
 */
public class TripTest extends TestCase {

    private Trip trip;
    private CarpoolUser user;
    private static final int PENDING = 1;
    private static final int CONFIRMED = 2;
    private static final int PICKED_UP = 3;
    private static final int DROPPED_OFF = 4;
    private static final int NO_SHOW = 5;

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
        assertEquals(CONFIRMED, user.CarpoolUserStatus);
    }

    public void testRemoveCarpoolUser() {
        trip.addCarpoolUser(user);
        assertEquals(1, trip.getCarpoolUsers().size());
        assertEquals(CONFIRMED, user.CarpoolUserStatus);
        trip.removeCarpoolUser(user);
        assertEquals(0, trip.getCarpoolUsers().size());
        assertEquals(0, user.CarpoolUserStatus);
    }

    public void testPickupCarpoolUser() {
        trip.pickupCarpoolUser(user);
        assertEquals(PICKED_UP, user.CarpoolUserStatus);
    }

    public void testDropoffCarpoolUser() {
        trip.dropoffCarpoolUser(user);
        assertEquals(DROPPED_OFF, user.CarpoolUserStatus);
    }

    public void testSkipNoShow() {
        trip.skipNoShow(user);
        assertEquals(NO_SHOW, user.CarpoolUserStatus);
    }

    public void testRequestPickup() {
        trip.requestPickup(user);
        assertEquals(PENDING, user.CarpoolUserStatus);
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