package com.carpooler.trips;

import com.carpooler.users.Address;

import java.util.Collection;

/**
 * Created by Aidos on 07.06.2015.
 */
public interface TripService {
    public Collection<Trip> findTrips (Address from, Address to);
}
