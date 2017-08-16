package com.example.akki.popularmovies.rest.model.review;

import com.example.akki.popularmovies.rest.model.review.MovieReview;

import java.util.ArrayList;

/**
 * Created by Akshay on 28-07-2017.
 */

public class MovieReviewList {

    private Integer id;
    private Integer page;
    private ArrayList<MovieReview> results;
    private Integer total_pages;
    private Integer total_results;

    public ArrayList<MovieReview> getResults() {
        return results;
    }

}
