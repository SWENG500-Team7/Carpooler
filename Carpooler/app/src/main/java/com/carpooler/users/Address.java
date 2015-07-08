package com.carpooler.users;

import android.util.Log;

import com.carpooler.dao.dto.AddressData;

/**
 * Created by raymond on 6/6/15.
 */
public class Address {
    private final AddressData addressData;

    public Address(AddressData addressData) {
        this.addressData = addressData;
    }

    public double getLon(){
        return addressData.getLocation().getLon();
    }
    public double getLat(){
        return addressData.getLocation().getLat();
    }
    public void setLon(double lon){
        addressData.getLocation().setLon(lon);
    }
    public void setLat(double lat){
        addressData.getLocation().setLat(lat);
    }
    public String getStreetNumber() {
        return addressData.getStreetNumber();
    }

    public void setStreetNumber(String streetNumber) {
        addressData.setStreetNumber(streetNumber);
    }

    public String getZip() {
        return addressData.getZip();
    }

    public void setZip(String zip) {
        addressData.setZip(zip);
    }

    public String getStreet(){
        return addressData.getStreet();
    }
    public void setStreet(String street){
        addressData.setStreet(street);
    }

    public String getCity(){
        return addressData.getCity();
    }
    public void setCity(String city){
        addressData.setCity(city);
    }
    public String getState(){
        return addressData.getState();
    }
    public void setState(String state){
        addressData.setState(state);
    }
}
