package com.carpooler.dao;

import android.os.RemoteException;

import com.carpooler.dao.dto.TripData;

/**
 * Created by raymond on 6/20/15.
 */
public class TripDataService {
    public void getTripData(String id, DatabaseService.Connection connection) throws RemoteException {
        IdRequest request = new IdRequest(id, TripData.class);
        connection.get(request);
    }

    public void createTrip(TripData data, DatabaseService.Connection connection) throws RemoteException {
        connection.create(data);
    }

    public void putMapping(DatabaseService.Connection connection) throws RemoteException {
        connection.putMapping(TripData.class);
    }

    public void findAvailableTrips(FindTripQuery query, DatabaseService.Connection connection) throws RemoteException {
        QueryRequest<TripData> queryRequest = new QueryRequest<>(query.toJson(),TripData.class);
        connection.query(queryRequest);
    }
}
