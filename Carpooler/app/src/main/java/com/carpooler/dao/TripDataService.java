package com.carpooler.dao;

import android.os.RemoteException;

import com.carpooler.dao.dto.TripData;

/**
 * Created by raymond on 6/20/15.
 */
public class TripDataService {
    private DatabaseService.Connection connection;

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
}
