package com.upt.ac.campusfinderapp.utils;

import com.upt.ac.campusfinderapp.model.WifiAccessPoint;

import java.util.List;

public class WifiFilter {

    public WifiAccessPoint[] filterWifiAccessPoints(List<WifiAccessPoint> scannedWifiAccessPoints){
        List<WifiAccessPoint> databaseWifiAccessPoints = WifiAccessPointRepository.getInstance().getWifiAccessPoints();
        for(WifiAccessPoint scannedWifiAccessPoint : scannedWifiAccessPoints) {
            for (WifiAccessPoint databaseWifiAccessPoint : databaseWifiAccessPoints) {
                if (scannedWifiAccessPoint.getSSID().equals(databaseWifiAccessPoint.getSSID())) {
                    scannedWifiAccessPoint.setLatitude(databaseWifiAccessPoint.getLatitude());
                    scannedWifiAccessPoint.setLongitude(databaseWifiAccessPoint.getLongitude());
                    scannedWifiAccessPoint.setFloor(databaseWifiAccessPoint.getFloor());
                }
            }
        }
        WifiAccessPoint filteredWifiAccessPoints[]= new WifiAccessPoint[3];
        filteredWifiAccessPoints[0] = scannedWifiAccessPoints.get(0);
        filteredWifiAccessPoints[1] = scannedWifiAccessPoints.get(1);
        filteredWifiAccessPoints[2] = scannedWifiAccessPoints.get(2);
        return filteredWifiAccessPoints;
    }
}
