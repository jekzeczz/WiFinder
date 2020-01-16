package com.example.wifinder.data.model;

public class RatingResult {
    private Integer sumRating;
    private Integer numRating;

    public RatingResult() {
    }

    public RatingResult(Integer sumRating, Integer numRating) {
        this.sumRating = sumRating;
        this.numRating = numRating;
    }

    public int getSumRating() {
        return sumRating;
    }

    public void setSumRating(Integer sumRating) {
        this.sumRating = sumRating;
    }

    public int getNumRating() {
        return numRating;
    }

    public void setNumRating(Integer numRating) {
        this.numRating = numRating;
    }
}
