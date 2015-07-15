package com.carpooler.test;

import android.test.ActivityInstrumentationTestCase2;

import com.carpooler.ui.activities.CarpoolerActivity;

/**
 * Created by raymond on 7/6/15.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<CarpoolerActivity> {
    public MainActivityTest() {
        super(CarpoolerActivity.class);
    }

    public void testAct(){
        CarpoolerActivity main = getActivity();
        main.onDrawerItemSelected(null,2);
    }
}
