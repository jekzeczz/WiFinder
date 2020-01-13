package com.example.wifinder.data.model;

public class Spots {

    public Integer id;
    public String name;
    public String address;
    public String ssid;
    public double latitude;
    public double longitude;

    public Spots() {
    }

    public Spots(Integer id, String name, String address, String ssid, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.ssid = ssid;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
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
