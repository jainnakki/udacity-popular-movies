package com.example.akki.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MovieFragment extends Fragment implements  Callback<MoviesList> {

    @BindView(R.id.my_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.checkbox_popular)
    CheckBox popular;
    @BindView(R.id.checkbox_top_rated)
    CheckBox top_rated;

    @BindString(R.string.online_status)
    String onlineMessage;
    @BindString(R.string.notOnline_status)
    String notOnlineMessage;

    private Unbinder unbinder;
    private MovieAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    MoviesApiService service;

    public static final String MOVIE_API_URL = "https://api.themoviedb.org/3/";
    private AndroidMoviesAdapter movieAdapter;
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
        unbinder = ButterKnife.bind(this,rootView);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        int spanCount = 3; // 3 columns
        int spacing = 50; // 50px
        boolean includeEdge = true;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

        mAdapter = new MovieAdapter(getContext(),url);
        recyclerView.setAdapter(mAdapter);
        mLayoutManager = new GridLayoutManager(getActivity(),3);
        recyclerView.setLayoutManager(mLayoutManager);

        popular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                top_rated.setChecked(false);
                updateMoviesList("popular");
            }
        });
        top_rated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popular.setChecked(false);
                updateMoviesList("top_rated");
            }
        });

        //URL - api.themoviedb.org/3/movie/popular?api_key=72b6dbc52bed1237a8eabba476078bc2

        //recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView,
        //                    new RecyclerItemClickListener.OnItemClickListener() {

        return rootView;
    }

    private void updateMoviesList(String endpoint) {

        if (AppStatus.getInstance(getActivity()).isOnline()) {

            Toast t = Toast.makeText(getContext(), onlineMessage, Toast.LENGTH_SHORT);
            t.show();
            fetchMovieTask(endpoint);
        } else {

            Toast t = Toast.makeText(getContext(), notOnlineMessage, Toast.LENGTH_SHORT);
            t.show();
        }


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

        updateMoviesList("popular");
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    void fetchMovieTask(String endpoint) {


        //MoviesApiService service = restAdapter.create(MoviesApiService.class);
        Call<MoviesList> movieResultCallback = null;
        switch(endpoint) {
            case "popular": movieResultCallback = service.getPopularMovies();
                            break;
            case "top_rated": movieResultCallback = service.getTopRatedMovies();
                              break;

            default: Log.d("Endpoint error","End point not accepted, data corrupted!!");
        }
        movieResultCallback.enqueue(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {

        int code = response.code();
        Log.i("response code", String.valueOf(response.code()));
        mAdapter.setMoviesList(response.body().getResults());
    }

    @Override
    public void onFailure(Call<MoviesList> call, Throwable t) {
        Log.i("RESPONSE FAILED",t.getMessage());
    }
}
