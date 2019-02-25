package com.upt.ac.campusfinderapp.dao;

import android.arch.lifecycle.LifecycleOwner;
import android.support.v7.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.upt.ac.campusfinderapp.model.SavedPlace;
import com.upt.ac.campusfinderapp.savedplaces.SavedPlacesRecycleAdapter;

public class SavedPlaceDAO {

    private DatabaseReference databaseReference;

    public SavedPlaceDAO() {
        databaseReference = FirebaseDatabase.getInstance().getReference("savedplace");
    }

    public void firebaseSavedPlaceSearch(LifecycleOwner lifecycleOwner, RecyclerView recyclerView) {
        Query query = databaseReference;

        FirebaseRecyclerOptions<SavedPlace> options =
                new FirebaseRecyclerOptions.Builder<SavedPlace>()
                        .setQuery(query, SavedPlace.class)
                        .setLifecycleOwner(lifecycleOwner)
                        .build();

        SavedPlacesRecycleAdapter savedPlacesRecycleAdapter = new SavedPlacesRecycleAdapter(options);
        recyclerView.setAdapter(savedPlacesRecycleAdapter);
    }

    public void firebaseSavedPlaceUserSearch(LifecycleOwner lifecycleOwner, RecyclerView recyclerView, String string){

        Query query = databaseReference.orderByChild("name").startAt(string).endAt(string + "\uf8ff");

        FirebaseRecyclerOptions<SavedPlace> options =
                new FirebaseRecyclerOptions.Builder<SavedPlace>()
                        .setQuery(query, SavedPlace.class)
                        .setLifecycleOwner(lifecycleOwner)
                        .build();

        SavedPlacesRecycleAdapter savedPlacesRecycleAdapter = new SavedPlacesRecycleAdapter(options);
        recyclerView.setAdapter(savedPlacesRecycleAdapter);
    }
}
