package com.carpooler.users;

import android.os.RemoteException;

import com.carpooler.dao.dto.AddressData;
import com.carpooler.dao.dto.CarpoolUserData;
import com.carpooler.trips.AddressErrorCallback;
import com.carpooler.trips.AddressLoadCallback;
import com.carpooler.ui.activities.ServiceActivityCallback;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.util.Date;

/**
 * Created by raymond on 6/6/15.
 */
public class CarpoolUser {
    private final CarpoolUserData carpoolUserData;
    private final ServiceActivityCallback serviceActivityCallback;
    private User user;
    public CarpoolUser(CarpoolUserData carpoolUserData,
                       ServiceActivityCallback serviceActivityCallback) {
        this.carpoolUserData = carpoolUserData;
        this.serviceActivityCallback=serviceActivityCallback;
        PendingResult<People.LoadPeopleResult> result = Plus.PeopleApi.load(serviceActivityCallback.getGoogleApiClient(),carpoolUserData.getUserId());
        result.setResultCallback(new UserLoaderCallback());
    }

    public Address getPickupLocation() {
        return new Address(carpoolUserData.getPickupLocation());
    }


    public Address getDropoffLocation() {
        return new Address(carpoolUserData.getDropoffLocation());
    }

    public void setPickupLocation(String searchAddress, AddressErrorCallback addressErrorCallback) throws RemoteException {
        setAddress(searchAddress,addressErrorCallback,false);
    }

    public void setDropoffLocation(String searchAddress, AddressErrorCallback addressErrorCallback) throws RemoteException {
        setAddress(searchAddress,addressErrorCallback,true);
    }

    private void setAddress(String searchAddress, AddressErrorCallback addressErrorCallback, boolean destination) throws RemoteException {
        UserAddressLoadCallback tripAddressLoadCallback = new UserAddressLoadCallback(addressErrorCallback,destination);
        serviceActivityCallback.getConnection().geocode(searchAddress,tripAddressLoadCallback);
    }

    public CarpoolUserStatus getStatus() {
        return carpoolUserData.getStatus();
    }

    public double getPaymentAmount() {
        return carpoolUserData.getPaymentAmount();
    }

    public void setPaymentAmount(double paymentAmount) {
        carpoolUserData.setPaymentAmount(paymentAmount);
    }

    public Date getPickupDate() {
        return carpoolUserData.getPickupDate();
    }

    public void setPickupDate(Date pickupDate) {
        carpoolUserData.setPickupDate(pickupDate);
    }

    public Date getDropoffDate() {
        return carpoolUserData.getDropoffDate();
    }

    public void setDropoffDate(Date dropoffDate) {
        carpoolUserData.setDropoffDate(dropoffDate);
    }

    public void confirmPickup(){
        changeStatus(CarpoolUserStatus.PICKED_UP);
    }

    public void changeStatus(CarpoolUserStatus nextStatus){
        if (carpoolUserData.getStatus().isValidateNextState(nextStatus)){
            carpoolUserData.setStatus(nextStatus);
        }else{
            throw new IllegalArgumentException("Invalid to move from " + carpoolUserData.getStatus() + " to " + nextStatus);
        }
    }

    public User getUser(){
        return user;
    }

    private class UserLoaderCallback implements ResultCallback<People.LoadPeopleResult>{

        @Override
        public void onResult(People.LoadPeopleResult loadPeopleResult) {
            Person person = loadPeopleResult.getPersonBuffer().get(1);
            user = new User(person,serviceActivityCallback);
        }
    }
    private class UserAddressLoadCallback extends AddressLoadCallback {
        private final boolean destination;

        public UserAddressLoadCallback(AddressErrorCallback errorCallback, boolean destination) {
            super(errorCallback);
            this.destination = destination;
        }

        @Override
        protected void setAddressData(AddressData addressData) {
            if (destination){
                carpoolUserData.setDropoffLocation(addressData);
            }else{
                carpoolUserData.setPickupLocation(addressData);
            }
        }
    }
}
