package com.upt.ac.campusfinderapp.model;

import android.location.Location;

public class SavedPlace {
    private Location location;

    public SavedPlace(Location location) {
        this.location = location;
    }

    public String getLocationName() {
        // TODO: Get location name
        return location.toString();
    }

    public Location getLocation() {
        return location;
    }
}
