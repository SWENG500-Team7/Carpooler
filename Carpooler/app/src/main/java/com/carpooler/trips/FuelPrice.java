package com.carpooler.trips;

import android.util.Log;

import com.carpooler.GeoPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * FuelPrice handles all GET requests for local fuel stations, converts the response
 * JSON string into a list of fuel prices, and provides the average local fuel price.
 *
 * Created by jcsax on 6/13/15.
 */
public class FuelPrice {

    private static final String myGasFeedURL = "http://devapi.mygasfeed.com/stations/radius/";
    private static final String ARRAY_KEY = "stations";
    private static final String ELEMENT_KEY = "reg_price";
    private static final int CONNECTION_ATTEMPTS = 5;

    /**
     * Get the fuel unit cost around a particular location
     * @param location - a GeoPoint
     * @return a cost average
     */
    public double getFuelUnitCost(GeoPoint location) {
        String json = requestFuelStations(location);
        List<Double> prices = convertJSONStringToPriceList(json, ARRAY_KEY, ELEMENT_KEY);
        return computePriceAverage(prices);
    }

    /**
     * Requests for local fuel stations
     * @param location - a GeoPoint
     * @return a response JSON String
     */
    public String requestFuelStations(GeoPoint location) {
        String url = myGasFeedURL + location.getLatitude() + "/" + location.getLongititude() + "/1/reg/price/rfej9napna.json";
        HttpURLConnection urlConnection = null;
        InputStream in = null;
        String result = null;

        //Make multiple attempts if it initially fails
        for (int i = 0; i < CONNECTION_ATTEMPTS; i++) {
            try {
                //Make the connection and get the response
                URL requestUrl = new URL(url);
                urlConnection = (HttpURLConnection) requestUrl.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream());
                urlConnection.disconnect();

                // convert inputstream to string
                if (in != null)
                    result = convertInputStreamToString(in);

            } catch (Exception e) {
                Log.d("FuelPrice", e.getLocalizedMessage());
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            if(result != null) {
                break;
            }
        }

        return result;
    }

    /**
     * Converts an InputStream to a String
     * @param in
     * @return result a String of the contents of the InputStream
     */
    private String convertInputStreamToString(InputStream in) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        in.close();
        return result;
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
        List<Double> prices = new ArrayList<Double>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray stations = (JSONArray) jsonObject.get(arraykey);
            for (int i = 0; i < stations.length(); i++) {
                prices.add(Double.parseDouble(stations.getJSONObject(i).getString(elementkey)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return prices;
    }

    /**
     * Computes the average fuel price from the list of local prices
     * @param prices
     * @return a double
     */
    public double computePriceAverage(List<Double> prices) {
        Double sum = 0.0;
        if(!prices.isEmpty()) {
            for (Double price : prices) {
                sum += price;
            }
            return sum.doubleValue()/prices.size();
        }
        return sum;
    }

}
