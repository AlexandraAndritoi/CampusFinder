package com.upt.ac.campusfinderapp.indoorlocation;

import android.content.Context;

import com.upt.ac.campusfinderapp.model.WifiAccessPoint;
import com.upt.ac.campusfinderapp.utils.FileWriter;
import com.upt.ac.campusfinderapp.utils.WifiAccessPointRepository;
import java.util.List;
import io.indoorlocation.core.IndoorLocation;


class IndoorLocationCalculator {
    private static final double EARTH_RADIUS = 6378.137;
    private static final double K = (1 / ((2 * Math.PI / 360) * EARTH_RADIUS)) / 1000;

    private Context context;
    private double latitude;
    private double longitude;

    IndoorLocationCalculator(Context context){
        this.context = context;
    }

    IndoorLocation calculateIndoorLocationFromWifiAccessPointData(WifiAccessPoint wifiAccessPoint) {
        List<WifiAccessPoint> wifiAccessPoints = WifiAccessPointRepository.getInstance().getWifiAccessPoints();
        for (WifiAccessPoint wap: wifiAccessPoints) {
            if(wifiAccessPoint.getSSID().equals(wap.getSSID())){
                latitude = wap.getLatitude();
                longitude = wap.getLongitude();
            }
        }
        double distance = calculateDistanceFromDeviceToWifiAccessPoint(wifiAccessPoint.getLevel(), wifiAccessPoint.getFrequency());
        IndoorLocation location = calculateIndoorLocation(distance);
        FileWriter fileWriter = new FileWriter(context);
        fileWriter.writeToExternalStorage(wifiAccessPoint, location, distance);
        return location;
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
