package com.carpooler.test;

import android.os.RemoteException;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.SmallTest;

import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.GetRequest;
import com.carpooler.dao.UserDataService;
import com.carpooler.dao.dto.UserData;

import java.util.concurrent.TimeUnit;

/**
 * Created by raymond on 6/14/15.
 */
public class UserDataServiceTest extends DatabaseServiceTest {
    private UserDataService userDataService = new UserDataService();

    public static final String TEST_ID ="testid";

    public void testCreateUser() throws RemoteException, InterruptedException {
        UserData data = new UserData();
        data.setUserId(TEST_ID);
        userDataService.createUser(data, conn);
        checkResponse();
    }

    public void testGetUser() throws InterruptedException, RemoteException {
        userDataService.getUserData(TEST_ID, conn);
        checkResponse();
    }

    public void testPutMapping() throws RemoteException, InterruptedException {
        userDataService.putMapping(conn);
        checkResponse();
    }
}
