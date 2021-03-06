package com.carpooler.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.carpooler.GeoPoint;
import com.carpooler.R;
import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.TripDataService;
import com.carpooler.dao.UserDataService;
import com.carpooler.payment.PaymentService;
import com.carpooler.trips.FuelPrice;
import com.carpooler.trips.LocationService;
import com.carpooler.users.Address;
import com.carpooler.users.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.util.Date;

public class CarpoolerActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener, TripDetailCallback, VehicleDetailCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, TripSearchCallback, LocationListener {
    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private DatabaseService.Connection conn;
    private GoogleApiClient mGoogleApiClient;
    private User user;
    private TripDataService tripDataService;
    private UserDataService userDataService;
    private LocationService locationService;
    private PaymentService paymentService;
    private GeoPoint geoPoint;
    private boolean mIntentInProgress = false;
    private int GPlusSignIn = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                FragmentManager fragmentManager = getSupportFragmentManager();
                if (fragmentManager.getBackStackEntryCount()>0) {
                    String name = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1).getName();
                    getSupportActionBar().setTitle(name);
                }
            }
        });
        setContentView(R.layout.activity_carpooler);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        conn = new DatabaseService.Connection();
        Intent intent = new Intent(this, DatabaseService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        tripDataService = new TripDataService(conn);
        userDataService = new UserDataService(conn);
        locationService = new LocationService(this, this, conn);
        paymentService = new PaymentService(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
        mGoogleApiClient.connect();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationService.stopUsingGPS();
        unbindService(conn);
        mGoogleApiClient.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_carpooler, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, CarpoolerSettings.class);
            startActivity(i);
            return true;
        } else if (id == R.id.action_search){
            Fragment fragment = new SearchTripsFragment();
            String title = getString(R.string.title_search_trips);
            pushFragment(fragment,title);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }
    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = createTripInProgressFragment();
                title = getString(R.string.nav_item_trip_in_progress);
                break;
            case 1:
                fragment = createTripListFragment(true);
                title = getString(R.string.nav_item_hosted_trips);
                break;
            case 2:
                fragment = createTripListFragment(false);
                title = getString(R.string.nav_item_joined_trips);
                break;
            case 3:
                fragment = createVehicleManagerFragment();
                title = getString(R.string.title_vehicles);
                break;
            default:
                break;
        }

        pushFragment(fragment, title);

    }

    private Fragment createVehicleManagerFragment() {
        return VehicleListFragment.newInstance(VehicleListFragment.VehicleListType.MANAGER);
    }
    
    private Fragment createTripInProgressFragment() {
        Fragment fragment = new TripInProgressFragment();
        return fragment;
    }

    private Fragment createTripListFragment(boolean hosted) {
        Bundle args = new Bundle();
        args.putBoolean(TripListFragment.Status.HOSTED_ARG, hosted);
        Fragment fragment = new TripListMainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void pushFragment(Fragment fragment, String title) {
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .addToBackStack(title)
                    .replace(R.id.container_body, fragment)
                    .commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
        user = new User(currentPerson,this);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
        displayView(0);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this,
                    0).show();
            return;
        }
        if (!mIntentInProgress) {
            if (connectionResult.hasResolution()) {
                try {
                    mIntentInProgress = true;
                    connectionResult.startResolutionForResult(this, GPlusSignIn);
                } catch (IntentSender.SendIntentException e) {
                    mIntentInProgress = false;
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, responseCode, intent);
        if (requestCode == GPlusSignIn) {
            mIntentInProgress = false;
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public User getUser(){
        return user;
    }

    @Override
    public void onTripSelected(String tripId) {
        TripViewFragment fragment = new TripViewFragment();
        String title = getString(R.string.title_trip_detail);
        pushSingleTripFragment(fragment,tripId,title);
    }

    private void pushSingleTripFragment(Fragment fragment, String tripId, String title){
        Bundle args = new Bundle();
        args.putString(TripViewFragment.TRIP_ID_ARG,tripId);
        fragment.setArguments(args);
        pushFragment(fragment, title);
    }
    @Override
    public void onAddTrip() {
        TripAddFragment fragment = new TripAddFragment();
        String title = getString(R.string.title_trip_detail);
        pushFragment(fragment, title);
    }

    @Override
    public void onVehicleSelected(String plateNumber) {
        VehicleDetailFragment fragment = VehicleDetailFragment.newInstance(plateNumber);
        String title = getString(R.string.title_vehicle_detail);
        pushFragment(fragment, title);
    }

    @Override
    public void onAddVehicle() {
        VehicleDetailFragment fragment = VehicleDetailFragment.newInstance();
        String title = getString(R.string.title_vehicle_detail);
        pushFragment(fragment, title);
    }

    public void goToHome() {
        //clear back stack
        FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }

        //go to home
        Fragment fragment = createTripListFragment(false);
        String title = getString(R.string.nav_item_joined_trips);
        pushFragment(fragment, title);
    }

    public void goBack(String title) {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();

        // set the toolbar title
        getSupportActionBar().setTitle(title);
    }

    @Override
    public TripDataService getTripDataService() {
        return tripDataService;
    }

    @Override
    public UserDataService getUserDataService() {
        return userDataService;
    }

    @Override
    public LocationService getLocationService() {
        return locationService;
    }

    @Override
    public PaymentService getPaymentService() {
        return paymentService;
    }

    @Override
    public void search(double startLon, double startLat, double endLon, double endLat, Date startDate, int searchDistance, int timeRangeMinutes) {
        Bundle args = new Bundle();
        args.putDouble(TripListFragment.Search.START_LON, startLon);
        args.putDouble(TripListFragment.Search.START_LAT, startLat);
        args.putDouble(TripListFragment.Search.END_LON, endLon);
        args.putDouble(TripListFragment.Search.END_LAT, endLat);
        args.putLong(TripListFragment.Search.START_DATE, startDate.getTime());
        args.putInt(TripListFragment.Search.SEARCH_DISTANCE, searchDistance);
        args.putInt(TripListFragment.Search.TIME_RANGE, timeRangeMinutes);
        TripListFragment searchList = new TripListFragment.Search();
        searchList.setArguments(args);
        pushFragment(searchList, "Search Results");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("CarpoolerActivity", "Latitude: " + location.getLatitude() + "; Longitude: " + location.getLongitude());
        geoPoint = new GeoPoint(location.getLongitude(), location.getLatitude());
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

    @Override
    public DatabaseService.Connection getConnection() {
        return conn;
    }

    @Override
    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }


    @Override
    public People getPeople() {
        return Plus.PeopleApi;
    }

    @Override
    public void onHostCompleteTrip(String tripId) {
        TripCompleteFragment fragment = TripCompleteFragment.newInstance(tripId,
                TripCompleteFragment.TripCompleteTypeEnum.HOST.ordinal());
        String title = getString(R.string.title_trip_complete);
        pushFragment(fragment, title);
    }

    @Override
    public void onUserCompleteTrip(String tripId) {
        TripCompleteFragment fragment = TripCompleteFragment.newInstance(tripId,
                TripCompleteFragment.TripCompleteTypeEnum.USER.ordinal());
        String title = getString(R.string.title_trip_complete);
        pushFragment(fragment, title);
    }

    @Override
    public void onUserPaid(String tripId) {
        UserReviewFragment fragment = UserReviewFragment.newInstance(tripId);
        String title = getString(R.string.review);
        pushFragment(fragment, title);
    }

    @Override
    public void onJoinTripSelected(String tripId) {
        JoinTripFragment fragment = new JoinTripFragment();
        String title = getString(R.string.title_join_trip);
        pushSingleTripFragment(fragment,tripId,title);

    }

    @Override
    public void navigate(Address address) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+address.getLat()+","+address.getLon());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }
}
