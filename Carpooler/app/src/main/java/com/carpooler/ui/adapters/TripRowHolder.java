package com.carpooler.ui.adapters;

import android.os.RemoteException;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.carpooler.R;
import com.carpooler.trips.Trip;
import com.carpooler.trips.TripStatus;
import com.carpooler.trips.UserLoader;
import com.carpooler.ui.activities.TripDetailCallback;
import com.carpooler.users.Address;
import com.carpooler.users.User;

/**
 * Created by raymond on 7/12/15.
 */
public class TripRowHolder extends RecyclerView.ViewHolder {
    private ImageView hostImage;
    private TextView startTime;
    private TextView openSeats;
    private TextView startStreet;
    private TextView startCity;
    private TextView startState;
    private TextView endStreet;
    private TextView endCity;
    private TextView endState;
    private Button payButton;
    private final TripDetailCallback callback;
    private String tripId;

    public TripRowHolder(View itemView, TripDetailCallback callback) {
        super(itemView);
        this.callback = callback;
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
                TripRowHolder.this.callback.onUserCompleteTrip(tripId);
            }
        });
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TripRowHolder.this.callback.onTripSelected(tripId);
            }
        });
    }

    public void loadData(Trip data) {
        data.loadUserData(new UserLoader.Callback() {
            @Override
            public void loadData(User user) {
                try {
                    user.loadUserImage(hostImage, 150);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        startTime.setText(data.getStartTime().toString());
        openSeats.setText(Integer.toString(data.getOpenSeats()));
        Address startAddress = data.getStartLocation();
        Address endAddress = data.getEndLocation();
        if (startAddress != null) {
            startStreet.setText(startAddress.getStreetNumber());
            startCity.setText(startAddress.getCity());
            startState.setText(startAddress.getState());
        }

        if (endAddress != null) {
            endStreet.setText(endAddress.getStreetNumber());
            endCity.setText(endAddress.getCity());
            endState.setText(endAddress.getState());
        }

        if (data.getStatus().equals(TripStatus.COMPLETED) /*&& TODO THIS IS NEEDED ONCE ADD TRIP IS FIXED
                (data.getHost().getGoogleId() != callback.getUser().getGoogleId())*/) {
            payButton.setVisibility(View.VISIBLE);
        }
        tripId = data.getTripId();
    }

}
