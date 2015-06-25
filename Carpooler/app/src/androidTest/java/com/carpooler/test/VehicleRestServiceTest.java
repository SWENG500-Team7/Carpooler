package com.carpooler.test;

import android.test.AndroidTestCase;
import android.test.ServiceTestCase;

import com.carpooler.dao.VehicleRestService;

import junit.framework.TestCase;

import java.util.Arrays;

/**
 * Created by Kevin on 6/20/2015.
 */
public class VehicleRestServiceTest extends AndroidTestCase {

    public void testGetYears() {
        String[] yearsArray = VehicleRestService.getYears();
        assertTrue(yearsArray.length > 0);
        assertTrue(Arrays.asList(yearsArray).contains("2006"));
    }

    public void testGetMakes() {
        String[] makesArray = VehicleRestService.getMakes("2015");
        assertTrue(makesArray.length > 0);
        assertTrue(Arrays.asList(makesArray).contains("Dodge"));
    }

    public void testGetModels() {
        String[] modelsArray = VehicleRestService.getModels("2006", "Dodge");
        assertTrue(modelsArray.length > 0);
        assertTrue(Arrays.asList(modelsArray).contains("Charger"));
    }

    public void testGetMPG() {
        int mpg = VehicleRestService.getMPG("Honda", "Fit", "2015");
        assertEquals(34, mpg);
    }
}
