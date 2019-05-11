package com.upt.ac.campusfinderapp.indoorlocation;

import android.content.Context;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.os.Build;

import com.navisens.motiondnaapi.WifiScanner;

import org.json.JSONObject;

import java.util.List;

import io.indoorlocation.core.IndoorLocationProvider;

public class WifiIndoorLocationProvider extends IndoorLocationProvider implements WifiScanner.Listener {

    private Context context;
    private JSONObject wifiAccessPoint;
    private boolean isStarted = false;

    private static final long WIFI_SCAN_RATE = 10000L;

    public WifiIndoorLocationProvider(Context context) {
        this.context = context;
        WifiScanner.addListener(context,this, WIFI_SCAN_RATE);
    }

    @Override
    public void onScanResult(List<ScanResult> scanResults) {
        double signalLevelInDbMax = -120;
        try {
            for(ScanResult scanResult: scanResults) {
                if(scanResult.level > signalLevelInDbMax){
                    signalLevelInDbMax = scanResult.level;
                    wifiAccessPoint = new JSONObject();
                    wifiAccessPoint.put("SSID", scanResult.SSID);
                    wifiAccessPoint.put("BSSID", scanResult.BSSID);
                    wifiAccessPoint.put("capabilities", scanResult.capabilities);
                    wifiAccessPoint.put("level", scanResult.level);
                    wifiAccessPoint.put("frequency", scanResult.frequency);
                    if (Build.VERSION.SDK_INT >= 23) {
                        wifiAccessPoint.put("channelWidth", scanResult.channelWidth);
                        wifiAccessPoint.put("centerFreq0", scanResult.centerFreq0);
                        wifiAccessPoint.put("centerFreq1", scanResult.centerFreq1);
                    }
                }
            }
            if(wifiAccessPoint != null){
                double distance = calculateDistanceFromDeviceToWifiAccessPoint((int)wifiAccessPoint.get("level"), (int)wifiAccessPoint.get("frequency"));
                Location location = calculateLocation(distance);
                System.out.print(distance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final double EARTH_RADIUS = 6378.137;
    private static final double K = (1 / ((2 * Math.PI / 360) * EARTH_RADIUS)) / 1000;
    private static final double latitude = 45.774568;
    private static final double longitude = 21.244662;

    private double calculateDistanceFromDeviceToWifiAccessPoint(int signalLevelInDb, int freqInMHz){
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(signalLevelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }

    private Location calculateLocation(double distance) {
        double newLatitude = latitude + (distance * K);
        double newLongitude = longitude + (distance * K) / Math.cos(newLatitude * (Math.PI / 180));
        Location location = new Location("");
        location.setLatitude(newLatitude);
        location.setLongitude(newLongitude);
        return location;
    }

    @Override
    public boolean supportsFloor() {
        return true;
    }

    @Override
    public void start() {
        isStarted = true;
        WifiScanner.addListener(context,this, WIFI_SCAN_RATE);
    }

    @Override
    public void stop() {
        isStarted = false;
        WifiScanner.removeListener(this);
    }

    @Override
    public boolean isStarted() {
        return isStarted;
    }
}
