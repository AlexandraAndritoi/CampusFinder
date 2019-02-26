package com.upt.ac.campusfinderapp.savedplaces;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.upt.ac.campusfinderapp.R;
import com.upt.ac.campusfinderapp.model.SavedPlace;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

public class SavedPlacesFirebaseRecycleAdapter extends FirebaseRecyclerAdapter<SavedPlace, SavedPlacesViewHolder> {

    public SavedPlacesFirebaseRecycleAdapter(@NonNull FirebaseRecyclerOptions<SavedPlace> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull SavedPlacesViewHolder holder, int position, @NonNull SavedPlace model) {
        holder.displaySavedPlace(model.getName());
    }

    @NonNull
    @Override
    public SavedPlacesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.saved_places_list_layout, viewGroup, false);

        return new SavedPlacesViewHolder(view);
    }
}
