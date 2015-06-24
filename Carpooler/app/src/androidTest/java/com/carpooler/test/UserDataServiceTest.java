package com.carpooler.test;

import android.os.RemoteException;

import com.carpooler.dao.UserDataService;
import com.carpooler.dao.dto.UserData;
import com.carpooler.dao.dto.VehicleData;

/**
 * Created by raymond on 6/14/15.
 */
public class UserDataServiceTest extends DatabaseServiceTest {
    private UserDataService userDataService;

    public static final String TEST_ID ="testid";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        userDataService = new UserDataService(conn);
    }

    public void testCreateUser() throws RemoteException, InterruptedException {
        UserData data = new UserData();
        data.setUserId(TEST_ID);
        VehicleData vehicle = new VehicleData();
        vehicle.setPlateNumber("ABCDE");
        data.getVehicle().add(vehicle);
        userDataService.createUser(data,100);
        checkResponse();
    }

    public void testGetUser() throws InterruptedException, RemoteException {
        userDataService.getUserData(TEST_ID,100);
        checkResponse();
    }

    public void testPutMapping() throws RemoteException, InterruptedException {
        userDataService.putMapping(100);
        checkResponse();
    }
}
