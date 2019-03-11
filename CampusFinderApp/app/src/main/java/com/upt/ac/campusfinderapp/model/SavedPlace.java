package com.upt.ac.campusfinderapp.model;

import com.google.android.libraries.places.api.model.Place;

public class SavedPlace {

    private String address;
    private double latitude;
    private double longitude;
    private String name;
    private String placeId;

    public SavedPlace() {}

    public SavedPlace(String address, double latitude, double longitude, String name, String placeId) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.placeId = placeId;
    }

    public SavedPlace(String address, double latitude, double longitude, String name) {
        this(address, latitude, longitude, name, null);
    }

    public SavedPlace(Place place) {
        placeId = place.getId();
        name = place.getName();
        address = place.getAddress();
        latitude = place.getLatLng().latitude;
        longitude = place.getLatLng().longitude;
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
