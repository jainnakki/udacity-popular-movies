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

package com.example.akki.popularmovies.ui.activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.akki.popularmovies.rest.AppStatus;
import com.example.akki.popularmovies.rest.model.review.MovieReview;
import com.example.akki.popularmovies.ui.adapter.MovieReviewAdapter;
import com.example.akki.popularmovies.R;
import com.example.akki.popularmovies.data.MoviesContentProvider;
import com.example.akki.popularmovies.data.MoviesTable;
import com.example.akki.popularmovies.rest.LoggingInterceptor;
import com.example.akki.popularmovies.rest.model.movies.AndroidMovies;
import com.example.akki.popularmovies.rest.model.review.MovieReviewList;
import com.example.akki.popularmovies.rest.model.video.MovieVideo;
import com.example.akki.popularmovies.rest.model.video.MovieVideoList;
import com.example.akki.popularmovies.rest.service.MoviesApiService;
import com.example.akki.popularmovies.ui.adapter.MovieTrailerAdapter;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDetails extends AppCompatActivity implements Callback<MovieReviewList> {

    private final String TAG = MovieDetails.class.getSimpleName();

    @BindView(R.id.movie_details_coordinatorLayout)
    CoordinatorLayout movie_details_coordinatorLayout;

    @BindView(R.id.movie_poster)
    ImageView movie_poster;
    @BindView(R.id.headerPoster)
    ImageView header_poster;

    @BindView(R.id.expand_text_view)
    ExpandableTextView overviewExpTv;

    @BindView(R.id.movie_title)
    TextView title;
    @BindView(R.id.movie_rating)
    TextView rating;
    @BindView(R.id.movie_releaseDate)
    TextView release_date;
    @BindView(R.id.movie_popularity)
    TextView movie_popularity;
    @BindView(R.id.movie_votes)
    TextView movie_votes;
    @BindView(R.id.movie_genre)
    TextView movie_genre;

    @BindView(R.id.review_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.trailer_recycler_view)
    RecyclerView trailerRecyclerView;

    @BindView(R.id.appbar)
    AppBarLayout appBar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.movie_favourite_button)
    ToggleButton favouriteButton;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindString(R.string.max_rating)
    String maxRating;
    @BindString(R.string.online_status)
    String onlineMessage;
    @BindString(R.string.notOnline_status)
    String notOnlineMessage;
    @BindString(R.string.movieFavourite_status)
    String movieFavouriteMessage;
    @BindString(R.string.movieNotFavourite_status)
    String movieNotFavouriteMessage;

    private File tmpPosterMyDir = null;
    private Bitmap tmpPosterBitmap = null;

    private List<MovieReview> mMoviesReviewData = null;
    private String[] mVideoKeys = null;

    private MovieReviewAdapter mrAdapter;
    private MovieTrailerAdapter trailerAdapter;

    private MoviesApiService service;

    private final String LOG_TAG = MovieDetails.class.getSimpleName();
    private static final String MOVIE_API_URL = "https://api.themoviedb.org/3/";

    private static int flag;

    private final String KEY_RECYCLER_TRAILER_STATE = "trailer_recycler_state";
    private final String KEY_RECYCLER_REVIEW_STATE = "review_recycler_state";
    private static final String SAVED_MOVIES_REVIEW_DATA = "MOVIES_REVIEW_DATA";
    private static final String SAVED_MOVIES_TRAILER_DATA = "MOVIES_TRAILER_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_movie_details);
        ButterKnife.bind(this);

        mrAdapter = new MovieReviewAdapter(this);
        recyclerView.setAdapter(mrAdapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        trailerAdapter = new MovieTrailerAdapter(this);
        trailerRecyclerView.setAdapter(trailerAdapter);
        trailerRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager
                .HORIZONTAL, false));

        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        final AndroidMovies parcelableMovieData = intent.getExtras().getParcelable("movie_details");

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

        if (savedInstanceState != null) {

            Parcelable reviewListState = savedInstanceState.getParcelable(KEY_RECYCLER_REVIEW_STATE);
            if (reviewListState != null)
                recyclerView.getLayoutManager().onRestoreInstanceState(reviewListState);

            mMoviesReviewData = savedInstanceState.getParcelableArrayList(SAVED_MOVIES_REVIEW_DATA);
            mrAdapter.setMovieReviewList(mMoviesReviewData);

            Parcelable trailerListState = savedInstanceState.getParcelable(KEY_RECYCLER_TRAILER_STATE);
            if (trailerListState != null)
                trailerRecyclerView.getLayoutManager().onRestoreInstanceState(trailerListState);

            mVideoKeys = savedInstanceState.getStringArray(SAVED_MOVIES_TRAILER_DATA);
            trailerAdapter.setVideoKeys(mVideoKeys);

        } else {
            if (parcelableMovieData != null)
                updateMovieDataList(parcelableMovieData.getId());
        }

        String posterLoadingPath;
        assert parcelableMovieData != null;
        if (parcelableMovieData.getFavourite() == 0) {
            posterLoadingPath = "http://image.tmdb.org/t/p/w185" + parcelableMovieData
                    .getPoster_path();
            Log.v(LOG_TAG, "POSTER URL NOT FAV: " + parcelableMovieData.getPoster_path());

            Glide.with(this)
                    .load(posterLoadingPath)
                    .asBitmap()
                    .into(movie_poster);

        } else {
            posterLoadingPath = parcelableMovieData.getPoster_path();
            Log.v(LOG_TAG, "IMAGE URL FAV: " + parcelableMovieData.getPoster_path());
            Uri posterImageUri = Uri.fromFile(new File(posterLoadingPath));

            Glide.with(this)
                    .load(posterImageUri)
                    .asBitmap()
                    .error(R.drawable.error)
                    .into(movie_poster);

        }


        String headerPosterLoadingPath = "http://image.tmdb.org/t/p/w342" + parcelableMovieData
                .getBackdrop_path();
        Log.v(LOG_TAG, "BACKDROP URL NOT FAV: " + parcelableMovieData.getBackdrop_path());

        Glide.with(this)
                .load(headerPosterLoadingPath)
                .asBitmap()
                .placeholder(R.drawable.poster_placeholder_image)
                .into(header_poster);

        title.setText(parcelableMovieData.getOriginal_title());

        int popularityScore = (int) Math.ceil(parcelableMovieData.getPopularity());
        String pScore = String.valueOf(popularityScore) + "%";
        movie_popularity.setText(pScore);

        int movieVotes = parcelableMovieData.getVote_count();
        String mVotes = movieVotes + " votes";
        movie_votes.setText(mVotes);

        overviewExpTv.setText(parcelableMovieData.getOverview());
        String ratingString = String.valueOf(parcelableMovieData.getUser_rating()) + maxRating;
        rating.setText(ratingString);

        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(parcelableMovieData
                    .getRelease_date());
            String formattedDate = new SimpleDateFormat("MMM dd, yyyy", Locale.US).format(date);
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
            Date finalDate = new SimpleDateFormat("MMM dd, yyyy", Locale.US).parse(formattedDate);
            String movieReleaseDate = dateFormat.format(finalDate);

            release_date.setText(movieReleaseDate);
        } catch (ParseException e) {
            e.printStackTrace();
            release_date.setText(parcelableMovieData.getRelease_date());
        }

        movie_genre.setText(parcelableMovieData.getGenre_names());
        Log.i(LOG_TAG, "MOVIE GENRES: " + parcelableMovieData.getGenre_names());

        if (parcelableMovieData.getFavourite() == 0) {
            favouriteButton.setChecked(false);
        } else {
            favouriteButton.setChecked(true);
        }

        favouriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && parcelableMovieData.getFavourite() == 0)
                    saveMovieDetailsInDatabase(parcelableMovieData);

                if (!isChecked && parcelableMovieData.getFavourite() == 1)
                    removeMovieDetailsFromDatabase(parcelableMovieData);
            }
        });


        /**
         * Floating Action Button added for styling, no functional operation assigned!!
         * Optional, you may wish to implement any feature you want.
         */
        flag = 0;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag++;
                if (flag % 2 == 1) {
                    fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R
                            .drawable.favourite));
                }
                if (flag % 2 == 0) {
                    fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R
                            .drawable.favourite_border));
                }
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mMoviesReviewData != null)
            outState.putParcelableArrayList(SAVED_MOVIES_REVIEW_DATA, (ArrayList<? extends Parcelable>) mMoviesReviewData);

        Parcelable reviewListState = recyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(KEY_RECYCLER_REVIEW_STATE, reviewListState);

        if (mVideoKeys != null)
            outState.putStringArray(SAVED_MOVIES_TRAILER_DATA, mVideoKeys);

        Parcelable trailerListState = trailerRecyclerView.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(KEY_RECYCLER_TRAILER_STATE, trailerListState);
    }


    private void saveMovieDetailsInDatabase(AndroidMovies MoviesData) {
        String posterImageURL = "http://image.tmdb.org/t/p/w185" + MoviesData.getPoster_path();
        String posterImagePathOnStorage = null;

        try {
            posterImagePathOnStorage = saveImageOffline(posterImageURL, MoviesData.getId());

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (posterImagePathOnStorage != null) {
            Log.i("Offline Path Saved 1: ", posterImagePathOnStorage);

            Bitmap bmap = BitmapFactory.decodeFile(posterImagePathOnStorage);
            movie_poster.setImageBitmap(bmap);
            Log.i("Image Set", "Success");

            ContentValues values = new ContentValues();
            values.put(MoviesTable.COLUMN_ID, MoviesData.getId());
            values.put(MoviesTable.COLUMN_ORIGINAL_TITLE, MoviesData.getOriginal_title());
            values.put(MoviesTable.COLUMN_OVERVIEW, MoviesData.getOverview());
            values.put(MoviesTable.COLUMN_RELEASE_DATE, MoviesData.getRelease_date());
            values.put(MoviesTable.COLUMN_GENRES, MoviesData.getGenre_names());
            values.put(MoviesTable.COLUMN_VOTE_COUNT, MoviesData.getVote_count());
            values.put(MoviesTable.COLUMN_RATING, MoviesData.getUser_rating());
            values.put(MoviesTable.COLUMN_POPULARITY, MoviesData.getPopularity());
            values.put(MoviesTable.COLUMN_POSTER_PATH, posterImagePathOnStorage);
            values.put(MoviesTable.COLUMN_BACKDROP_PATH, MoviesData.getBackdrop_path());

            getContentResolver().insert(MoviesContentProvider.CONTENT_URI, values);

            Snackbar snackbar = Snackbar.make(movie_details_coordinatorLayout, movieFavouriteMessage, Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
    }

    private void removeMovieDetailsFromDatabase(AndroidMovies MoviesData) {
        Uri uri = Uri.parse(MoviesContentProvider.CONTENT_URI + "/"
                + MoviesData.getId());
        getContentResolver().delete(uri, null, null);

        Snackbar snackbar = Snackbar.make(movie_details_coordinatorLayout, movieNotFavouriteMessage, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private String saveImageOffline(String ImageUrl, final int imageId) throws IOException {


        InputStream input = null;
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/PopularMovies");

        try {

            URL url = new URL(ImageUrl);

            if (!myDir.exists()) {
                myDir.mkdirs();
            }

            String outputName = imageId + "-thumbnail.jpg";

            myDir = new File(myDir, outputName);

            input = url.openConnection().getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);

            if (isStoragePermissionGranted()) {
                SaveImage(bitmap, myDir);
            } else {
                tmpPosterBitmap = bitmap;
                tmpPosterMyDir = myDir;
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                input.close();
            }
        }

        return myDir.getPath();
    }

    private void SaveImage(Bitmap finalBitmap, File myDir) {

        try {
            FileOutputStream out = new FileOutputStream(myDir);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission
                        .WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        } else {
            //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 2: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();

                    if (tmpPosterBitmap != null && tmpPosterMyDir != null)
                        SaveImage(tmpPosterBitmap, tmpPosterMyDir);

                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    private void makeToast(String message) {
        Toast.makeText(this, message,
                Toast.LENGTH_SHORT).show();
    }


    private void updateMovieDataList(int movieId) {

        if (AppStatus.getInstance(this).isOnline()) {

            makeToast(onlineMessage);
            fetchMovieVideoTask(movieId);
            fetchMovieReviewTask(movieId);
        } else {

            makeToast(notOnlineMessage);
        }

    }

    private void fetchMovieVideoTask(int movieId) {

        Call<MovieVideoList> videoResultCallback;
        if (service != null) {
            videoResultCallback = service.getMovieVideoList(movieId);
            videoResultCallback.enqueue(new Callback<MovieVideoList>() {
                @Override
                public void onResponse(Call<MovieVideoList> call, Response<MovieVideoList>
                        response) {
                    Log.i("video response code", String.valueOf(response.code()));
                    if (call.isExecuted()) {
                        setVideoKeys(response.body().getResults());
                    }
                }

                @Override
                public void onFailure(Call<MovieVideoList> call, Throwable t) {
                    Log.e("Video key error", t.getMessage());
                }
            });
        }
    }

    private void setVideoKeys(List<MovieVideo> VideoList) {
        String[] videoKeys = new String[VideoList.size()];
        MovieVideo video;
        for (int i = 0; i < videoKeys.length; i++) {
            video = VideoList.get(i);
            videoKeys[i] = video.getKey();
        }

        if (videoKeys != null) {
            trailerAdapter.setVideoKeys(videoKeys);
            mVideoKeys = videoKeys;
        }
    }

    private void fetchMovieReviewTask(int movieId) {

        Call<MovieReviewList> reviewResultCallback;
        if (service != null) {
            reviewResultCallback = service.getMovieReviewList(movieId);
            reviewResultCallback.enqueue(this);
        }
    }

    @Override
    public void onResponse(Call<MovieReviewList> call, Response<MovieReviewList> response) {

        Log.i("review response code", String.valueOf(response.code()));
        if (call.isExecuted()) {
            mrAdapter.setMovieReviewList(response.body().getResults());
            mMoviesReviewData = response.body().getResults();
        }
    }

    @Override
    public void onFailure(Call<MovieReviewList> call, Throwable t) {
        Log.i("REVIEW RESPONSE FAILED", t.getMessage());
    }
}
