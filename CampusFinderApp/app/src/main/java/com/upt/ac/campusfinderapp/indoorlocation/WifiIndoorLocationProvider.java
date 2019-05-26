package com.upt.ac.campusfinderapp.indoorlocation;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.preference.PreferenceManager;

import com.navisens.motiondnaapi.WifiScanner;
import com.upt.ac.campusfinderapp.model.WifiAccessPoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.indoorlocation.core.IndoorLocation;
import io.indoorlocation.core.IndoorLocationProvider;

public class WifiIndoorLocationProvider extends IndoorLocationProvider implements WifiScanner.Listener {

    private Context context;
    private SharedPreferences sharedPreferences;
    private IndoorLocation indoorLocation;
    private IndoorLocationCalculator indoorLocationCalculator;
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
        WifiAccessPoint wifiAccessPoints[]= new WifiAccessPoint[3];
        if(scanResults != null && scanResults.size()>0){
            Collections.sort(scanResults, (o1, o2) -> Integer.compare(o2.level, o1.level));
            for(int i=0; i<3; i++){
                ScanResult scanResult = scanResults.get(i);
                wifiAccessPoints[i] = new WifiAccessPoint();
                wifiAccessPoints[i].setSSID(scanResult.SSID);
                wifiAccessPoints[i].setBSSID(scanResult.BSSID);
                wifiAccessPoints[i].setLevel(scanResult.level);
                wifiAccessPoints[i].setFrequency(scanResult.frequency);
            }
            boolean isWifiBasedIndoorLocationEnabled = sharedPreferences.getBoolean("wifi_indoor_location", true);
            if(isWifiBasedIndoorLocationEnabled){
                indoorLocation = indoorLocationCalculator.calculateIndoorLocationFromWifiAccessPointData(wifiAccessPoints);
            }
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
