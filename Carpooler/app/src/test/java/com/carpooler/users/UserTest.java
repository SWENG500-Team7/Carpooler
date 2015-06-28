package com.carpooler.users;

import com.carpooler.trips.Trip;
import com.carpooler.trips.TripStatus;
import com.carpooler.trips.Vehicle;

import junit.framework.Assert;

import org.junit.Test;

import java.util.*;

/**
 * Created by Aidos on 07.06.2015.
 */
public class UserTest {

    @Test
    public void testAddRating() {
        User ch = new User("googleId");
        ch.addRating(Rating.B);
        ch.addRating(Rating.D);
        Assert.assertEquals(Rating.C, ch.getRating());
    }
    @Test
    public void testAddVehicle() {
        Vehicle v = new Vehicle (2, "NYC321");
        User ch = new User("googleId");
        ch.addVechicle(v);
        Assert.assertEquals(1, ch.getVehicles().size());

    }
    @Test
    public void testRemoveVehicle() {
        Vehicle v = new Vehicle (3, "01KZ");
        Vehicle v2 = new Vehicle(1, "02KZ");
        User ch = new User("googleId");
        ch.addVechicle(v);
        ch.addVechicle(v2);
        ch.removeVehicle(v);
        Assert.assertEquals(1, ch.getVehicles().size());
    }
    @Test
    public void testCreateTrip() {
        Vehicle v = new Vehicle(2, "03KZ");
        User ch = new User("googleId");
        Assert.assertNotNull(ch.createTrip(v));

    }
    @Test
    public void testSaveTrip() {
        Trip t = new Trip();
        t.setStatus(TripStatus.COMPLETED);
        User ch = new User("googleId");
        ch.saveTrip(t);
        Assert.assertEquals(1, ch.getTrips(TripStatus.COMPLETED).size());

    }
    @Test
    public void testCancelTrip() {
        Trip t = new Trip();
        User ch = new User("googleId");
        ch.cancelTrip(t);
        Assert.assertFalse(ch.getTrips(TripStatus.CANCELLED).contains(t));
    }
    @Test
    public void testGetTrips() {
        User ch = new User("googleId");
        Trip t1 = new Trip();
        t1.setStatus(TripStatus.CANCELLED);
        ch.saveTrip(t1);
        Trip t2 = new Trip();
        t2.setStatus(TripStatus.IN_ROUTE);
        ch.saveTrip(t2);
        List<Trip> trips = ch.getTrips(TripStatus.CANCELLED);
        Assert.assertEquals(1, trips.size());
    }

    @Test
    public void testAddRatingWithReview() {
        User ch = new User("googleId");
        String review = "Crazy Schumacher!";
        User u = new User("speed_racer");
        ch.addRating(u, Rating.D, review);
        Assert.assertFalse(ch.getReviews().isEmpty());
        Assert.assertEquals(Rating.D, ch.getRating());
    }
}
