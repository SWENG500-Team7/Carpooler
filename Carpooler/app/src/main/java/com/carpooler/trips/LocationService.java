package com.carpooler.trips;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * Created by jcsax on 6/21/15.
 */
public class LocationService {

    private static final String TAG = LocationService.class.getSimpleName();
    private final Context mContext;

    private Geocoder geocoder;

    // flag for GPS status
    private boolean isGPSEnabled = false;

    // flag for network status
    private boolean isNetworkEnabled = false;

    // flag for GPS status
    private boolean canGetLocation = false;

    // The minimum distance to change updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 0;

    private Location location;
    protected LocationManager locationManager;

    private LocationListener mListener;

    public LocationService(Context context, LocationListener listener) {
        mContext = context;
        mListener = listener;
        geocoder = new Geocoder(context);
        initialize();
    }

    /**
     * Sets up the location service providers, starts the updates, and initializes to the last known location.
     * @return location
     */
    public Location initialize() {
        try {
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            canGetLocation = true;
            // We would prefer to use the GPS for location updates, as it has better accuracy and resolution.
            if (isGPSEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, mListener);
                Log.d(TAG, "GPS enabled");
                if (locationManager != null) {
                    if (isNetworkEnabled) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    } else {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                }
            } else if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, mListener);
                Log.d(TAG, "Network enabled");
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
            }
        } catch (Exception e) {
            Log.d(TAG, "Could not access location", e);
        }
        return location;
    }

    /**
     * Stops updates to the location listener.
     * Calling this function will stop GPS from being passed to your app.
     */
    public void stopUsingGPS() {
        if(locationManager != null) {
            Log.i(TAG, "Remove updates");
            locationManager.removeUpdates(mListener);
        }
        canGetLocation = false;
    }

    /**
     * Checks whether or not GPS/Wifi enabled
     * @return canGetLocation
     */
    public boolean canGetLocation() {
        return canGetLocation;
    }

    /**
     * Gets a location object from an address
     * @param address String
     * @return location
     */
    public Location getLocationFromAddressName(String address) {
        Location location = new Location("");
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            Address extractedAddress = addresses.get(0);
            location.setLatitude(extractedAddress.getLatitude());
            location.setLongitude(extractedAddress.getLongitude());
        } catch (IOException e) {
            Log.i(TAG, "Failed to get address from name");
        }
        return location;
    }

    /**
     * Determines the next destination based on which is the closest
     * @param start current Location
     * @param destinations a list of Locations
     * @return nextDestination
     */
    public Location selectNextDestination(Location start, List<Location> destinations) {
        Location nextDestination = null;
        for (Location destination : destinations) {
            if(nextDestination == null) {
                nextDestination = destination;
            } else {
                float first_distance = start.distanceTo(nextDestination);
                float second_distance = start.distanceTo(destination);
                if(second_distance < first_distance) {
                    nextDestination = destination;
                }
            }
        }
        return nextDestination;
    }

}
