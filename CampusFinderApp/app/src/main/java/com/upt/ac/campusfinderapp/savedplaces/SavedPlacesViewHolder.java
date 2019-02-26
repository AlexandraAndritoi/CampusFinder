package com.upt.ac.campusfinderapp.savedplaces;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.upt.ac.campusfinderapp.R;

class SavedPlacesViewHolder extends RecyclerView.ViewHolder  {

    private View mView;

    private TextView savedPlaceName;
    private ImageButton goToSavedPlaceButton;

    SavedPlacesViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;

        savedPlaceName = mView.findViewById(R.id.SavedPlaceTextView);
        goToSavedPlaceButton = mView.findViewById(R.id.GoToSavedPlaceButton);
    }

    void displaySavedPlace(String name) {
        savedPlaceName.setText(name);
    }
}
