package com.upt.ac.campusfinderapp.indoormap;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.dynamic.SupportFragmentWrapper;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.upt.ac.campusfinderapp.R;

import io.indoorlocation.manual.ManualIndoorLocationProvider;
import io.mapwize.mapwizecomponents.ui.MapwizeFragment;
import io.mapwize.mapwizecomponents.ui.MapwizeFragmentUISettings;
import io.mapwize.mapwizeformapbox.api.MapwizeObject;
import io.mapwize.mapwizeformapbox.map.MapOptions;
import io.mapwize.mapwizeformapbox.map.MapwizePlugin;

public class IndoorMapFragment extends Fragment implements MapwizeFragment.OnFragmentInteractionListener {

    private MapwizeFragment mapwizeFragment;
    private Mapbox mapbox;
    private MapwizePlugin mapwizePlugin;
    private ManualIndoorLocationProvider manualIndoorLocationProvider;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapOptions options = new MapOptions.Builder()
                                .centerOnVenue("")
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

        Toast.makeText(getActivity(), "IndoorMap Fragment", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMenuButtonClick() {

    }

    @Override
    public void onInformationButtonClick(MapwizeObject mapwizeObject) {

    }

    @Override
    public void onFragmentReady(MapboxMap mapboxMap, MapwizePlugin mapwizePlugin) {

    }

    @Override
    public void onFollowUserButtonClickWithoutLocation() {

    }
}
