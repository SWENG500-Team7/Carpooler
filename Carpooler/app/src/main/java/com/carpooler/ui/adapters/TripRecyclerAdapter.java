package com.carpooler.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carpooler.R;
import com.carpooler.trips.Trip;
import com.carpooler.trips.TripSearchResults;
import com.carpooler.ui.activities.TripDetailCallback;

/**
 * Created by raymond on 6/21/15.
 */
public class TripRecyclerAdapter extends EmptyAdapter<TripRowHolder> {
    private final TripSearchResults tripSearchResults;
    private final TripDetailCallback callback;
    public TripRecyclerAdapter(TripSearchResults tripSearchResults, TripDetailCallback callback) {
        this.tripSearchResults = tripSearchResults;
        this.callback = callback;
    }

    @Override
    protected int getRealSize() {
        return tripSearchResults.size();
    }

    @Override
    protected int getEmptyStringId() {
        return R.string.no_trip_found;
    }

    @Override
    protected TripRowHolder getRealViewHolder(ViewGroup parent, int viewType) {
        View v =LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_list_row, null);
        TripRowHolder tripRowHolder = new TripRowHolder(v, callback);
        return tripRowHolder;
    }

    @Override
    protected void onBindViewHolderReal(TripRowHolder tripRowHolder, int position) {
        Trip data = tripSearchResults.get(position);
        tripRowHolder.loadData(data);
    }

    @Override
    public int getItemCount() {
        return tripSearchResults.size()>0?tripSearchResults.size():1;
    }

}
