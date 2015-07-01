package com.carpooler.ui.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carpooler.R;
import com.carpooler.trips.TripStatus;

public class TripListFragment extends Fragment {

    private FragmentTabHost mTabHost;
    public static final String STATUS_ARG = "status";
    public static final String HOSTED_ARG = "hosted";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mTabHost = new FragmentTabHost(getActivity());
        mTabHost.setup(getActivity(), getChildFragmentManager(), R.layout.fragment_trip_list);
        Bundle openArgs = new Bundle();
        openArgs.putString(STATUS_ARG, TripStatus.OPEN.name());
        mTabHost.addTab(mTabHost.newTabSpec("open_trips").setIndicator("Open Trips"), OpenTripListFragment.class, openArgs);
        Bundle completedArgs = new Bundle();
        completedArgs.putString(STATUS_ARG, TripStatus.COMPLETED.name());
        mTabHost.addTab(mTabHost.newTabSpec("completed_trips").setIndicator("Completed Trips"), CompletedTripListFragment.class, completedArgs);

        return mTabHost;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabHost = null;
    }

}
