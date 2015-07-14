package com.carpooler.users;

import android.os.RemoteException;
import android.widget.ImageView;

import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.dto.TripData;
import com.carpooler.dao.dto.UserData;
import com.carpooler.dao.dto.UserReviewData;
import com.carpooler.dao.dto.VehicleData;
import com.carpooler.trips.Trip;
import com.carpooler.trips.TripStatus;
import com.carpooler.trips.Vehicle;
import com.carpooler.ui.activities.ImageViewBitmapLoader;
import com.carpooler.ui.activities.ServiceActivityCallback;
import com.google.android.gms.plus.model.people.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * User class keeps track of user google identity and payment information
 *
 * Created by Kevin on 6/4/2015.
 */
public class User {

    private final Person googleUser;
    private UserData userData;
    private final  ServiceActivityCallback serviceActivityCallback;

    public User(Person googleUser, ServiceActivityCallback serviceActivityCallback) {
        this.googleUser = googleUser;
        this.serviceActivityCallback = serviceActivityCallback;
        loadUserData();
    }

    public String getGoogleId() {
        return googleUser.getId();
    }

    public Rating getRating() {
        return userData.getAverageRating();
    }

    public String getName(){
        return googleUser.getDisplayName();
    }

    public void addRating (User u, Rating r, String s) {
        UserReviewData userReviewData = new UserReviewData();
        userReviewData.setUserId(u.getGoogleId());
        userReviewData.setComment(s);
        userData.getReviews().add(userReviewData);
        this.addRating(r);

    }

    public void addRating(Rating r) {
        int newRatings = userData.getRatings()+1;
        userData.setRatings(newRatings);
        int average = (userData.getAverageRating().ordinal() + r.ordinal())/newRatings;
        userData.setAverageRating(Rating.values()[average]);
    }

    public Vehicle getVehicle(String plateNumber) {
        Vehicle returnVehicle = null;
        if (userData.getVehicle() != null && !userData.getVehicle().isEmpty()) {
            VehicleData searchVehicleData = new VehicleData();
            for (VehicleData vehicle : userData.getVehicle()) {
                if (vehicle.getPlateNumber().equals(plateNumber)) {
                    returnVehicle = new Vehicle(vehicle);
                }
            }
        }
        return returnVehicle;
    }

    public Vehicle createVehicle(){
        VehicleData vehicleData = new VehicleData();
        userData.getVehicle().add(vehicleData);
        return new Vehicle(vehicleData);
    }

    public void removeVehicle(Vehicle vehicle){
        userData.getVehicle().remove(vehicle.getData());
    }

    public Trip createTrip(Vehicle v) {
        return new Trip (new TripData(),serviceActivityCallback);
    }

    public void findHostedTrips(TripStatus tripStatus,DatabaseService.QueryCallback<TripData> callback) throws RemoteException {
        serviceActivityCallback.getTripDataService().findTripsByHostIdAndStatus(userData.getUserId(),tripStatus,callback);
    }

    public void findParticipatingTrips(TripStatus tripStatus,DatabaseService.QueryCallback<TripData> callback) throws RemoteException {
        serviceActivityCallback.getTripDataService().findTripsByUserIdAndStatus(userData.getUserId(), tripStatus, callback);
    }

    private void updateUserData() {
        //If new user create new DTO
        if (userData == null) {
            userData = new UserData();
            userData.setUserId(getGoogleId());
        }
    }

    private void loadUserData()  {
        try {
            serviceActivityCallback.getUserDataService().getUserData(getGoogleId(), new GetUserCallback());
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
            serviceActivityCallback.getUserDataService().createUser(userData, new CreateUserCallback());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public List<Vehicle> getVehicles() {
        List<Vehicle> ret = new ArrayList<>(userData.getVehicle().size());
        for (VehicleData vehicleData: userData.getVehicle()){
            ret.add(new Vehicle(vehicleData));
        }
        return ret;
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
            userData.setUserId(getGoogleId());
            saveUser();
        }

        @Override
        public void doException(Exception exception) {

        }

        @Override
        public void doSuccess(UserData data) {
            userData = data;
        }
    }

    public void loadUserImage(ImageView imageView, int size) throws RemoteException {
        String photoUrl = googleUser.getImage().getUrl();
        photoUrl = photoUrl.substring(0, photoUrl.length()-2) + size;
        serviceActivityCallback.getConnection().loadBitmap(photoUrl,new ImageViewBitmapLoader(imageView));
    }
}
