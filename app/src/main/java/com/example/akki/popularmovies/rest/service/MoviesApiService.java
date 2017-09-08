/*
 * Copyright 2017.  Akshay Jain
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.akki.popularmovies.rest.service;

/**
 * Created by Akshay on 24-07-2017.
 * Interface for network calls using retrofit
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
