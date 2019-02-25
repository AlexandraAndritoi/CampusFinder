package com.upt.ac.campusfinderapp.model;

import com.google.android.libraries.places.api.model.Place;

public class SavedPlace {

    private String address;
    private float latitude;
    private float longitude;
    private String name;
    private String placeId;

    public SavedPlace() {}

    public SavedPlace(String address, float latitude, float longitude, String name, String placeId) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.placeId = placeId;
    }

    public SavedPlace(Place place) {
        placeId = place.getId();
        name = place.getName();
        address = place.getAddress();
        latitude = (float) place.getLatLng().latitude;
        longitude = (float) place.getLatLng().longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
