package com.upt.ac.campusfinderapp.savedplaces;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.upt.ac.campusfinderapp.R;
import com.upt.ac.campusfinderapp.model.SavedPlace;
import com.upt.ac.campusfinderapp.outdoormap.OutdoorMapFragment;

import java.util.ArrayList;

public class SavedPlaceRecyclerAdapter extends RecyclerView.Adapter<SavedPlacesViewHolder> {

    private ArrayList<SavedPlace> savedPlaces;
    private Context context;

    SavedPlaceRecyclerAdapter(Context context, ArrayList<SavedPlace> savedPlaces) {
        this.context = context;
        this.savedPlaces = savedPlaces;
    }

    @NonNull
    @Override
    public SavedPlacesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.saved_places_list_layout, viewGroup, false);
        SavedPlacesViewHolder savedPlacesViewHolder = new SavedPlacesViewHolder(view);
        savedPlacesViewHolder.setOnSavedPlaceClickListener(new OnSavedPlaceClickListener() {
            @Override
            public void onSavedPlaceItemClick(String name, int position) {
                Log.i("ADAPTER", "It's working");
                startOutdoorMapFragmentWithSavedPlace(name);
            }

            private void startOutdoorMapFragmentWithSavedPlace(String name) {
                OutdoorMapFragment fragment = new OutdoorMapFragment();
                Bundle args = new Bundle();
                args.putString("Place name", name);
                fragment.setArguments(args);
                ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.screen_area, fragment).commit();
            }
        });

        return savedPlacesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SavedPlacesViewHolder savedPlacesViewHolder, int i) {
        SavedPlace savedPlace = savedPlaces.get(i);
        savedPlacesViewHolder.displaySavedPlace(savedPlace.getName());
    }

    @Override
    public int getItemCount() {
        return savedPlaces.size();
    }
}
