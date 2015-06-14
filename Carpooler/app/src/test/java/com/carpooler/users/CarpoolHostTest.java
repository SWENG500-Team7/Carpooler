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
public class CarpoolHostTest {

    @Test
    public void testAddRating() {
        CarpoolHost ch = new CarpoolHost();
        ch.addRating(Rating.B);
        ch.addRating(Rating.D);
        Assert.assertEquals(Rating.C, ch.getRating());
    }
    @Test
    public void testAddVehicle() {
        Vehicle v = new Vehicle (2, "NYC321");
        CarpoolHost ch = new CarpoolHost();
        ch.addVechicle(v);
        Assert.assertEquals(1, ch.getVehicles().size());

    }
    @Test
    public void testRemoveVehicle() {
        Vehicle v = new Vehicle (3, "01KZ");
        Vehicle v2 = new Vehicle(1, "02KZ");
        CarpoolHost ch = new CarpoolHost();
        ch.addVechicle(v);
        ch.addVechicle(v2);
        ch.removeVehicle(v);
        Assert.assertEquals(1, ch.getVehicles().size());
    }
    @Test
    public void testCreateTrip() {
        Vehicle v = new Vehicle(2, "03KZ");
        CarpoolHost ch = new CarpoolHost();
        Assert.assertNotNull(ch.createTrip(v));

    }
    @Test
    public void testSaveTrip() {
        Trip t = new Trip();
        t.setStatus(TripStatus.COMPLETED);
        CarpoolHost ch = new CarpoolHost();
        ch.saveTrip(t);
        Assert.assertEquals(1, ch.getTrips(TripStatus.COMPLETED).size());

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
        CarpoolHost ch = new CarpoolHost();
        String review = "Crazy Schumacher!";
        User u = new User("speed_racer");
        ch.addRating(u, Rating.D, review);
        Assert.assertFalse(ch.getReviews().isEmpty());
        Assert.assertEquals(Rating.D, ch.getRating());
    }
}
