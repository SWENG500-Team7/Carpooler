package com.carpooler.test;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.RemoteException;

import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.TripDataService;
import com.carpooler.dao.UserDataService;
import com.carpooler.dao.dto.AddressData;
import com.carpooler.dao.dto.CarpoolUserData;
import com.carpooler.dao.dto.TripData;
import com.carpooler.dao.dto.UserData;
import com.carpooler.dao.dto.VehicleData;
import com.carpooler.trips.AddressErrorCallback;
import com.carpooler.trips.AddressLoadCallback;
import com.carpooler.trips.LocationService;
import com.carpooler.trips.TripStatus;
import com.carpooler.users.CarpoolUserStatus;

import junit.framework.Assert;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * Created by raymond on 8/6/15.
 */
public class TripSimulateTest extends DatabaseServiceTest {
    private TripDataService tripDataService;
    private LocationService locationService;
    private UserDataService userDataService;
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        tripDataService = new TripDataService(conn);
        locationService = new LocationService(getContext(),locationListener,conn);
        userDataService = new UserDataService(conn);
    }

    private TripData findTrip(String tripId) throws RemoteException, InterruptedException {
        latch = new CountDownLatch(1);
        FindTripCallback callback = new FindTripCallback();
        tripDataService.getTripData(tripId, callback);
        checkResponse();
        return callback.foundTrip;
    }

    private UserData findUser(String userId) throws RemoteException, InterruptedException {
        latch = new CountDownLatch(1);
        FindUserCallback findUserCallback = new FindUserCallback();
        userDataService.getUserData(userId, findUserCallback);
        checkResponse();
        return findUserCallback.userData;
    }
    private AddressData findAddress(String searchAddress) throws RemoteException, InterruptedException {
        latch = new CountDownLatch(1);
        AddressLoader addressLoader = new AddressLoader();
        locationService.getLocationFromAddressName(searchAddress, addressLoader);
        checkResponse();
        return addressLoader.addressData;
    }

    private void saveTrip(TripData tripData) throws InterruptedException, RemoteException {
        tripDataService.createTrip(tripData, new StringResponseCallback());
        checkResponse();
    }

    public void testAddUser() throws RemoteException, InterruptedException {
        String tripId = "";
        String userId = "";
        String pickupLocationSearch = "";
        String dropoffLocationSearch = "";
        TripData tripData =findTrip(tripId);
        CarpoolUserData carpoolUserData = new CarpoolUserData();
        carpoolUserData.setUserId(userId);
        AddressData pickupLocation = findAddress(pickupLocationSearch);
        AddressData dropoffLocation = findAddress(dropoffLocationSearch);
        carpoolUserData.setPickupLocation(pickupLocation);
        carpoolUserData.setDropoffLocation(dropoffLocation);

        tripData.getUsers().add(carpoolUserData);
        tripData.setOpenSeats(tripData.getOpenSeats()-1);

        saveTrip(tripData);
    }

    public void testChangeUserStatus() throws RemoteException, InterruptedException {
        String tripId = "";
        String userId = "";
        CarpoolUserStatus carpoolUserStatus = CarpoolUserStatus.PENDING_PICK_UP;
        TripData tripData =findTrip(tripId);
        for(CarpoolUserData carpoolUser:tripData.getUsers()){
            if (carpoolUser.getUserId().equals(userId)){
                carpoolUser.setStatus(carpoolUserStatus);
                saveTrip(tripData);
                break;
            }
        }
    }

    public void testChangeTripStatus() throws RemoteException, InterruptedException {
        String tripId = "";
        TripStatus tripStatus = TripStatus.IN_ROUTE;
        TripData tripData = findTrip(tripId);
        tripData.setStatus(tripStatus);
        saveTrip(tripData);
    }

    public void testCreateTrip() throws RemoteException, InterruptedException {
        String startAddressSearch = "";
        String endAddressSearch = "";
        String hostId = "";
        Date startDate = new Date();

        TripData tripData = new TripData();

        AddressData startAddress = findAddress(startAddressSearch);
        AddressData endAddress = findAddress(endAddressSearch);

        tripData.setStartTime(startDate);
        tripData.setStartLocation(startAddress);
        tripData.setEndLocation(endAddress);

        UserData userData = findUser(hostId);
        VehicleData vehicleData = userData.getVehicle().get(0);
        tripData.setHostVehicle(vehicleData);
        tripData.setOpenSeats(vehicleData.getSeats());

        saveTrip(tripData);
    }

    private class FindTripCallback extends AbstractTestCallback<TripData> implements DatabaseService.GetCallback<TripData>{
        private TripData foundTrip;

        @Override
        public void doSuccess(TripData data) {
            foundTrip=data;
            latch.countDown();
        }
    }
    private class FindUserCallback extends AbstractTestCallback<UserData> implements DatabaseService.GetCallback<UserData>{
        private UserData userData;
        @Override
        public void doSuccess(UserData data) {
            this.userData = data;
            latch.countDown();
        }
    }

    private class AddressLoader extends AddressLoadCallback{
        private AddressData addressData;

        public AddressLoader() {
            super(new AddressErrorCallback() {
                @Override
                public void doError(String message) {
                    Assert.assertNull("Error found", message);
                    latch.countDown();
                }

                @Override
                public void doException(Exception exception) {
                    Assert.assertNull("Exception found", exception);
                    latch.countDown();
                }

                @Override
                public void doSuccess(String address) {
                    latch.countDown();
                }
            });
        }

        @Override
        protected void setAddressData(AddressData addressData) {
            this.addressData = addressData;
        }
    }
}
