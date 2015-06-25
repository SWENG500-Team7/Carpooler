package com.carpooler.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.carpooler.R;
import com.carpooler.dao.DatabaseService;
import com.carpooler.dao.TripDataService;
import com.carpooler.dao.UserDataService;

public class CarpoolerMain extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener, TripDetailCallback{
    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private DatabaseService.Connection conn;
    private MessageFragment activeFragment;

    @Override
    public void onTripSelected(String tripId) {
        TripDetailFragment fragment = new TripDetailFragment();
        String title = getString(R.string.title_trip_detail);
        transitionFragment(fragment,title);
    }

    @Override
    public TripDataService getTripDataService() {
        return tripDataService;
    }

    @Override
    public UserDataService getUserDataService() {
        return userDataService;
    }

    private class FragmentDataHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what<0){
                handleErrorDbResponse(msg);
            }else{
                handleSuccessDbResponse(msg);
            }
        }
    }

    private TripDataService tripDataService;
    private UserDataService userDataService;

    private void handleErrorDbResponse(Message msg){
        switch (msg.what){
            case DatabaseService.ERROR:
                Toast.makeText(this,(String)msg.obj,Toast.LENGTH_LONG);
                break;
            case DatabaseService.EXCEPTION:
                Toast.makeText(this,((Exception)msg.obj).getMessage(),Toast.LENGTH_LONG);
                break;
        }
        if (activeFragment!=null){
            activeFragment.handleError(msg);
        }
    }
    private void handleSuccessDbResponse(Message msg){
        if (activeFragment!=null){
            activeFragment.handleMessage(msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpooler_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
        Messenger responseMessenger = new Messenger(new FragmentDataHandler());
        conn = new DatabaseService.Connection(responseMessenger);
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
                fragment = new TripListFragment();
                title = getString(R.string.title_trips);
                break;
            default:
                break;
        }

        transitionFragment(fragment,title);

    }

    private void transitionFragment(Fragment fragment, String title){
        if (fragment != null) {
            if (fragment instanceof MessageFragment) {
                activeFragment = (MessageFragment) fragment;
            }
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
            getSupportActionBar().setTitle(title);
        }

    }
}
