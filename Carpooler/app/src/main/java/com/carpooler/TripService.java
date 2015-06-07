package com.carpooler;

import java.util.Collection;

/**
 * Created by Aidos on 07.06.2015.
 */
public interface TripService {
    public Collection<Trip> findTrips (Address from, Address to);
}
