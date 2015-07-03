package com.carpooler.ui.activities;

import com.carpooler.dao.TripDataService;
import com.carpooler.dao.UserDataService;
import com.carpooler.users.User;

/**
 * Created by raymond on 6/24/15.
 */
public interface ServiceActivityCallback {
    public TripDataService getTripDataService();
    public UserDataService getUserDataService();
    public User getUser();
}