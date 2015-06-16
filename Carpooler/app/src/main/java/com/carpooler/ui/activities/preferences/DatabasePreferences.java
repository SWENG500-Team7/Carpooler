package com.carpooler.ui.activities.preferences;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.carpooler.R;

/**
 * Created by raymond on 6/12/15.
 */
public class DatabasePreferences extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.database_preferences);
    }
}
