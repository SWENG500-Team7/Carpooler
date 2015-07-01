package com.carpooler.users;

import android.os.RemoteException;

import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.UserDataService;
import com.carpooler.dao.dto.UserData;
import com.carpooler.dao.dto.VehicleData;
import com.carpooler.trips.Trip;
import com.carpooler.trips.TripStatus;
import com.carpooler.trips.Vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * User class keeps track of user google identity and payment information
 *
 * Created by Kevin on 6/4/2015.
 */
public class User {

    private String mGoogleId;
    private UserDataService userDataService;
    private UserData userData;
    private Rating averageRating;
    private int ratings;
    private Map<User, String> reviews;
    private List<Vehicle> vehicles;
    private List<Trip> saved_trips;

    public User(String pGoogleId) {
        mGoogleId = pGoogleId;
        averageRating = Rating.F;
        ratings = 0;
        saved_trips = new ArrayList<Trip>();
        vehicles = new ArrayList<Vehicle>();
        reviews = new HashMap<User, String>();
    }

    public User(String pGoogleId, UserDataService userDataService) {
        mGoogleId = pGoogleId;
        this.userDataService = userDataService;
        loadUserData();
    }

    public String getGoogleId() {
        return mGoogleId;
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

    public Vehicle getVehicle(String plateNumber) {
        Vehicle returnVehicle = null;
        if (vehicles != null && !vehicles.isEmpty()) {
            for (Vehicle vehicle : vehicles) {
                if (vehicle.getPlateNumber().equals(plateNumber)) {
                    returnVehicle = vehicle;
                }
            }
        }
        return returnVehicle;
    }

    public void addVechicle(Vehicle v) {
        vehicles.add(v);
    }

    public void removeVehicle(String plateNumber) {
        if (vehicles.isEmpty()) return;
        else {
            for (Iterator<Vehicle> iter = this.vehicles.listIterator(); iter.hasNext();) {
                Vehicle v2 = iter.next();
                if (v2.getPlateNumber().equalsIgnoreCase(plateNumber)) {
                    iter.remove();
                    return;
                }
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

    private void updateUserData() {
        //If new user create new DTO
        if (userData == null) {
            userData = new UserData();
            userData.setUserId(mGoogleId);
        }

        //Convert Vehicle List to DTOs and add to user DTO
        List<VehicleData> vehicleDTOs =
                new ArrayList<VehicleData>();
        for (Vehicle vehicle : vehicles) {
            vehicleDTOs.add(vehicle.toVehicleDTO());
        }
        userData.setVehicle(vehicleDTOs);

        //TODO: add ratings
        //TODO: add reviews
        //TODO: add trips
    }

    private void loadUserData()  {
        try {
            userDataService.getUserData(mGoogleId, new GetUserCallback());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void refreshUserData(){
        loadUserData();
    }

    public void saveUser(){
        try {
            //update the DTO to send to DB
            updateUserData();

            //Persist
            userDataService.createUser(userData, new CreateUserCallback());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    private class CreateUserCallback implements DatabaseService.IndexCallback{

        @Override
        public void doError(String message) {
        }

        @Override
        public void doException(Exception exception) {

        }

        @Override
        public void doSuccess(String data) {
            refreshUserData();
        }
    }
    private class GetUserCallback implements DatabaseService.GetCallback<UserData>{

        @Override
        public void doError(String message) {
            userData = new UserData();
            userData.setUserId(mGoogleId);
            saveUser();
        }

        @Override
        public void doException(Exception exception) {

        }

        @Override
        public void doSuccess(UserData data) {
            userData = data;

            //Convert Vehicle DTOs to Vehicle and set in User
            vehicles = new ArrayList<Vehicle>();
            List<VehicleData> vehicleDTOs = userData.getVehicle();
            if (vehicleDTOs != null) {
                for (VehicleData vehicleDTO : vehicleDTOs) {
                    addVechicle(Vehicle.fromVehicleDTO(vehicleDTO));
                }
            }

            //TODO: add ratings
            //TODO: add reviews
            //TODO: add trips
        }
    }
}
