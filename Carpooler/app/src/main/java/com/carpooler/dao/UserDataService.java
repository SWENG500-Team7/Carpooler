package com.carpooler.dao;

import android.os.RemoteException;

import com.carpooler.dao.dto.DatabaseObject;
import com.carpooler.dao.dto.UserData;

/**
 * Created by raymond on 6/13/15.
 */
public class UserDataService {
    public void getUserData(String id, DatabaseService.Connection connection) throws RemoteException {
        GetRequest request = new GetRequest(id,UserData.class);
        connection.get(request);
    }

    public void createUser(UserData data,DatabaseService.Connection connection) throws RemoteException {
        connection.create(data);
    }

    public void putMapping(DatabaseService.Connection connection) throws RemoteException {
        connection.putMapping(UserData.class);
    }
}
