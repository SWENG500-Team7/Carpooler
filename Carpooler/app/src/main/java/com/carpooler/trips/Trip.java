package com.carpooler.trips;

import android.os.AsyncTask;
import android.os.RemoteException;

import com.carpooler.GeoPoint;
import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.dto.AddressData;
import com.carpooler.dao.dto.CarpoolUserData;
import com.carpooler.dao.dto.TripData;
import com.carpooler.ui.activities.ServiceActivityCallback;
import com.carpooler.users.Address;
import com.carpooler.users.CarpoolUser;
import com.carpooler.users.CarpoolUserStatus;
import com.carpooler.users.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
    private Vehicle vehicle;
    private double fuel_price = 0.0;
    private static final double METERS_PER_MILE = 1609.34;

    public Trip(TripData tripData, ServiceActivityCallback serviceActivityCallback) {
        this.serviceActivityCallback = serviceActivityCallback;
        this.tripData = tripData;
        if (tripData.getHostId()==null) {
            User host = serviceActivityCallback.getUser();
            tripData.setHostId(host.getGoogleId());
        }

        userLoader = new UserLoader(serviceActivityCallback, tripData.getHostId());

    }

    private class FuelPriceCollector extends AsyncTask<Void, Void, Void> {

        private GeoPoint geoPoint;

        public FuelPriceCollector(GeoPoint geoPoint) {
            this.geoPoint = geoPoint;
        }

        @Override
        protected Void doInBackground(Void... params) {
            fuel_price = new FuelPrice().getFuelUnitPrice(geoPoint);
            return null;
        }
    }

    public void setFuelPrice() {
        new FuelPriceCollector(new GeoPoint(getStartLocation().getLon(), getStartLocation().getLat())).execute();
    }

    public double getFuelPrice() {
        return fuel_price;
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
        return new CarpoolUser(carpoolUserData,serviceActivityCallback);
    }

    public void confirmCarpoolUser(CarpoolUser carpoolUser){
        if (canConfirmPickup()) {
            carpoolUser.confirmPickup();
        }else{
            throw new IllegalArgumentException("Cannot confirm pickup");
        }
    }

    public boolean isLoggedInUser(){
        return userLoader.isLoggedInUser();
    }

    /**c
     * Removes a CarpoolUser for the list for this trip
     * @param user - a CarpoolUser
     */
    public void removeCarpoolUser(CarpoolUser user) {
        user.cancel();
    }

    /**
     * Updates the status of a CarpoolUser to PICKED_UP for this trip
     * @param user - a CarpoolUser
     */
    public void pickupCarpoolUser(CarpoolUser user) {
        if (canPickupUser(user)) {
            user.pickup();
        }else{
            throw new IllegalArgumentException("Cannot pickup user");
        }
    }

    public void dropoffCarpoolUser(CarpoolUser user) {
        if (canDropOffUser(user)){
            user.dropOff();
        }else{
            throw new IllegalArgumentException("Cannot dropoff user");
        }
    }

    /**
     * Updates the status of a CarpoolUser to NO_SHOW for this trip
     * @param user - a CarpoolUser
     */
    public void skipNoShow(CarpoolUser user) {
        if (canMarkNoShow(user)) {
            user.markNoShow();
            saveTrip();
        }else{
            throw new IllegalArgumentException("Cannot no show user");
        }
    }

    // TODO: Need to build new unit test for this
    public void splitFuelCost(int distance_in_km) {
        double pricePerMile = fuel_price/getVehicle().getMPG();
        double distance_in_miles = distance_in_km / METERS_PER_MILE;
        double fuel_split = (Math.round(pricePerMile * distance_in_miles) * 100.0) / 100.0;
        Iterator<CarpoolUser> users = getCarpoolUsers().iterator();
        while (users.hasNext()) {
            CarpoolUser user = users.next();
            if (user.getStatus() == CarpoolUserStatus.PICKED_UP) {
                user.setPaymentAmount(user.getPaymentAmount() + fuel_split);
            }
        }
    }

    // TODO: Remove this as it does not perform the desired function. Replaced by above method.
    /**
     * Splits the total fuel cost evenly among the list of CarpoolUsers
     * for each trip segment and adds to current fuel split
     * @param cost - the total fuel cost for the current trip segment
     * @return fuel_split - the fuel split for the current trip segment
     */
    public double splitFuelCost(double cost) {
        //Update total of trip
        tripData.setFuelTotal(tripData.getFuelTotal() + cost);

        //Split total and update users
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
            changeStatus(TripStatus.COMPLETED);
            saveTrip();
        }else{
            throw new IllegalArgumentException("Cannot Complete Trip");
        }
    }

    public void cancelTrip(){
        if (canCancelTrip()){
            changeStatus(TripStatus.CANCELLED);
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

    public List<Address> getDestinations() {
        List<Address> destinations = new ArrayList<>();
        while (getCarpoolUsers().iterator().hasNext()) {
            CarpoolUser user = getCarpoolUsers().iterator().next();
            if (user.getStatus() == CarpoolUserStatus.CONFIRMED_FOR_PICKUP) {
                destinations.add(user.getPickupLocation());
            } else if (user.getStatus() == CarpoolUserStatus.PICKED_UP) {
                destinations.add(user.getDropoffLocation());
            }
        }
        return destinations;
    }

    public double getTolls() {
        return tripData.getTolls();
    }

    public double getFuelTotal() {
        return tripData.getFuelTotal();
    }

    public void setTolls(double tolls) {
        //Set total tolls
        tripData.setTolls(tolls);

        //Split tolls between users
        int numUsers = 0;
        for(CarpoolUserData carpoolUser : tripData.getUsers()) {
            if(carpoolUser.getStatus() == CarpoolUserStatus.DROPPED_OFF)
                numUsers++;
        }
        for(CarpoolUserData carpoolUser : tripData.getUsers()) {
            if(carpoolUser.getStatus() == CarpoolUserStatus.DROPPED_OFF)
                carpoolUser.setPaymentAmount(Math.round((carpoolUser.getPaymentAmount()
                        + tolls/numUsers)*100.0)/100.0);
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
        setAddress(searchAddress, addressErrorCallback, true);
    }

    private void setAddress(String searchAddress, AddressErrorCallback addressErrorCallback, boolean destination) throws RemoteException {
        TripAddressLoadCallback tripAddressLoadCallback = new TripAddressLoadCallback(addressErrorCallback,destination);
        serviceActivityCallback.getLocationService().getLocationFromAddressName(searchAddress, tripAddressLoadCallback);
    }

    private void changeStatus(TripStatus status){
        if (isAllowedNextStaus(status)){
            tripData.setStatus(status);
        }else{
            throw new IllegalArgumentException("Invalid move from " + tripData.getStatus() + " to " + status);
        }
    }
    private boolean isAllowedNextStaus(TripStatus tripStatus){
        return tripData.getStatus().isValidateNextState(tripStatus);
    }

    public boolean canStartTrip(){
        return isOpenTrip();
    }

    public boolean canCancelTrip(){
        return isOpenTrip();
    }

    public boolean canCompleteTrip(){
        return isAllowedNextStaus(TripStatus.COMPLETED);
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
        if (isCompleted() && isLoggedInUserInCarpool()){
            CarpoolUser user = getLoggedInCarpoolUser();
//            if (getTripId().equals("AU5fMY2mtHTBJzAXf60c")) {//TODO remove, just for testing paying host
//                tripData.setFuelSplit(0);
//                tripData.setFuelTotal(0);
//                for(CarpoolUserData carpoolUser : tripData.getUsers()) {//Pikcup everyone
//                    carpoolUser.setStatus(CarpoolUserStatus.PICKED_UP);
//                    carpoolUser.setPaymentAmount(0);
//                }
//                user.setPaymentAmount(splitFuelCost(60));//Complete the journey and split gas
//                for(CarpoolUserData carpoolUser : tripData.getUsers()) {//Everyone gets out
//                    carpoolUser.setStatus(CarpoolUserStatus.DROPPED_OFF);
//                }
//                setTolls(7.50);//Set and split tolls
//                saveTrip();
//            }//TODO remove, just for testing paying host
            ret = user.isPaymentRequired();
        }
        return ret;
    }

    public boolean canConfirmPickup(){
        boolean ret = false;
        if (isInRoute() && isLoggedInUserInCarpool()){
            CarpoolUser user = getLoggedInCarpoolUser();
            ret = user.canConfirmPickup();
        }
        return ret;
    }

    public boolean canCancelPickup(){
        boolean ret = false;
        if (isOpenTrip() && isLoggedInUserInCarpool()){
            CarpoolUser user = getLoggedInCarpoolUser();
            ret = user.canCancelPickup();
        }
        return ret;
    }

    private boolean isOpenTrip(){
        return tripData.getStatus()==TripStatus.OPEN;
    }
    private boolean isInRoute(){
        return tripData.getStatus()==TripStatus.IN_ROUTE;
    }
    private boolean isCompleted(){
        return tripData.getStatus()==TripStatus.COMPLETED;
    }

    public boolean canRequestJoin(){
        return isOpenTrip() && !isLoggedInUserInCarpool() && !isLoggedInUser() && getOpenSeats()>0;
    }

    public boolean canDropOffUser(CarpoolUser carpoolUser) {
        return isLoggedInUser() && carpoolUser.canDropoff();
    }

    public boolean canMarkNoShow(CarpoolUser carpoolUser) {
        return isLoggedInUser() && carpoolUser.canMarkNoShow();
    }

    public boolean canPickupUser(CarpoolUser carpoolUser) {
        return isLoggedInUser() && carpoolUser.canPickup();
    }

    public boolean canNavigatePickupUser(CarpoolUser carpoolUser) {
        return isLoggedInUser() && carpoolUser.canNavigatePickup();
    }

    public boolean canAcceptRequest(CarpoolUser carpoolUser) {
        return isOpenTrip() && isLoggedInUserInCarpool() && carpoolUser.canAcceptRequest();
    }

    public void startTrip() {
        if (canStartTrip()){
            changeStatus(TripStatus.IN_ROUTE);
            saveTrip();
        }else{
            throw new IllegalArgumentException("Cannot Start Trip");
        }

    }

    public void confirmPickup() {
        if (canConfirmPickup()){
            loggedInUser.confirmPickup();
            saveTrip();
        }else{
            throw new IllegalArgumentException("Cannot Confirm Pickup");
        }
    }

    public void cancelPickup() {
        if (canCancelPickup()){
            loggedInUser.cancel();
            tripData.setOpenSeats(tripData.getOpenSeats() + 1);
            saveTrip();
        }else{
            throw new IllegalArgumentException("Cannot Cancel Pickup");
        }
    }

    public void acceptPickupRequest(CarpoolUser carpoolUser) {
        if (canAcceptRequest(carpoolUser)){
            carpoolUser.acceptRequest();
            tripData.setOpenSeats(tripData.getOpenSeats() - 1);
            saveTrip();
        }else{
            throw new IllegalArgumentException("Cannot Accept Pickup");
        }
    }

    public void rejectPickupRequest(CarpoolUser carpoolUser){
        if (canRejectRequest(carpoolUser)){
            carpoolUser.rejectRequest();
            saveTrip();
        }
    }

    public boolean canRejectRequest(CarpoolUser carpoolUser) {
        return isOpenTrip() && isLoggedInUserInCarpool() && carpoolUser.canRejectRequest();
    }

    public boolean canNavigateDropoffUser(CarpoolUser carpoolUser) {
        return isLoggedInUser() && carpoolUser.canNavigateDropoff();
    }

    public void addUser(CarpoolUser carpoolUser) {
        if (canRequestJoin()) {
            tripData.getUsers().add(carpoolUser.getCarpoolUserData());
            saveTrip();
        }else{
            throw new IllegalArgumentException("Cannot join trip");
        }
    }

    public boolean canConfirmDropoff() {
        boolean ret = false;
        if (isInRoute() && isLoggedInUserInCarpool()){
            CarpoolUser user = getLoggedInCarpoolUser();
            ret = user.canConfirmDropoff();
        }
        return ret;
    }

    public void confirmDropoff() {
        if (canConfirmDropoff()){
            loggedInUser.confirmDropoff();
            saveTrip();
        }else{
            throw new IllegalArgumentException("Cannot Confirm Dropoff");
        }
    }

    public void navigatePickupUser(CarpoolUser carpoolUser) {
        if (canNavigatePickupUser(carpoolUser)){
            carpoolUser.navigatePickup();
        }else{
            throw new IllegalArgumentException("Cannot navigate pickup");
        }
    }

    public void navigateDropoff(CarpoolUser carpoolUser) {
        if (canNavigateDropoffUser(carpoolUser)){
            carpoolUser.navigateDropoff();
        }else{
            throw new IllegalArgumentException("Cannot navigate dropoff");
        }
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
        this.vehicle = vehicle;
        tripData.setHostVehicle(vehicle.getPlateNumber());
        tripData.setOpenSeats(vehicle.getSeats());
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public String getVehiclePlateNumber() {
        return tripData.getHostVehicle();
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
