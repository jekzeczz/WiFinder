package com.example.wifinder.data.model;

public class Spot {
    private String username, email;
    private int id;
    
    public Spot(int id, String username, String email) {
    private String spotname;
    private double longitude, latitude; // 緯度　経度

    public Spot(int id, String spotName, double longitude, double latitude) {
        this.id = id;
        this.spotname = spotName;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public String getSpotname() {
        return spotname;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

}
