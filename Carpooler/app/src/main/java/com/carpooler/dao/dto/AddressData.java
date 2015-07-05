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
                    + "\"streetNumber\":{\"type\":\"string\", \"index\":\"not_analyzed\"},"
                    + "\"street\":{\"type\":\"string\", \"index\":\"not_analyzed\"},"
                    + "\"city\":{\"type\":\"string\", \"index\":\"not_analyzed\"},"
                    + "\"state\":{\"type\":\"string\", \"index\":\"not_analyzed\"},"
                    + "\"zip\":{\"type\":\"string\", \"index\":\"not_analyzed\"}"
                + "}"
            + "}";
    private GeoPointData location;
    private String streetNumber;
    private String street;
    private String city;
    private String state;
    private String zip;

    public GeoPointData getLocation() {
        return location;
    }

    public void setLocation(GeoPointData location) {
        this.location = location;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
