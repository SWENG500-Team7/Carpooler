package com.carpooler.users;
import android.os.RemoteException;
import android.support.annotation.NonNull;

import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.UserDataService;
import com.carpooler.dao.dto.UserData;
import com.carpooler.trips.Vehicle;
import com.carpooler.trips.Trip;
import com.carpooler.trips.TripStatus;

import java.util.*;

/**
 * Created by Aidos on 07.06.2015.
 */
public class CarpoolHost {
    private User user;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    /**
     * Converts CarpoolHost instance to a UserData object and persists
     * using given database connection
     * @param conn
     * @return
     */
    public boolean persistHost(DatabaseService.Connection conn) {
        //If no user info or vehicles exist, can't persist
        if (user == null || vehicles == null || vehicles.size() <= 0) {
            return false;
        }

        //Create User and Vehicle DTOs
        UserData data = new UserData();
        data.setUserId(user.getGoogleId());
        List<com.carpooler.dao.dto.Vehicle> vehicleDTOs =
                new ArrayList<com.carpooler.dao.dto.Vehicle>();
        for (Vehicle vehicle : vehicles) {
            vehicleDTOs.add(vehicle.toVehicleDTO());
        }
        data.setVehicle(vehicleDTOs);

        //Persist user data
        try {
            //Persist user data
            UserDataService dataService = new UserDataService();
            dataService.createUser(data, conn);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }


}
