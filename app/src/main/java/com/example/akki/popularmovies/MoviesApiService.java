package com.example.akki.popularmovies;

/**
 * Created by Akshay on 24-07-2017.
 */
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
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
