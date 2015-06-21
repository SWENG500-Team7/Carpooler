package com.carpooler.dao;

import android.os.RemoteException;

import com.carpooler.dao.dto.UserData;

/**
 * Created by raymond on 6/13/15.
 */
public class UserDataService {
    private DatabaseService.Connection connection;

    public UserDataService(DatabaseService.Connection connection) {
        this.connection = connection;
    }

    public void getUserData(String id) throws RemoteException {
        IdRequest request = new IdRequest(id,UserData.class);
        connection.get(request);
    }

    public void createUser(UserData data) throws RemoteException {
        connection.create(data);
    }

    public void putMapping() throws RemoteException {
        connection.putMapping(UserData.class);
    }
}
