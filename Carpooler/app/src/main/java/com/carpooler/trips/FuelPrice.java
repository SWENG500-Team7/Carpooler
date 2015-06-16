package com.carpooler.trips;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * FuelPrice handles all GET requests for local fuel stations, converts the response
 * JSON string into a list of fuel prices, and provides the average local fuel price.
 *
 * Created by jcsax on 6/13/15.
 */
public class FuelPrice {

    /**
     * Requests for local fuel stations from the input url
     * @param url - a String
     * @return a response JSON String
     */
    public String requestFuelStations(String url) {
        return null;
    }

    /**
     * Converts an InputStream to a String
     * @param inputStream
     * @return a String of the contents of the InputStream
     */
    private String convertInputStreamToString(InputStream inputStream) throws IOException{
        return null;
    }

    /**
     * Converts a JSON String to a list of fuel prices
     * @param json - a String
     * @param arraykey - a key String for the JSONArray of fuel stations,
     * i.e. "stations"
     * @param elementkey - a key String for the price in each JSONArray element,
     * i.e. "reg_price"
     * @return fuel_prices
     */
    public List<Double> convertJSONStringToPriceList(String json, String arraykey, String elementkey) {
        return null;
    }

    /**
     * Computes the average fuel price from the list of local prices
     * @param prices
     * @return a double
     */
    public double computePriceAverage(List<Double> prices) {
        return 0.0;
    }

}
