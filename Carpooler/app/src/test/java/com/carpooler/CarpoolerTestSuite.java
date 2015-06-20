package com.carpooler;

/**
 * Created by raymond on 6/7/15.
 */
import com.carpooler.payment.PaymentServiceTest;
import com.carpooler.trips.FuelPriceTest;
import com.carpooler.trips.TripTest;
import com.carpooler.users.CarpoolHostTest;
import com.carpooler.users.CarpoolUserTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({CarpoolHostTest.class,CarpoolUserTest.class,PaymentServiceTest.class,TripTest.class, FuelPriceTest.class})
public class CarpoolerTestSuite {

}


