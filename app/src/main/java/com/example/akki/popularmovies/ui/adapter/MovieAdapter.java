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

package com.example.akki.popularmovies.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.akki.popularmovies.rest.AppStatus;
import com.example.akki.popularmovies.R;
import com.example.akki.popularmovies.rest.model.movies.AndroidMovies;
import com.example.akki.popularmovies.rest.model.genre.MoviesGenre;
import com.example.akki.popularmovies.ui.activity.MovieDetails;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Custom movie adapter class for movies recycler view in MoviesFragment Class
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private final Context context;

    private List<AndroidMovies> MoviesList;
    private List<MoviesGenre> GenreList;
    private Map<Integer, String> genreDictionary = null;

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.movie_image)
        public ImageView moviePoster;
        @BindView(R.id.progressBar)
        public ProgressBar progressBar = null;
        @BindView(R.id.movie_title_home)
        public TextView movieTitle;
        @BindView(R.id.movie_popularity_home)
        public TextView moviePopularity;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }

    }

    private AndroidMovies addDummyMoviesData() {
        AndroidMovies MovieDummyData = new AndroidMovies();
        MovieDummyData.setOriginal_title("----");
        MovieDummyData.setPopularity(0.0f);
        MovieDummyData.setVote_count(0);
        MovieDummyData.setId(0);
        MovieDummyData.setGenre_names("----");
        MovieDummyData.setOverview("----");
        MovieDummyData.setUser_rating(0.0f);
        MovieDummyData.setRelease_date("----");

        return MovieDummyData;
    }

    private MoviesGenre addDummyGenreData() {
        MoviesGenre GenreDummyData = new MoviesGenre();
        GenreDummyData.setId(0);
        GenreDummyData.setName("None");

        return GenreDummyData;
    }

    public MovieAdapter(Context context) {
        super();
        this.context = context;
        this.MoviesList = new ArrayList<>();
        this.MoviesList.add(addDummyMoviesData());
        this.GenreList = new ArrayList<>();
        this.GenreList.add(addDummyGenreData());
        genreDictionary = new HashMap<>();
    }

    public void setGenreList(List<MoviesGenre> genreList) {
        this.GenreList = new ArrayList<>();
        this.GenreList = genreList;
        notifyDataSetChanged();
        MoviesGenre genre;

        if (GenreList != null) {
            Log.i("GenreList STATUS: ", "NOT NULL!!");
            for (int i = 0; i < GenreList.size(); i++) {
                genre = GenreList.get(i);
                genreDictionary.put(genre.getId(), genre.getName());
            }
        } else {
            Log.i("GenreList STATUS: ", "IT IS NULL!!");
        }
    }

    public void setMoviesList(List<AndroidMovies> movieList) {
        this.MoviesList = movieList;
        notifyDataSetChanged();
    }

    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        //LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.movie_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final AndroidMovies movies = MoviesList.get(position);
        Log.i("MOVIE ADAPTER", "ID is : " + movies.getId());
        List<Integer> genreIds = movies.getGenre_ids();
        String genre_names = "";
        String posterLoadingPath;
        if (movies.getFavourite() == 0) {
            posterLoadingPath = "http://image.tmdb.org/t/p/w185" + movies.getPoster_path();
            if (genreIds != null) {
                Log.d("genreIds no error", "it is not null!!!");
                int finalGenreIdSize = genreIds.size() > 3 ? 3 : genreIds.size();
                for (int i = 0; i < finalGenreIdSize; i++)
                    genre_names += genreDictionary.get(genreIds.get(i)) + (i != finalGenreIdSize
                            - 1 ? " | " : "");
            } else {
                Log.e("genreIds error", "it is null!!!");
            }
        } else {
            posterLoadingPath = movies.getPoster_path();
            genre_names = movies.getGenre_names();
        }

        if (AppStatus.getInstance(context).isOnline()) {
            holder.progressBar.setVisibility(View.VISIBLE);
        }
        Log.i("POSTER PATH: ", "http://image.tmdb.org/t/p/w185" + movies.getPoster_path());


        if (movies.getFavourite() == 0) {
            Picasso.with(context)
                    .load(posterLoadingPath)
                    .fit()
                    .error(R.drawable.error)
                    .into(holder.moviePoster, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            if (holder.progressBar != null) {
                                holder.progressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onError() {

                        }
                    });


        } else {
            Uri imageUri = Uri.fromFile(new File(posterLoadingPath));
            Picasso.with(context)
                    .load(imageUri)
                    .fit()
                    .error(R.drawable.error)
                    .into(holder.moviePoster, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            if (holder.progressBar != null) {
                                holder.progressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }

        holder.movieTitle.setText(movies.getOriginal_title());

        int popularityScore = (int) Math.ceil(movies.getPopularity());
        Log.d("POPULARITY SCORE ADAP", String.valueOf(movies.getPopularity()));
        String pScore = String.valueOf(popularityScore) + "%";
        holder.moviePopularity.setText(pScore);

        final String finalGenre_names = genre_names;

        holder.moviePoster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MovieDetails.class);

                AndroidMovies movie = new AndroidMovies();
                movie.setVote_count(movies.getVote_count());
                movie.setId(movies.getId());
                movie.setUser_rating(movies.getUser_rating());
                movie.setPopularity(movies.getPopularity());
                movie.setPoster_path(movies.getPoster_path());
                movie.setOriginal_title(movies.getOriginal_title());
                movie.setGenre_ids(movies.getGenre_ids());
                movie.setGenre_names(finalGenre_names);
                movie.setBackdrop_path(movies.getBackdrop_path());
                movie.setOverview(movies.getOverview());
                movie.setRelease_date(movies.getRelease_date());
                movie.setFavourite(movies.getFavourite());

                intent.putExtra("movie_details", movie);

                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return (MoviesList == null) ? 0 : MoviesList.size();
    }

}
