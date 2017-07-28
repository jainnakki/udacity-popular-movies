package com.example.akki.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Akshay on 20-07-2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private Context context;

    //public static final String MOVIE_API_URL = "https://api.themoviedb.org/3/";

    List<AndroidMovies>MoviesList;
    List<MoviesGenre>GenreList;
    Map<Integer, String> genreDictionary = null;

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.movie_image)
        public ImageView moviePoster;
        @BindView(R.id.progressBar)
        public ProgressBar progressBar = null;

        public View layout;


        public ViewHolder(View v){
            super(v);
            layout = v;
            ButterKnife.bind(this,v);
        }

    }

    private AndroidMovies addDummyMoviesData() {
        AndroidMovies MovieDummyData = new AndroidMovies();
        MovieDummyData.setOriginal_title("----");
        MovieDummyData.setPopularity(0.0f);
        MovieDummyData.setVote_count(0);
        MovieDummyData.setGenre_names("----");
        //MovieDummyData.setPoster_path("drawable/not_connected.png");
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
        genreDictionary = new HashMap<Integer, String>();
    }

    public void setGenreList(List<MoviesGenre> genreList) {
        this.GenreList = new ArrayList<>();
        this.GenreList = genreList;
        notifyDataSetChanged();
        MoviesGenre genre;

        if(GenreList!=null) {
            Log.i("GenreList STATUS: ","NOT NULL!!");
            for (int i = 0; i < GenreList.size(); i++) {
                genre = GenreList.get(i);
                genreDictionary.put(genre.getId(), genre.getName());
            }
        } else {
            Log.i("GenreList STATUS: ","IT IS NULL!!");
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
        View v = inflater.inflate(R.layout.movie_item,parent,false);
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final AndroidMovies movies = MoviesList.get(position);
        Log.i("MOVIE ADAPTER", "ID is : " + movies.getId());
        List<Integer> genreIds = movies.getGenre_ids();
        String genre_names = "";
        if(genreIds!=null) {
            Log.e("genreIds no error", "it is not null!!!");
            for (int i = 0; i < genreIds.size(); i++)
                genre_names += genreDictionary.get(genreIds.get(i)) + (i != genreIds.size() - 1 ? "," : "");
        }
        else {
            Log.e("genreIds error", "it is null!!!");
        }
        if (AppStatus.getInstance(context).isOnline()) {
        holder.progressBar.setVisibility(View.VISIBLE);}
        Log.i("POSTER PATH: ","http://image.tmdb.org/t/p/w185" + movies.getPoster_path());
        Picasso.with(context)
                .load("http://image.tmdb.org/t/p/w185" + movies.getPoster_path())
                //.resize(400, 400)
                .fit()
                .error(R.drawable.error)
                .into(holder.moviePoster, new com.squareup.picasso.Callback(){
                    @Override
                    public void onSuccess(){
                        if (holder.progressBar != null) {
                            holder.progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError() {

                    }
                });

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
                movie.setOverview(movies.getOverview());
                movie.setRelease_date(movies.getRelease_date());
                intent.putExtra("movie_details", movie);

                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return (MoviesList == null)? 0 : MoviesList.size();
    }

}
