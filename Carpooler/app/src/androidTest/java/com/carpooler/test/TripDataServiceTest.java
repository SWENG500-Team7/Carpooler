package com.carpooler.test;

import android.os.RemoteException;

import com.carpooler.dao.FindTripQuery;
import com.carpooler.dao.TripDataService;
import com.carpooler.dao.dto.AddressData;
import com.carpooler.dao.dto.GeoPointData;
import com.carpooler.dao.dto.TripData;
import com.carpooler.trips.TripStatus;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by raymond on 6/20/15.
 */
public class TripDataServiceTest extends DatabaseServiceTest {
    private TripDataService service = new TripDataService();

    public void testPutMapping() throws RemoteException, InterruptedException {
        service.putMapping(conn);
        checkResponse();
    }

    public void testCreateTrip() throws RemoteException, InterruptedException {
        TripData tripData = new TripData();
        tripData.setStatus(TripStatus.OPEN);
        AddressData startLocation = new AddressData();
        GeoPointData startGeo = getStartGeoPointData();
        startLocation.setLocation(startGeo);
        startLocation.setStreetAddress("123 Main St");
        startLocation.setZip("18360");
        tripData.setStartLocation(startLocation);
        tripData.setStartTime(new Date());
        tripData.setEndTime(getEndDate(0));
        GeoPointData endGeo = getEndGeoPointData();
        AddressData endLocation = new AddressData();
        endLocation.setStreetAddress("20 main st warren nj");
        endLocation.setLocation(endGeo);
        endLocation.setZip("07059");
        tripData.setEndLocation(endLocation);
        service.createTrip(tripData, conn);
        checkResponse();
    }

    public void testFindAvailableTrips() throws RemoteException, InterruptedException {
        FindTripQuery query = new FindTripQuery();
        query.setStartTime(new Date());
        query.setEndTime(getEndDate(5));
        query.setTimeRange(20);
        GeoPointData startPoint = getStartGeoPointData();
        adjustGeoPoint(startPoint, .005);
        GeoPointData endPoint = getEndGeoPointData();
        adjustGeoPoint(endPoint, .005);
        query.setStartPoint(startPoint);
        query.setEndPoint(endPoint);
        query.setDistance(20);

        service.findAvailableTrips(query, conn);
        checkResponse();

    }

    private void adjustGeoPoint(GeoPointData data, double adjustment) {
        data.setLat(data.getLat() + adjustment);
        data.setLon(data.getLon() + adjustment);
    }

    private Date getEndDate(int minutesAdjust) {
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.HOUR_OF_DAY, 8);
        endDate.add(Calendar.MINUTE, minutesAdjust);
        return endDate.getTime();
    }

    private GeoPointData getStartGeoPointData() {
        GeoPointData startGeo = new GeoPointData();
        startGeo.setLat(40.990127);
        startGeo.setLon(-75.187720);
        return startGeo;
    }

    private GeoPointData getEndGeoPointData() {
        GeoPointData endGeo = new GeoPointData();
        endGeo.setLat(40.557280);
        endGeo.setLon(-74.527352);
        return endGeo;
    }
}
