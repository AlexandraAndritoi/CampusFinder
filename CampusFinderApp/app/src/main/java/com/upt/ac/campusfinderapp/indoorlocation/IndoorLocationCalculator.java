package com.upt.ac.campusfinderapp.indoorlocation;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.upt.ac.campusfinderapp.model.WifiAccessPoint;

import io.indoorlocation.core.IndoorLocation;

class IndoorLocationCalculator {
    private static final double EARTH_RADIUS = 6378.137;
    private static final double K = (1 / ((2 * Math.PI / 360) * EARTH_RADIUS)) / 1000;

    private DatabaseReference mDatabaseReference;
    private static final String WIFIACCESSPOINT = "wifiaccesspoint";
    private static final String SSID = "ssid";

    IndoorLocationCalculator(){
        mDatabaseReference = FirebaseDatabase.getInstance().getReference(WIFIACCESSPOINT);
    }


    private double latitude = 45.774568;
    private double longitude = 21.244662;

    IndoorLocation calculateIndoorLocationFromWifiAccessPointData(WifiAccessPoint wifiAccessPoint) {

        Query query = mDatabaseReference.orderByChild(SSID).equalTo(wifiAccessPoint.getSSID());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    WifiAccessPoint wap = ds.getValue(WifiAccessPoint.class);
                    if(wap != null) {
                        latitude = wap.getLatitude();
                        longitude = wap.getLongitude();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(wifiAccessPoint.getSSID().equals("B414")){
            latitude = 45.747170;
            longitude = 21.226210;
        }
        if(wifiAccessPoint.getSSID().equals("cmrssi")){
            latitude = 45.747247;
            longitude = 21.226090;
        }
        if(wifiAccessPoint.getSSID().equals("B424_WiFi")){
            latitude = 45.747441;
            longitude = 21.226276;
        }
        if(wifiAccessPoint.getSSID().equals("UPT-eduroam") && wifiAccessPoint.getBSSID().contains("51:00")){
            latitude = 45.747247;
            longitude = 21.226208;
        }
        double distance = calculateDistanceFromDeviceToWifiAccessPoint(wifiAccessPoint.getLevel(), wifiAccessPoint.getFrequency());
        return calculateIndoorLocation(distance);
    }

    private double calculateDistanceFromDeviceToWifiAccessPoint(int signalLevelInDb, int freqInMHz){
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + Math.abs(signalLevelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }

    private IndoorLocation calculateIndoorLocation(double distance) {
        Double newLatitude = latitude + (distance * K);
        Double newLongitude = longitude + (distance * K) / Math.cos(newLatitude * (Math.PI / 180));
        return new IndoorLocation(WifiIndoorLocationProvider.class.getName(), newLatitude, newLongitude, 0.0, System.currentTimeMillis());
    }
}
