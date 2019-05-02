package com.upt.ac.campusfinderapp.indoorlocation;

import io.indoorlocation.core.IndoorLocationProvider;

public class WifiIndoorLocationProvider extends IndoorLocationProvider {

    private static final WifiIndoorLocationProvider provider = new WifiIndoorLocationProvider();

    private WifiIndoorLocationProvider() {
        super();
    }

    private boolean isStarted = false;

    @Override
    public boolean supportsFloor() {
        return true;
    }

    @Override
    public void start() {
        isStarted = true;
    }

    @Override
    public void stop() {
        isStarted = false;
    }

    @Override
    public boolean isStarted() {
        return isStarted;
    }

    public static WifiIndoorLocationProvider getInstance() {
        return provider;
    }
}
