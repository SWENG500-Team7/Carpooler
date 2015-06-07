package com.carpooler;

/**
 * Created by raymond on 6/6/15.
 */
public class GeoPoint {
    private double longititude;
    private double latitude;

    public GeoPoint(double longititude, double latitude) {
        this.longititude = longititude;
        this.latitude = latitude;
    }

    public double getLongititude() {
        return longititude;
    }


    public double getLatitude() {
        return latitude;
    }

}
