package com.example.akki.popularmovies.rest.model.video;

import com.example.akki.popularmovies.rest.model.video.MovieVideo;

import java.util.ArrayList;

/**
 * Created by Akshay on 29-07-2017.
 */

public class MovieVideoList {

    Integer id;

    private ArrayList<MovieVideo> results;

    public ArrayList<MovieVideo> getResults() {
        return results;
    }
}
