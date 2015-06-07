package com.carpooler;
import java.util.*;

/**
 * Created by Aidos on 07.06.2015.
 */
public class CarpoolHost {
    private Rating rating;
    private Map<User, String> reviews;
    private List<Vehicle> vehicles;
    private List<Trip> saved_trips;

    public Rating getRating() {
        return this.rating;
    }

    public Map<User, String> getReviews(){
        return this.reviews;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void addRating (User u, Rating r, String s) {
        reviews.put(u, s);
        this.rating = r;

    }

    public void addRating(Rating r) {
        this.rating = r;
    }

    public void addVechicle(Vehicle v) {
        vehicles.add(v);
    }

    public void removeVehicle(Vehicle v) {
        if (vehicles.isEmpty()) return;
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

    public Collection<Trip> getTrips(TripStatus ts) {
        return saved_trips;
    }
}
