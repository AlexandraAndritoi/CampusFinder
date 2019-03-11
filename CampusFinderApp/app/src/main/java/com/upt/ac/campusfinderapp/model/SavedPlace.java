package com.upt.ac.campusfinderapp.model;

public class SavedPlace {
    
    private String placeId;
    private String name;
    private String address;
    private double latitude;
    private double longitude;

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
