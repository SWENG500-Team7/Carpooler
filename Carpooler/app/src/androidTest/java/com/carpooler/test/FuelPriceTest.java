package com.carpooler.test;

import com.carpooler.GeoPoint;
import com.carpooler.trips.FuelPrice;

import junit.framework.TestCase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jcsax on 6/14/15.
 */
public class FuelPriceTest extends TestCase {

    private FuelPrice fuelPrice = new FuelPrice();
    private static final String JSON = "{\"status\":{\"error\":\"NO\",\"code\":200,\"description\":\"none\",\"message\":\"Request ok\"},\"geoLocation\":{\"country_short\":\"US\",\"address\":\"Castro Street\",\"lat\":\"37.391025\",\"lng\":\"-122.080143\",\"country_long\":\"United States\",\"region_short\":\"CA\",\"region_long\":\"CA\",\"city_long\":\"Mountain View\"},\"stations\":[{\"country\":\"United States\",\"zip\":\"94040\",\"reg_price\":\"3.71\",\"mid_price\":\"3.85\",\"pre_price\":\"4.05\",\"diesel_price\":\"N\\/A\",\"reg_date\":\"3 years ago\",\"mid_date\":\"3 years ago\",\"pre_date\":\"3 years ago\",\"diesel_date\":\"3 years ago\",\"address\":\"101 E El Camino Real\",\"diesel\":\"1\",\"id\":\"99498\",\"lat\":\"37.380180\",\"lng\":\"-122.072777\",\"station\":\"Citgo\",\"region\":\"California\",\"city\":null,\"distance\":\"0.9 miles\"},{\"country\":\"United States\",\"zip\":\"94040\",\"reg_price\":\"3.75\",\"mid_price\":\"3.85\",\"pre_price\":\"3.95\",\"diesel_price\":\"4.41\",\"reg_date\":\"3 years ago\",\"mid_date\":\"3 years ago\",\"pre_date\":\"3 years ago\",\"diesel_date\":\"3 years ago\",\"address\":\"59 W El Camino Real\",\"diesel\":\"1\",\"id\":\"99500\",\"lat\":\"37.380989\",\"lng\":\"-122.074463\",\"station\":\"Marathon\",\"region\":\"California\",\"city\":null,\"distance\":\"0.8 miles\"},{\"country\":\"United States\",\"zip\":\"94040\",\"reg_price\":\"3.79\",\"mid_price\":\"3.89\",\"pre_price\":\"3.99\",\"diesel_price\":\"N\\/A\",\"reg_date\":\"3 years ago\",\"mid_date\":\"3 years ago\",\"pre_date\":\"3 years ago\",\"diesel_date\":\"3 years ago\",\"address\":\"1288 W El Camino Real\",\"diesel\":\"1\",\"id\":\"99494\",\"lat\":\"37.388149\",\"lng\":\"-122.088570\",\"station\":\"Sunoco\",\"region\":\"California\",\"city\":null,\"distance\":\"0.5 miles\"},{\"country\":\"United States\",\"zip\":\"94043\",\"reg_price\":\"3.83\",\"mid_price\":\"3.93\",\"pre_price\":\"4.03\",\"diesel_price\":\"3.89\",\"reg_date\":\"3 years ago\",\"mid_date\":\"3 years ago\",\"pre_date\":\"3 years ago\",\"diesel_date\":\"3 years ago\",\"address\":\"495 Moffett Blvd\",\"diesel\":\"1\",\"id\":\"99514\",\"lat\":\"37.400108\",\"lng\":\"-122.073372\",\"station\":\"Marathon\",\"region\":\"California\",\"city\":null,\"distance\":\"0.7 miles\"},{\"country\":\"United States\",\"zip\":\"94040\",\"reg_price\":\"3.87\",\"mid_price\":\"3.95\",\"pre_price\":\"4.03\",\"diesel_price\":\"N\\/A\",\"reg_date\":\"3 years ago\",\"mid_date\":\"3 years ago\",\"pre_date\":\"3 years ago\",\"diesel_date\":\"3 years ago\",\"address\":\"45 W El Camino Real\",\"diesel\":\"1\",\"id\":\"99496\",\"lat\":\"37.380508\",\"lng\":\"-122.073540\",\"station\":null,\"region\":\"California\",\"city\":null,\"distance\":\"0.8 miles\"},{\"country\":\"United States\",\"zip\":\"94040\",\"reg_price\":\"3.89\",\"mid_price\":\"N\\/A\",\"pre_price\":\"4.09\",\"diesel_price\":\"N\\/A\",\"reg_date\":\"3 years ago\",\"mid_date\":\"3 years ago\",\"pre_date\":\"3 years ago\",\"diesel_date\":\"3 years ago\",\"address\":\"1010 El Monte Ave\",\"diesel\":\"1\",\"id\":\"99497\",\"lat\":\"37.390400\",\"lng\":\"-122.095909\",\"station\":\"Xtramart\",\"region\":\"California\",\"city\":null,\"distance\":\"0.9 miles\"},{\"country\":\"United States\",\"zip\":\"94040\",\"reg_price\":\"3.93\",\"mid_price\":\"4.19\",\"pre_price\":\"N\\/A\",\"diesel_price\":\"N\\/A\",\"reg_date\":\"3 years ago\",\"mid_date\":\"3 years ago\",\"pre_date\":\"3 years ago\",\"diesel_date\":\"3 years ago\",\"address\":\"1220 Grant Rd\",\"diesel\":\"1\",\"id\":\"99501\",\"lat\":\"37.377949\",\"lng\":\"-122.074982\",\"station\":\"Sunoco\",\"region\":\"California\",\"city\":null,\"distance\":\"0.9 miles\"}]}";
    private static final String ARRAY_KEY = "stations";
    private static final String ELEMENT_KEY = "reg_price";

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {

    }

    public void testGetFuelUnitCost() {
        GeoPoint location = new GeoPoint(-122.080143, 37.391025);
        double unit_cost = fuelPrice.getFuelUnitCost(location);
        assertTrue(unit_cost > 0.0);
    }

    public void testRequestFuelStations() {
        GeoPoint location = new GeoPoint(-122.080143, 37.391025);
        String response = fuelPrice.requestFuelStations(location);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assertNotNull(jsonObject);
    }

    public void testConvertJSONStringToPriceList() {
        List<Double> prices = fuelPrice.convertJSONStringToPriceList(JSON, ARRAY_KEY, ELEMENT_KEY);
        assertNotNull(prices);
        assertEquals(7, prices.size());
    }

    public void testComputePriceAverage() {
        List<Double> prices = new ArrayList<Double>();
        prices.add(3.71);
        prices.add(3.75);
        prices.add(3.79);
        double avg = (3.71+3.75+3.79)/3;
        double average = fuelPrice.computePriceAverage(prices);
        assertEquals(avg, average);
    }

}
