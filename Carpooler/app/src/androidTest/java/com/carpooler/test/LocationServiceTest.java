package com.carpooler.test;

import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.dto.AddressData;
import com.carpooler.trips.LocationService;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jcsax on 6/21/15.
 */
public class LocationServiceTest extends DatabaseServiceTest {

    private LocationService mLocationService;

    private LocationListener mListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i("LocationServiceTest", "Location changed to " + location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i("LocationServiceTest", "Provider, " + provider + ", status is " + status + "\n" + "with the following extras: " + extras);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i("LocationServiceTest", provider + " has been enabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i("LocationServiceTest", provider + " has been disabled");
        }
    };

    protected void setUp() throws Exception {
        super.setUp();
        mLocationService = new LocationService(getContext(), mListener,conn);
    }


    public void testGetInitialLocation() {
        Location location = mLocationService.initialize();
        assertTrue(mLocationService.canGetLocation());
//        assertNotNull(location);
//        assertNotNull(location.getLatitude());
//        assertNotNull(location.getLongitude());
    }

    public void testStopUsingGPS() {
        mLocationService.stopUsingGPS();
        assertFalse(mLocationService.canGetLocation());
    }

    public void testGetLocationFromAddressName() throws RemoteException, InterruptedException {
        mLocationService.getLocationFromAddressName("1600 Amphitheatre Parkway Mountain View, CA 94043", new AddressResponseCallback());
        checkResponse();
    }

    public void testSelectNextDestination() {
        AddressData startAddressData = new AddressData();
        startAddressData.setStreetNumber("183 Dawn Circle");
        startAddressData.setCity("Galt");
        startAddressData.setState("CA");
        com.carpooler.users.Address start = new com.carpooler.users.Address(startAddressData);
        AddressData destination1AddressData = new AddressData();
        destination1AddressData.setStreetNumber("450 Serra Mall");
        destination1AddressData.setCity("Stanford");
        destination1AddressData.setState("CA");
        com.carpooler.users.Address destination1 = new com.carpooler.users.Address(destination1AddressData);
        AddressData destination2AddressData = new AddressData();
        destination2AddressData.setStreetNumber("1500 Shoreline Blvd");
        destination2AddressData.setCity("Mountain View");
        destination2AddressData.setState("CA");
        com.carpooler.users.Address destination2 = new com.carpooler.users.Address(destination2AddressData);

        List<com.carpooler.users.Address> destinations = new ArrayList<com.carpooler.users.Address>();
        destinations.add(destination1);
        destinations.add(destination2);

        com.carpooler.users.Address nextDestination = mLocationService.selectNextDestination(start, destinations, null);
        assertEquals("1500 Shoreline Blvd", nextDestination.getStreetNumber());
    }

    class AddressResponseCallback extends AbstractTestCallback<Address> implements DatabaseService.GeocodeCallback{
        @Override
        public void doSuccess(Address data) {
            Assert.assertNotNull("Success is null", data);
            latch.countDown();
        }
    }

}
