package com.upt.ac.campusfinderapp.indoorlocation;

import com.upt.ac.campusfinderapp.model.WifiAccessPoint;

import org.json.JSONObject;

import java.util.HashMap;

import io.indoorlocation.core.IndoorLocation;

class IndoorLocationCalculator {
    private static final double EARTH_RADIUS = 6378.137;
    private static final double K = (1 / ((2 * Math.PI / 360) * EARTH_RADIUS)) / 1000;

    private JSONObject B414 = new JSONObject();


    private static final double latitude = 45.774568;
    private static final double longitude = 21.244662;

    IndoorLocation calculateIndoorLocationFromWifiAccessPointData(WifiAccessPoint wifiAccessPoint) {
        double distance = calculateDistanceFromDeviceToWifiAccessPoint(wifiAccessPoint.getLevel(), wifiAccessPoint.getFrequency());
        return calculateIndoorLocation(distance);
    }

    private double calculateDistanceFromDeviceToWifiAccessPoint(int signalLevelInDb, int freqInMHz){
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(signalLevelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }

    private IndoorLocation calculateIndoorLocation(double distance) {
        Double newLatitude = latitude + (distance * K);
        Double newLongitude = longitude + (distance * K) / Math.cos(newLatitude * (Math.PI / 180));
        return new IndoorLocation(WifiIndoorLocationProvider.class.getName(), newLatitude, newLongitude, 0.0, System.currentTimeMillis());
    }
}
