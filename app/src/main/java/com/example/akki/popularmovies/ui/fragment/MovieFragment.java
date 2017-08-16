package com.example.akki.popularmovies.ui.fragment;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

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

    private Unbinder unbinder;
    private MovieAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    MoviesApiService service;
    List<MoviesGenre> GenreList;
    List<AndroidMovies> databaseMoviesList = null;

    public static final String MOVIE_API_URL = "https://api.themoviedb.org/3/";
    AndroidMovies[] androidMovies;
    String[] moviePosterPath, url;
    String[] movieId, movieTitle, movieReleaseDate, movieVoteAverage, movieOverview;


    public MovieFragment() {
        url = new String[]{"drawable/not_connected.png"};
        movieId = new String[]{"----"};
        movieTitle = new String[]{"----"};
        movieReleaseDate = new String[]{"----"};
        movieVoteAverage = new String[]{"----"};
        movieOverview = new String[]{"----"};
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        unbinder = ButterKnife.bind(this, rootView);
        getLoaderManager().initLoader(0, null, this);

        int spanCount = 3; // 3 columns
        int spacing = 50; // 50px
        boolean includeEdge = true;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

        mAdapter = new MovieAdapter(getContext());
        recyclerView.setAdapter(mAdapter);
        mLayoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(mLayoutManager);

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


        //URL - api.themoviedb.org/3/movie/popular?api_key=72b6dbc52bed1237a8eabba476078bc2

        //recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView,
        //                    new RecyclerItemClickListener.OnItemClickListener() {

        return rootView;
    }

    private void updateMoviesList(String endpoint) {

        if (!endpoint.equals("favourite")) {
            if (AppStatus.getInstance(getActivity()).isOnline()) {

                Toast t = Toast.makeText(getContext(), onlineMessage, Toast.LENGTH_SHORT);
                t.show();
                fetchMovieTask(endpoint);
            } else {

                Toast t = Toast.makeText(getContext(), notOnlineMessage, Toast.LENGTH_SHORT);
                t.show();
            }
        } else {

            Toast t = Toast.makeText(getContext(), "Loading from Database!", Toast.LENGTH_SHORT);
            t.show();
            if (databaseMoviesList != null)
                mAdapter.setMoviesList(databaseMoviesList);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        //if(favourite.isChecked())
        //getLoaderManager().initLoader(0, null, this);
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
            popular.setChecked(true);
            updateMoviesList("popular");
        }
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    void fetchMovieTask(String endpoint) {


        //MoviesApiService service = restAdapter.create(MoviesApiService.class);
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
        movieResultCallback.enqueue(new Callback<MoviesList>() {
            @Override
            public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {
                int code = response.code();
                Log.i("response code", String.valueOf(response.code()));
                if (call.isExecuted()) {
                    mAdapter.setMoviesList(response.body().getResults());
                    fetchMoviesGenreList();
                }
            }

            @Override
            public void onFailure(Call<MoviesList> call, Throwable t) {
                Log.i("RESPONSE FAILED", t.getMessage());
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    void fetchMoviesGenreList() {
        Call<MoviesGenreList> moviesGenreCallback = service.getMoviesGenreList();
        moviesGenreCallback.enqueue(new Callback<MoviesGenreList>() {

            @Override
            public void onResponse(Call<MoviesGenreList> call, Response<MoviesGenreList> response) {
                int code = response.code();
                Log.i("genre response code", String.valueOf(response.code()));
                if (call.isExecuted())
                    mAdapter.setGenreList(response.body().getResults());
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
                MoviesTable.COLUMN_POPULARITY, MoviesTable.COLUMN_POSTER_PATH};
        CursorLoader cursorLoader = new CursorLoader(getContext(),
                MoviesContentProvider.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        databaseMoviesList = new ArrayList<>();

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
            movies.setFavourite(1);
            databaseMoviesList.add(movies);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
