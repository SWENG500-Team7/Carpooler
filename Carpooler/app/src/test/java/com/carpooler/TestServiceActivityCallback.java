package com.carpooler;

import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.TripDataService;
import com.carpooler.dao.UserDataService;
import com.carpooler.payment.PaymentService;
import com.carpooler.trips.LocationService;
import com.carpooler.ui.activities.ServiceActivityCallback;
import com.carpooler.users.User;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.People;

/**
 * Created by raymond on 7/5/15.
 */
public class TestServiceActivityCallback implements ServiceActivityCallback {
    private TripDataService tripDataService;
    private UserDataService userDataService;
    private LocationService locationService;
    private PaymentService paymentService;
    private User user;
    private DatabaseService.Connection connection;
    private GoogleApiClient googleApiClient;
    private People people;
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
    public User getUser() {
        return user;
    }

    @Override
    public DatabaseService.Connection getConnection() {
        return connection;
    }

    @Override
    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    @Override
    public People getPeople() {
        return people;
    }

    public void setTripDataService(TripDataService tripDataService) {
        this.tripDataService = tripDataService;
    }

    public void setUserDataService(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    public void setLocationService(LocationService locationService) {
        this.locationService = locationService;
    }

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setConnection(DatabaseService.Connection connection) {
        this.connection = connection;
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }

    public void setPeople(People people) {
        this.people = people;
    }
}
