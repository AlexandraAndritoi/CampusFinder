package com.upt.ac.campusfinderapp.savedplaces;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.upt.ac.campusfinderapp.R;
import com.upt.ac.campusfinderapp.model.SavedPlace;

import java.util.ArrayList;

public class SavedPlaceRecyclerAdapter extends RecyclerView.Adapter<SavedPlacesViewHolder> {

    private ArrayList<SavedPlace> savedPlaces;

    public SavedPlaceRecyclerAdapter() { }

    public SavedPlaceRecyclerAdapter(ArrayList<SavedPlace> savedPlaces) {
        this.savedPlaces = savedPlaces;
    }

    @NonNull
    @Override
    public SavedPlacesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.saved_places_list_layout, viewGroup, false);

        return new SavedPlacesViewHolder(view);
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

    public class SavedPlaceViewholder extends RecyclerView.ViewHolder {

        public SavedPlaceViewholder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
