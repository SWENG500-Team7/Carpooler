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
import com.carpooler.trips.UserLoader;
import com.carpooler.ui.activities.TripDetailCallback;
import com.carpooler.users.Address;
import com.carpooler.users.CarpoolUser;
import com.carpooler.users.User;

/**
 * Created by raymond on 7/12/15.
 */
public class CarpoolUserAdapter extends EmptyAdapter<CarpoolUserAdapter.CarpoolUserRowHolder>{
    private final Trip trip;
    private final Trip.CarpoolUserIterator carpoolUserIterator;
    private final TripDetailCallback callback;


    public CarpoolUserAdapter(Trip trip, TripDetailCallback callback) {
        this.trip = trip;
        this.callback = callback;
        carpoolUserIterator = trip.getCarpoolUsers();
    }

    @Override
    protected int getRealSize() {
        return carpoolUserIterator.size();
    }

    @Override
    protected int getEmptyStringId() {
        return R.string.no_users_found;
    }

    @Override
    protected CarpoolUserRowHolder getRealViewHolder(ViewGroup parent, int viewType) {
        View v =LayoutInflater.from(parent.getContext()).inflate(R.layout.carpool_user_row, null);
        CarpoolUserRowHolder carpoolUserRowHolder = new CarpoolUserRowHolder(v, callback);
        return carpoolUserRowHolder;
    }

    @Override
    protected void onBindViewHolderReal(CarpoolUserRowHolder holder, int position) {
        CarpoolUser carpoolUser = carpoolUserIterator.get(position);
        holder.loadData(trip,carpoolUser);
    }


    class CarpoolUserRowHolder extends RecyclerView.ViewHolder{
        private final TextView userName;
        private final ImageView userImage;
        private final TextView startStreet;
        private final TextView startCity;
        private final TextView startState;
        private final TextView endStreet;
        private final TextView endCity;
        private final TextView endState;
        private final Button dropoffButton;
        private final Button noShowButton;
        private final Button pickupUserButton;
        private final Button navigatePickupButton;
        private final Button navigateDropoffButton;
        private final Button acceptRequestButton;
        private final Button rejectRequestButton;
        private Trip trip;
        private CarpoolUser carpoolUser;
        private final TripDetailCallback callback;

        public CarpoolUserRowHolder(View itemView, TripDetailCallback callback) {
            super(itemView);
            this.callback = callback;
            userName = (TextView) itemView.findViewById(R.id.userName);
            userImage = (ImageView) itemView.findViewById(R.id.userImage);
            startStreet = (TextView) itemView.findViewById(R.id.startStreet);
            startCity = (TextView) itemView.findViewById(R.id.startCity);
            startState = (TextView) itemView.findViewById(R.id.startState);
            endStreet = (TextView) itemView.findViewById(R.id.endStreet);
            endCity = (TextView) itemView.findViewById(R.id.endCity);
            endState = (TextView) itemView.findViewById(R.id.endState);
            dropoffButton = (Button) itemView.findViewById(R.id.dropoffButton);
            noShowButton = (Button) itemView.findViewById(R.id.noShowButton);
            pickupUserButton = (Button) itemView.findViewById(R.id.pickupUserButton);
            navigatePickupButton = (Button) itemView.findViewById(R.id.navigatePickupButton);
            navigateDropoffButton = (Button) itemView.findViewById(R.id.navigateDropoffButton);
            acceptRequestButton = (Button) itemView.findViewById(R.id.acceptRequestButton);
            rejectRequestButton = (Button) itemView.findViewById(R.id.rejectRequestButton);
        }

        public void loadData(Trip trip, CarpoolUser carpoolUser){
            this.trip = trip;
            this.carpoolUser = carpoolUser;
            carpoolUser.loadUserData(new UserLoader.Callback() {
                @Override
                public void loadData(User user) {
                    userName.setText(user.getName());
                    try {
                        user.loadUserImage(userImage, 150);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });

            Address startAddress = carpoolUser.getPickupLocation();
            Address endAddress = carpoolUser.getDropoffLocation();
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
            for(ButtonToggle buttonToggle:ButtonToggle.values()){
                Button button = buttonToggle.getButton(this);
                if (buttonToggle.isValidButton(trip,carpoolUser)){
                    if (buttonToggle.isVisible(trip,carpoolUser)){
                        buttonToggle.setupButton(button,this);
                        button.setVisibility(View.VISIBLE);
                    }else{
                        button.setVisibility(View.GONE);
                    }
                }else{
                    button.setVisibility(View.GONE);
                }
            }
        }
    }

    private enum ButtonToggle{
        DROP_OFF(true, false) {
            @Override
            public void setupButton(Button button, final CarpoolUserRowHolder carpoolUserRowHolder) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        carpoolUserRowHolder.trip.dropoffCarpoolUser(carpoolUserRowHolder.carpoolUser);
                        carpoolUserRowHolder.callback.onTripSelected(carpoolUserRowHolder.trip.getTripId());
                    }
                });
            }

            @Override
            public Button getButton(CarpoolUserRowHolder carpoolUserRowHolder) {
                return carpoolUserRowHolder.dropoffButton;
            }

            @Override
            public boolean isVisible(Trip trip, CarpoolUser carpoolUser) {
                return trip.canDropOffUser(carpoolUser);
            }


        },
        NO_SHOW(true, false) {
            @Override
            public void setupButton(Button button, final CarpoolUserRowHolder carpoolUserRowHolder) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        carpoolUserRowHolder.trip.skipNoShow(carpoolUserRowHolder.carpoolUser);
                        carpoolUserRowHolder.callback.onTripSelected(carpoolUserRowHolder.trip.getTripId());
                    }
                });

            }

            @Override
            public Button getButton(CarpoolUserRowHolder carpoolUserRowHolder) {
                return carpoolUserRowHolder.noShowButton;
            }

            @Override
            public boolean isVisible(Trip trip, CarpoolUser carpoolUser) {
                return trip.canMarkNoShow(carpoolUser);
            }

        },
        PICKUP(true, false) {
            @Override
            public void setupButton(Button button, final CarpoolUserRowHolder carpoolUserRowHolder) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        carpoolUserRowHolder.trip.pickupCarpoolUser(carpoolUserRowHolder.carpoolUser);
                        carpoolUserRowHolder.callback.onTripSelected(carpoolUserRowHolder.trip.getTripId());
                    }
                });

            }

            @Override
            public Button getButton(CarpoolUserRowHolder carpoolUserRowHolder) {
                return carpoolUserRowHolder.pickupUserButton;
            }

            @Override
            public boolean isVisible(Trip trip, CarpoolUser carpoolUser) {
                return trip.canPickupUser(carpoolUser);
            }

        },
        NAVIGATE_PICKUP(true, false) {
            @Override
            public void setupButton(Button button, final CarpoolUserRowHolder carpoolUserRowHolder) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        carpoolUserRowHolder.callback.navigate(carpoolUserRowHolder.carpoolUser.getPickupLocation());
                    }
                });

            }

            @Override
            public Button getButton(CarpoolUserRowHolder carpoolUserRowHolder) {
                return carpoolUserRowHolder.navigatePickupButton;
            }

            @Override
            public boolean isVisible(Trip trip, CarpoolUser carpoolUser) {
                return trip.canNavigatePickupUser(carpoolUser);
            }

        },
        NAVIGATE_DROPOFF(true, false) {
            @Override
            public void setupButton(Button button, final CarpoolUserRowHolder carpoolUserRowHolder) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        carpoolUserRowHolder.callback.navigate(carpoolUserRowHolder.carpoolUser.getDropoffLocation());
                    }
                });

            }

            @Override
            public Button getButton(CarpoolUserRowHolder carpoolUserRowHolder) {
                return carpoolUserRowHolder.navigateDropoffButton;
            }

            @Override
            public boolean isVisible(Trip trip, CarpoolUser carpoolUser) {
                return trip.canNavigateDropoffUser(carpoolUser);
            }

        },
        ACCEPT_REQUEST(true, false) {
            @Override
            public void setupButton(Button button, final CarpoolUserRowHolder carpoolUserRowHolder) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        carpoolUserRowHolder.trip.acceptPickupRequest(carpoolUserRowHolder.carpoolUser);
                        carpoolUserRowHolder.callback.onTripSelected(carpoolUserRowHolder.trip.getTripId());
                    }
                });
            }

            @Override
            public Button getButton(CarpoolUserRowHolder carpoolUserRowHolder) {
                return carpoolUserRowHolder.acceptRequestButton;
            }

            @Override
            public boolean isVisible(Trip trip, CarpoolUser carpoolUser) {
                return trip.canAcceptRequest(carpoolUser);
            }

        },
        REJECT_REQUEST(true, false) {
            @Override
            public void setupButton(Button button, final CarpoolUserRowHolder carpoolUserRowHolder) {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        carpoolUserRowHolder.trip.rejectPickupRequest(carpoolUserRowHolder.carpoolUser);
                        carpoolUserRowHolder.callback.onTripSelected(carpoolUserRowHolder.trip.getTripId());
                    }
                });
            }

            @Override
            public Button getButton(CarpoolUserRowHolder carpoolUserRowHolder) {
                return carpoolUserRowHolder.rejectRequestButton;
            }

            @Override
            public boolean isVisible(Trip trip, CarpoolUser carpoolUser) {
                return trip.canRejectRequest(carpoolUser);
            }

        };
        private final boolean host;
        private final boolean user;

        ButtonToggle(boolean host, boolean user) {
            this.host = host;
            this.user = user;
        }
        public abstract void setupButton(Button button, CarpoolUserRowHolder carpoolUserRowHolder);
        public abstract Button getButton(CarpoolUserRowHolder carpoolUserRowHolder);
        public abstract boolean isVisible(Trip trip, CarpoolUser carpoolUser);
        public boolean isValidButton(Trip trip, CarpoolUser carpoolUser){
            boolean tripHost = trip.isLoggedInUser();
            boolean tripUser = carpoolUser.isLoggedInUser();
            return tripHost==host && tripUser==user;
        }
    }



}
