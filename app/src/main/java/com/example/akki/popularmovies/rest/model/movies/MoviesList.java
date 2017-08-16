package com.example.akki.popularmovies.rest.model.movies;

import com.example.akki.popularmovies.rest.model.movies.AndroidMovies;

import java.util.ArrayList;

/**
 * Created by Akshay on 24-07-2017.
 */

public class MoviesList {

    private int total_pages;

    public int getTotal_pages() {
        return total_pages;
    }

    private ArrayList<AndroidMovies> results;

    public ArrayList<AndroidMovies> getResults() {
        return results;
    }
}
