package com.carpooler.ui.adapters;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;
import com.carpooler.R;
import com.carpooler.trips.Vehicle;
import com.carpooler.ui.activities.VehicleDetailCallback;
import com.carpooler.users.User;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Kevin on 6/28/2015.
 */
public class VehicleRecyclerAdapter extends RecyclerView.Adapter<VehicleRecyclerAdapter.VehicleRowHolder> {

    /* List of vehicles displayed */
    private List<Vehicle> mVehicles;

    /* Callback for when a vehicle is clicked */
    private VehicleDetailCallback mCallback;

    /* Multiselector for handling selection modes */
    private MultiSelector mMultiSelector;

    /* Context of the adapter so we can change the menu items around */
    private AppCompatActivity mActivity;

    public VehicleRecyclerAdapter(List<Vehicle> vehicles, VehicleDetailCallback callback,
                                  MultiSelector multiSelector, Activity context) {
        mVehicles = vehicles;
        mCallback = callback;
        mMultiSelector = multiSelector;
        mActivity = (AppCompatActivity) context;
    }

    @Override
    public VehicleRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_list_row, parent, false);
        VehicleRowHolder vehicleRowHolder = new VehicleRowHolder(v);
        return vehicleRowHolder;
    }

    @Override
    public void onBindViewHolder(VehicleRowHolder holder, int position) {
        Vehicle data = mVehicles.get(position);
        holder.bindVehicle(data);
    }

    @Override
    public int getItemCount() {
        return mVehicles == null ? 0 : mVehicles.size();
    }

    /**
     * Action mode for when a user long presses on vehicle, they can delete or cancel
     */
    private ActionMode.Callback vehicleDeleteMode = new ModalMultiSelectorCallback(mMultiSelector) {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            setMultiSelector(mMultiSelector);
            mActivity.getMenuInflater().inflate(R.menu.menu_vehicle_list_item_context, menu);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.mi_delete_vehicle:
                    //Delete vehicles
                    User currentUser = mCallback.getUser();
                    ArrayList<String> toRemove = new ArrayList<String>();
                    for (int i = mVehicles.size(); i > 0; i--) {
                        if (mMultiSelector.isSelected(i, 0)) {
                            Vehicle vehicle = mVehicles.get(i);
                            toRemove.add(vehicle.getPlateNumber());
                            currentUser.removeVehicle(vehicle.getPlateNumber());
                        }
                    }
                    for (Iterator<Vehicle> iterator = mVehicles.iterator(); iterator.hasNext(); ) {
                        Vehicle vehicle = iterator.next();
                        if (toRemove.contains(vehicle.getPlateNumber())) {
                            iterator.remove();
                        }
                    }

                    //Save changed user to DB
                    currentUser.saveUser();

                    //Finish up the mode
                    notifyDataSetChanged();
                    mode.finish();
                    mMultiSelector.clearSelections();
                    return true;
                default:
                    mode.finish();
                    mMultiSelector.clearSelections();
                    break;
            }
            return false;
        }
    };

    /**
     * Holder for each vehicle list item
     */
    class VehicleRowHolder extends SwappingHolder implements View.OnClickListener, View.OnLongClickListener {

        /* Text to show in list row */
        TextView make;
        TextView model;
        TextView plateNumber;
        Vehicle mVehicle;

        public VehicleRowHolder(View itemView) {
            super(itemView, mMultiSelector);

            //Reference UI components and set listeners
            make = (TextView) itemView.findViewById(R.id.make);
            model = (TextView) itemView.findViewById(R.id.model);
            plateNumber = (TextView) itemView.findViewById(R.id.plateNumber);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bindVehicle(Vehicle vehicle) {
            mVehicle = vehicle;
            make.setText(mVehicle.getMake());
            model.setText(mVehicle.getModel());
            plateNumber.setText(mVehicle.getPlateNumber());

            setSelectable(mMultiSelector.isSelectable());
            setActivated(mMultiSelector.isSelected(getAdapterPosition(), getItemId()));
        }

        @Override
        public void onClick(View v) {
            if (mMultiSelector.isSelectable()) {
                //Selection is active, toggle activation
                setActivated(!isActivated());
                mMultiSelector.setSelected(getAdapterPosition(), getItemId(), isActivated());
            } else {
                //If we're not in selection mode start Vehicle Details fragment
                if (!mMultiSelector.tapSelection(this)) {
                    mCallback.onVehicleSelected(mVehicle.getPlateNumber());
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            //On long click start up multi-delete action mode
            mActivity.startSupportActionMode(vehicleDeleteMode);
            mMultiSelector.setSelectable(true);
            mMultiSelector.setSelected(this, true);
            return true;
        }
    }
}
