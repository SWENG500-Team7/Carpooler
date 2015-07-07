package com.carpooler.users;

import android.os.RemoteException;

import com.carpooler.AbstractServiceActivityMockTest;
import com.carpooler.dao.dto.CarpoolUserData;

import org.junit.Test;

/**
 * Created by raymond on 6/7/15.
 */
public class CarpoolUserTest extends AbstractServiceActivityMockTest {
    private CarpoolUser carpoolUser;

    @Override
    public void setup() throws RemoteException {
        super.setup();
        carpoolUser = new CarpoolUser(new CarpoolUserData(),callback);
    }

    @Test
    public void cancelTest() {
        carpoolUser.changeStatus(CarpoolUserStatus.PENDING);
        carpoolUser.changeStatus(CarpoolUserStatus.CANCELLED);
    }

    @Test
    public void confirmPickupTest() {
        carpoolUser.changeStatus(CarpoolUserStatus.CONFIRMED_FOR_PICKUP);
        carpoolUser.confirmPickup();
    }

    @Test
    public void pickupDropOffTest() {
        carpoolUser.changeStatus(CarpoolUserStatus.CONFIRMED_FOR_PICKUP);
        carpoolUser.changeStatus(CarpoolUserStatus.PICKED_UP);
        carpoolUser.changeStatus(CarpoolUserStatus.DROPPED_OFF);
        carpoolUser.changeStatus(CarpoolUserStatus.PAID);
    }

    @Test
    public void noShowTest() {
        carpoolUser.changeStatus(CarpoolUserStatus.CONFIRMED_FOR_PICKUP);
        carpoolUser.changeStatus(CarpoolUserStatus.NO_SHOW);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cancelUnlistedTest() {
        carpoolUser.changeStatus(CarpoolUserStatus.CANCELLED);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cancelNoShowTest() {
        carpoolUser.changeStatus(CarpoolUserStatus.NO_SHOW);
        carpoolUser.changeStatus(CarpoolUserStatus.CANCELLED);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cancelPickupTest() {
        carpoolUser.changeStatus(CarpoolUserStatus.PICKED_UP);
        carpoolUser.changeStatus(CarpoolUserStatus.CANCELLED);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cancelDroppedOffTest() {
        carpoolUser.changeStatus(CarpoolUserStatus.PICKED_UP);
        carpoolUser.changeStatus(CarpoolUserStatus.DROPPED_OFF);
        carpoolUser.changeStatus(CarpoolUserStatus.CANCELLED);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cancelPaidTest() {
        carpoolUser.changeStatus(CarpoolUserStatus.CONFIRMED_FOR_PICKUP);
        carpoolUser.changeStatus(CarpoolUserStatus.PICKED_UP);
        carpoolUser.changeStatus(CarpoolUserStatus.DROPPED_OFF);
        carpoolUser.changeStatus(CarpoolUserStatus.PAID);
        carpoolUser.changeStatus(CarpoolUserStatus.CANCELLED);
    }
}
