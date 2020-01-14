package com.example.wifinder.data.model;

public class Favorite {

    public String email;
    public Integer spotId;
    public String spotName;
    public String spotAddress;
    public Integer isFavorite;

    public Favorite() {
    }

    public Favorite(String email, Integer spotId, String spotName, String spotAddress, Integer isFavorite) {
        this.email = email;
        this.spotId = spotId;
        this.spotName = spotName;
        this.spotAddress = spotAddress;
        this.isFavorite = isFavorite;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getSpotId() {
        return spotId;
    }

    public void setSpotId(Integer spotId) {
        this.spotId = spotId;
    }

    public String getSpotName() {
        return spotName;
    }

    public void setSpotName(String spotName) {
        this.spotName = spotName;
    }

    public String getSpotAddress() {
        return spotAddress;
    }

    public void setSpotAddress(String spotAddress) {
        this.spotAddress = spotAddress;
    }

    public Integer getIsFavorite() {
        return isFavorite;
    }

    public void setIsFavorite(Integer isFavorite) {
        this.isFavorite = isFavorite;
    }
}
