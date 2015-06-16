package com.carpooler.users;

import com.carpooler.GeoPoint;

/**
 * Created by raymond on 6/6/15.
 */
public class Address {
    private GeoPoint location;
    private String  streetAddress;
    private String zip;

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
}
