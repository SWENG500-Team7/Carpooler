package com.carpooler.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.carpooler.R;
import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.TripDataService;
import com.carpooler.dao.UserDataService;
import com.carpooler.trips.TripStatus;

public class CarpoolerMain extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener, TripDetailCallback{
    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private DatabaseService.Connection conn;

    @Override
    public void onTripSelected(String tripId) {
        TripDetailFragment fragment = new TripDetailFragment();
        String title = getString(R.string.title_trip_detail);
        Bundle args = new Bundle();
        args.putString(TripDetailFragment.TRIP_ID_ARG,tripId);
        fragment.setArguments(args);
        transitionFragment(fragment, title);
    }

    @Override
    public TripDataService getTripDataService() {
        return tripDataService;
    }

    @Override
    public UserDataService getUserDataService() {
        return userDataService;
    }

    private TripDataService tripDataService;
    private UserDataService userDataService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpooler_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
        conn = new DatabaseService.Connection();
        Intent intent = new Intent(this, DatabaseService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        tripDataService = new TripDataService(conn);
        userDataService = new UserDataService(conn);
        displayView(0);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(conn);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_carpooler_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, CarpoolerSettings.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }
    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = createTripListFragment(TripStatus.OPEN);
                title = getString(R.string.nav_item_open_trips);
                break;
            case 1:
                fragment = createTripListFragment(TripStatus.IN_ROUTE);
                title = getString(R.string.nav_item_in_route_trips);
                break;
            default:
                break;
        }

        transitionFragment(fragment, title);

    }

    private Fragment createTripListFragment(TripStatus status){
        Bundle args = new Bundle();
        args.putString(TripListFragment.STATUS_ARG, status.name());
        Fragment fragment = new TripListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void transitionFragment(Fragment fragment, String title){
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }

    }
}
