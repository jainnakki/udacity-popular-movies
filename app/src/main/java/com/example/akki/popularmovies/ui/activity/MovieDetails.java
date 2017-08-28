package com.example.akki.popularmovies.ui.activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.akki.popularmovies.rest.AppStatus;
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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MovieDetails extends AppCompatActivity implements Callback<MovieReviewList> {

    private final String TAG = MovieDetails.class.getSimpleName();

    @BindView(R.id.movie_poster)
    ImageView movie_poster;

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

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.indicator)
    CircleIndicator indicator;

    @BindView(R.id.movie_favourite_button)
    ToggleButton favouriteButton;

    @BindString(R.string.max_rating)
    String maxRating;
    @BindString(R.string.online_status)
    String onlineMessage;
    @BindString(R.string.notOnline_status)
    String notOnlineMessage;

    private File tmpMyDir = null;
    private Bitmap tmpBitmap = null;
    private MovieReviewAdapter mrAdapter;
    private MoviesApiService service;
    private MovieTrailerAdapter trailerAdapter;

    private final String LOG_TAG = MovieDetails.class.getSimpleName();
    private static final String MOVIE_API_URL = "https://api.themoviedb.org/3/";
    private String[] videoKeys = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        mrAdapter = new MovieReviewAdapter(this);
        recyclerView.setAdapter(mrAdapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

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

        //Log.i(LOG_TAG, "MOVIE ID : " + (parcelableMovieData != null ? parcelableMovieData.getId() : null));
        if (parcelableMovieData != null)
            updateMovieDataList(parcelableMovieData.getId());


        //Log.v(LOG_TAG, "IMAGE URL: " + parcelableMovieData.getPoster_path());

        String posterLoadingPath;
        if (parcelableMovieData.getFavourite() == 0) {
            posterLoadingPath = "http://image.tmdb.org/t/p/w185" + parcelableMovieData.getPoster_path();
            Log.v(LOG_TAG, "IMAGE URL NOT FAV: " + parcelableMovieData.getPoster_path());
            Picasso.with(this)
                    .load(posterLoadingPath)
                    .fit()
                    .error(R.drawable.error)
                    .into(movie_poster);
        } else {
            posterLoadingPath = parcelableMovieData.getPoster_path();
            Log.v(LOG_TAG, "IMAGE URL FAV: " + parcelableMovieData.getPoster_path());
            Uri imageUri = Uri.fromFile(new File(posterLoadingPath));
            Picasso.with(this)
                    .load(imageUri)
                    .fit()
                    .error(R.drawable.error)
                    .into(movie_poster);
        }

        title.setText(parcelableMovieData.getOriginal_title());

        int popularityScore = (int) Math.ceil(parcelableMovieData.getPopularity());
        Log.d("POPULARITY: ", String.valueOf(parcelableMovieData.getPopularity()));
        String pScore = String.valueOf(popularityScore) + "%";
        movie_popularity.setText(pScore);

        int movieVotes = parcelableMovieData.getVote_count();
        Log.d("MOVIE VOTES: ", String.valueOf(parcelableMovieData.getVote_count()));
        String mVotes = movieVotes + " votes";
        movie_votes.setText(mVotes);

        overviewExpTv.setText(parcelableMovieData.getOverview());
        Log.i(LOG_TAG, "MOVIE RATING: " + parcelableMovieData.getUser_rating());
        String ratingString = String.valueOf(parcelableMovieData.getUser_rating()) + maxRating;
        rating.setText(ratingString);

        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(parcelableMovieData.getRelease_date());
            String formattedDate = new SimpleDateFormat("MMM dd, yyyy", Locale.US).format(date);
            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
            Date date1 = new SimpleDateFormat("MMM dd, yyyy", Locale.US).parse(formattedDate);
            String formattedDate1 = dateFormat.format(date1);
            //release_date.setText(formattedDate);
            release_date.setText(formattedDate1);
        } catch (ParseException e) {
            e.printStackTrace();
            release_date.setText(parcelableMovieData.getRelease_date());
        }

        movie_genre.setText(parcelableMovieData.getGenre_names());
        Log.i(LOG_TAG, "MOVIE GENRES: " + parcelableMovieData.getGenre_names());

        if (parcelableMovieData.getFavourite() == 0)
            favouriteButton.setChecked(false);
        else
            favouriteButton.setChecked(true);

        favouriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && parcelableMovieData.getFavourite() == 0)
                    saveMovieDetailsInDatabase(parcelableMovieData);

                if (!isChecked && parcelableMovieData.getFavourite() == 1)
                    removeMovieDetailsFromDatabase(parcelableMovieData);
            }
        });

    }

    private void saveMovieDetailsInDatabase(AndroidMovies MoviesData) {
        String imageURL = "http://image.tmdb.org/t/p/w185" + MoviesData.getPoster_path();
        String imagePathOnStorage = null;

        try {
            imagePathOnStorage = saveImageOffline(imageURL, MoviesData.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (imagePathOnStorage != null) {
            Log.i("Offline Path Saved: ", imagePathOnStorage);
            Bitmap bmap = BitmapFactory.decodeFile(imagePathOnStorage);
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
            values.put(MoviesTable.COLUMN_POSTER_PATH, imagePathOnStorage);

            getContentResolver().insert(MoviesContentProvider.CONTENT_URI, values);
            makeToast("Details Saved!");
        }
    }

    private void removeMovieDetailsFromDatabase(AndroidMovies MoviesData) {
        Uri uri = Uri.parse(MoviesContentProvider.CONTENT_URI + "/"
                + MoviesData.getId());
        getContentResolver().delete(uri, null, null);
    }

    private String saveImageOffline(String ImageUrl, final int imageId) throws IOException {


        InputStream input = null;
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/PopularMovies");

        try {

            URL url = new URL(ImageUrl);


            if (!myDir.exists())
                myDir.mkdirs();


            String outputName = imageId + "-thumbnail.jpg";
            myDir = new File(myDir, outputName);

            input = url.openConnection().getInputStream();

            Bitmap bitmap = BitmapFactory.decodeStream(input);

            if (isStoragePermissionGranted()) {
                SaveImage(bitmap, myDir);
            } else {
                tmpBitmap = bitmap;
                tmpMyDir = myDir;
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (input != null)
                input.close();
        }

        return myDir.getPath();
    }

    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 2: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();

                    if (tmpBitmap != null && tmpMyDir != null)
                        SaveImage(tmpBitmap, tmpMyDir);

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

    private void setVideoKeys(List<MovieVideo> VideoList) {
        videoKeys = new String[VideoList.size()];
        MovieVideo video;
        for (int i = 0; i < videoKeys.length; i++) {
            video = VideoList.get(i);
            videoKeys[i] = video.getKey();
        }

        if(videoKeys != null) {
            trailerAdapter = new MovieTrailerAdapter(MovieDetails.this,videoKeys);
            viewPager.setAdapter(trailerAdapter);
            indicator.setViewPager(viewPager);
        }
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

        //MoviesApiService service = restAdapter.create(MoviesApiService.class);
        Call<MovieVideoList> videoResultCallback;
        if (service != null) {
            videoResultCallback = service.getMovieVideoList(movieId);
            videoResultCallback.enqueue(new Callback<MovieVideoList>() {
                @Override
                public void onResponse(Call<MovieVideoList> call, Response<MovieVideoList> response) {
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

    private void fetchMovieReviewTask(int movieId) {

        //MoviesApiService service = restAdapter.create(MoviesApiService.class);
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
        }
    }

    @Override
    public void onFailure(Call<MovieReviewList> call, Throwable t) {
        Log.i("REVIEW RESPONSE FAILED", t.getMessage());
    }
}
