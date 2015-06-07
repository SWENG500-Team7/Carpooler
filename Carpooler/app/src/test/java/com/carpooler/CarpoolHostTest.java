package com.carpooler;
import android.support.annotation.NonNull;

import junit.framework.TestCase;
import junit.framework.Assert;

import org.junit.Test;

import java.util.*;
import java.util.Iterator;

/**
 * Created by Aidos on 07.06.2015.
 */
public class CarpoolHostTest {

    @Test
    public void testAddRating() {
        CarpoolHost ch = new CarpoolHost();
        ch.addRating(Rating.B);
        Assert.assertEquals(Rating.B, ch.getRating());
    }
    @Test
    public void testAddVehicle() {
        Vehicle v = new Vehicle ();
        CarpoolHost ch = new CarpoolHost();
        ch.addVechicle(v);
        Assert.assertEquals(true, ch.getVehicles().contains(v));

    }
    @Test
    public void testRemoveVehicle() {
        Vehicle v = new Vehicle ();
        CarpoolHost ch = new CarpoolHost();
        ch.addVechicle(v);
        ch.removeVehicle(v);
        Assert.assertEquals(false, ch.getVehicles().contains(v));
    }
    @Test
    public void testCreateTrip() {
        Vehicle v = new Vehicle();
        CarpoolHost ch = new CarpoolHost();
        Assert.assertNotNull(ch.createTrip(v));

    }
    @Test
    public void testSaveTrip() {
        Trip t = new Trip();
        CarpoolHost ch = new CarpoolHost();
        ch.saveTrip(t);
        Assert.assertTrue(ch.getTrips(TripStatus.COMPLETED).contains(t));

    }
    @Test
    public void testCancelTrip() {
        Trip t = new Trip();
        CarpoolHost ch = new CarpoolHost();
        ch.cancelTrip(t);
        Assert.assertFalse(ch.getTrips(TripStatus.CANCELLED).contains(t));
    }
    @Test
    public void testGetTrips() {
        CarpoolHost ch = new CarpoolHost();
        Collection<Trip> trips = ch.getTrips(TripStatus.CANCELLED);
        Assert.assertFalse(trips.isEmpty());
    }

    @Test
    public void testAddRatingWithReview() {
        CarpoolHost ch = new CarpoolHost();
        String review = "Crazy Schumacher!";
        User u = new User("speed_racer");
        ch.addRating(u, Rating.F, review);
        Assert.assertTrue(ch.getReviews().containsKey(u));
    }
}
