package com.carpooler.ui.adapters;

import android.os.RemoteException;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.carpooler.R;
import com.carpooler.trips.Trip;
import com.carpooler.trips.UserLoader;
import com.carpooler.ui.activities.TripDetailCallback;
import com.carpooler.users.Address;
import com.carpooler.users.User;

import java.text.SimpleDateFormat;

/**
 * Created by raymond on 7/12/15.
 */
public class TripRowHolder extends RecyclerView.ViewHolder {
    private final ImageView hostImage;
    private final TextView startTime;
    private final TextView openSeats;
    private final TextView startStreet;
    private final TextView startCity;
    private final TextView startState;
    private final TextView endStreet;
    private final TextView endCity;
    private final TextView endState;
    private final Button payButton;
    private final Button startButton;
    private final Button cancelTripButton;
    private final Button completeTripButton;
    private final Button requestButton;
    private final Button cancelJoinButton;
    private final Button confirmPickupButton;
    private final Button confirmDropoff;
    private final TripDetailCallback callback;
    private String tripId;
    private Trip trip;

    public TripRowHolder(View itemView, TripDetailCallback callback, boolean selectable) {
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
        startButton = (Button) itemView.findViewById(R.id.startButton);
        cancelTripButton = (Button) itemView.findViewById(R.id.cancelTripButton);
        completeTripButton = (Button) itemView.findViewById(R.id.completeTripButton);
        requestButton = (Button) itemView.findViewById(R.id.requestButton);
        cancelJoinButton = (Button) itemView.findViewById(R.id.cancelJoinButton);
        confirmPickupButton = (Button) itemView.findViewById(R.id.confirmPickupButton);
        confirmDropoff = (Button) itemView.findViewById(R.id.confirmDropoff);

        if (selectable) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TripRowHolder.this.callback.onTripSelected(tripId);
                }
            });
        }
    }

    public void loadData(Trip data) {
        trip = data;
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

        startTime.setText((new SimpleDateFormat("EEE, MMM d ''yy h:mm aaa")).format(data.getStartTime()));
        openSeats.setText(Integer.toString(data.getOpenSeats()));
        Address startAddress = data.getStartLocation();
        Address endAddress = data.getEndLocation();
        if (startAddress != null) {
            startStreet.setText((startAddress.getStreetNumber() == null ? "" : startAddress.getStreetNumber() + " ")
                    + (startAddress.getStreet() == null ? "" : startAddress.getStreet()));
            startCity.setText(startAddress.getCity());
            startState.setText(startAddress.getState());
        }

        if (endAddress != null) {
            endStreet.setText((endAddress.getStreetNumber() == null ? "" : endAddress.getStreetNumber() + " ")
                    + (endAddress.getStreet() == null ? "" : endAddress.getStreet()));
            endCity.setText(endAddress.getCity());
            endState.setText(endAddress.getState());
        }

        for (ButtonToggle buttonToggle:ButtonToggle.values()){
            Button button = buttonToggle.getButton(this);
            if (buttonToggle.isValidButton(data)){
                if (buttonToggle.isVisible(data)) {
                    button.setVisibility(View.VISIBLE);
                    buttonToggle.setupButton(button, this);
                }else{
                    button.setVisibility(View.GONE);
                }
            }else{
                button.setVisibility(View.GONE);
            }
        }
        tripId = data.getTripId();
    }

    private enum ButtonToggle{
        START(true, false) {
            @Override
            public void setupButton(Button button, final TripRowHolder tripRowHolder) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tripRowHolder.trip.setCurrentLocation(tripRowHolder.trip.getStartLocation());
                        tripRowHolder.trip.startTrip();
                        tripRowHolder.callback.onTripSelected(tripRowHolder.tripId);
//                        tripRowHolder.callback.getLocationService().selectNextDestination(tripRowHolder.trip.getStartLocation(), tripRowHolder.trip.getDestinations(), tripRowHolder.trip, tripRowHolder);
                    }
                });
            }

            @Override
            public Button getButton(TripRowHolder tripRowHolder) {
                return tripRowHolder.startButton;
            }

            @Override
            public boolean isVisible(Trip trip) {
                return trip.canStartTrip();
            }
        },
        CANCEL_TRIP(true, false) {
            @Override
            public void setupButton(Button button, final TripRowHolder tripRowHolder) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tripRowHolder.trip.cancelTrip();
                        tripRowHolder.callback.onTripSelected(tripRowHolder.tripId);

                    }
                });
            }

            @Override
            public Button getButton(TripRowHolder tripRowHolder) {
                return tripRowHolder.cancelTripButton;
            }

            @Override
            public boolean isVisible(Trip trip) {
                return trip.canCancelTrip();
            }
        },
        COMPLETE_TRIP(true, false) {
            @Override
            public void setupButton(Button button, final TripRowHolder tripRowHolder) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tripRowHolder.callback.onHostCompleteTrip(tripRowHolder.tripId);
                    }
                });
            }

            @Override
            public Button getButton(TripRowHolder tripRowHolder) {
                return tripRowHolder.completeTripButton;
            }

            @Override
            public boolean isVisible(Trip trip) {
                return trip.canCompleteTrip();
            }
        },
        PAY_HOST(false, true) {
            @Override
            public void setupButton(Button button, final TripRowHolder tripRowHolder) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tripRowHolder.callback.onUserCompleteTrip(tripRowHolder.tripId);
                    }
                });

            }

            @Override
            public Button getButton(TripRowHolder tripRowHolder) {
                return tripRowHolder.payButton;
            }

            @Override
            public boolean isVisible(Trip trip) {
                return trip.canPayHost();
            }
        },
        CONFIRM_DROPOFF(false, true) {
            @Override
            public void setupButton(Button button, final TripRowHolder tripRowHolder) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tripRowHolder.trip.confirmDropoff();
                        tripRowHolder.callback.onTripSelected(tripRowHolder.tripId);
                    }
                });

            }

            @Override
            public Button getButton(TripRowHolder tripRowHolder) {
                return tripRowHolder.confirmDropoff;
            }

            @Override
            public boolean isVisible(Trip trip) {
                return trip.canConfirmDropoff();
            }
        },
        CONFIRM_PICKUP(false, true) {
            @Override
            public void setupButton(Button button, final TripRowHolder tripRowHolder) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tripRowHolder.trip.confirmPickup();
                        tripRowHolder.callback.onTripSelected(tripRowHolder.tripId);
                    }
                });

            }

            @Override
            public Button getButton(TripRowHolder tripRowHolder) {
                return tripRowHolder.confirmPickupButton;
            }

            @Override
            public boolean isVisible(Trip trip) {
                return trip.canConfirmPickup();
            }
        },
        CANCEL_PICKUP(false, true) {
            @Override
            public void setupButton(Button button, final TripRowHolder tripRowHolder) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tripRowHolder.trip.cancelPickup();
                        tripRowHolder.callback.onTripSelected(tripRowHolder.tripId);
                    }
                });

            }

            @Override
            public Button getButton(TripRowHolder tripRowHolder) {
                return tripRowHolder.cancelJoinButton;
            }

            @Override
            public boolean isVisible(Trip trip) {
                return trip.canCancelPickup();
            }
        },
        REQUEST_JOIN(false, false) {
            @Override
            public void setupButton(Button button, final TripRowHolder tripRowHolder) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tripRowHolder.callback.onJoinTripSelected(tripRowHolder.tripId);
                    }
                });
            }

            @Override
            public Button getButton(TripRowHolder tripRowHolder) {
                return tripRowHolder.requestButton;
            }

            @Override
            public boolean isVisible(Trip trip) {
                return trip.canRequestJoin();
            }
        };

        final boolean host;
        final boolean user;

        ButtonToggle(boolean host, boolean user) {
            this.host = host;
            this.user = user;
        }

        public abstract void setupButton(Button button, TripRowHolder tripRowHolder);
        public abstract Button getButton(TripRowHolder tripRowHolder);
        public abstract boolean isVisible(Trip trip);
        public boolean isValidButton(Trip trip){
            boolean tripHost = trip.isLoggedInUser();
            boolean tripUser = trip.isLoggedInUserInCarpool();
            return tripHost==host && tripUser==user;
        }
    }

}
