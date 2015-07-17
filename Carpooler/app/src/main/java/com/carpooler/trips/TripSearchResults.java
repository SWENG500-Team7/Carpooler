package com.carpooler.trips;

import com.carpooler.dao.dto.TripData;
import com.carpooler.ui.activities.ServiceActivityCallback;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.searchbox.core.SearchResult;

/**
 * Created by raymond on 7/4/15.
 */
public class TripSearchResults {
    private final List<TripData> tripDatas;
    private final List<SearchResult.Hit<TripData, Void>> hitData;
    private final ServiceActivityCallback serviceActivityCallback;
    private final Mode mode;

    public TripSearchResults(List<TripData> tripDatas, ServiceActivityCallback serviceActivityCallback) {
        this(tripDatas,null,serviceActivityCallback);
    }

    public TripSearchResults(List<SearchResult.Hit<TripData, Void>> hitData, ServiceActivityCallback serviceActivityCallback,boolean d) {
        this(null,hitData,serviceActivityCallback);
    }

    private TripSearchResults(List<TripData> tripDatas, List<SearchResult.Hit<TripData, Void>> hitData, ServiceActivityCallback serviceActivityCallback) {
        this.tripDatas = tripDatas;
        this.serviceActivityCallback = serviceActivityCallback;
        this.hitData = hitData;
        if (tripDatas!=null){
            mode=Mode.TRIP;
        }else{
            mode=Mode.HIT;
        }
    }
    public int size(){
        return mode.size(this);
    }

    public Trip get(int index){
        return mode.get(index, this);
    }

    public String getSearchDistanceFromStart(int index){
        return hitData==null?"":hitData.get(index).sort.get(0);
    }

    public void sortByStartDistance(){
        Collections.sort(hitData, new StartDistanceComparator());
    }
    private enum Mode{
        HIT {
            @Override
            public int size(TripSearchResults tripSearchResults) {
                return tripSearchResults.hitData==null?0:tripSearchResults.hitData.size();
            }

            @Override
            public Trip get(int index, TripSearchResults tripSearchResults) {
                return new Trip(tripSearchResults.hitData.get(index).source,tripSearchResults.serviceActivityCallback);
            }
        },
        TRIP {
            @Override
            public int size(TripSearchResults tripSearchResults) {
                return tripSearchResults.tripDatas==null?0:tripSearchResults.tripDatas.size();
            }

            @Override
            public Trip get(int index, TripSearchResults tripSearchResults) {
                return new Trip(tripSearchResults.tripDatas.get(index),tripSearchResults.serviceActivityCallback);
            }
        };
        public abstract int size(TripSearchResults tripSearchResults);
        public abstract Trip get(int index, TripSearchResults tripSearchResults);
    }

    private class StartDistanceComparator implements Comparator<SearchResult.Hit<TripData,Void>>{

        @Override
        public int compare(SearchResult.Hit<TripData, Void> lhs, SearchResult.Hit<TripData, Void> rhs) {
            String s1 = lhs.sort.get(0);
            String s2 = rhs.sort.get(0);
            return s1.compareTo(s2);
        }
    }
}
