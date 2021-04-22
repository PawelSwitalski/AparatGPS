package com.main.aparatgps.photo;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MapsFragmentTest {

    String latitude, longitude;
    final double correctLa = 51.5;
    final double correctLo = 11.27;

    @Before
    public void setUP() {
        latitude = "51,5,1287/25(51.48)";
        longitude = "11,27,2079/250(8.316)";
    }

    @Test
    public void getNumberTestLatitude() {
        double latitudeNumber = MapsFragment.getNumber(latitude);
        assertEquals(correctLa, latitudeNumber, 0.02);
    }

    @Test
    public void getNumberTestLongitude() {
        double longitudeNumber = MapsFragment.getNumber(longitude);
        assertEquals(correctLo, longitudeNumber, 0.02);
    }
}