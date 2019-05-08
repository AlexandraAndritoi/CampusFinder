package com.upt.ac.campusfinderapp.indoorlocation;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Build;

import com.navisens.motiondnaapi.WifiScanner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import io.indoorlocation.core.IndoorLocationProvider;

public class WifiIndoorLocationProvider extends IndoorLocationProvider implements WifiScanner.Listener {

    private Context context;

    private boolean isStarted = false;
    private static final long wifiScanRate = 10000L;

    public WifiIndoorLocationProvider(Context context) {
        this.context = context;
        WifiScanner.addListener(context,this, wifiScanRate);
    }

    @Override
    public void onScanResult(List<ScanResult> list) {
        try {
            JSONArray var2 = new JSONArray();

            JSONObject var5;
            for(Iterator var3 = list.iterator(); var3.hasNext(); var2.put(var5)) {
                ScanResult var4 = (ScanResult)var3.next();
                var5 = new JSONObject();
                var5.put("SSID", var4.SSID);
                var5.put("BSSID", var4.BSSID);
                var5.put("capabilities", var4.capabilities);
                var5.put("level", var4.level);
                var5.put("frequency", var4.frequency);
                if (Build.VERSION.SDK_INT >= 23) {
                    var5.put("channelWidth", var4.channelWidth);
                    var5.put("centerFreq0", var4.centerFreq0);
                    var5.put("centerFreq1", var4.centerFreq1);
                }
                System.out.print(var5.toString());
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        }
    }

    @Override
    public boolean supportsFloor() {
        return true;
    }

    @Override
    public void start() {
        isStarted = true;
        WifiScanner.addListener(context,this, wifiScanRate);
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
