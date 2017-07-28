package com.example.akki.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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

public class MovieDetails extends AppCompatActivity implements Callback<MovieReviewList> {

    @BindView(R.id.movie_poster)
    ImageView movie_poster;
    @BindView(R.id.movie_title)
    TextView title;
    //@BindView(R.id.movie_overview)
    //TextView overview;
    @BindView(R.id.expand_text_view)
    ExpandableTextView expTv1;
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

    @BindString(R.string.max_rating)
    String maxRating;
    @BindString(R.string.online_status)
    String onlineMessage;
    @BindString(R.string.notOnline_status)
    String notOnlineMessage;

    private MovieReviewAdapter mrAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    MoviesApiService service;
    List<MovieReview> ReviewList;

    private final String LOG_TAG = MovieDetails.class.getSimpleName();
    public static final String MOVIE_API_URL = "https://api.themoviedb.org/3/";
    private String[] videoKeys = null;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        mrAdapter = new MovieReviewAdapter(this);
        recyclerView.setAdapter(mrAdapter);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        AndroidMovies parcelableMovieData = intent.getExtras().getParcelable("movie_details");

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

        Log.i(LOG_TAG, "MOVIE ID : " + parcelableMovieData.getId());
        if(parcelableMovieData!=null)
        updateMovieDataList(parcelableMovieData.getId());

        Log.v(LOG_TAG, "IMAGE URL: " + parcelableMovieData.getPoster_path());

        Picasso.with(this)
                .load("http://image.tmdb.org/t/p/w185" + parcelableMovieData.getPoster_path())
                //.resize(500, 500)
                .fit()
                .error(R.drawable.error)
                .into(movie_poster);

        title.setText(parcelableMovieData.getOriginal_title());
        int popularityScore = (int) Math.ceil(parcelableMovieData.getPopularity());
        Log.d("POPULARITY: ", String.valueOf(parcelableMovieData.getPopularity()));
        movie_popularity.setText(String.valueOf(popularityScore) + "%");

        int movieVotes = parcelableMovieData.getVote_count();
        Log.d("MOVIE VOTES: ", String.valueOf(parcelableMovieData.getVote_count()));
        movie_votes.setText(movieVotes + " votes");

        //overview.setText(parcelableMovieData.getOverview());
        expTv1.setText(parcelableMovieData.getOverview());
        Log.i(LOG_TAG, "MOVIE RATING: " + parcelableMovieData.getUser_rating());
        String ratingString = String.valueOf(parcelableMovieData.getUser_rating()) + maxRating;
        rating.setText(ratingString);

        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(parcelableMovieData.getRelease_date());
            String formattedDate = new SimpleDateFormat("MMM dd, yyyy").format(date);
            release_date.setText(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
            release_date.setText(parcelableMovieData.getRelease_date());
        }

        movie_genre.setText(parcelableMovieData.getGenre_names());
        Log.i(LOG_TAG, "MOVIE GENRES: " + parcelableMovieData.getGenre_names());

    }

    @OnClick({ R.id.movie_poster, R.id.video_preview_play_image })
    public void playVideo() {
        if(videoKeys!=null)
        watchYoutubeVideo(videoKeys[0]);
    }


    public void watchYoutubeVideo(String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            startActivity(webIntent);
        }
    }

    private void setVideoKeys(List<MovieVideo> VideoList) {
        videoKeys = new String[VideoList.size()];
        MovieVideo video;
        for(int i=0;i<videoKeys.length;i++) {
            video = VideoList.get(i);
            videoKeys[i] = video.getKey();
        }
    }

    private void updateMovieDataList(int movieId) {

        if (AppStatus.getInstance(this).isOnline()) {

            Toast t = Toast.makeText(this, onlineMessage, Toast.LENGTH_SHORT);
            t.show();
            fetchMovieVideoTask(movieId);
            fetchMovieReviewTask(movieId);
        } else {

            Toast t = Toast.makeText(this, notOnlineMessage, Toast.LENGTH_SHORT);
            t.show();
        }

    }

    void fetchMovieVideoTask(int movieId) {

        //MoviesApiService service = restAdapter.create(MoviesApiService.class);
        Call<MovieVideoList> videoResultCallback;
        if(service!=null) {
            videoResultCallback = service.getMovieVideoList(movieId);
            videoResultCallback.enqueue(new Callback<MovieVideoList>() {
                @Override
                public void onResponse(Call<MovieVideoList> call, Response<MovieVideoList> response) {
                    int code = response.code();
                    Log.i("video response code", String.valueOf(response.code()));
                    if(call.isExecuted()){
                        setVideoKeys(response.body().getResults());
                    }
                }

                @Override
                public void onFailure(Call<MovieVideoList> call, Throwable t) {
                    Log.e("Video key error",t.getMessage());
                }
            });
        }
    }

    void fetchMovieReviewTask(int movieId) {

        //MoviesApiService service = restAdapter.create(MoviesApiService.class);
        Call<MovieReviewList> reviewResultCallback;
        if(service!=null) {
            reviewResultCallback = service.getMovieReviewList(movieId);
            reviewResultCallback.enqueue(this);
        }
    }

    @Override
    public void onResponse(Call<MovieReviewList> call, Response<MovieReviewList> response) {

        int code = response.code();
        Log.i("review response code", String.valueOf(response.code()));
        if(call.isExecuted()){
            mrAdapter.setMovieReviewList(response.body().getResults());
        }
    }

    @Override
    public void onFailure(Call<MovieReviewList> call, Throwable t) {
        Log.i("REVIEW RESPONSE FAILED",t.getMessage());
    }
}
