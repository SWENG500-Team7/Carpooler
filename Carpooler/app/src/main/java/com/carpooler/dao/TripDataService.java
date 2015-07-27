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
    private static final String HOST_OR_USER_STATUS_QUERY =
    "{"
        + "\"filter\" : {"
            +"\"and\" : ["
                +"{\"term\": {\"status\": \"%2$s\"}},"
                +"{"
                    +"\"or\" :["
                        +"{\"term\": {\"hostId\": \"%1$s\"}},"
                        +"{"
                            +"\"nested\": {"
                                +"\"path\": \"users\","
                                + "\"filter\" : {"
                                    + "\"term\": {\"users.userId\": \"%1$s\"}"
                                +"}"
                            + "}"
                        + "}"
                    +"]"
                + "}"
            +"]"
        +"}"
    +"}";

    public TripDataService(DatabaseService.Connection connection) {
        this.connection = connection;
    }

    public void getTripData(String id, DatabaseService.GetCallback<TripData> callback) throws RemoteException {
        IdRequest request = new IdRequest(id, TripData.class);
        connection.get(request,callback);
    }

    public void createTrip(TripData data, DatabaseService.IndexCallback callback) throws RemoteException {
        connection.create(data, callback);
    }

    public void putMapping(DatabaseService.PutMappingCallback callback) throws RemoteException {
        connection.putMapping(TripData.class, callback);
    }

    public void findAvailableTrips(FindTripQuery query, DatabaseService.QueryHitsCallback<TripData> callback) throws RemoteException {
        QueryRequest<TripData> queryRequest = new QueryRequest<>(query.toJson(),TripData.class);
        connection.queryHits(queryRequest, callback);
    }

    public void findTripsByHostIdAndStatus(String hostId, TripStatus tripStatus, DatabaseService.QueryCallback<TripData> callback) throws RemoteException {
        String json = String.format(HOST_ID_STATUS_QUERY,hostId,tripStatus);
        QueryRequest<TripData> queryRequest = new QueryRequest<>(json,TripData.class);
        connection.query(queryRequest, callback);
    }
    public void findTripsByUserIdAndStatus(String userId, TripStatus tripStatus, DatabaseService.QueryCallback<TripData> callback) throws RemoteException {
        String json = String.format(USER_ID_STATUS_QUERY,userId,tripStatus);
        QueryRequest<TripData> queryRequest = new QueryRequest<>(json,TripData.class);
        connection.query(queryRequest,callback);
    }
    public void findTripsByHostOrUserIdAndStatus(String userId, TripStatus tripStatus, DatabaseService.QueryCallback<TripData> callback) throws RemoteException {
        String json = String.format(HOST_OR_USER_STATUS_QUERY,userId,tripStatus);
        QueryRequest<TripData> queryRequest = new QueryRequest<>(json,TripData.class);
        connection.query(queryRequest,callback);
    }
}
