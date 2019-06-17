package com.upt.ac.campusfinderapp.navisens;

import android.content.Context;
import android.content.pm.PackageManager;

import com.navisens.motiondnaapi.MotionDna;
import com.navisens.motiondnaapi.MotionDnaApplication;
import com.navisens.motiondnaapi.MotionDnaInterface;
import com.upt.ac.campusfinderapp.utils.CampusFinderApplication;

import java.util.Map;

import io.indoorlocation.core.IndoorLocation;
import io.indoorlocation.core.IndoorLocationProvider;
import io.indoorlocation.core.IndoorLocationProviderListener;

public class NavisensIndoorLocationProvider extends IndoorLocationProvider implements MotionDnaInterface, IndoorLocationProviderListener {

    private Context mContext;

    private MotionDnaApplication mMotionDna;
    private String mNavisensKey;
    private IndoorLocationProvider mSourceProvider;

    private boolean mStarted = false;
    private Double mCurrentFloor = null;

    private int mUpdateRate = 1000;
    private MotionDna.PowerConsumptionMode mPowerMode = MotionDna.PowerConsumptionMode.PERFORMANCE;

    public NavisensIndoorLocationProvider(Context context, String navisensKey) {
        super();
        mContext = context;
        mNavisensKey = navisensKey;
        mMotionDna = new MotionDnaApplication(this);
    }

    public NavisensIndoorLocationProvider(Context context, String navisensKey, IndoorLocationProvider sourceProvider) {
        super();
        mContext = context;
        mNavisensKey = navisensKey;
        mSourceProvider = sourceProvider;
        mSourceProvider.addListener(this);
        mMotionDna = new MotionDnaApplication(this);
        mMotionDna.runMotionDna(CampusFinderApplication.NAVISENS_API_KEY);
    }

    public MotionDnaApplication getMotionDna() {
        return mMotionDna;
    }

    public void setIndoorLocation(IndoorLocation indoorLocation) {
        dispatchIndoorLocationChange(indoorLocation);
        mCurrentFloor = indoorLocation.getFloor();
        mMotionDna.setLocationLatitudeLongitude(indoorLocation.getLatitude(), indoorLocation.getLongitude());
        mMotionDna.setHeadingMagInDegrees();
        if (mCurrentFloor != null) {
            mMotionDna.setFloorNumber(mCurrentFloor.intValue());
        }
    }

    @Override
    public void receiveMotionDna(MotionDna motionDna) {
        MotionDna.Location location = motionDna.getLocation();
        mCurrentFloor = (double)location.floor;
        IndoorLocation indoorLocation = new IndoorLocation(getName(), location.globalLocation.latitude, location.globalLocation.longitude, mCurrentFloor, System.currentTimeMillis());
        dispatchIndoorLocationChange(indoorLocation);
    }

    @Override
    public void receiveNetworkData(MotionDna motionDna) {

    }

    @Override
    public void receiveNetworkData(MotionDna.NetworkCode networkCode, Map<String, ?> map) {

    }

    @Override
    public void reportError(MotionDna.ErrorCode errorCode, String s) {
        this.dispatchOnProviderError(new Error(errorCode.toString() + " " + s));
    }

    @Override
    public Context getAppContext() {
        return mContext.getApplicationContext();
    }

    @Override
    public PackageManager getPkgManager() {
        return mContext.getPackageManager();
    }

    @Override
    public void onProviderStarted() {
        this.dispatchOnProviderStarted();
    }

    @Override
    public void onProviderStopped() {
        this.dispatchOnProviderStopped();
    }

    @Override
    public void onProviderError(Error error) {
        dispatchOnProviderError(error);
    }

    @Override
    public void onIndoorLocationChange(IndoorLocation indoorLocation) {
        setIndoorLocation(indoorLocation);
    }

    @Override
    public boolean supportsFloor() {
        return false;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {
        mMotionDna.stop();
    }

    @Override
    public boolean isStarted() {
        return false;
    }
}
