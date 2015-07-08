package com.carpooler.ui.activities;

import android.widget.DatePicker;

import java.util.Date;

/**
 * Created by raymond on 7/1/15.
 */
public interface TripSearchCallback extends ServiceActivityCallback{
    public void search(double startLon, double startLat,
                       double endLon, double endLat,
                       Date startDate,
                       int searchDistance,
                       int timeRangeMinutes);

}
