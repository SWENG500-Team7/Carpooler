package com.carpooler.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.carpooler.R;
import com.carpooler.trips.Vehicle;
import com.carpooler.ui.adapters.VehicleRecyclerAdapter;
import com.carpooler.users.User;

import java.util.List;


public class VehicleListFragment extends Fragment implements View.OnClickListener {

    /* Fragment can be used to create chooser screen or manager screen */
    public static final String ARG_TYPE = "type";
    public static enum VehicleListType {
        CHOOSER, MANAGER
    }

    /* This fragment instance's type */
    private VehicleListType mType;

    /* UI Components */
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mRefreshLayout;
    private VehicleRecyclerAdapter mAdapter;
    private VehicleDetailCallback mCallback;
    private ImageButton mAddButton;
    private MultiSelector mMultiSelector = new MultiSelector();

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param pType Parameter 1.
     * @return A new instance of fragment VehicleListFragment.
     */
    public static VehicleListFragment newInstance(VehicleListType pType) {
        VehicleListFragment fragment = new VehicleListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TYPE, pType.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    public VehicleListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = VehicleListType.values()[getArguments().getInt(ARG_TYPE)];
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_vehicle_list, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new VehicleRecyclerAdapter(null, mCallback, mMultiSelector, getActivity());
        mRecyclerView.setAdapter(mAdapter);

        mRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.contentView);
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        mAddButton = (ImageButton) rootView.findViewById(R.id.btn_add_vehicle);
        mAddButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallback = (VehicleDetailCallback) activity;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_vehicle:
                mCallback.onAddVehicle();
                break;
        }
    }

    protected void setupAdapter(List<Vehicle> vehicles) {
        mAdapter = new VehicleRecyclerAdapter(vehicles, mCallback, mMultiSelector, getActivity());
        mRecyclerView.setAdapter(mAdapter);
    }

    private void loadData(){
        if (!mRefreshLayout.isRefreshing()){
            mRefreshLayout.setRefreshing(true);
        }
        User user = mCallback.getUser();
        user.refreshUserData();
        setupAdapter(user.getVehicles());
        mRefreshLayout.setRefreshing(false);
    }
}
