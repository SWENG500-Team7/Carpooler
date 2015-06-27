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

    public void getUserData(String id, DatabaseService.GetCallback<UserData> callback) throws RemoteException {
        IdRequest request = new IdRequest(id,UserData.class);
        connection.get(request,callback);
    }

    public void createUser(UserData data, DatabaseService.IndexCallback callback) throws RemoteException {
        connection.create(data,callback);
    }

    public void putMapping(DatabaseService.PutMappingCallback callback) throws RemoteException {
        connection.putMapping(UserData.class,callback);
    }
}
