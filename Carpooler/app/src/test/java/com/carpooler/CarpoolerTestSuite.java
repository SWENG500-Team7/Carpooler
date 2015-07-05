package com.carpooler;

/**
 * Created by raymond on 6/7/15.
 */
import com.carpooler.trips.TripTest;
import com.carpooler.users.UserTest;
import com.carpooler.users.CarpoolUserTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({UserTest.class,CarpoolUserTest.class,TripTest.class})
public class CarpoolerTestSuite {

}


