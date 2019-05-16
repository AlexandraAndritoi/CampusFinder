package com.upt.ac.campusfinderapp.indoorlocation;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.preference.PreferenceManager;

import com.navisens.motiondnaapi.WifiScanner;
import com.upt.ac.campusfinderapp.model.WifiAccessPoint;

import java.util.ArrayList;
import java.util.List;

import io.indoorlocation.core.IndoorLocation;
import io.indoorlocation.core.IndoorLocationProvider;

public class WifiIndoorLocationProvider extends IndoorLocationProvider implements WifiScanner.Listener {

    private Context context;
    private SharedPreferences sharedPreferences;
    private IndoorLocation indoorLocation;
    private IndoorLocationCalculator indoorLocationCalculator;
    private WifiAccessPoint wifiAccessPoint;
    private boolean isStarted = false;

    private static final long WIFI_SCAN_RATE = 10000L;

    public WifiIndoorLocationProvider(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        indoorLocationCalculator = new IndoorLocationCalculator(context);
        WifiScanner.addListener(context,this, WIFI_SCAN_RATE);
    }

    @Override
    public void onScanResult(List<ScanResult> scanResults) {
        double signalLevelInDbMax = -120;
        for(ScanResult scanResult: scanResults) {
            if(scanResult.level > signalLevelInDbMax){
                signalLevelInDbMax = scanResult.level;
                wifiAccessPoint = new WifiAccessPoint();
                wifiAccessPoint.setSSID(scanResult.SSID);
                wifiAccessPoint.setBSSID(scanResult.BSSID);
                wifiAccessPoint.setLevel(scanResult.level);
                wifiAccessPoint.setFrequency(scanResult.frequency);
            }
        }
        boolean isWifiBasedIndoorLocationEnabled = sharedPreferences.getBoolean("wifi_indoor_location", true);
        if(wifiAccessPoint != null && isWifiBasedIndoorLocationEnabled){
            indoorLocation = indoorLocationCalculator.calculateIndoorLocationFromWifiAccessPointData(wifiAccessPoint);
        }
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

    public IndoorLocation getIndoorLocation(){
        return indoorLocation;
    }
}
