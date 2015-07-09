package com.carpooler.test;

import android.os.RemoteException;

import com.carpooler.dao.FindTripQuery;
import com.carpooler.dao.TripDataService;
import com.carpooler.dao.dto.AddressData;
import com.carpooler.dao.dto.CarpoolUserData;
import com.carpooler.dao.dto.GeoPointData;
import com.carpooler.dao.dto.TripData;
import com.carpooler.trips.TripStatus;

import java.util.Date;

/**
 * Created by raymond on 6/20/15.
 */
public class TripDataServiceTest extends DatabaseServiceTest {
    private TripDataService service;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        service = new TripDataService(conn);
    }

    public void testPutMapping() throws RemoteException, InterruptedException {
        service.putMapping(new StringResponseCallback());
        checkResponse();
    }

    public void testCreateTrip() throws RemoteException, InterruptedException {
        TripData tripData = new TripData();
        tripData.setHostId("100251564284199788464");
        tripData.setStatus(TripStatus.OPEN);
        AddressData startLocation = new AddressData();
        GeoPointData startGeo = getStartGeoPointData();
        startLocation.setLocation(startGeo);
        startLocation.setStreet("Main St");
        startLocation.setStreetNumber("123");
        startLocation.setCity("Stroudsburg");
        startLocation.setState("PA");
        startLocation.setZip("18360");
        tripData.setStartLocation(startLocation);
        tripData.setStartTime(new Date());
        GeoPointData endGeo = getEndGeoPointData();
        AddressData endLocation = new AddressData();
        endLocation.setStreet("main st");
        endLocation.setStreetNumber("20");
        endLocation.setCity("Warren");
        endLocation.setState("NJ");
        endLocation.setLocation(endGeo);
        endLocation.setZip("07059");
        tripData.setEndLocation(endLocation);
        CarpoolUserData userData = new CarpoolUserData();
        userData.setUserId("testcpuser");
        tripData.getUsers().add(userData);
        service.createTrip(tripData,new StringResponseCallback());
        checkResponse();
    }

    public void testFindAvailableTrips() throws RemoteException, InterruptedException {
        FindTripQuery query = new FindTripQuery();
        query.setStartTime(new Date());
        query.setTimeRange(200);
        GeoPointData startPoint = getStartGeoPointData();
        adjustGeoPoint(startPoint, .005);
        GeoPointData endPoint = getEndGeoPointData();
        adjustGeoPoint(endPoint, .005);
        query.setStartPoint(startPoint);
        query.setEndPoint(endPoint);
        query.setDistance(20);

        service.findAvailableTrips(query, new QueryResponseCallback<TripData>());
        checkResponse();

    }

    public void testFindTripsByHostIdAndStatus() throws RemoteException, InterruptedException {
        service.findTripsByHostIdAndStatus("testuser", TripStatus.OPEN, new QueryResponseCallback<TripData>());
        checkResponse();
    }

    public void testFindTripsByUserIdAndStatus() throws RemoteException, InterruptedException {
        service.findTripsByUserIdAndStatus("testcpuser", TripStatus.OPEN, new QueryResponseCallback<TripData>());
        checkResponse();
    }

    private void adjustGeoPoint(GeoPointData data, double adjustment) {
        data.setLat(data.getLat() + adjustment);
        data.setLon(data.getLon() + adjustment);
    }

    private GeoPointData getStartGeoPointData() {
        GeoPointData startGeo = new GeoPointData();
        startGeo.setLat(40.990127);
        startGeo.setLon(-75.187720);
        return startGeo;
    }

    private GeoPointData getEndGeoPointData() {
        GeoPointData endGeo = new GeoPointData();
        endGeo.setLat(40.9250315);
        endGeo.setLon(-74.3199495);
        return endGeo;
    }
}
