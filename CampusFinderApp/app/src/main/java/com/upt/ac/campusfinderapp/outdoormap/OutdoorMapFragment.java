package com.upt.ac.campusfinderapp.outdoormap;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.map.MapFragment;
import com.tomtom.online.sdk.map.OnMapReadyCallback;
import com.tomtom.online.sdk.map.TomtomMap;
import com.tomtom.online.sdk.map.TomtomMapCallback;
import com.upt.ac.campusfinderapp.R;

public class OutdoorMapFragment extends Fragment implements OnMapReadyCallback,
        TomtomMapCallback.OnMapLongClickListener {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(getActivity(), "OutdoorMap Fragment", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_outdoor_map, null);
        initTomTomServices();
        initUIViews();
        setupUIViewListeners();
        return v;
    }

    private void initTomTomServices() {
        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.tomtom_map);
        mapFragment.getAsyncMap(this);
    }

    private void initUIViews() {}
    private void setupUIViewListeners() {}

    @Override
    public void onMapReady(@NonNull TomtomMap tomtomMap) {}

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {}
}
