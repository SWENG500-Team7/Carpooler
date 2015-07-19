package com.carpooler.users;

import android.os.RemoteException;

import com.carpooler.dao.dto.AddressData;
import com.carpooler.dao.dto.CarpoolUserData;
import com.carpooler.trips.AddressErrorCallback;
import com.carpooler.trips.AddressLoadCallback;
import com.carpooler.trips.UserLoader;
import com.carpooler.ui.activities.ServiceActivityCallback;

import java.util.Date;

/**
 * Created by raymond on 6/6/15.
 */
public class CarpoolUser {
    private final CarpoolUserData carpoolUserData;
    private final ServiceActivityCallback serviceActivityCallback;
    private UserLoader userLoader;
    public CarpoolUser(CarpoolUserData carpoolUserData,
                       ServiceActivityCallback serviceActivityCallback) {
        this.carpoolUserData = carpoolUserData;
        this.serviceActivityCallback=serviceActivityCallback;
        if (carpoolUserData.getUserId()==null) {
            User host = serviceActivityCallback.getUser();
            carpoolUserData.setUserId(host.getGoogleId());
        }

        userLoader = new UserLoader(serviceActivityCallback, carpoolUserData.getUserId());
    }

    public Address getPickupLocation() {
        if (carpoolUserData.getPickupLocation()==null){
            return null;
        }else {
            return new Address(carpoolUserData.getPickupLocation());
        }
    }

    public void dropOff(){
        changeStatus(CarpoolUserStatus.DROPPED_OFF);
    }

    public Address getDropoffLocation() {
        if (carpoolUserData.getDropoffLocation()==null){
            return null;
        }else {
            return new Address(carpoolUserData.getDropoffLocation());
        }
    }

    public void setPickupLocation(String searchAddress, AddressErrorCallback addressErrorCallback) throws RemoteException {
        setAddress(searchAddress,addressErrorCallback,false);
    }

    public void setDropoffLocation(String searchAddress, AddressErrorCallback addressErrorCallback) throws RemoteException {
        setAddress(searchAddress, addressErrorCallback, true);
    }

    private void setAddress(String searchAddress, AddressErrorCallback addressErrorCallback, boolean destination) throws RemoteException {
        UserAddressLoadCallback tripAddressLoadCallback = new UserAddressLoadCallback(addressErrorCallback,destination);
        serviceActivityCallback.getLocationService().getLocationFromAddressName(searchAddress, tripAddressLoadCallback);
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
        if (isAllowedNextStaus(nextStatus)){
            carpoolUserData.setStatus(nextStatus);
        }else{
            throw new IllegalArgumentException("Invalid to move from " + carpoolUserData.getStatus() + " to " + nextStatus);
        }
    }

    private boolean isAllowedNextStaus(CarpoolUserStatus carpoolUserStatus){
        return carpoolUserData.getStatus().isValidateNextState(carpoolUserStatus);
    }
    public boolean canDropoff() {
        return isAllowedNextStaus(CarpoolUserStatus.DROPPED_OFF);
    }

    public boolean canMarkNoShow() {
        return isAllowedNextStaus(CarpoolUserStatus.NO_SHOW);
    }

    public boolean canPickup() {
        return isAllowedNextStaus(CarpoolUserStatus.PICKED_UP);
    }

    public boolean canNavigatePickup() {
        return isAllowedNextStaus(CarpoolUserStatus.PICKED_UP);
    }

    public boolean canAcceptRequest() {
        return isAllowedNextStaus(CarpoolUserStatus.CONFIRMED_FOR_PICKUP);
    }

    public boolean isPaymentRequired() {
        return isAllowedNextStaus(CarpoolUserStatus.PAID);
    }

    public boolean canConfirmPickup() {
        return isAllowedNextStaus(CarpoolUserStatus.PICKED_UP);
    }

    public boolean canCancelPickup() {
        return isAllowedNextStaus(CarpoolUserStatus.CANCELLED);
    }

    public void pickup() {
        changeStatus(CarpoolUserStatus.PICKED_UP);
    }

    public void cancel() {
        changeStatus(CarpoolUserStatus.CANCELLED);
    }

    public void acceptRequest() {
        changeStatus(CarpoolUserStatus.CONFIRMED_FOR_PICKUP);
    }

    public void setPaid() {
        changeStatus(CarpoolUserStatus.PAID);
    }

    public boolean canNavigateDropoff() {
        return isAllowedNextStaus(CarpoolUserStatus.DROPPED_OFF);
    }

    public boolean canRejectRequest() {
        return isAllowedNextStaus(CarpoolUserStatus.REJECTED_FOR_PICKUP);
    }

    public void rejectRequest() {
        changeStatus(CarpoolUserStatus.REJECTED_FOR_PICKUP);
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

    public void loadUserData(UserLoader.Callback callback){
        userLoader.addCallback(callback);
    }

    public boolean isLoggedInUser(){
        return userLoader.isLoggedInUser();
    }

    public CarpoolUserData getCarpoolUserData() {
        return carpoolUserData;
    }
}
