package com.example.wifinder.data.model;

public class Spot {
    private int id;
    private String spotname;
    private double longitude, latitude; // 経度　緯度

    public Spot(int id, String spotName, double longitude, double latitude) {
        this.id = id;
        this.spotname = spotName;
        this.longitude = longitude;
        this.latitude = latitude;
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
