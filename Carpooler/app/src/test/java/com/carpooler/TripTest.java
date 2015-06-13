package com.carpooler;

import junit.framework.TestCase;

/**
 * Created by jcsax on 6/2/15.
 */
public class TripTest extends TestCase {

    private Trip trip = new Trip();
    private CarpoolUser user = new CarpoolUser();

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {

    }

    public void testAddCarpoolUser() {
        trip.addCarpoolUser(user);
        assertEquals(1, trip.getCarpoolUsers().size());
        assertEquals(CarpoolUserStatus.CONFIRMED_FOR_PICKUP, user.getStatus());
    }

    public void testRemoveCarpoolUser() {
        trip.addCarpoolUser(user);
        assertEquals(1, trip.getCarpoolUsers().size());
        assertEquals(CarpoolUserStatus.CONFIRMED_FOR_PICKUP, user.getStatus());
        trip.removeCarpoolUser(user);
        assertEquals(0, trip.getCarpoolUsers().size());
        assertEquals(CarpoolUserStatus.CANCELLED, user.getStatus());
    }

    public void testPickupCarpoolUser() {
        trip.addCarpoolUser(user);
        assertEquals(1, trip.getCarpoolUsers().size());
        assertEquals(CarpoolUserStatus.CONFIRMED_FOR_PICKUP, user.getStatus());
        trip.pickupCarpoolUser(user);
        assertEquals(CarpoolUserStatus.PICKED_UP, user.getStatus());
    }

    public void testDropoffCarpoolUser() {
        trip.addCarpoolUser(user);
        assertEquals(1, trip.getCarpoolUsers().size());
        assertEquals(CarpoolUserStatus.CONFIRMED_FOR_PICKUP, user.getStatus());
        trip.pickupCarpoolUser(user);
        assertEquals(CarpoolUserStatus.PICKED_UP, user.getStatus());
        trip.dropoffCarpoolUser(user, 1.00);
        assertEquals(CarpoolUserStatus.DROPPED_OFF, user.getStatus());
    }

    public void testSkipNoShow() {
        trip.addCarpoolUser(user);
        assertEquals(1, trip.getCarpoolUsers().size());
        assertEquals(CarpoolUserStatus.CONFIRMED_FOR_PICKUP, user.getStatus());
        trip.skipNoShow(user);
        assertEquals(CarpoolUserStatus.NO_SHOW, user.getStatus());
    }

    public void testRequestPickup() {
        trip.requestPickup(user);
        assertEquals(CarpoolUserStatus.PENDING, user.getStatus());
    }

    public void testSplitFuelCost() {
        CarpoolUser user1 = new CarpoolUser();
        CarpoolUser user2 = new CarpoolUser();
        CarpoolUser user3 = new CarpoolUser();
        double fuel_cost = 12.00;
        trip.addCarpoolUser(user1);
        trip.addCarpoolUser(user2);
        trip.addCarpoolUser(user3);
        assertEquals(3, trip.getCarpoolUsers().size());
        trip.pickupCarpoolUser(user1);
        trip.pickupCarpoolUser(user2);
        trip.pickupCarpoolUser(user3);
        double fuel_split = trip.splitFuelCost(fuel_cost);
        assertEquals(4.00, fuel_split);
    }

}