package com.carpooler.dao.dto;

/**
 * Created by raymond on 6/20/15.
 */
public class GeoPointData {
    protected static final String MAPPING=
            "{\"type\":\"geo_point\"}";
    private double lon;
    private double lat;

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}
