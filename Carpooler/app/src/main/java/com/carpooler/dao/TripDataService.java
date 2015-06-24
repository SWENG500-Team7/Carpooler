package com.carpooler.dao;

import android.os.RemoteException;

import com.carpooler.dao.dto.TripData;
import com.carpooler.trips.TripStatus;

/**
 * Created by raymond on 6/20/15.
 */
public class TripDataService {
    private DatabaseService.Connection connection;

    private static final String HOST_ID_STATUS_QUERY =
    "{"
        + "\"filter\" : {"
            +"\"and\" : ["
                +"{\"term\": {\"hostId\": \"%1$s\"}},"
                +"{\"term\": {\"status\": \"%2$s\"}}"
            +"]"
        +"}"
    +"}";
    private static final String USER_ID_STATUS_QUERY =
    "{"
        + "\"filter\" : {"
            +"\"and\" : ["
                +"{"
                    +"\"nested\": {"
                        +"\"path\": \"users\","
                        + "\"filter\" : {"
                            + "\"term\": {\"users.userId\": \"%1$s\"}"
                        +"}"
                    + "}"
                + "},"
                +"{\"term\": {\"status\": \"%2$s\"}}"
            +"]"
        +"}"
    +"}";
    public TripDataService(DatabaseService.Connection connection) {
        this.connection = connection;
    }

    public void getTripData(String id, int callbackId) throws RemoteException {
        IdRequest request = new IdRequest(id, TripData.class);
        connection.get(request,callbackId);
    }

    public void createTrip(TripData data, int callbackId) throws RemoteException {
        connection.create(data,callbackId);
    }

    public void putMapping(int callbackId) throws RemoteException {
        connection.putMapping(TripData.class,callbackId);
    }

    public void findAvailableTrips(FindTripQuery query, int callbackId) throws RemoteException {
        QueryRequest<TripData> queryRequest = new QueryRequest<>(query.toJson(),TripData.class);
        connection.query(queryRequest,callbackId);
    }

    public void findTripsByHostIdAndStatus(String hostId, TripStatus tripStatus, int callbackId) throws RemoteException {
        String json = String.format(HOST_ID_STATUS_QUERY,hostId,tripStatus);
        QueryRequest<TripData> queryRequest = new QueryRequest<>(json,TripData.class);
        connection.query(queryRequest,callbackId);
    }
    public void findTripsByUserIdAndStatus(String userId, TripStatus tripStatus, int callbackId) throws RemoteException {
        String json = String.format(USER_ID_STATUS_QUERY,userId,tripStatus);
        QueryRequest<TripData> queryRequest = new QueryRequest<>(json,TripData.class);
        connection.query(queryRequest,callbackId);
    }
}
