package com.example.akki.popularmovies.rest.service;

/**
 * Created by Akshay on 24-07-2017.
 */

import com.example.akki.popularmovies.rest.model.review.MovieReviewList;
import com.example.akki.popularmovies.rest.model.video.MovieVideoList;
import com.example.akki.popularmovies.rest.model.genre.MoviesGenreList;
import com.example.akki.popularmovies.rest.model.movies.MoviesList;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.Call;

public interface MoviesApiService {

    @GET("movie/popular")
    Call<MoviesList> getPopularMovies();

    @GET("movie/top_rated")
    Call<MoviesList> getTopRatedMovies();

    @GET("genre/movie/list")
    Call<MoviesGenreList> getMoviesGenreList();

    @GET("movie/{id}/reviews")
    Call<MovieReviewList> getMovieReviewList(@Path("id") int movieId);

    @GET("movie/{id}/videos")
    Call<MovieVideoList> getMovieVideoList(@Path("id") int movieId);
}
