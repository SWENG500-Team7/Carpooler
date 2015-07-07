package com.carpooler.users;

import com.carpooler.AbstractServiceActivityMockTest;
import com.carpooler.trips.Vehicle;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by Aidos on 07.06.2015.
 */
public class UserTest extends AbstractServiceActivityMockTest {
    @Test
    public void testAddRating() {
        user.addRating(Rating.B);
        user.addRating(Rating.D);
        Assert.assertEquals(Rating.C, user.getRating());
    }
    @Test
    public void testAddVehicle() {
        user.createVehicle();
        Assert.assertEquals(1, user.getVehicles().size());

    }
    @Test
    public void testRemoveVehicle() {
        Vehicle v = user.createVehicle();
        v.setPlateNumber("01KZ");
        Vehicle v2 = user.createVehicle();
        v2.setPlateNumber("02KZ");
        user.removeVehicle(v);
        Assert.assertEquals(1, user.getVehicles().size());
    }
    @Test
    public void testCreateTrip() {
        Vehicle v = user.createVehicle();
        v.setPlateNumber("03KZ");
        Assert.assertNotNull(user.createTrip(v));

    }
}
