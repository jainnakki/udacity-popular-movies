package com.example.akki.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetails extends AppCompatActivity {

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

    @BindString(R.string.max_rating)
    String maxRating;

    private final String LOG_TAG = MovieDetails.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        ButterKnife.bind(this);

        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        AndroidMovies parcelableMovieData = (AndroidMovies) intent.getExtras().getParcelable("movie_details");


        Log.v(LOG_TAG, "IMAGE URL: " + parcelableMovieData.getPoster_path());

        Picasso.with(this)
                .load("http://image.tmdb.org/t/p/w185" + parcelableMovieData.getPoster_path())
                .resize(500, 500)
                //.fit()
                .error(R.drawable.error)
                .into(movie_poster);

        title.setText(parcelableMovieData.getOriginal_title());
        double popularityScore = parcelableMovieData.getPopularity();
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
        release_date.setText(parcelableMovieData.getRelease_date());


    }
}
