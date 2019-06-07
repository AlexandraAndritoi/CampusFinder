package com.upt.ac.campusfinderapp.utils;

import android.net.wifi.ScanResult;

import com.upt.ac.campusfinderapp.model.WifiAccessPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WifiParser {
    public List<WifiAccessPoint> parseScanResultsIntoWifiAccessPoints(List<ScanResult> scanResults){
        List<WifiAccessPoint> scannedFifiAccessPoints =  new ArrayList<>();
        Collections.sort(scanResults, (o1, o2) -> Integer.compare(o2.level, o1.level));
        for(ScanResult scanResult: scanResults){
            WifiAccessPoint wifiAccessPoint = new WifiAccessPoint();
            wifiAccessPoint.setSSID(scanResult.SSID);
            wifiAccessPoint.setBSSID(scanResult.BSSID);
            wifiAccessPoint.setLevel(scanResult.level);
            wifiAccessPoint.setFrequency(scanResult.frequency);
            scannedFifiAccessPoints.add(wifiAccessPoint);
        }
        return  scannedFifiAccessPoints;
    }
}
