package com.carpooler.dao.dto;

/**
 * Created by raymond on 6/20/15.
 */
public class AddressData {
    protected static final String MAPPING=
            "{"
                + "\"type\":\"nested\","
                + "\"properties\":{"
                    + "\"location\":" + GeoPointData.MAPPING + ","
                    + "\"streetAddress\":{\"type\":\"string\"},"
                    + "\"zip\":{\"type\":\"string\"}"
                + "}"
            + "}";
    private GeoPointData location;
    private String streetAddress;
    private String zip;

    public GeoPointData getLocation() {
        return location;
    }

    public void setLocation(GeoPointData location) {
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
