package com.carpooler.trips;

import com.carpooler.dao.dto.TripData;
import com.carpooler.ui.activities.ServiceActivityCallback;

import java.util.List;

/**
 * Created by raymond on 7/4/15.
 */
public class TripSearchResults {
    private final List<TripData> tripDatas;
    private final ServiceActivityCallback serviceActivityCallback;

    public TripSearchResults(List<TripData> tripDatas, ServiceActivityCallback serviceActivityCallback) {
        this.tripDatas = tripDatas;
        this.serviceActivityCallback = serviceActivityCallback;
    }

    public int size(){
        return tripDatas==null?0:tripDatas.size();
    }

    public Trip get(int index){
        return new Trip(tripDatas.get(index),serviceActivityCallback);
    }

}
