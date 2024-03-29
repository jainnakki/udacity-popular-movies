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

package com.example.akki.popularmovies.ui.fragment;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.akki.popularmovies.rest.AppStatus;
import com.example.akki.popularmovies.ui.adapter.GridSpacingItemDecoration;
import com.example.akki.popularmovies.ui.adapter.MovieAdapter;
import com.example.akki.popularmovies.R;
import com.example.akki.popularmovies.data.MoviesContentProvider;
import com.example.akki.popularmovies.data.MoviesTable;
import com.example.akki.popularmovies.rest.LoggingInterceptor;
import com.example.akki.popularmovies.rest.model.movies.AndroidMovies;
import com.example.akki.popularmovies.rest.model.genre.MoviesGenre;
import com.example.akki.popularmovies.rest.model.genre.MoviesGenreList;
import com.example.akki.popularmovies.rest.model.movies.MoviesList;
import com.example.akki.popularmovies.rest.service.MoviesApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.my_recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.checkbox_popular)
    CheckBox popular;
    @BindView(R.id.checkbox_top_rated)
    CheckBox top_rated;
    @BindView(R.id.checkbox_favourite)
    CheckBox favourite;

    @BindString(R.string.online_status)
    String onlineMessage;
    @BindString(R.string.notOnline_status)
    String notOnlineMessage;
    @BindString(R.string.noInternet_status)
    String noInternetMessage;

    private Unbinder unbinder;
    private MovieAdapter mAdapter;
    private MoviesApiService service;

    private List<AndroidMovies> databaseMoviesList = null;
    private List<AndroidMovies> mMoviesData = null;
    private List<MoviesGenre> mMoviesGenreData = null;

    private final String MOVIE_FRAGMENT_TAG = MovieFragment.class.getSimpleName();
    private static final String MOVIE_API_URL = "https://api.themoviedb.org/3/";

    private static final String SAVED_MOVIES_DATA = "MOVIES_DATA";
    private static final String SAVED_MOVIES_GENRE_DATA = "MOVIES_GENRE_DATA";
    private static final String SAVED_SORT_BY_POPULAR = "POPULAR";
    private static final String SAVED_SORT_BY_TOP_RATED = "TOP_RATED";
    private static final String SAVED_SORT_BY_FAVOURITE = "FAVOURITE";

    private final String KEY_RECYCLER_STATE = "recycler_state";

    public MovieFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        getLoaderManager().initLoader(0, null, this);

        int spanCount = 3; // 3 columns
        int spacing = 10; // 50px
        boolean includeEdge = true;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, true));
        recyclerView.setHasFixedSize(true);

        mAdapter = new MovieAdapter(getContext());
        recyclerView.setAdapter(mAdapter);

        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(3, 1);
        recyclerView.setLayoutManager(mLayoutManager);

        Bundle mBundleRecyclerViewState = savedInstanceState;
        if (mBundleRecyclerViewState != null) {
            Parcelable listState = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
            if (listState != null) {
                recyclerView.getLayoutManager().onRestoreInstanceState(listState);
            }

            popular.setChecked(mBundleRecyclerViewState.getBoolean(SAVED_SORT_BY_POPULAR));
            top_rated.setChecked(mBundleRecyclerViewState.getBoolean(SAVED_SORT_BY_TOP_RATED));
            favourite.setChecked(mBundleRecyclerViewState.getBoolean(SAVED_SORT_BY_FAVOURITE));

            mMoviesData = mBundleRecyclerViewState.getParcelableArrayList(SAVED_MOVIES_DATA);
            mAdapter.setMoviesList(mMoviesData);

            mMoviesGenreData = mBundleRecyclerViewState.getParcelableArrayList(SAVED_MOVIES_GENRE_DATA);
            mAdapter.setGenreList(mMoviesGenreData);
        } else {
            popular.setChecked(true);
            top_rated.setChecked(false);
            favourite.setChecked(false);
        }


        popular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                top_rated.setChecked(false);
                favourite.setChecked(false);
                updateMoviesList("popular");
            }
        });
        top_rated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popular.setChecked(false);
                favourite.setChecked(false);
                updateMoviesList("top_rated");
            }
        });
        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popular.setChecked(false);
                top_rated.setChecked(false);
                updateMoviesList("favourite");
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        final OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new LoggingInterceptor())
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl(MOVIE_API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory
                        .create()).build();

        service = restAdapter.create(MoviesApiService.class);

        if (popular.isChecked())
            updateMoviesList("popular");
        else if (top_rated.isChecked())
            updateMoviesList("top_rated");
        else if (favourite.isChecked())
            updateMoviesList("favourite");
        else {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mMoviesData != null)
            outState.putParcelableArrayList(SAVED_MOVIES_DATA, (ArrayList<? extends Parcelable>) mMoviesData);

        if (mMoviesGenreData != null)
            outState.putParcelableArrayList(SAVED_MOVIES_GENRE_DATA, (ArrayList<? extends Parcelable>) mMoviesGenreData);

        outState.putBoolean(SAVED_SORT_BY_POPULAR, popular.isChecked());
        outState.putBoolean(SAVED_SORT_BY_TOP_RATED, top_rated.isChecked());
        outState.putBoolean(SAVED_SORT_BY_FAVOURITE, favourite.isChecked());

        Parcelable listState = recyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(KEY_RECYCLER_STATE, listState);
    }

    private void updateMoviesList(final String endpoint) {

        if (!endpoint.equals("favourite")) {

            if (AppStatus.getInstance(getActivity()).isOnline()) {

                Snackbar snackbar = Snackbar.make(coordinatorLayout, onlineMessage, Snackbar.LENGTH_SHORT);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.GREEN);
                snackbar.show();

                fetchMovieTask(endpoint);
                if (mMoviesGenreData == null) {
                    fetchMoviesGenreList();
                }
            } else {

                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, noInternetMessage, Snackbar.LENGTH_INDEFINITE)
                        .setAction("RETRY", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                updateMoviesList(endpoint);
                            }
                        });

                snackbar.setActionTextColor(Color.RED);

                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();

            }

        } else {

            Log.i(MOVIE_FRAGMENT_TAG, "Loading from Database!");

            if (databaseMoviesList != null) {
                mAdapter.setMoviesList(databaseMoviesList);
                mMoviesData = databaseMoviesList;
            }
        }
    }

    private void fetchMovieTask(String endpoint) {

        Call<MoviesList> movieResultCallback = null;
        switch (endpoint) {
            case "popular":
                movieResultCallback = service.getPopularMovies();
                break;
            case "top_rated":
                movieResultCallback = service.getTopRatedMovies();
                break;

            default:
                Log.d("Endpoint error", "End point not accepted, data corrupted!!");
        }

        assert movieResultCallback != null;

        movieResultCallback.enqueue(new Callback<MoviesList>() {
            @Override
            public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {
                Log.i("response code", String.valueOf(response.code()));
                if (call.isExecuted()) {
                    mAdapter.setMoviesList(response.body().getResults());
                    mMoviesData = response.body().getResults();
                }
            }

            @Override
            public void onFailure(Call<MoviesList> call, Throwable t) {
                Log.i("RESPONSE FAILED", t.getMessage());
            }
        });

    }

    private void fetchMoviesGenreList() {
        Call<MoviesGenreList> moviesGenreCallback = service.getMoviesGenreList();
        moviesGenreCallback.enqueue(new Callback<MoviesGenreList>() {

            @Override
            public void onResponse(Call<MoviesGenreList> call, Response<MoviesGenreList> response) {
                Log.i("genre response code", String.valueOf(response.code()));
                if (call.isExecuted())
                    mAdapter.setGenreList(response.body().getResults());
                mMoviesGenreData = response.body().getResults();
            }

            @Override
            public void onFailure(Call<MoviesGenreList> call, Throwable t) {
                Log.i("GENRE RESPONSE FAILED", t.getMessage());
            }
        });
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {MoviesTable.COLUMN_ID,
                MoviesTable.COLUMN_ORIGINAL_TITLE, MoviesTable.COLUMN_OVERVIEW,
                MoviesTable.COLUMN_RELEASE_DATE, MoviesTable.COLUMN_GENRES,
                MoviesTable.COLUMN_VOTE_COUNT, MoviesTable.COLUMN_RATING,
                MoviesTable.COLUMN_POPULARITY, MoviesTable.COLUMN_POSTER_PATH, MoviesTable.COLUMN_BACKDROP_PATH};

        return new CursorLoader(getContext(),
                MoviesContentProvider.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<AndroidMovies> tmpDatabaseMoviesList = new ArrayList<>();

        if (data == null)
            return;

        while (data.moveToNext()) {

            AndroidMovies movies = new AndroidMovies();
            movies.setFavourite(1);

            movies.setId(data.getInt(0));
            Log.i("Cursor id", String.valueOf(data.getInt(0)));
            movies.setOriginal_title(data.getString(1));
            Log.i("Cursor title", String.valueOf(data.getString(1)));
            movies.setOverview(data.getString(2));
            Log.i("Cursor overview", String.valueOf(data.getString(2)));
            movies.setRelease_date(data.getString(3));
            Log.i("Cursor date", String.valueOf(data.getString(3)));
            movies.setGenre_names(data.getString(4));
            Log.i("Cursor genre", String.valueOf(data.getString(4)));
            movies.setVote_count(data.getInt(5));
            Log.i("Cursor voteCount", String.valueOf(data.getString(5)));
            movies.setUser_rating(data.getFloat(6));
            Log.i("Cursor userRating", String.valueOf(data.getFloat(6)));
            movies.setPopularity(data.getFloat(7));
            Log.i("Cursor popularity", String.valueOf(data.getFloat(7)));
            movies.setPoster_path(data.getString(8));
            Log.i("Cursor posterPath", String.valueOf(data.getString(8)));
            movies.setBackdrop_path(data.getString(9));
            Log.i("Cursor backdropPath", String.valueOf(data.getString(9)));

            tmpDatabaseMoviesList.add(movies);
        }
        data.close();

        databaseMoviesList = new ArrayList<>();
        databaseMoviesList = tmpDatabaseMoviesList;

        if (favourite.isChecked()) {
            mAdapter.setMoviesList(databaseMoviesList);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
