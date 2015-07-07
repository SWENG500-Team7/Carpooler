package com.carpooler.trips;

import android.os.RemoteException;
import android.widget.ImageView;

import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.dto.AddressData;
import com.carpooler.dao.dto.CarpoolUserData;
import com.carpooler.dao.dto.TripData;
import com.carpooler.ui.activities.ServiceActivityCallback;
import com.carpooler.users.Address;
import com.carpooler.users.CarpoolUser;
import com.carpooler.users.CarpoolUserStatus;
import com.carpooler.users.User;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Trip manages the number of CarpoolUsers and their individual statuses
 * as well as the fuel split for each CarpoolUser's trip segment.
 *
 * Created by jcsax on 6/2/15.
 */
public class Trip {

    private final ServiceActivityCallback serviceActivityCallback;
    private TripData tripData;
    private User host;
    private Queue<HostImageLoader> hostImageLoaders= new LinkedList<>();

    public Trip(TripData tripData, ServiceActivityCallback serviceActivityCallback) {
        this.serviceActivityCallback = serviceActivityCallback;
        this.tripData = tripData;
        if (tripData.getHostId()!=null) {
            if (serviceActivityCallback.getUser().getGoogleId().equals(tripData.getHostId())) {
                host = serviceActivityCallback.getUser();
            } else {
                PendingResult<People.LoadPeopleResult> result = Plus.PeopleApi.load(serviceActivityCallback.getGoogleApiClient(), tripData.getHostId());
                result.setResultCallback(new HostLoaderCallback());
            }
        }
    }

    /**
     * Adds a CarpoolUser to the list for this trip
     */
    public CarpoolUser requestJoinTrip() {
        CarpoolUserData carpoolUserData = new CarpoolUserData();
        carpoolUserData.setUserId(serviceActivityCallback.getUser().getGoogleId());
        carpoolUserData.setStatus(CarpoolUserStatus.PENDING);
        tripData.getUsers().add(carpoolUserData);
        return new CarpoolUser(carpoolUserData,serviceActivityCallback);
    }

    public void confirmCarpoolUser(CarpoolUser carpoolUser){
        carpoolUser.changeStatus(CarpoolUserStatus.CONFIRMED_FOR_PICKUP);
    }

    /**c
     * Removes a CarpoolUser for the list for this trip
     * @param user - a CarpoolUser
     */
    public void removeCarpoolUser(CarpoolUser user) {
        user.changeStatus(CarpoolUserStatus.CANCELLED);
    }

    /**
     * Updates the status of a CarpoolUser to PICKED_UP for this trip
     * @param user - a CarpoolUser
     */
    public void pickupCarpoolUser(CarpoolUser user) {
        user.changeStatus(CarpoolUserStatus.PICKED_UP);
    }

    /**
     * Updates the status of a CarpoolUser to DROPPED_OFF for this trip
     * @param user - a CarpoolUser
     * @param cost - the total fuel cost for the current trip segment
     */
    public void dropoffCarpoolUser(CarpoolUser user, double cost) {
        double split = splitFuelCost(cost);
        for(CarpoolUserData carpoolUser : tripData.getUsers()) {
            if(carpoolUser.getStatus() == CarpoolUserStatus.PICKED_UP)
                carpoolUser.setPaymentAmount(split);
        }
        user.changeStatus(CarpoolUserStatus.DROPPED_OFF);
    }

    /**
     * Updates the status of a CarpoolUser to NO_SHOW for this trip
     * @param user - a CarpoolUser
     */
    public void skipNoShow(CarpoolUser user) {
        user.changeStatus(CarpoolUserStatus.NO_SHOW);
    }

    /**
     * Splits the total fuel cost evenly among the list of CarpoolUsers
     * for each trip segment and adds to current fuel split
     * @param cost - the total fuel cost for the current trip segment
     * @return fuel_split - the fuel split for the current trip segment
     */
    public double splitFuelCost(double cost) {
        int userCount = 0;
        for(CarpoolUserData user : tripData.getUsers()) {
            if(user.getStatus() == CarpoolUserStatus.PICKED_UP)
                userCount++;
        }
        if(userCount > 0)
            tripData.setFuelSplit(Math.round((tripData.getFuelSplit()+ cost/userCount)*100.0)/100.0);
        return tripData.getFuelSplit();
    }

    public void saveTrip() {
        try {
            serviceActivityCallback.getTripDataService().createTrip(tripData, new CreateTripCallback());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public Date getStartTime() {
        return tripData.getStartTime();
    }

    public Date getEndTime() {
        return tripData.getEndTime();
    }

    public Address getStartLocation() {
        return new Address(tripData.getStartLocation());
    }

    public Address getEndLocation() {
        return new Address(tripData.getEndLocation());
    }

    public void setStartLocation(String searchAddress, AddressErrorCallback addressErrorCallback) throws RemoteException {
        setAddress(searchAddress,addressErrorCallback,false);
    }

    public void setEndLocation(String searchAddress, AddressErrorCallback addressErrorCallback) throws RemoteException {
        setAddress(searchAddress,addressErrorCallback,true);
    }

    private void setAddress(String searchAddress, AddressErrorCallback addressErrorCallback, boolean destination) throws RemoteException {
        TripAddressLoadCallback tripAddressLoadCallback = new TripAddressLoadCallback(addressErrorCallback,destination);
        serviceActivityCallback.getLocationService().getLocationFromAddressName(searchAddress,tripAddressLoadCallback);
    }

    private class CreateTripCallback implements DatabaseService.IndexCallback{

        @Override
        public void doError(String message) {
        }

        @Override
        public void doException(Exception exception) {

        }

        @Override
        public void doSuccess(String data) {

        }
    }

    /**
     * Gets the list of CarpoolUsers
     * @return users - the list of CarpoolUsers
     */
//    public List<CarpoolUser> getCarpoolUsers() {
//        return users;
//    }

    /**
     * Sets the current trip status
     * @param status - the trip status
     */
    public void setStatus(TripStatus status) {
        tripData.setStatus(status);
    }

    /**
     * Gets the current trip status
     * @return status - the trip status
     */
    public TripStatus getStatus() {
        return tripData.getStatus();
    }

    /**
     * Sets the vehicle to be associated with this trip
     * @param vehicle - the trip vehicle
     */
    public void setVehicle(Vehicle vehicle) {
        tripData.setHostVehicle(vehicle.getPlateNumber());
    }

    /**
     * Gets the vehicle associated with this trip
     * @return vehicle - the trip vehicle
     */
    public Vehicle getVehicle() {
        return host.getVehicle(tripData.getHostVehicle());
    }

    public String getTripId(){
        return tripData.get_id();
    }

    public User getHost() {
        return host;
    }

    public void loadHostImage(ImageView imageView,int size) throws RemoteException {
        if (host==null){
            hostImageLoaders.add(new HostImageLoader(imageView,size));
        }else{
            host.loadUserImage(imageView,size);
        }
    }

    public int getOpenSeats(){
        return tripData.getOpenSeats();
    }

    private class HostImageLoader{
        final ImageView imageView;
        final int size;
        public HostImageLoader(ImageView imageView, int size) {
            this.imageView = imageView;
            this.size = size;
        }
    }

    public Iterator<CarpoolUser> getCarpoolUsers(){
        return new CarpoolUserIterator();
    }

    private class CarpoolUserIterator implements Iterator<CarpoolUser>{
        private int currentIndex=0;
        @Override
        public boolean hasNext() {
            return (!tripData.getUsers().isEmpty()) && tripData.getUsers().size()>=currentIndex;
        }

        @Override
        public CarpoolUser next() {
            return new CarpoolUser(tripData.getUsers().get(currentIndex++   ),serviceActivityCallback);
        }

        @Override
        public void remove() {
            tripData.getUsers().remove(currentIndex);
        }
    }
    private class HostLoaderCallback implements ResultCallback<People.LoadPeopleResult> {

        @Override
        public void onResult(People.LoadPeopleResult loadPeopleResult) {
            Person person = loadPeopleResult.getPersonBuffer().iterator().next();
            host = new User(person,serviceActivityCallback);
            for (HostImageLoader hostImageLoader:hostImageLoaders){
                try {
                    host.loadUserImage(hostImageLoader.imageView,hostImageLoader.size);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class TripAddressLoadCallback extends AddressLoadCallback{
        private final boolean destination;

        public TripAddressLoadCallback(AddressErrorCallback errorCallback, boolean destination) {
            super(errorCallback);
            this.destination = destination;
        }

        @Override
        protected void setAddressData(AddressData addressData) {
            if (destination){
                tripData.setEndLocation(addressData);
            }else{
                tripData.setStartLocation(addressData);
            }
        }
    }

}
