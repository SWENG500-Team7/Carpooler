package com.carpooler.ui.activities;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carpooler.R;
import com.carpooler.trips.TripStatus;

public class TripListMainFragment extends Fragment {
    private FragmentTabHost mTabHost;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trip_list_main, container, false);
        mTabHost = (FragmentTabHost) rootView.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);
        Bundle mainBundle = getArguments();
        boolean hosted = true;
        if (mainBundle!=null){
            hosted = mainBundle.getBoolean(TripListFragment.Status.HOSTED_ARG);
        }
        Bundle openArgs = createTabBundle(TripStatus.OPEN,hosted);
        mTabHost.addTab(mTabHost.newTabSpec("open_trips").setIndicator("Open Trips"), TripListFragment.Status.class, openArgs);
        Bundle completedArgs = createTabBundle(TripStatus.COMPLETED,hosted);
        mTabHost.addTab(mTabHost.newTabSpec("completed_trips").setIndicator("Completed Trips"), TripListFragment.Status.class, completedArgs);

        return rootView;
    }

    private Bundle createTabBundle(TripStatus tripStatus, boolean hosted){
        Bundle tabBundle = new Bundle();
        tabBundle.putBoolean(TripListFragment.Status.HOSTED_ARG, hosted);
        tabBundle.putString(TripListFragment.Status.STATUS_ARG,tripStatus.name());
        return tabBundle;
    }



}
