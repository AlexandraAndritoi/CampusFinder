package com.upt.ac.campusfinderapp.indoorlocation;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Build;

import com.navisens.motiondnaapi.WifiScanner;

import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import io.indoorlocation.core.IndoorLocationProvider;

public class WifiIndoorLocationProvider extends IndoorLocationProvider implements WifiScanner.Listener {

    private Context context;
    private JSONObject wifiAccessPoint;
    private boolean isStarted = false;

    private static final long WIFI_SCAN_RATE = 10000L;
    private static final int WIFI_SIGNAL_STRENGTH_MAX = -120;

    public WifiIndoorLocationProvider(Context context) {
        this.context = context;
        WifiScanner.addListener(context,this, WIFI_SCAN_RATE);
    }

    @Override
    public void onScanResult(List<ScanResult> list) {
        try {
            for(Iterator iterator = list.iterator(); iterator.hasNext(); ) {
                ScanResult scanResult = (ScanResult)iterator.next();
                if(scanResult.level > WIFI_SIGNAL_STRENGTH_MAX){
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
                        wifiAccessPoint.put("venue", scanResult.venueName);
                    }
                    System.out.print(wifiAccessPoint.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
}
