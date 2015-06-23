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

    public void getTripData(String id) throws RemoteException {
        IdRequest request = new IdRequest(id, TripData.class);
        connection.get(request);
    }

    public void createTrip(TripData data) throws RemoteException {
        connection.create(data);
    }

    public void putMapping() throws RemoteException {
        connection.putMapping(TripData.class);
    }

    public void findAvailableTrips(FindTripQuery query) throws RemoteException {
        QueryRequest<TripData> queryRequest = new QueryRequest<>(query.toJson(),TripData.class);
        connection.query(queryRequest);
    }

    public void findTripsByHostIdAndStatus(String hostId, TripStatus tripStatus) throws RemoteException {
        String json = String.format(HOST_ID_STATUS_QUERY,hostId,tripStatus);
        QueryRequest<TripData> queryRequest = new QueryRequest<>(json,TripData.class);
        connection.query(queryRequest);
    }
    public void findTripsByUserIdAndStatus(String userId, TripStatus tripStatus) throws RemoteException {
        String json = String.format(USER_ID_STATUS_QUERY,userId,tripStatus);
        QueryRequest<TripData> queryRequest = new QueryRequest<>(json,TripData.class);
        connection.query(queryRequest);
    }
}
