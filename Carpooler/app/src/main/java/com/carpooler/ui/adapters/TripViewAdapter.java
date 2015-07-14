package com.carpooler.ui.adapters;

import android.os.RemoteException;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class TripViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Trip trip;
    private final Trip.CarpoolUserIterator carpoolUserIterator;
    private final TripDetailCallback callback;
    private static final int HEADER = 0;
    private static final int DETAIL = 1;


    public TripViewAdapter(Trip trip, TripDetailCallback callback) {
        this.trip = trip;
        this.callback = callback;
        carpoolUserIterator = trip.getCarpoolUsers();
    }

    @Override
    public int getItemViewType(int position) {
        if (position==HEADER){
            return HEADER;
        }else{
            if (carpoolUserIterator.size()==0){
                return EmptyAdapter.EMPTY_VIEW;
            }else {
                return DETAIL;
            }
        }
    }

    private boolean isEmpty(){
        return carpoolUserIterator.size()==0;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType==HEADER){
            View v =LayoutInflater.from(parent.getContext()).inflate(R.layout.trip_list_row, null);
            TripRowHolder tripRowHolder = new TripRowHolder(v, callback);
            return tripRowHolder;
        }else if (viewType==EmptyAdapter.EMPTY_VIEW) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_row, null);
            EmptyViewHolder emptyViewHolder = new EmptyViewHolder(v,R.string.no_users_found);
            return emptyViewHolder;
        }else{
            View v =LayoutInflater.from(parent.getContext()).inflate(R.layout.carpool_user_row, null);
            CarpoolUserRowHolder carpoolUserRowHolder = new CarpoolUserRowHolder(v);
            return carpoolUserRowHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position==HEADER){
            TripRowHolder tripRowHolder = (TripRowHolder) holder;
            tripRowHolder.loadData(trip);
        }else{
            if (!isEmpty()){
                CarpoolUserRowHolder carpoolUserRowHolder = (CarpoolUserRowHolder) holder;
                CarpoolUser carpoolUser = carpoolUserIterator.get(position-1);
                carpoolUserRowHolder.loadData(carpoolUser);
            }
        }
    }

    @Override
    public int getItemCount() {
        int userSize=1;
        if (!isEmpty()){
            userSize = carpoolUserIterator.size();
        }
        return userSize+1;
    }

    class CarpoolUserRowHolder extends RecyclerView.ViewHolder{
        private TextView userName;
        private ImageView userImage;
        private TextView startStreet;
        private TextView startCity;
        private TextView startState;
        private TextView endStreet;
        private TextView endCity;
        private TextView endState;

        public CarpoolUserRowHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.userName);
            userImage = (ImageView) itemView.findViewById(R.id.userImage);
            startStreet = (TextView) itemView.findViewById(R.id.startStreet);
            startCity = (TextView) itemView.findViewById(R.id.startCity);
            startState = (TextView) itemView.findViewById(R.id.startState);
            endStreet = (TextView) itemView.findViewById(R.id.endStreet);
            endCity = (TextView) itemView.findViewById(R.id.endCity);
            endState = (TextView) itemView.findViewById(R.id.endState);
        }

        public void loadData(CarpoolUser carpoolUser){
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
        }
    }




}
