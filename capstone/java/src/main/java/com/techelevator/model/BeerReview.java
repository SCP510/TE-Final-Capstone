package com.techelevator.model;

import java.util.Date;

public class BeerReview {
    // Instance Variables/Properties
    private int beerReviewId;
    private int beerId;
    private int reviewerId;
    private int beerRating;
    private String beerReview;
    private Date datePosted;



    // Constructors
    public BeerReview() {
    }

    public BeerReview(int reviewId, int beerId, int reviewerId, int beerRating, String beerReview, Date datePosted) {
        this.beerReviewId = reviewId;
        this.beerId = beerId;
        this.reviewerId = reviewerId;
        this.beerRating = beerRating;
        this.beerReview = beerReview;
        this.datePosted = datePosted;
    }



    // Getters and Setters
    public int getBeerReviewId() {
        return beerReviewId;
    }

    public void setBeerReviewId(int beerReviewId) {
        this.beerReviewId = beerReviewId;
    }

    public int getBeerId() {
        return beerId;
    }

    public void setBeerId(int beerId) {
        this.beerId = beerId;
    }

    public int getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(int reviewerId) {
        this.reviewerId = reviewerId;
    }

    public int getBeerRating() {
        return beerRating;
    }

    public void setBeerRating(int beerRating) {
        this.beerRating = beerRating;
    }

    public String getBeerReview() {
        return beerReview;
    }

    public void setBeerReview(String beerReview) {
        this.beerReview = beerReview;
    }

    public Date getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(Date datePosted) {
        this.datePosted = datePosted;
    }



    // Methods
    @Override
    public String toString() {
        return "BeerReview{" +
                "beerReviewId=" + beerReviewId +
                ", beerId=" + beerId +
                ", reviewerId=" + reviewerId +
                ", beerRating=" + beerRating +
                ", beerReview='" + beerReview +
                ", datePosted=" + datePosted +
                '}';
    }
}