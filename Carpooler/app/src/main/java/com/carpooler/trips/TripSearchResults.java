package com.carpooler.trips;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.RemoteException;
import android.widget.Toast;

import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.dto.TripData;
import com.carpooler.dao.dto.UserData;
import com.carpooler.ui.activities.ServiceActivityCallback;
import com.carpooler.users.Rating;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Date;

import io.searchbox.core.SearchResult;

/**
 * Created by raymond on 7/4/15.
 */
public class TripSearchResults {
    private final List<TripData> tripDatas;
    private final List<SearchResult.Hit<TripData, Void>> hitData;
    private final ServiceActivityCallback serviceActivityCallback;
    private final Mode mode;
    private UserData ud;

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

    public void sortByOpenSeats(){
        Collections.sort(hitData, new SeatsComparator());
    }

    public void sortByStartTime(){
        Collections.sort(hitData, new StartTimeComparator());
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

    private class UserDataCallback implements DatabaseService.GetCallback<UserData> {
        public void doError(String message) {

        }
        public void doException(Exception exception){
            exception.printStackTrace();
        }

        public void doSuccess(UserData data){
            ud = data;
        }
    }

    private class StartDistanceComparator implements Comparator<SearchResult.Hit<TripData,Void>>{

        @Override
        public int compare(SearchResult.Hit<TripData, Void> lhs, SearchResult.Hit<TripData, Void> rhs) {
            String s1 = lhs.sort.get(0);
            String s2 = rhs.sort.get(0);
            return s1.compareTo(s2);
        }
    }

    private class SeatsComparator implements Comparator<SearchResult.Hit<TripData, Void>> {

        @Override
        public int compare(SearchResult.Hit<TripData, Void> lhs, SearchResult.Hit<TripData, Void> rhs) {
            return Integer.compare(lhs.source.getOpenSeats(), rhs.source.getOpenSeats());
        }
    }

    private class StartTimeComparator implements Comparator<SearchResult.Hit<TripData, Void>> {

        @Override
        public int compare(SearchResult.Hit<TripData, Void> lhs, SearchResult.Hit<TripData, Void> rhs) {
            return lhs.source.getStartTime().compareTo(rhs.source.getStartTime());
        }
    }

    private class RatingComparator implements Comparator<SearchResult.Hit<TripData, Void>> {
        private Rating lhsRating;
        private Rating rhsRating;

        @Override
        public int compare(SearchResult.Hit<TripData, Void> lhs, SearchResult.Hit<TripData, Void> rhs) {
            try {
                serviceActivityCallback.getUserDataService().getUserData(lhs.source.getHostId(), new UserDataCallback());
            } catch (RemoteException e) {
                    e.printStackTrace();
            }

            lhsRating = ud.getAverageRating();

            try {
                serviceActivityCallback.getUserDataService().getUserData(rhs.source.getHostId(), new UserDataCallback());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            rhsRating = ud.getAverageRating();

            return Integer.compare(lhsRating.ordinal(), rhsRating.ordinal());
        }
    }
}
