package com.example.wifinder.data.model;

public class RatingResult {
    private int sumRating;
    private int numRating;

    public RatingResult() {
    }

    public RatingResult(int sumRating, int numRating) {
        this.sumRating = sumRating;
        this.numRating = numRating;
    }

    public int getSumRating() {
        return sumRating;
    }

    public void setSumRating(int sumRating) {
        this.sumRating = sumRating;
    }

    public int getNumRatings() {
        return numRating;
    }

    public void setNumRating(int numRating) {
        this.numRating = numRating;
    }
}
