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
import com.upt.ac.campusfinderapp.R;
import com.upt.ac.campusfinderapp.utils.CampusFinderApplication;

import io.mapwize.mapwizecomponents.ui.MapwizeFragment;
import io.mapwize.mapwizecomponents.ui.MapwizeFragmentUISettings;
import io.mapwize.mapwizeformapbox.map.MapOptions;

public class IndoorMapFragment extends Fragment {

    private MapwizeFragment mapwizeFragment;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapOptions options = new MapOptions.Builder()
                                .restrictContentToVenue(CampusFinderApplication.MAPWIZE_VENUE_ID)
                                .centerOnVenue(CampusFinderApplication.MAPWIZE_VENUE_ID)
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
        assert fragmentManager != null;
        FragmentTransaction ft = fragmentManager.beginTransaction();

        ft.add(R.id.fragmentContainer, mapwizeFragment);

        ft.commit();
    }
}
