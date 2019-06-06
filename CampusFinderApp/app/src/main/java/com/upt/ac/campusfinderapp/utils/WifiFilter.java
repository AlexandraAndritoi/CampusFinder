package com.upt.ac.campusfinderapp.utils;

import com.upt.ac.campusfinderapp.model.WifiAccessPoint;

import java.util.List;

public class WifiFilter {

    public WifiAccessPoint[] filterWifiAccessPoints(List<WifiAccessPoint> scannedWifiAccessPoints){
        int i = 0;
        WifiAccessPoint[] filteredWifiAccessPoints = new WifiAccessPoint[3];
        List<WifiAccessPoint> databaseWifiAccessPoints = WifiAccessPointRepository.getInstance().getWifiAccessPoints();
        for(WifiAccessPoint scannedWifiAccessPoint : scannedWifiAccessPoints) {
            for (WifiAccessPoint databaseWifiAccessPoint : databaseWifiAccessPoints) {
                if (scannedWifiAccessPoint.getSSID().equals(databaseWifiAccessPoint.getSSID()) && i < 3) {
                    scannedWifiAccessPoint.setLatitude(databaseWifiAccessPoint.getLatitude());
                    scannedWifiAccessPoint.setLongitude(databaseWifiAccessPoint.getLongitude());
                    scannedWifiAccessPoint.setFloor(databaseWifiAccessPoint.getFloor());
                    filteredWifiAccessPoints[i++] = scannedWifiAccessPoint;
                }
            }
        }
        return filteredWifiAccessPoints;
    }
}
