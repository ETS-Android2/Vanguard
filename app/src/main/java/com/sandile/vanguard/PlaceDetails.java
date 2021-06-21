package com.sandile.vanguard;

import java.net.URL;

public class PlaceDetails {
    private String address, id, name;
    private double latitude, longitude;
    private URL icon;

    public PlaceDetails(){
    }

    public PlaceDetails(String address, String id, String name, double latitude, double longitude) {
        this.address = address;
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public URL getIcon() {
        return icon;
    }

    public void setAIcon(URL icon) {
        this.icon = icon;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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