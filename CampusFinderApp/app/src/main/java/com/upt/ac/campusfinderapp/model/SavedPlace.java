package com.upt.ac.campusfinderapp.model;

import com.google.android.libraries.places.api.model.Place;

public class SavedPlace {

    private String placeId;
    private String name;
    private String address;

    private double latitude;
    private double longitude;


    public SavedPlace(Place place) {
        placeId = place.getId();
        name = place.getName();
        address = place.getAddress();
        latitude = place.getLatLng().latitude;
        longitude = place.getLatLng().longitude;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
