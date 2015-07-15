package com.carpooler.trips;

import android.os.RemoteException;

import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.dto.AddressData;
import com.carpooler.dao.dto.CarpoolUserData;
import com.carpooler.dao.dto.TripData;
import com.carpooler.ui.activities.ServiceActivityCallback;
import com.carpooler.users.Address;
import com.carpooler.users.CarpoolUser;
import com.carpooler.users.CarpoolUserStatus;
import com.carpooler.users.User;

import java.util.Date;
import java.util.Iterator;

/**
 * Trip manages the number of CarpoolUsers and their individual statuses
 * as well as the fuel split for each CarpoolUser's trip segment.
 *
 * Created by jcsax on 6/2/15.
 */
public class Trip {

    private final ServiceActivityCallback serviceActivityCallback;
    private TripData tripData;
    private UserLoader userLoader;
    // populated for logged in carpool user
    private CarpoolUser loggedInUser;
    private boolean loggedInUserChecked = false;
    public Trip(TripData tripData, ServiceActivityCallback serviceActivityCallback) {
        this.serviceActivityCallback = serviceActivityCallback;
        this.tripData = tripData;
        if (tripData.getHostId()==null) {
            User host = serviceActivityCallback.getUser();
            tripData.setHostId(host.getGoogleId());
        }

        userLoader = new UserLoader(serviceActivityCallback, tripData.getHostId());

    }

    private void setLoggedInUser(){
        if (!loggedInUserChecked) {
            for (CarpoolUser user : getCarpoolUsers()) {
                if (user.isLoggedInUser()) {
                    loggedInUser = user;
                    break;
                }
            }
            loggedInUserChecked = true;
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

    public boolean isLoggedInUser(){
        return userLoader.isLoggedInUser();
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

    public void completeTrip(){
        if (canCompleteTrip()){
            tripData.setStatus(TripStatus.COMPLETED);
            saveTrip();
        }else{
            throw new IllegalArgumentException("Cannot Complete Trip");
        }
    }

    public void cancelTrip(){
        if (canCancelTrip()){
            tripData.setStatus(TripStatus.CANCELLED);
            saveTrip();
        }else{
            throw new IllegalArgumentException("Cannot Cancel Trip");
        }

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

    public void setStartTime(Date startTime){
        tripData.setStartTime(startTime);
    }

    public void setEndTime(Date endTime){
        tripData.setEndTime(endTime);
    }
    public Address getStartLocation() {
        if (tripData.getStartLocation()!=null) {
            return new Address(tripData.getStartLocation());
        }else{
            return null;
        }
    }

    public Address getEndLocation() {
        if (tripData.getEndLocation()!=null) {
            return new Address(tripData.getEndLocation());
        }else{
            return null;
        }
    }

    public void setStartLocation(String searchAddress, AddressErrorCallback addressErrorCallback) throws RemoteException {
        setAddress(searchAddress,addressErrorCallback,false);
    }

    public void setEndLocation(String searchAddress, AddressErrorCallback addressErrorCallback) throws RemoteException {
        setAddress(searchAddress,addressErrorCallback,true);
    }

    private void setAddress(String searchAddress, AddressErrorCallback addressErrorCallback, boolean destination) throws RemoteException {
        TripAddressLoadCallback tripAddressLoadCallback = new TripAddressLoadCallback(addressErrorCallback,destination);
        serviceActivityCallback.getLocationService().getLocationFromAddressName(searchAddress, tripAddressLoadCallback);
    }

    public boolean canStartTrip(){
        return tripData.getStatus() == TripStatus.OPEN;
    }

    public boolean canCancelTrip(){
        return tripData.getStatus() == TripStatus.OPEN;
    }

    public boolean canCompleteTrip(){
        return tripData.getStatus() == TripStatus.IN_ROUTE;
        // TODO add check for all users dropped off
    }

    public CarpoolUser getLoggedInCarpoolUser(){
        setLoggedInUser();
        return loggedInUser;
    }

    public boolean isLoggedInUserInCarpool(){
        CarpoolUser user = getLoggedInCarpoolUser();
        boolean ret = false;
        if (user!=null){
            ret=true;
        }
        return ret;
    }
    public boolean canPayHost(){
        boolean ret = false;
        if (isLoggedInUserInCarpool()){
            //TODO
        }
        return ret;
    }

    public boolean canConfirmPickup(){
        boolean ret = false;
        if (isLoggedInUserInCarpool()){
            //TODO
        }
        return ret;
    }

    public boolean canCancelPickup(){
        boolean ret = false;
        if (isLoggedInUserInCarpool()){
            //TODO
        }
        return ret;
    }

    public boolean canRequestJoin(){
        return !isLoggedInUserInCarpool() && isLoggedInUser();
    }

    public boolean canDropOffUser(CarpoolUser carpoolUser) {
        return isLoggedInUserInCarpool() && carpoolUser.canDropoff();
    }

    public boolean canMarkNoShow(CarpoolUser carpoolUser) {
        return isLoggedInUserInCarpool() && carpoolUser.canMarkNoShow();
    }

    public boolean canPickupUser(CarpoolUser carpoolUser) {
        return isLoggedInUserInCarpool() && carpoolUser.canPickup();
    }

    public boolean canNavigateUser(CarpoolUser carpoolUser) {
        return isLoggedInUserInCarpool() && carpoolUser.canNavigate();
    }

    public boolean canAcceptRequest(CarpoolUser carpoolUser) {
        return isLoggedInUserInCarpool() && carpoolUser.canAcceptRequest();
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
     * Sets the vehicle to be associated with this trip
     * @param vehicle - the trip vehicle
     */
    public void setVehicle(Vehicle vehicle) {
        tripData.setHostVehicle(vehicle.getPlateNumber());
    }

    public void loadVehicleData(UserLoader.VehicleCallback callback){
        userLoader.addCallback(callback);
    }

    public void loadUserData(UserLoader.Callback callback){
        userLoader.addCallback(callback);
    }

    public String getTripId(){
        return tripData.get_id();
    }

    public int getOpenSeats(){
        return tripData.getOpenSeats();
    }


    public CarpoolUserIterator getCarpoolUsers(){
        return new CarpoolUserIterator();
    }

    public class CarpoolUserIterator implements Iterator<CarpoolUser>, Iterable<CarpoolUser>{
        private int currentIndex=0;
        @Override
        public boolean hasNext() {
            return (!tripData.getUsers().isEmpty()) && tripData.getUsers().size()>currentIndex;
        }

        @Override
        public CarpoolUser next() {
            return get(currentIndex++);
        }

        @Override
        public void remove() {
            tripData.getUsers().remove(currentIndex);
        }

        public CarpoolUser get(int index){
            return new CarpoolUser(tripData.getUsers().get(index),serviceActivityCallback);
        }
        public int size(){
            return tripData.getUsers().size();
        }

        @Override
        public Iterator<CarpoolUser> iterator() {
            reset();
            return this;
        }
        public void reset(){
            currentIndex=0;
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
