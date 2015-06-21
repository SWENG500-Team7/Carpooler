package com.carpooler.test;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.test.AndroidTestCase;
import android.util.Log;

import com.carpooler.trips.LocationService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jcsax on 6/21/15.
 */
public class LocationServiceTest extends AndroidTestCase {

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
        mLocationService = new LocationService(getContext(), mListener);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
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

    public void testGetLocationFromAddressName() {
        Location location = mLocationService.getLocationFromAddressName("1600 Amphitheatre Parkway Mountain View, CA 94043");
        assertNotNull(location);
        assertNotNull(location.getLatitude());
        assertNotNull(location.getLongitude());
    }

    public void testSelectNextDestination() {
        Location start = new Location("");
        start.setLatitude(37.374412);
        start.setLongitude(-122.065147);
        Location destination1 = new Location("");
        destination1.setLatitude(37.414771);
        destination1.setLongitude(-122.081119);
        Location destination2 = new Location("");
        destination2.setLatitude(37.439293);
        destination2.setLongitude(-122.173539);
        List<Location> destinations = new ArrayList<Location>();
        destinations.add(destination1);
        destinations.add(destination2);
        Location location = mLocationService.selectNextDestination(start, destinations);
        assertNotNull(location);
        assertEquals(37.414771, location.getLatitude());
        assertEquals(-122.081119, location.getLongitude());
    }

}
