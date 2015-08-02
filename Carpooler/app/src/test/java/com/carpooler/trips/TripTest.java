package com.carpooler.trips;

import android.os.RemoteException;

import com.carpooler.AbstractServiceActivityMockTest;
import com.carpooler.dao.dto.TripData;
import com.carpooler.dao.dto.VehicleData;
import com.carpooler.users.CarpoolUser;
import com.carpooler.users.CarpoolUserStatus;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by jcsax on 6/2/15.
 */
public class TripTest extends AbstractServiceActivityMockTest {

    private Trip trip;
    private TripData tripData = new TripData();


    @Override
    public void setup() throws RemoteException {
        super.setup();
        trip = new Trip(tripData,callback);
    }

    @Test
    public void testRemoveCarpoolUser() {
        CarpoolUser carpoolUser = trip.requestJoinTrip();
        Assert.assertTrue(trip.getCarpoolUsers().hasNext());
        trip.confirmCarpoolUser(carpoolUser);
        Assert.assertEquals(CarpoolUserStatus.CONFIRMED_FOR_PICKUP, carpoolUser.getStatus());
        trip.removeCarpoolUser(carpoolUser);
        Assert.assertTrue(trip.getCarpoolUsers().hasNext());
        Assert.assertEquals(CarpoolUserStatus.CANCELLED, carpoolUser.getStatus());
    }

    @Test
    public void testPickupCarpoolUser() {
        CarpoolUser carpoolUser = trip.requestJoinTrip();
        Assert.assertTrue(trip.getCarpoolUsers().hasNext());
        trip.confirmCarpoolUser(carpoolUser);
        Assert.assertEquals(CarpoolUserStatus.CONFIRMED_FOR_PICKUP, carpoolUser.getStatus());
        trip.pickupCarpoolUser(carpoolUser);
        Assert.assertEquals(CarpoolUserStatus.PICKED_UP, carpoolUser.getStatus());
    }

    @Test
    public void testDropoffCarpoolUser() {
        CarpoolUser carpoolUser = trip.requestJoinTrip();
        Assert.assertTrue(trip.getCarpoolUsers().hasNext());
        trip.confirmCarpoolUser(carpoolUser);
        Assert.assertEquals(CarpoolUserStatus.CONFIRMED_FOR_PICKUP, carpoolUser.getStatus());
        trip.pickupCarpoolUser(carpoolUser);
        Assert.assertEquals(CarpoolUserStatus.PICKED_UP, carpoolUser.getStatus());
        trip.dropoffCarpoolUser(carpoolUser, 1.00);
        Assert.assertEquals(CarpoolUserStatus.DROPPED_OFF, carpoolUser.getStatus());
    }

    @Test
    public void testSkipNoShow() {
        CarpoolUser carpoolUser = trip.requestJoinTrip();
        Assert.assertTrue(trip.getCarpoolUsers().hasNext());
        trip.confirmCarpoolUser(carpoolUser);
        Assert.assertEquals(CarpoolUserStatus.CONFIRMED_FOR_PICKUP, carpoolUser.getStatus());
        trip.skipNoShow(carpoolUser);
        Assert.assertEquals(CarpoolUserStatus.NO_SHOW, carpoolUser.getStatus());
    }

    @Test
    public void testRequestPickup() {
        CarpoolUser carpoolUser = trip.requestJoinTrip();
        Assert.assertEquals(CarpoolUserStatus.PENDING, carpoolUser.getStatus());
    }

    @Test
    public void testSplitFuelCost() {
        VehicleData vehicleData = new VehicleData();
        vehicleData.setMPG(30);
        Vehicle hostVehicle = new Vehicle(vehicleData);
        trip.setHostVehicle(hostVehicle);
        tripData.setFuelPrice(3.50);
        CarpoolUser user1 = trip.requestJoinTrip();
        CarpoolUser user2 = trip.requestJoinTrip();
        CarpoolUser user3 = trip.requestJoinTrip();
        trip.confirmCarpoolUser(user1);
        trip.confirmCarpoolUser(user2);
        trip.confirmCarpoolUser(user3);
        Assert.assertTrue(trip.getCarpoolUsers().hasNext());
        trip.getCarpoolUsers().next();
        Assert.assertTrue(trip.getCarpoolUsers().hasNext());
        trip.getCarpoolUsers().next();
        Assert.assertTrue(trip.getCarpoolUsers().hasNext());
        trip.pickupCarpoolUser(user1);
        trip.pickupCarpoolUser(user2);
        trip.pickupCarpoolUser(user3);
        trip.splitFuelCost(16093);
        Assert.assertEquals(0.39, user1.getPaymentAmount(), .01);
        Assert.assertEquals(0.39, user2.getPaymentAmount(), .01);
        Assert.assertEquals(0.39, user3.getPaymentAmount(), .01);
    }

}