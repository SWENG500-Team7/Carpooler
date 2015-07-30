package com.carpooler.trips;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;

import com.carpooler.dao.DatabaseService;
import com.carpooler.users.Address;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by jcsax on 6/21/15.
 */
public class LocationService {

    private static final String TAG = LocationService.class.getSimpleName();
    private final Context mContext;
    private LocationManager locationManager;
    private LocationListener mListener;
    private DatabaseService.Connection connection;
    private DestinationSelectionCallback mCallback;

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
    private Address last_destination;

    public LocationService(Context context, LocationListener listener, DatabaseService.Connection connection) {
        mContext = context;
        mListener = listener;
        this.connection = connection;
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
    public void getLocationFromAddressName(String address, DatabaseService.GeocodeCallback callback) throws RemoteException {
        connection.geocode(address,callback);
    }

    private class DestinationSelector extends AsyncTask<Void, Void, Void> {

        private Address start;
        private List<Address> destinations;
        private Trip trip;

        public DestinationSelector(Address start, List<Address> destinations, Trip trip) {
            this.start = start;
            this.destinations = destinations;
            this.trip = trip;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Address destination = selectNextDestination(start, destinations, trip);
            last_destination = destination;
            if (mCallback != null) {
                mCallback.onDestinationSelected(destination);
            }
            return null;
        }
    }

    public Address getLastDestination() {
        return last_destination;
    }

    public void selectNextDestination(Address start, List<Address> destinations, Trip trip, DestinationSelectionCallback callback) {
        mCallback = callback;
        new DestinationSelector(start, destinations, trip).execute();
    }

    /**
     * Determines the next destination based on which is the closest
     * @param start Address
     * @param destinations a list of Addresses
     * @return nextDestination
     */
    public Address selectNextDestination(Address start, List<Address> destinations, Trip trip) {
        int[] durations = new int[destinations.size()];
        String requestUrlString = "https://maps.googleapis.com/maps/api/distancematrix/json?origins="
                +start.getStreetNumber().replace(" ", "+")+"+"+start.getCity().replace(" ", "+")+"+"+start.getState().replace(" ", "+")
                +"&destinations=";
        for (Address destination : destinations) {
            requestUrlString = requestUrlString + destination.getStreetNumber().replace(" ", "+")+"+"
                    +destination.getCity().replace(" ", "+")+"+"+destination.getState().replace(" ", "+")+"|";
        }
        requestUrlString = requestUrlString.substring(0, requestUrlString.length()-1);
        Log.i("LocationService", requestUrlString);
        //Make the connection and get the JSON
        HttpURLConnection urlConnection = null;
        ByteArrayOutputStream buffer = null;
        try {
            URL requestUrl = new URL(requestUrlString);
            urlConnection = (HttpURLConnection) requestUrl.openConnection();
            InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());

            //Ensure all data is read from connection
            buffer = new ByteArrayOutputStream();
            int data;
            while ((data = in.read()) > -1) {
                buffer.write(data);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        int pos = 0;
        try {
            String jsonString = buffer.toString();
            Log.i("LocationService", jsonString);
            JSONObject json = new JSONObject(jsonString);
            JSONArray elements = json.getJSONArray("rows").getJSONObject(0).getJSONArray("elements");
            for (int i=0; i<durations.length; i++) {
                durations[i] = elements.getJSONObject(i).getJSONObject("duration").getInt("value");
            }
            int shortestDuration = 0;
            for (int i=0; i<durations.length; i++) {
                if (i == 0) {
                    shortestDuration = durations[i];
                } else if (durations[i] < shortestDuration) {
                    shortestDuration = durations[i];
                    pos = i;
                }
            }
            int distance = elements.getJSONObject(pos).getJSONObject("distance").getInt("value");
            if (trip != null) {
                trip.setTotalDistance(trip.getTotalDistance() + distance);
                trip.splitFuelCost(distance);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return destinations.get(pos);
    }

}
