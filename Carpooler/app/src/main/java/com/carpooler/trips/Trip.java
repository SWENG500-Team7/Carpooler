package com.carpooler.trips;

import android.location.Location;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;

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
    private static final double METERS_PER_MILE = 1609.34;

    private CarpoolUser nextPickupUser;
    private CarpoolUser nextDropoffUser;
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
            tripData.setFuelPrice(new FuelPrice().getFuelUnitPrice(geoPoint));
            return null;
        }
    }

    public void setFuelPrice() {
        new FuelPriceCollector(new GeoPoint(getStartLocation().getLon(), getStartLocation().getLat())).execute();
    }

    public double getFuelPrice() {
        return tripData.getFuelPrice();
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

    public void splitFuelCost(int distance_in_km) {
        double pricePerMile = getFuelPrice()/getHostVehicle().getMPG();
        double distance_in_miles = distance_in_km / METERS_PER_MILE;
        double fuel_cost = (Math.round(pricePerMile * distance_in_miles) * 100.0) / 100.0;
        //Update fuel total of trip
        tripData.setFuelTotal(tripData.getFuelTotal() + fuel_cost);
        Iterator<CarpoolUser> usersCount = getCarpoolUsers().iterator();
        int pickedUpUserCount = 0;
        while (usersCount.hasNext()) {
            CarpoolUser user = usersCount.next();
            if (user.canNavigateDropoff()) {
                pickedUpUserCount++;
            }
        }
        double fuel_split = (Math.round(fuel_cost/pickedUpUserCount) * 100.0) / 100.0;
        Iterator<CarpoolUser> usersPayment = getCarpoolUsers().iterator();
        while (usersPayment.hasNext()) {
            CarpoolUser user = usersPayment.next();
            if (user.canNavigateDropoff()) {
                //Update each user's payment amount
                user.setPaymentAmount(user.getPaymentAmount() + fuel_split);
            }
        }
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
            if (user.canNavigatePickup()) {
                destinations.add(user.getPickupLocation());
            } else if (user.canNavigateDropoff()) {
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

    public int getTotalDistance() {
        return tripData.getTotalDistance();
    }

    public void setTotalDistance(int totalDistance) {
        tripData.setTotalDistance(totalDistance);
    }

    public void setStartLocation(String searchAddress, AddressErrorCallback addressErrorCallback) throws RemoteException {
        setAddress(searchAddress, addressErrorCallback, false);
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
//                tripData.setFuelTotal(0);
//                for(CarpoolUserData carpoolUser : tripData.getUsers()) {//Pikcup everyone
//                    carpoolUser.setStatus(CarpoolUserStatus.PICKED_UP);
//                    carpoolUser.setPaymentAmount(30);
//                }
//                for(CarpoolUserData carpoolUser : tripData.getUsers()) {//Everyone gets out
//                    carpoolUser.setStatus(CarpoolUserStatus.CONFIRMED_DROPPED_OFF);
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
        checkNavigateUsers(true);
        return isLoggedInUser()
                && isInRoute()
                && carpoolUser.canNavigatePickup()
                && carpoolUser.equals(nextPickupUser);
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
        checkNavigateUsers(false);
        return isLoggedInUser()
                && isInRoute()
                && carpoolUser.canNavigateDropoff()
                && carpoolUser.equals(nextDropoffUser);
    }

    private void checkNavigateUsers(boolean pickup){
        CarpoolUser nextUser;
        if (pickup){
            nextUser = nextPickupUser;
        }else{
            nextUser = nextDropoffUser;
        }
        if (isLoggedInUser()
                && isInRoute()
                && nextUser==null){
            Location startLocation = serviceActivityCallback.getLocationService().initialize();
            CarpoolUserIterator carpoolUsers = getCarpoolUsers();
            List<CarpoolUser> navigatableUsers = new ArrayList<CarpoolUser>();
            for (CarpoolUser user:carpoolUsers){
                if (pickup){
                    if (user.canNavigatePickup()){
                        navigatableUsers.add(user);
                    }
                }else{
                    if (user.canNavigateDropoff()){
                        navigatableUsers.add(user);
                    }
                }
            }
            carpoolUsers.reset();
            if (!navigatableUsers.isEmpty()){
                nextUser = serviceActivityCallback.getLocationService().selectNextDestination(startLocation,navigatableUsers,pickup);
                if (pickup){
                    nextPickupUser = nextUser;
                }else{
                    nextDropoffUser = nextUser;
                }
            }
        }
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
            Log.e("CreateTripCallback",message);
        }

        @Override
        public void doException(Exception exception) {
            Log.e("CreateTripCallback",exception.getMessage());
        }

        @Override
        public void doSuccess(String data) {
            Log.e("CreateTripCallback",data);
        }
    }

    /**
     * Sets the vehicle to be associated with this trip
     * @param hostVehicle - the trip vehicle
     */
    public void setHostVehicle(Vehicle hostVehicle) {
        tripData.setHostVehicle(hostVehicle.getData());
        tripData.setOpenSeats(hostVehicle.getSeats());
    }

    public Vehicle getHostVehicle() {
        return new Vehicle(tripData.getHostVehicle());
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
