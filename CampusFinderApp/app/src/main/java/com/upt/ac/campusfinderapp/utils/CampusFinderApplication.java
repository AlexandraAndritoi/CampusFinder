package com.upt.ac.campusfinderapp.utils;

import android.app.Application;

import io.mapwize.mapwizeformapbox.AccountManager;

public class CampusFinderApplication extends Application {

    public static final String MAPWIZE_API_KEY = "";
    public static final String NAVISENS_API_KEY = "";
    public static final String MAPWIZE_VENUE_ID = "";

    @Override
    public void onCreate() {
        super.onCreate();
        AccountManager.start(this, MAPWIZE_API_KEY);
        WifiAccessPointRepository.getInstance().queryWifiAccessPointsFromFirebase();
    }
}
