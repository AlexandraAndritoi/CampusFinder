package com.upt.ac.campusfinderapp.indoorlocation;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.preference.PreferenceManager;

import com.navisens.motiondnaapi.WifiScanner;
import com.upt.ac.campusfinderapp.model.WifiAccessPoint;
import com.upt.ac.campusfinderapp.utils.WifiFilter;

import java.util.ArrayList;
import java.util.Collections;
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
        boolean isWifiBasedIndoorLocationEnabled = sharedPreferences.getBoolean("wifi_indoor_location", true);
        WifiFilter wifiFilter = new WifiFilter();
        if(scanResults != null && scanResults.size()>0){
            WifiAccessPoint filteredWifiAccessPoints[] =
                    wifiFilter.filterWifiAccessPoints(parseScanResultsIntoWifiAccessPoints(scanResults));
            if(isWifiBasedIndoorLocationEnabled){
                indoorLocation = indoorLocationCalculator.calculateIndoorLocationFromWifiAccessPointData(filteredWifiAccessPoints);
            }
        }
    }

    private List<WifiAccessPoint> parseScanResultsIntoWifiAccessPoints(List<ScanResult> scanResults){
        List<WifiAccessPoint> scannedFifiAccessPoints =  new ArrayList<>();
        Collections.sort(scanResults, (o1, o2) -> Integer.compare(o2.level, o1.level));
        for(ScanResult scanResult: scanResults){
            WifiAccessPoint wifiAccessPoint = new WifiAccessPoint();
            wifiAccessPoint.setSSID(scanResult.SSID);
            wifiAccessPoint.setLevel(scanResult.level);
            wifiAccessPoint.setFrequency(scanResult.frequency);
            scannedFifiAccessPoints.add(wifiAccessPoint);
        }
        return  scannedFifiAccessPoints;
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
