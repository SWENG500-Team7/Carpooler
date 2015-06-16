package com.carpooler.users;
import android.support.annotation.NonNull;

import com.carpooler.trips.Vehicle;
import com.carpooler.trips.Trip;
import com.carpooler.trips.TripStatus;

import java.util.*;

/**
 * Created by Aidos on 07.06.2015.
 */
public class CarpoolHost {
    private Rating averageRating;
    private int ratings;
    private Map<User, String> reviews;
    private List<Vehicle> vehicles;
    private List<Trip> saved_trips;

    public CarpoolHost() {
        this.averageRating = Rating.F;
        this.ratings = 0;
        this.saved_trips = new ArrayList<Trip>();
        this.vehicles = new ArrayList<Vehicle>();
        this.reviews = new HashMap<User, String>();
    }

    public Rating getRating() {
        return this.averageRating;
    }

    public Map<User, String> getReviews(){
        return this.reviews;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void addRating (User u, Rating r, String s) {
        reviews.put(u, s);
        this.addRating(r);

    }

    public void addRating(Rating r) {
        this.ratings++;
        int average = (this.averageRating.ordinal() + r.ordinal())/this.ratings;
        this.averageRating = Rating.values()[average];
    }

    public void addVechicle(Vehicle v) {
        vehicles.add(v);
    }

    public void removeVehicle(Vehicle v) {
        if (vehicles.isEmpty()) return;
        else {
            for (Iterator<Vehicle> iter = this.vehicles.listIterator(); iter.hasNext();) {
                Vehicle v2 = iter.next();
                if (v2.getPlateNumber().equalsIgnoreCase(v.getPlateNumber()))
                    iter.remove();
            }
        }
    }

    public Trip createTrip(Vehicle v) {
        return new Trip ();
    }

    public void saveTrip(Trip t) {
        saved_trips.add(t);
    }

    public void cancelTrip(Trip t) {
        saved_trips.remove(t);
    }

    public List<Trip> getTrips(TripStatus ts) {
        ArrayList<Trip> temp = new ArrayList<Trip>();
        for (Iterator<Trip> iter = this.saved_trips.listIterator(); iter.hasNext();) {
            Trip t = iter.next();
            if (t.getStatus() == ts)
                temp.add(t);
        }
        return temp;
    }
}
