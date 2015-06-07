package com.carpooler;

import org.junit.Test;

/**
 * Created by raymond on 6/7/15.
 */
public class CarpoolUserTest {

    @Test
    public void cancelTest() {
        CarpoolUser user = new CarpoolUser();
        user.changeStatus(CarpoolUserStatus.CANCELLED);
    }

    @Test
    public void pickupDropOffTest() {
        CarpoolUser user = new CarpoolUser();
        user.changeStatus(CarpoolUserStatus.PICKED_UP);
        user.changeStatus(CarpoolUserStatus.CONFIRMED_PICK_UP);
        user.changeStatus(CarpoolUserStatus.DROPPED_OFF);
        user.changeStatus(CarpoolUserStatus.PAID);
    }

    @Test
    public void noShowTest() {
        CarpoolUser user = new CarpoolUser();
        user.changeStatus(CarpoolUserStatus.NO_SHOW);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cancelNoShowTest() {
        CarpoolUser user = new CarpoolUser();
        user.changeStatus(CarpoolUserStatus.NO_SHOW);
        user.changeStatus(CarpoolUserStatus.CANCELLED);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cancelPickupTest() {
        CarpoolUser user = new CarpoolUser();
        user.changeStatus(CarpoolUserStatus.PICKED_UP);
        user.changeStatus(CarpoolUserStatus.CANCELLED);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cancelConfirmedPickupTest() {
        CarpoolUser user = new CarpoolUser();
        user.changeStatus(CarpoolUserStatus.PICKED_UP);
        user.changeStatus(CarpoolUserStatus.CONFIRMED_PICK_UP);
        user.changeStatus(CarpoolUserStatus.CANCELLED);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cancelDroppedOffTest() {
        CarpoolUser user = new CarpoolUser();
        user.changeStatus(CarpoolUserStatus.PICKED_UP);
        user.changeStatus(CarpoolUserStatus.CONFIRMED_PICK_UP);
        user.changeStatus(CarpoolUserStatus.DROPPED_OFF);
        user.changeStatus(CarpoolUserStatus.CANCELLED);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cancelPaidTest() {
        CarpoolUser user = new CarpoolUser();
        user.changeStatus(CarpoolUserStatus.PICKED_UP);
        user.changeStatus(CarpoolUserStatus.CONFIRMED_PICK_UP);
        user.changeStatus(CarpoolUserStatus.DROPPED_OFF);
        user.changeStatus(CarpoolUserStatus.PAID);
        user.changeStatus(CarpoolUserStatus.CANCELLED);
    }

    @Test
    public void confirmPickupTest() {
        CarpoolUser user = new CarpoolUser();
        user.changeStatus(CarpoolUserStatus.PICKED_UP);
        user.confirmPickup();
    }
}
