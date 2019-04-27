package com.upt.ac.campusfinderapp.indoormap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.upt.ac.campusfinderapp.R;
import com.upt.ac.campusfinderapp.navisens.NavisensIndoorLocationProvider;

import io.indoorlocation.core.IndoorLocation;
import io.indoorlocation.manual.ManualIndoorLocationProvider;
import io.mapwize.mapwizecomponents.ui.MapwizeFragment;
import io.mapwize.mapwizecomponents.ui.MapwizeFragmentUISettings;
import io.mapwize.mapwizeformapbox.api.LatLngFloor;
import io.mapwize.mapwizeformapbox.api.MapwizeObject;
import io.mapwize.mapwizeformapbox.map.MapOptions;
import io.mapwize.mapwizeformapbox.map.MapwizePlugin;
import io.mapwize.mapwizeformapbox.map.MapwizePluginFactory;

public class IndoorMapFragment extends Fragment implements MapwizeFragment.OnFragmentInteractionListener {

    private MapwizeFragment mapwizeFragment;
    private MapboxMap mapbox;
    private MapwizePlugin mapwizePlugin;
    private ManualIndoorLocationProvider manualIndoorLocationProvider;
    private NavisensIndoorLocationProvider navisensIndoorLocationProvider;

    private static final String NAVISENS_API_KEY = "";
    private static final String VENUE_ID = "";
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapOptions options = new MapOptions.Builder()
                                .restrictContentToVenue(VENUE_ID)
                                .centerOnVenue(VENUE_ID)
                                .build();
        MapwizeFragmentUISettings settings = new MapwizeFragmentUISettings.Builder()
                                                .menuButtonHidden(true)
                                                .build();
        mapwizeFragment = MapwizeFragment.newInstance(options, settings);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_indoor_map, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();

        ft.add(R.id.fragmentContainer, mapwizeFragment);

        ft.commit();
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            setupLocationProvider();
        }
    }

    private void setupLocationProvider() {
        IndoorLocation manualIndoorLocation = new IndoorLocation("Manual", 45.747338, 21.226126, (double)0, System.currentTimeMillis());
        manualIndoorLocationProvider = new ManualIndoorLocationProvider();
        manualIndoorLocationProvider.setIndoorLocation(manualIndoorLocation);
        navisensIndoorLocationProvider = new NavisensIndoorLocationProvider(getContext(), NAVISENS_API_KEY, manualIndoorLocationProvider);
        mapwizePlugin.setLocationProvider(navisensIndoorLocationProvider);
    }

    @Override
    public void onMenuButtonClick() {

    }

    @Override
    public void onInformationButtonClick(MapwizeObject mapwizeObject) {

    }

    @Override
    public void onFragmentReady(MapboxMap mapboxMap, MapwizePlugin mapwizePlugin) {
        mapbox = mapboxMap;
        this.mapwizePlugin = mapwizePlugin;

        IndoorLocation manualIndoorLocation = new IndoorLocation("Manual", 45.747338, 21.226126, (double)0, System.currentTimeMillis());
        manualIndoorLocationProvider.setIndoorLocation(manualIndoorLocation);
        manualIndoorLocationProvider = new ManualIndoorLocationProvider();
        this.mapwizePlugin.setLocationProvider(manualIndoorLocationProvider);

//        this.mapwizePlugin.addOnLongClickListener(clickEvent -> {
//            LatLngFloor latLngFloor = clickEvent.getLatLngFloor();
//            IndoorLocation indoorLocation = new IndoorLocation(manualIndoorLocationProvider.getName(), latLngFloor.getLatitude(), latLngFloor.getLongitude(), latLngFloor.getFloor(), System.currentTimeMillis());
//            manualIndoorLocationProvider.setIndoorLocation(indoorLocation);
//        });
        this.mapwizePlugin.setOnDidLoadListener( plugin -> {
            requestLocationPermission();
        });

        this.mapwizePlugin.addOnClickListener(clickEvent -> {
            LatLngFloor latLngFloor = clickEvent.getLatLngFloor();
            IndoorLocation indoorLocation = new IndoorLocation(manualIndoorLocationProvider.getName(), latLngFloor.getLatitude(), latLngFloor.getLongitude(), latLngFloor.getFloor(), System.currentTimeMillis());
            manualIndoorLocationProvider.dispatchIndoorLocationChange(indoorLocation);
        });
    }

    @Override
    public void onFollowUserButtonClickWithoutLocation() {

    }
}
