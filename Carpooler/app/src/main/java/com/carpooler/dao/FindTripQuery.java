package com.carpooler.dao;

import com.carpooler.dao.dto.GeoPointData;
import com.carpooler.trips.TripStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Calendar;
import java.util.Date;

import io.searchbox.client.AbstractJestClient;

/**
 * Created by raymond on 6/21/15.
 */
public class FindTripQuery {
    private static final String QUERY =
"{"
    + "\"filter\" : {"
        +"\"and\" : ["
            + "{"
                + "\"nested\" : {"
                    +"\"path\" : \"startLocation\","
                        + "\"filter\" : {"
                            +"\"geo_distance\": {"
                            +"\"distance\": \"%1$s mi\","
                            +"\"startLocation.location\": %2$s"
                        +"}"
                    +"}"
                +"}"
            +"},"
            +"{"
                +"\"nested\" : {"
                    +"\"path\" : \"endLocation\","
                        +"\"filter\" : {"
                            +"\"geo_distance\": {"
                            +"\"distance\": \"%1$s mi\","
                            +"\"endLocation.location\": %3$s"
                        +"}"
                    +"}"
                +"}"
            +"},"
            +"{"
                +"\"range\" : {"
                    +"\"startTime\" :{"
                        +"\"gte\": %4$s,"
                        +"\"lte\": %5$s"
                    +"}"
                +"}"
            +"},"
            +"{"
                +"\"term\": {"
                    +"\"status\": \"%6$s\""
                +"}"
            +"}"
        +"]"
    +"}"
+"}";

    private GeoPointData startPoint;
    private GeoPointData endPoint;
    private int distance;
    private Date startTime;
    private int timeRange;
    private final Gson gson = new GsonBuilder()
            .setDateFormat(AbstractJestClient.ELASTIC_SEARCH_DATE_FORMAT)
            .create();

    public String toJson(){
        return String.format(QUERY,
                distance,
                geoJson(startPoint),
                geoJson(endPoint),
                formatDate(startTime,-timeRange),
                formatDate(startTime,timeRange),
                TripStatus.OPEN);
    }

    private String formatDate(Date date, int adjustMinutes){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MINUTE,adjustMinutes);
        Date adjustedDate = c.getTime();
        return gson.toJson(adjustedDate);
    }

    private String geoJson(GeoPointData data){
        return gson.toJson(data);
    }

    public GeoPointData getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(GeoPointData startPoint) {
        this.startPoint = startPoint;
    }

    public GeoPointData getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(GeoPointData endPoint) {
        this.endPoint = endPoint;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public int getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(int timeRange) {
        this.timeRange = timeRange;
    }
}
