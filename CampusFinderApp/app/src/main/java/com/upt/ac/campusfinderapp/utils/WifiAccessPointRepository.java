package com.upt.ac.campusfinderapp.utils;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upt.ac.campusfinderapp.model.WifiAccessPoint;

import java.util.ArrayList;
import java.util.List;

public class WifiAccessPointRepository {

    private static final WifiAccessPointRepository repository = new WifiAccessPointRepository();
    private List<WifiAccessPoint> wifiAccessPoints = new ArrayList<>();
    private static final String WIFI_ACCESS_POINT = "wifiaccesspoint";

    private WifiAccessPointRepository() {}

    public static WifiAccessPointRepository getInstance() {
        return repository;
    }

    public void queryWifiAccessPointsFromFirebase(){
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.child(WIFI_ACCESS_POINT).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    WifiAccessPoint wap = ds.getValue(WifiAccessPoint.class);
                    if(wap != null){
                        wifiAccessPoints.add(wap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public List<WifiAccessPoint> getWifiAccessPoints() {
        return wifiAccessPoints;
    }
}
