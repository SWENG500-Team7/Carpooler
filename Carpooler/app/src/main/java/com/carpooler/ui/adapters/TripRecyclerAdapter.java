package com.carpooler.ui.adapters;

import android.os.RemoteException;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.carpooler.R;
import com.carpooler.trips.Trip;
import com.carpooler.trips.TripSearchResults;
import com.carpooler.trips.TripStatus;
import com.carpooler.ui.activities.TripDetailCallback;
import com.carpooler.users.Address;

/**
 * Created by raymond on 6/21/15.
 */
public class TripRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final TripSearchResults tripSearchResults;
    private final TripDetailCallback callback;
    private static final int EMPTY_VIEW = 10;
    public TripRecyclerAdapter(TripSearchResults tripSearchResults, TripDetailCallback callback) {
        this.tripSearchResults = tripSearchResults;
        this.callback = callback;
    }

    @Override
    public int getItemViewType(int position) {
        if (tripSearchResults.size()==0){
            return EMPTY_VIEW;
        }else {
            return super.getItemViewType(position);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (i==EMPTY_VIEW){
            View v =LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.empty_trip_row, null);
            EmptyViewHolder emptyViewHolder = new EmptyViewHolder(v);
            return emptyViewHolder;
        }else {
            View v =LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.trip_list_row, null);
            TripRowHolder tripRowHolder = new TripRowHolder(v);
            return tripRowHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof TripRowHolder) {
            TripRowHolder tripRowHolder = (TripRowHolder) viewHolder;
            Trip data = tripSearchResults.get(i);
            try {
                data.loadHostImage(tripRowHolder.hostImage, 150);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            tripRowHolder.startTime.setText(data.getStartTime().toString());
            tripRowHolder.openSeats.setText(Integer.toString(data.getOpenSeats()));
            Address startAddress = data.getStartLocation();
            Address endAddress = data.getEndLocation();
            if (startAddress!=null) {
                tripRowHolder.startStreet.setText(startAddress.getStreetNumber());
                tripRowHolder.startCity.setText(startAddress.getCity());
                tripRowHolder.startState.setText(startAddress.getState());
            }

            if (endAddress!=null) {
                tripRowHolder.endStreet.setText(endAddress.getStreetNumber());
                tripRowHolder.endCity.setText(endAddress.getCity());
                tripRowHolder.endState.setText(endAddress.getState());
            }

            if (data.getStatus().equals(TripStatus.COMPLETED)) {
                tripRowHolder.payButton.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return tripSearchResults.size()>0?tripSearchResults.size():1;
    }

    class TripRowHolder extends RecyclerView.ViewHolder {
        ImageView hostImage;
        TextView startTime;
        TextView openSeats;
        TextView startStreet;
        TextView startCity;
        TextView startState;
        TextView endStreet;
        TextView endCity;
        TextView endState;
        Button payButton;

        public TripRowHolder(View itemView) {
            super(itemView);
            hostImage = (ImageView) itemView.findViewById(R.id.hostImage);
            startTime = (TextView) itemView.findViewById(R.id.startTime);
            openSeats = (TextView) itemView.findViewById(R.id.openSeats);
            startStreet = (TextView) itemView.findViewById(R.id.startStreet);
            startCity = (TextView) itemView.findViewById(R.id.startCity);
            startState = (TextView) itemView.findViewById(R.id.startState);
            endStreet = (TextView) itemView.findViewById(R.id.endStreet);
            endCity = (TextView) itemView.findViewById(R.id.endCity);
            endState = (TextView) itemView.findViewById(R.id.endState);
            payButton = (Button) itemView.findViewById(R.id.payButton);
            payButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Trip data = tripSearchResults.get(pos);
                    callback.onUserCompleteTrip(data.getTripId());
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    Trip data = tripSearchResults.get(pos);
                    callback.onTripSelected(data.getTripId());
                }
            });
        }

    }

    class EmptyViewHolder extends RecyclerView.ViewHolder{

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
