package com.carpooler.ui.activities;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import android.R;
import com.carpooler.ui.activities.preferences.DatabasePreferences;

/**
 * Created by raymond on 6/12/15.
 */
public class CarpoolerSettings extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        DatabasePreferences databasePreferences = new DatabasePreferences();
        fragmentTransaction.replace(R.id.content, databasePreferences);
        fragmentTransaction.commit();
    }
}
