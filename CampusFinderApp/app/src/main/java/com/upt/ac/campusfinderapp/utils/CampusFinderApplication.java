package com.upt.ac.campusfinderapp.utils;

import android.app.Application;

import io.mapwize.mapwizeformapbox.AccountManager;

public class CampusFinderApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AccountManager.start(this, "");
    }
}
